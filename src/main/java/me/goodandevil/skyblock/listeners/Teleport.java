package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.event.player.PlayerIslandEnterEvent;
import me.goodandevil.skyblock.api.event.player.PlayerIslandExitEvent;
import me.goodandevil.skyblock.api.event.player.PlayerIslandSwitchEvent;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.visit.Visit;

public class Teleport implements Listener {

	private final SkyBlock skyblock;

	public Teleport(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		islandManager.removeUpgrades(player);
		islandManager.loadPlayer(player);

		if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(Location.World.Normal).getName())
				|| player.getWorld().getName()
						.equals(skyblock.getWorldManager().getWorld(Location.World.Nether).getName())) {
			if (event.getCause() == TeleportCause.ENDER_PEARL || event.getCause() == TeleportCause.NETHER_PORTAL) {
				if (!islandManager.hasPermission(player, "Portal")) {
					event.setCancelled(true);

					messageManager.sendMessage(player, configLoad.getString("Island.Settings.Permission.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);

					return;
				}
			}
		}

		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();

		if (playerDataManager.hasPlayerData(player)) {
			PlayerData playerData = playerDataManager.getPlayerData(player);

			UUID islandOwnerUUID = playerData.getIsland();

			for (UUID islandList : islandManager.getIslands().keySet()) {
				Island island = islandManager.getIslands().get(islandList);

				for (Location.World worldList : Location.World.values()) {
					if (LocationUtil.isLocationAtLocationRadius(event.getTo(),
							island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
						if (!island.getOwnerUUID().equals(playerData.getOwner())) {
							if (!player.hasPermission("skyblock.bypass") && !player.hasPermission("skyblock.bypass.*")
									&& !player.hasPermission("skyblock.*")) {
								if (!island.isOpen() && !island.isCoopPlayer(player.getUniqueId())) {
									event.setCancelled(true);

									messageManager.sendMessage(player,
											configLoad.getString("Island.Visit.Closed.Plugin.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								} else if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
										.getFileConfiguration().getBoolean("Island.Visitor.Banning")
										&& island.getBan().isBanned(player.getUniqueId())) {
									event.setCancelled(true);

									messageManager.sendMessage(player,
											configLoad.getString("Island.Visit.Banned.Teleport.Message"));
									soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

									return;
								}
							}
						}

						if (playerData.getIsland() != null && !playerData.getIsland().equals(island.getOwnerUUID())) {
							Bukkit.getServer().getPluginManager().callEvent(new PlayerIslandExitEvent(player,
									islandManager.getIsland(islandOwnerUUID).getAPIWrapper()));
							Bukkit.getServer().getPluginManager().callEvent(new PlayerIslandSwitchEvent(player,
									islandManager.getIsland(islandOwnerUUID).getAPIWrapper(), island.getAPIWrapper()));

							playerData.setVisitTime(0);
						}

						if (worldList == Location.World.Normal) {
							if (!island.isWeatherSynchronized()) {
								player.setPlayerTime(island.getTime(),
										fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
												.getFileConfiguration().getBoolean("Island.Weather.Time.Cycle"));
								player.setPlayerWeather(island.getWeather());
							}
						}

						playerData.setIsland(island.getOwnerUUID());

						if (islandOwnerUUID != null && islandManager.containsIsland(islandOwnerUUID)
								&& (playerData.getOwner() == null || !playerData.getOwner().equals(islandOwnerUUID))) {
							islandManager.unloadIsland(islandManager.getIsland(islandOwnerUUID), null);
						}

						Visit visit = island.getVisit();

						if (!visit.isVisitor(player.getUniqueId())) {
							Bukkit.getServer().getPluginManager()
									.callEvent(new PlayerIslandEnterEvent(player, island.getAPIWrapper()));

							visit.addVisitor(player.getUniqueId());
							visit.save();
						}

						return;
					}
				}
			}

			player.resetPlayerTime();
			player.resetPlayerWeather();

			if (islandOwnerUUID != null) {
				me.goodandevil.skyblock.api.island.Island island = null;

				if (islandManager.hasIsland(islandOwnerUUID)) {
					island = islandManager.getIsland(islandOwnerUUID).getAPIWrapper();
				}

				Bukkit.getServer().getPluginManager().callEvent(new PlayerIslandExitEvent(player, island));

				playerData.setVisitTime(0);
			}

			playerData.setIsland(null);

			if (islandOwnerUUID != null && islandManager.containsIsland(islandOwnerUUID)
					&& (playerData.getOwner() == null || !playerData.getOwner().equals(islandOwnerUUID))) {
				islandManager.unloadIsland(islandManager.getIsland(islandOwnerUUID), null);
			}
		}
	}
}
