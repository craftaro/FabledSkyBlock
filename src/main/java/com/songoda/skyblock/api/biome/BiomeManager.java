package com.songoda.skyblock.api.biome;

import com.google.common.base.Preconditions;
import com.songoda.skyblock.api.island.Island;
import org.bukkit.block.Biome;

public class BiomeManager {

    private final com.songoda.skyblock.biome.BiomeManager biomeManager;

    public BiomeManager(com.songoda.skyblock.biome.BiomeManager biomeManager) {
        this.biomeManager = biomeManager;
    }

    /**
     * Set the Biome of an Island
     */
    public void setBiome(Island island, Biome biome) {
        Preconditions.checkArgument(island != null, "Cannot set biome to null island");
        Preconditions.checkArgument(biome != null, "Cannot set biome to null biome");

        this.biomeManager.setBiome(island.getIsland(), biome, null);
    }
}
