package com.songoda.skyblock.api.biome;

import com.google.common.base.Preconditions;
import com.songoda.core.compatibility.CompatibleBiome;
import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.island.IslandWorld;
import org.bukkit.block.Biome;

public interface BiomeManager {

    /**
     * Sets the Biome of the island
     */
    public void setBiome(Biome biome);

    /**
     * @return The Biome of the Island
     */
    Biome getBiome();

}
