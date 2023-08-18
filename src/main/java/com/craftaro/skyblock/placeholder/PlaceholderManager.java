package com.craftaro.skyblock.placeholder;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.manager.Manager;
import com.craftaro.skyblock.placeholder.hook.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class PlaceholderManager extends Manager {
    private final PlaceholderAPI skyBlockPlaceholderAPI;
    private final boolean placeholderAPIEnabled;

    public PlaceholderManager(SkyBlock plugin) {
        super(plugin);
        this.skyBlockPlaceholderAPI = new PlaceholderAPI(this.plugin);

        PluginManager pluginManager = this.plugin.getServer().getPluginManager();
        this.placeholderAPIEnabled = pluginManager.getPlugin("PlaceholderAPI") != null;
    }

    public void registerPlaceholders() {
        if (this.placeholderAPIEnabled) {
            this.skyBlockPlaceholderAPI.register();
        }
    }

    public boolean isPlaceholderAPIEnabled() {
        return this.placeholderAPIEnabled;
    }

    public String parsePlaceholders(Player player, String message) {
        String retValue = message;

        if (this.placeholderAPIEnabled) {
            retValue = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
            retValue = ChatColor.translateAlternateColorCodes('&', retValue);
        }
        return retValue;
    }
}
