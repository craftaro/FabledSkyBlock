package com.craftaro.skyblock.api.biome;

import com.craftaro.core.compatibility.CompatibleBiome;
import com.craftaro.skyblock.api.island.Island;
import com.craftaro.skyblock.island.IslandWorld;
import com.google.common.base.Preconditions;
import org.bukkit.block.Biome;

public class BiomeManager {
    private final com.craftaro.skyblock.biome.BiomeManager biomeManager;

    public BiomeManager(com.craftaro.skyblock.biome.BiomeManager biomeManager) {
        this.biomeManager = biomeManager;
    }

    /**
     * Set the Biome of an Island
     */
    public void setBiome(Island island, Biome biome) {
        Preconditions.checkArgument(island != null, "Cannot set biome to null island");
        Preconditions.checkArgument(biome != null, "Cannot set biome to null biome");

        this.biomeManager.setBiome(island.getIsland(), IslandWorld.NORMAL, CompatibleBiome.getBiome(biome), null);
    }
}
