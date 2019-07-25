package me.goodandevil.skyblock.api.biome;

import org.bukkit.block.Biome;

import com.google.common.base.Preconditions;

import me.goodandevil.skyblock.api.island.Island;

public class BiomeManager {

	private final me.goodandevil.skyblock.biome.BiomeManager biomeManager;

	public BiomeManager(me.goodandevil.skyblock.biome.BiomeManager biomeManager) {
		this.biomeManager = biomeManager;
	}

	/**
	 * Set the Biome of an Island
	 */
	public void setBiome(Island island, Biome biome) {
		Preconditions.checkArgument(island != null, "Cannot set biome to null island");
		Preconditions.checkArgument(biome != null, "Cannot set biome to null biome");

		this.biomeManager.setBiome(island.getIsland(), biome);
	}
}
