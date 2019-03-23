package me.goodandevil.skyblock.levelling;

import org.bukkit.inventory.ItemStack;

import me.goodandevil.skyblock.utils.version.Materials;

public class LevellingMaterial {

	private Materials materials;
	private long points;

	public LevellingMaterial(Materials materials, long points) {
		this.materials = materials;
		this.points = points;
	}

	public Materials getMaterials() {
		return materials;
	}

	public void setPoints(long points) {
		this.points = points;
	}

	public long getPoints() {
		return points;
	}

	public ItemStack getItemStack() {
		return materials.parseItem();
	}
}
