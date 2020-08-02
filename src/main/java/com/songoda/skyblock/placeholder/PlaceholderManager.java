package com.songoda.skyblock.placeholder;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.manager.Manager;
import com.songoda.skyblock.placeholder.hook.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class PlaceholderManager extends Manager {
    
    private boolean PlaceholderAPIEnabled = false;
    
    public PlaceholderManager(SkyBlock plugin) {
        super(plugin);
        
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        
        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            PlaceholderAPIEnabled = true;
        }
    }
    
    public void registerPlaceholders() {
        if (PlaceholderAPIEnabled) {
            new PlaceholderAPI(plugin).register();
        }
    }
    
    public boolean isPlaceholderAPIEnabled() {
        return PlaceholderAPIEnabled;
    }
    
    public String parsePlaceholders(Player player, String message) {
        String retValue = "";
        
        if(PlaceholderAPIEnabled) {
            retValue = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
        }
        return retValue;
    }
}
