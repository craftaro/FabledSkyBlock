package me.goodandevil.skyblock.api;

import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.island.Island;

public class SkyBlockAPI {
	
    private static SkyBlock implementation;
    
    /**
     * @param implementation the implementation to set
     */
    public static void setImplementation(SkyBlock implementation) {
        if (SkyBlockAPI.implementation != null) {
            throw new IllegalArgumentException("Cannot set API implementation twice");
        }
        
        SkyBlockAPI.implementation = implementation;
    }

    /**
     * @return The SkyBlock implementation
     */
    public static SkyBlock getImplementation() {
        return implementation;
    }
    
    /**
     * @return The Island of a player
     */
    public static Island getIsland(Player player) {
    	return implementation.getIslandManager().getIsland(player.getUniqueId()).getAPIWrapper();
    }
}
