package me.goodandevil.skyblock.message;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.placeholder.PlaceholderManager;

public class MessageManager {

	private final Main plugin;
	
	public MessageManager(Main plugin) {
		this.plugin = plugin;
	}
	
	public void sendMessage(Player player, String message) {
		PlaceholderManager placeholderManager = plugin.getPlaceholderManager();
		
		if (placeholderManager.isPlaceholderAPIEnabled()) {
			message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message.replace("&", "clr")).replace("clr", "&");
		}
		
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public String replaceMessage(Player player, String message) {
		PlaceholderManager placeholderManager = plugin.getPlaceholderManager();
		
		if (placeholderManager.isPlaceholderAPIEnabled()) {
			message = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message.replace("&", "clr")).replace("clr", "&");
		}
		
		return message;
	}
}
