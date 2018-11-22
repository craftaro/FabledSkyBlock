package me.goodandevil.skyblock.listeners;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.HorseInventory;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Inventory implements Listener {

	private final Main plugin;
	
 	public Inventory(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Player player = (Player) event.getPlayer();
		
		if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
			if (event.getInventory() instanceof HorseInventory) {
				if (!plugin.getIslandManager().hasPermission(player, "HorseInventory")) {
					event.setCancelled(true);
					
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message")));
					plugin.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			}	
		}
	}
}
