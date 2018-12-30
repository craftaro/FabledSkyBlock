package me.goodandevil.skyblock.listeners;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.IslandRole;

public class Death implements Listener {

	private final SkyBlock skyblock;

	public Death(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
			Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();

			boolean keepInventory = false;

			if (configLoad.getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
				if (skyblock.getIslandManager().hasSetting(player.getLocation(), IslandRole.Owner,
						"KeepItemsOnDeath")) {
					keepInventory = true;
				}
			} else if (configLoad.getBoolean("Island.KeepItemsOnDeath.Enable")) {
				keepInventory = true;
			} else {
				keepInventory = false;
			}

			if (keepInventory) {
				event.setKeepInventory(true);
				event.getDrops().clear();
				event.setKeepLevel(true);
				event.setDroppedExp(0);
			}

			if (configLoad.getBoolean("Island.Death.AutoRespawn")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(skyblock, new Runnable() {
					public void run() {
						player.spigot().respawn();
						player.setFallDistance(0.0F);
						player.setFireTicks(0);
					}
				}, 1L);
			}
		}
	}
}
