package com.songoda.skyblock.manager;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.entity.Player;

public abstract class Manager {
    protected SkyBlock plugin;

    public Manager(SkyBlock plugin) {
        this.plugin = plugin;
    }

    /**
     * Reloads the Manager's settings
     */
    public void reload() {
    }

    /**
     * Cleans up the Manager's resources
     */
    public void disable() {
    }

    public void triggerPlayerLogin(Player player) {
    }

    public void triggerPlayerLogout(Player player) {
    }
}
