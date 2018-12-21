package me.goodandevil.skyblock.hologram;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.IslandLevel;
import me.goodandevil.skyblock.leaderboard.Leaderboard;
import me.goodandevil.skyblock.leaderboard.LeaderboardManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.visit.Visit;

public class HologramManager {

	private final SkyBlock skyblock;
	private List<Hologram> hologramStorage = new ArrayList<>();

	public HologramManager(SkyBlock skyblock) {
		this.skyblock = skyblock;

		FileManager fileManager = skyblock.getFileManager();

		Bukkit.getServer().getScheduler().runTaskLater(skyblock, new Runnable() {
			@Override
			public void run() {
				removeWorldHolograms();

				for (HologramType hologramTypeList : HologramType.values()) {
					if (hologramTypeList == HologramType.Level || hologramTypeList == HologramType.Votes) {
						if (hologramTypeList == HologramType.Votes) {
							if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
									.getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
								continue;
							}
						}

						spawnHologram(hologramTypeList);
					}
				}
			}
		}, 200L);
	}

	public void onDisable() {
		removeHolograms();
	}

	public void spawnHologram(HologramType type, Location location, List<String> lines) {
		hologramStorage.add(new Hologram(type, location, lines));
	}

	public void spawnHologram(HologramType type) {
		LeaderboardManager leaderboardManager = skyblock.getLeaderboardManager();
		MessageManager messageManager = skyblock.getMessageManager();
		FileManager fileManager = skyblock.getFileManager();

		Config locationsConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "locations.yml"));
		FileConfiguration locationsConfigLoad = locationsConfig.getFileConfiguration();
		FileConfiguration languageConfigLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
				.getFileConfiguration();

		if (locationsConfigLoad.getString("Location.Hologram.Leaderboard." + type) != null) {
			List<String> hologramLines = new ArrayList<>();
			Leaderboard.Type leaderboardType = null;

			if (type == HologramType.Level) {
				leaderboardType = Leaderboard.Type.Level;
			} else if (type == HologramType.Votes) {
				leaderboardType = Leaderboard.Type.Votes;
			}

			hologramLines.add(messageManager.replaceMessage(null,
					languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Header")));

			for (int i = 0; i < 10; i++) {
				Leaderboard leaderboard = leaderboardManager.getLeaderboardFromPosition(leaderboardType, i);

				if (leaderboard == null) {
					hologramLines.add(messageManager.replaceMessage(null,
							languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Unclaimed")
									.replace("%position", "" + (i + 1))));
				} else {
					Visit visit = leaderboard.getVisit();

					Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
					String islandOwnerName;

					if (targetPlayer == null) {
						islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
					} else {
						islandOwnerName = targetPlayer.getName();
					}

					if (type == HologramType.Level) {
						IslandLevel level = visit.getLevel();
						hologramLines.add(ChatColor.translateAlternateColorCodes('&',
								languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Claimed")
										.replace("%position", "" + (i + 1)).replace("%player", islandOwnerName)
										.replace("%level", NumberUtil.formatNumberByDecimal(level.getLevel()))
										.replace("%points", NumberUtil.formatNumberByDecimal(level.getPoints()))));
					} else if (type == HologramType.Votes) {
						hologramLines.add(ChatColor.translateAlternateColorCodes('&',
								languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Claimed")
										.replace("%position", "" + (i + 1)).replace("%player", islandOwnerName)
										.replace("%votes",
												"" + NumberUtil.formatNumberByDecimal(visit.getVoters().size()))));
					}
				}
			}

			String hologramFooter = languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Footer");

			if (!hologramFooter.isEmpty()) {
				hologramLines.add(messageManager.replaceMessage(null, hologramFooter));
			}

			Collections.reverse(hologramLines);

			spawnHologram(type, skyblock.getFileManager().getLocation(locationsConfig,
					"Location.Hologram.Leaderboard." + type, true), hologramLines);
		}
	}

	public void removeHologram(Hologram hologram) {
		if (hologramStorage.contains(hologram)) {
			List<ArmorStand> holograms = hologram.getHolograms();

			for (Iterator<ArmorStand> it = holograms.iterator(); it.hasNext();) {
				it.next().remove();
			}

			hologramStorage.remove(hologram);
		}
	}

	public void removeHolograms() {
		for (Hologram hologramList : hologramStorage) {
			List<ArmorStand> holograms = hologramList.getHolograms();

			for (Iterator<ArmorStand> it = holograms.iterator(); it.hasNext();) {
				it.next().remove();
			}
		}
	}

	public void removeWorldHolograms() {
		FileManager fileManager = skyblock.getFileManager();

		List<Location> locations = new ArrayList<>();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "locations.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		for (HologramType hologramTypeList : HologramType.values()) {
			if (hologramTypeList == HologramType.Level || hologramTypeList == HologramType.Votes) {
				if (configLoad.getString("Location.Hologram.Leaderboard." + hologramTypeList.name()) != null) {
					locations.add(fileManager.getLocation(config,
							"Location.Hologram.Leaderboard." + hologramTypeList.name(), true));
				}
			}
		}

		for (World worldList : Bukkit.getWorlds()) {
			List<Entity> entities = worldList.getEntities();

			for (Iterator<Entity> it = entities.iterator(); it.hasNext();) {
				Entity entity = it.next();

				if (entity instanceof ArmorStand) {
					for (Location locationList : locations) {
						if (LocationUtil.isLocationAtLocationRadius(entity.getLocation(), locationList, 1)) {
							entity.remove();
						}
					}
				}
			}
		}
	}

	public Hologram getHologram(HologramType type) {
		for (Hologram hologramList : hologramStorage) {
			if (hologramList.getType() == type) {
				return hologramList;
			}
		}

		return null;
	}

	public boolean hasHologram(HologramType type) {
		for (Hologram hologramList : hologramStorage) {
			if (hologramList.getType() == type) {
				return true;
			}
		}

		return false;
	}

	public void resetHologram() {
		LeaderboardManager leaderboardManager = skyblock.getLeaderboardManager();
		MessageManager messageManager = skyblock.getMessageManager();
		FileManager fileManager = skyblock.getFileManager();

		FileConfiguration configLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
				.getFileConfiguration();

		for (HologramType hologramTypeList : HologramType.values()) {
			if (hologramTypeList == HologramType.Level || hologramTypeList == HologramType.Votes) {
				if (hologramTypeList == HologramType.Votes) {
					if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
							.getBoolean("Island.Visitor.Vote")) {
						continue;
					}
				}

				Hologram hologram;

				if (hasHologram(hologramTypeList)) {
					hologram = getHologram(hologramTypeList);
				} else {
					continue;
				}

				Leaderboard.Type leaderboardType = null;

				if (hologramTypeList == HologramType.Level) {
					leaderboardType = Leaderboard.Type.Level;
				} else if (hologramTypeList == HologramType.Votes) {
					leaderboardType = Leaderboard.Type.Votes;
				}

				for (int i = 0; i < 10; i++) {
					Leaderboard leaderboard = leaderboardManager.getLeaderboardFromPosition(leaderboardType, i);
					int hologramLine = 10 - i;

					if (leaderboard == null) {
						hologram.setLine(hologramLine, messageManager.replaceMessage(null,
								configLoad.getString("Hologram.Leaderboard." + hologramTypeList.name() + ".Unclaimed")
										.replace("%position", "" + (i + 1))));
					} else {
						Visit visit = leaderboard.getVisit();

						Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
						String islandOwnerName;

						if (targetPlayer == null) {
							islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
						} else {
							islandOwnerName = targetPlayer.getName();
						}

						if (hologramTypeList == HologramType.Level) {
							IslandLevel level = visit.getLevel();
							hologram.setLine(hologramLine, ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Hologram.Leaderboard." + hologramTypeList.name() + ".Claimed")
											.replace("%position", "" + (i + 1)).replace("%player", islandOwnerName)
											.replace("%level", NumberUtil.formatNumberByDecimal(level.getLevel()))
											.replace("%points", NumberUtil.formatNumberByDecimal(level.getPoints()))));
						} else if (hologramTypeList == HologramType.Votes) {
							hologram.setLine(hologramLine, ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Hologram.Leaderboard." + hologramTypeList.name() + ".Claimed")
											.replace("%position", "" + (i + 1)).replace("%player", islandOwnerName)
											.replace("%votes",
													"" + NumberUtil.formatNumberByDecimal(visit.getVoters().size()))));
						}
					}
				}
			}
		}
	}
}
