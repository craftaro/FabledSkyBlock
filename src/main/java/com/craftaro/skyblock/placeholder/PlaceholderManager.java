package com.craftaro.skyblock.placeholder;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.manager.Manager;
import com.craftaro.skyblock.placeholder.hook.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class PlaceholderManager extends Manager {
    private final boolean placeholderAPIEnabled;
    private final PlaceholderProcessor placeholderProcessor;

    public PlaceholderManager(SkyBlock plugin) {
        super(plugin);

        PluginManager pluginManager = this.plugin.getServer().getPluginManager();
        this.placeholderAPIEnabled = pluginManager.getPlugin("PlaceholderAPI") != null;
        this.placeholderProcessor = !this.placeholderAPIEnabled ? new PlaceholderProcessor() : null;
    }

    public void registerPlaceholders() {
        if (this.placeholderAPIEnabled) {
            new PlaceholderAPI(this.plugin).register();
        }
    }

    public boolean isPlaceholderAPIEnabled() {
        return this.placeholderAPIEnabled;
    }

    public String parsePlaceholders(Player player, String message) {
        String retValue;

        if (this.placeholderAPIEnabled) {
            retValue = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
            retValue = ChatColor.translateAlternateColorCodes('&', retValue);
        } else {
            retValue = manuallyReplaceSkyBlockPlaceholders(player, message);
        }
        return retValue;
    }

    private String manuallyReplaceSkyBlockPlaceholders(Player player, String message) {
        if (this.placeholderProcessor == null) {
            return message;
        }

        String retValue = message;

        int index = retValue.indexOf("%fabledskyblock_");
        while (index != -1) {
            int endIndex = retValue.indexOf("%", index + 1);
            if (endIndex != -1) {
                String placeholder = retValue.substring(index + 1, endIndex);
                String result = this.placeholderProcessor.processPlaceholder(player, placeholder);
                if (result != null) {
                    retValue = retValue.replace("%" + placeholder + "%", result);
                }
            }
            index = retValue.indexOf("%fabledskyblock_", index + 1);
        }
        return retValue;
    }
}
