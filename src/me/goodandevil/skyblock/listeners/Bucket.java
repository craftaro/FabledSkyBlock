package me.goodandevil.skyblock.listeners;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Bucket implements Listener {

	private final Main plugin;
	
 	public Bucket(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();

		if (event.getBlockClicked().getType() == Material.WATER || event.getBlockClicked().getType() == Materials.LEGACY_STATIONARY_WATER.getPostMaterial() || event.getBlockClicked().getType() == Material.LAVA || event.getBlockClicked().getType() == Materials.LEGACY_STATIONARY_LAVA.getPostMaterial()) {
			if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Nether).getName())) {
				if (!plugin.getIslandManager().hasPermission(player, "Bucket")) {
					event.setCancelled(true);
					
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message")));
					plugin.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			}
		}
	}
}
