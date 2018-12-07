package me.goodandevil.skyblock.utils.version;

import org.bukkit.block.Biome;

public enum Biomes {

	SWAMPLAMD("SWAMPLAND", "SWAMP"), COLD_BEACH("COLD_BEACH", "SNOWY_BEACH"),
	ROOFED_FOREST("ROOFED_FOREST", "DARK_FOREST");

	private String pre19biome;
	private String post19biome;
	private Biome resolvedBiome = null;

	Biomes(String pre19biome, String post19biome) {
		this.pre19biome = pre19biome;
		this.post19biome = post19biome;
	}

	public Biome bukkitBiome() {
		if (resolvedBiome != null)
			return resolvedBiome;

		try {
			return resolvedBiome = Biome.valueOf(post19biome);
		} catch (IllegalArgumentException e) {
			return resolvedBiome = Biome.valueOf(pre19biome);
		}
	}
}
