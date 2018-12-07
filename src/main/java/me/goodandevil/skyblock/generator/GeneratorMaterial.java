package me.goodandevil.skyblock.generator;

import me.goodandevil.skyblock.utils.version.Materials;

public class GeneratorMaterial {

	private Materials materials;
	private int chance;

	public GeneratorMaterial(Materials materials, int chance) {
		this.materials = materials;
		this.chance = chance;
	}

	public Materials getMaterials() {
		return materials;
	}

	public int getChance() {
		return chance;
	}

	public void setChance(int chance) {
		this.chance = chance;
	}
}
