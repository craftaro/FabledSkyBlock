package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class Respawn implements Listener {
	
	private final Main plugin;
	
 	public Respawn(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		
		for (me.goodandevil.skyblock.island.Location.World worldList : me.goodandevil.skyblock.island.Location.World.values()) {
			if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(worldList).getName())) {
				FileManager fileManager = plugin.getFileManager();
				Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
				FileConfiguration configLoad = config.getFileConfiguration();
				
				IslandManager islandManager = plugin.getIslandManager();
				
				if (configLoad.getBoolean("Island.Death.Respawn.Island")) {
					for (UUID islandList : islandManager.getIslands().keySet()) {
						Island island = islandManager.getIslands().get(islandList);
						
						if (LocationUtil.isLocationAtLocationRadius(player.getLocation(), island.getLocation(worldList, me.goodandevil.skyblock.island.Location.Environment.Island), island.getRadius())) {
							Location playerLocation = player.getLocation().clone(), islandLocation;
							
							if (island.isRole(Role.Member, player.getUniqueId()) || island.isRole(Role.Operator, player.getUniqueId()) || island.isRole(Role.Owner, player.getUniqueId())) {
								islandLocation = island.getLocation(worldList, me.goodandevil.skyblock.island.Location.Environment.Main);
							} else {
								islandLocation = island.getLocation(worldList, me.goodandevil.skyblock.island.Location.Environment.Visitor);
							}
							
							Bukkit.getServer().getPluginManager().callEvent(new PlayerTeleportEvent(player, playerLocation, islandLocation));
							event.setRespawnLocation(islandLocation);
							
							return;
						}
					}
				}
				
				config = fileManager.getConfig(new File(plugin.getDataFolder(), "locations.yml"));
	        	
				if (config.getFileConfiguration().getString("Location.Spawn") == null) {
					Bukkit.getServer().getLogger().log(Level.WARNING, "SkyBlock | Error: A spawn point hasn't been set.");
				} else {
					Location playerLocation = player.getLocation().clone(), islandLocation = fileManager.getLocation(config, "Location.Spawn", true);
					Bukkit.getServer().getPluginManager().callEvent(new PlayerTeleportEvent(player, playerLocation, islandLocation));
					event.setRespawnLocation(islandLocation);
				}
				
				break;
			}
		}
	}
}
