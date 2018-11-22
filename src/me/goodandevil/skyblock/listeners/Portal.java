package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class Portal implements Listener {

	private final Main plugin;
	
 	public Portal(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityPortalEnter(EntityPortalEnterEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		
		IslandManager islandManager = plugin.getIslandManager();
		SoundManager soundManager = plugin.getSoundManager();
		
		if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName())) {
			for (UUID islandList : islandManager.getIslands().keySet()) {
				Island island = islandManager.getIslands().get(islandList);
				
				if (LocationUtil.isLocationAtLocationRadius(player.getLocation(), island.getLocation(Location.World.Normal, Location.Environment.Island), island.getRadius())) {
					if (islandManager.hasPermission(player, "Portal")) {
						player.teleport(island.getLocation(Location.World.Nether, Location.Environment.Main));
						soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
					} else {
						player.teleport(island.getLocation(Location.World.Normal, Location.Environment.Main));
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message")));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					}
					
					break;
				}
			}
		} else if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
			for (UUID islandList : islandManager.getIslands().keySet()) {
				Island island = islandManager.getIslands().get(islandList);
				
				if (LocationUtil.isLocationAtLocationRadius(player.getLocation(), island.getLocation(Location.World.Nether, Location.Environment.Island), island.getRadius())) {
					if (islandManager.hasPermission(player, "Portal")) {
						player.teleport(island.getLocation(Location.World.Normal, Location.Environment.Main));
						soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
					} else {
						player.teleport(island.getLocation(Location.World.Nether, Location.Environment.Main));
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message")));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					}
					
					break;
				}
			}
		}
	}
}
