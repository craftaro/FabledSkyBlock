package me.goodandevil.skyblock.api;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.biome.BiomeManager;
import me.goodandevil.skyblock.api.island.IslandManager;
import me.goodandevil.skyblock.api.levelling.LevellingManager;
import me.goodandevil.skyblock.api.structure.StructureManager;

public class SkyBlockAPI {

	private static SkyBlock implementation;

	private static IslandManager islandManager;
	private static BiomeManager biomeManager;
	private static LevellingManager levellingManager;
	private static StructureManager structureManager;

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
	 * @return The IslandManager implementation
	 */
	public static IslandManager getIslandManager() {
		if (islandManager == null) {
			islandManager = new IslandManager(implementation.getIslandManager());
		}

		return islandManager;
	}

	/**
	 * @return The BiomeManager implementation
	 */
	public static BiomeManager getBiomeManager() {
		if (biomeManager == null) {
			biomeManager = new BiomeManager(implementation.getBiomeManager());
		}

		return biomeManager;
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
}
