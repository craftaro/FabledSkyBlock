package me.goodandevil.skyblock.scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandLevel;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.placeholder.PlaceholderManager;
import me.goodandevil.skyblock.utils.NumberUtil;

public class Scoreboard {

	private Player player;
	private String displayName;
	private List<String> displayList;
	private Map<String, String> displayVariables;
	private BukkitTask scheduler;

	public Scoreboard(Player player) {
		this.player = player;
		displayList = new ArrayList<>();
		displayVariables = new HashMap<>();
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setDisplayList(List<String> displayList) {
		this.displayList = displayList;
	}

	public void setDisplayVariables(Map<String, String> displayVariables) {
		this.displayVariables = displayVariables;
	}

	public void run() {
		SkyBlock skyblock = SkyBlock.getInstance();

		new BukkitRunnable() {
			@Override
			public void run() {
				final org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

				new BukkitRunnable() {
					@Override
					@SuppressWarnings("deprecation")
					public void run() {
						String ranStr = UUID.randomUUID().toString().split("-")[0];
						Objective obj;

						if (scoreboard.getObjective(player.getName()) != null) {
							obj = scoreboard.getObjective(player.getName());
						} else {
							obj = scoreboard.registerNewObjective(player.getName(), "dummy");
						}

						obj.setDisplaySlot(DisplaySlot.SIDEBAR);

						String formattedDisplayName = ChatColor.translateAlternateColorCodes('&',
								replaceDisplayName(displayName));

						if (formattedDisplayName.length() > 32) {
							obj.setDisplayName(ChatColor.RED + "Too long...");
						} else {
							obj.setDisplayName(formattedDisplayName);
						}

						for (int i = 0; i < ChatColor.values().length; i++) {
							if (i == displayList.size()) {
								break;
							}

							ChatColor chatColor = ChatColor.values()[i];
							Team team = scoreboard.registerNewTeam(ranStr + i);
							team.addEntry(chatColor.toString());
							obj.getScore(chatColor.toString()).setScore(i);
						}

						scheduler = new BukkitRunnable() {
							int i1 = displayList.size();

							@Override
							public void run() {
								if (player.isOnline()) {
									try {
										String formattedDisplayName = ChatColor.translateAlternateColorCodes('&',
												replaceDisplayName(displayName));

										if (formattedDisplayName.length() > 32) {
											obj.setDisplayName(ChatColor.RED + "Too long...");
										} else {
											obj.setDisplayName(formattedDisplayName);
										}

										for (String displayLine : displayList) {
											i1--;

											displayLine = replaceDisplayLine(displayLine);

											if (displayLine.length() > 32) {
												displayLine = "&cLine too long...";
											}

											if (displayLine.length() >= 16) {
												String prefixLine = displayLine.substring(0,
														Math.min(displayLine.length(), 16));
												String suffixLine = displayLine.substring(16,
														Math.min(displayLine.length(), displayLine.length()));

												if (prefixLine.substring(prefixLine.length() - 1).equals("&")) {
													prefixLine = ChatColor.translateAlternateColorCodes('&',
															prefixLine.substring(0, prefixLine.length() - 1));
													suffixLine = ChatColor.translateAlternateColorCodes('&',
															"&" + suffixLine);
												} else {
													String lastColorCodes;

													if (prefixLine.contains("&")) {
														String[] colorCodes = prefixLine.split("&");
														String lastColorCodeText = colorCodes[colorCodes.length - 1];
														lastColorCodes = "&" + lastColorCodeText.substring(0,
																Math.min(lastColorCodeText.length(), 1));

														if ((colorCodes.length >= 2) && (lastColorCodes.equals("&l")
																|| lastColorCodes.equals("&m")
																|| lastColorCodes.equals("&n")
																|| lastColorCodes.equals("&o"))) {
															lastColorCodeText = colorCodes[colorCodes.length - 2];
															lastColorCodes = "&"
																	+ lastColorCodeText.substring(0,
																			Math.min(lastColorCodeText.length(), 1))
																	+ lastColorCodes;
														}
													} else {
														lastColorCodes = "&f";
													}

													prefixLine = ChatColor.translateAlternateColorCodes('&',
															prefixLine);
													suffixLine = ChatColor.translateAlternateColorCodes('&',
															lastColorCodes + suffixLine);
												}

												scoreboard.getTeam(ranStr + i1).setPrefix(prefixLine);
												scoreboard.getTeam(ranStr + i1).setSuffix(suffixLine);
											} else {
												scoreboard.getTeam(ranStr + i1).setPrefix(
														ChatColor.translateAlternateColorCodes('&', displayLine));
											}
										}

										i1 = displayList.size();
									} catch (Exception e) {
										cancel();
									}
								} else {
									cancel();
								}
							}
						}.runTaskTimerAsynchronously(skyblock, 0L, 20L);

						player.setScoreboard(scoreboard);
					}
				}.runTaskAsynchronously(skyblock);
			}
		}.runTask(skyblock);
	}

	private String replaceDisplayName(String displayName) {
		displayName = displayName.replace("%players_online", "" + Bukkit.getServer().getOnlinePlayers().size())
				.replace("%players_max", "" + Bukkit.getServer().getMaxPlayers());

		return displayName;
	}

	private String replaceDisplayLine(String displayLine) {
		SkyBlock skyblock = SkyBlock.getInstance();

		IslandManager islandManager = skyblock.getIslandManager();

		displayLine = displayLine.replace("%players_online", "" + Bukkit.getServer().getOnlinePlayers().size())
				.replace("%players_max", "" + Bukkit.getServer().getMaxPlayers());

		Island island = islandManager.getIsland(player);

		if (island != null) {
			IslandLevel level = island.getLevel();

			if (island.getRole(IslandRole.Member).size() == 0 && island.getRole(IslandRole.Operator).size() == 0) {
				displayLine = displayLine
						.replace("%island_level", "" + NumberUtil.formatNumberByDecimal(level.getLevel()))
						.replace("%island_members", ChatColor.RED + "0").replace("%island_role", ChatColor.RED + "null")
						.replace("%island_visitors", "" + islandManager.getVisitorsAtIsland(island).size())
						.replace("%island_size", "" + island.getSize())
						.replace("%island_radius", "" + island.getRadius());
			} else {
				int islandMembers = 1 + island.getRole(IslandRole.Member).size()
						+ island.getRole(IslandRole.Operator).size();
				String islandRole = "";

				if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
					islandRole = displayVariables.get("%owner");
				} else if (island.hasRole(IslandRole.Operator, player.getUniqueId())) {
					islandRole = displayVariables.get("%operator");
				} else if (island.hasRole(IslandRole.Member, player.getUniqueId())) {
					islandRole = displayVariables.get("%member");
				}

				displayLine = displayLine
						.replace("%island_points", "" + NumberUtil.formatNumberByDecimal(level.getPoints()))
						.replace("%island_level", "" + NumberUtil.formatNumberByDecimal(level.getLevel()))
						.replace("%island_members", "" + islandMembers).replace("%island_role", islandRole)
						.replace("%island_visitors", "" + islandManager.getVisitorsAtIsland(island).size())
						.replace("%island_size", "" + island.getSize())
						.replace("%island_radius", "" + island.getRadius());
			}
		} else {
			displayLine = displayLine.replace("%island_points", ChatColor.RED + "0")
					.replace("%island_level", ChatColor.RED + "0").replace("%island_members", ChatColor.RED + "0")
					.replace("%island_role", ChatColor.RED + "null").replace("%island_size", ChatColor.RED + "0")
					.replace("%island_radius", ChatColor.RED + "0");
		}

		PlaceholderManager placeholderManager = skyblock.getPlaceholderManager();

		if (placeholderManager.isPlaceholderAPIEnabled()) {
			displayLine = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, displayLine.replace("&", "clr"))
					.replace("clr", "&");
		}

		return displayLine;
	}

	public void cancel() {
		scheduler.cancel();
	}
}
