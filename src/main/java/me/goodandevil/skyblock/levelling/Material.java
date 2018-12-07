package me.goodandevil.skyblock.levelling;

import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.utils.version.Materials;

public class Material {

	private Materials materials;
	private int points;

	public Material(Materials materials, int points) {
		this.materials = materials;
		this.points = points;
	}

	public Materials getMaterials() {
		return materials;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getPoints() {
		return points;
	}

	public ItemStack getItemStack() {
		return materials.parseItem();
	}
}
