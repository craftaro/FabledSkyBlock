package me.goodandevil.skyblock.generator;

import me.goodandevil.skyblock.utils.version.Materials;

public class GeneratorMaterial {

	private Materials materials;
	private double chance;

	public GeneratorMaterial(Materials materials, double chance) {
		this.materials = materials;
		this.chance = chance;
	}

	public Materials getMaterials() {
		return materials;
	}

	public double getChance() {
		return chance;
	}

	public void setChance(double chance) {
		this.chance = chance;
	}
}
