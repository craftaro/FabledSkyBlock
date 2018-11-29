package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Setting;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class Death implements Listener {
	
	private final SkyBlock skyblock;
	
 	public Death(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		
		for (Location.World worldList : Location.World.values()) {
			if (player.getWorld().getName().equals(skyblock.getWorldManager().getWorld(worldList).getName())) {
				Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
				FileConfiguration configLoad = config.getFileConfiguration();
				
				IslandManager islandManager = skyblock.getIslandManager();
				
				boolean keepInventory = false;
				
				for (UUID islandList : islandManager.getIslands().keySet()) {
					Island island = islandManager.getIslands().get(islandList);
					
					if (LocationUtil.isLocationAtLocationRadius(player.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
						if (island.getSetting(Setting.Role.Owner, "KeepItemsOnDeath").getStatus()) {
							keepInventory = true;
						}
						
						break;
					}
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
				
				break;
			}
		}
	}
}
