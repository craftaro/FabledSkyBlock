package me.goodandevil.skyblock.placeholder;

import org.bukkit.plugin.PluginManager;

import me.goodandevil.skyblock.SkyBlock;

public class PlaceholderManager {

	private final SkyBlock skyblock;
	
	private boolean PlaceholderAPI = false;
	private boolean MVdWPlaceholderAPI = false;
	
	public PlaceholderManager(SkyBlock skyblock) {
		this.skyblock = skyblock;
		
		PluginManager pluginManager = skyblock.getServer().getPluginManager();
		
		if (pluginManager.getPlugin("PlaceholderAPI") != null) {
			PlaceholderAPI = true;
		}
		
		if (pluginManager.getPlugin("MVdWPlaceholderAPI") != null) {
			MVdWPlaceholderAPI = true;
		}
	}
	
	public void registerPlaceholders() {
		if (PlaceholderAPI) {
			new EZPlaceholder(skyblock).register();
		}
		
		if (MVdWPlaceholderAPI) {
			new MVdWPlaceholder(skyblock).register();
		}
	}
	
	public boolean isPlaceholderAPIEnabled() {
		return PlaceholderAPI;
	}
	
	public boolean isMVdWPlaceholderAPIEnabled() {
		return MVdWPlaceholderAPI;
	}
}
