package com.songoda.skyblock.api;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.biome.BiomeManager;
import com.songoda.skyblock.api.island.IslandManager;
import com.songoda.skyblock.api.levelling.LevellingManager;
import com.songoda.skyblock.api.structure.StructureManager;
import com.songoda.skyblock.database.DataManager;
import com.songoda.skyblock.database.DataProvider;

public class SkyBlockAPI {

    private static SkyBlock implementation;

    private static IslandManager islandManager;
    private static BiomeManager biomeManager;
    private static LevellingManager levellingManager;
    private static StructureManager structureManager;

    /**
     * @return The SkyBlock implementation
     */
    public static SkyBlock getImplementation() {
        return implementation;
    }

    /**
     * @param implementation the implementation to set
     */
    public static void setImplementation(SkyBlock implementation) throws IllegalStateException {
        if (SkyBlockAPI.implementation != null && SkyBlockAPI.implementation != implementation) {
            throw new IllegalStateException("Cannot set API implementation twice");
        }

        SkyBlockAPI.implementation = implementation;
    }

    /**
     * @return The IslandManager implementation
     */
    public static IslandManager getIslandManager() {
        if (islandManager == null) {
            islandManager = new IslandManager(implementation.getIslandManager());
        }

        return islandManager;
    }

    /**
     * @return The LevellingManager implementation
     */
    public static LevellingManager getLevellingManager() {
        if (levellingManager == null) {
            levellingManager = new LevellingManager(implementation.getLevellingManager());
        }

        return levellingManager;
    }

    /**
     * @return The StructureManager implementation
     */
    public static StructureManager getStructureManager() {
        if (structureManager == null) {
            structureManager = new StructureManager(implementation.getStructureManager());
        }

        return structureManager;
    }

    /**
     * @return The DataManager implementation
     */
    public static DataManager getDataManager() {
        return implementation.getDataManager();
    }

    /**
     * Use this method to use or modify Island and Player data
     * @return The DataProvider implementation
     */
    public static DataProvider getDataProvider() {
        return implementation.getDataManager().getDataProvider();
    }
}
