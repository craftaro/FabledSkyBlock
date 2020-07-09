package com.songoda.skyblock.manager;

import com.songoda.skyblock.SkyBlock;

public abstract class Manager {
    
    protected SkyBlock plugin;
    
    public Manager(SkyBlock plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Reloads the Manager's settings
     */
    public abstract void reload();
    
    /**
     * Cleans up the Manager's resources
     */
    public abstract void disable();
    
}
