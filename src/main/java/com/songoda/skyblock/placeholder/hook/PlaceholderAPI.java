package com.songoda.skyblock.placeholder.hook;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.placeholder.PlaceholderProcessor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPI extends PlaceholderExpansion {
    
    private final SkyBlock plugin;
    private final PlaceholderProcessor placeholderProcessor;
    
    public PlaceholderAPI(SkyBlock plugin) {
        this.plugin = plugin;
        this.placeholderProcessor = new PlaceholderProcessor();
    }
    
    public String getIdentifier() {
        return "fabledskyblock";
    }
    
    @Deprecated
    public String getPlugin() {
        return null;
    }
    
    public String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }
    
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }
    
    public boolean persist() {
        return true;
    }
    
    public String onPlaceholderRequest(Player player, String identifier) {
        
        return placeholderProcessor.processPlaceholder(player, "fabledskyblock_" + identifier);
    }
}
