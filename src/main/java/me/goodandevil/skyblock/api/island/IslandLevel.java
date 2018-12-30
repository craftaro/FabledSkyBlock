package me.goodandevil.skyblock.api.island;

import org.bukkit.Material;

import com.google.common.base.Preconditions;

import me.goodandevil.skyblock.utils.version.Materials;

public class IslandLevel {

	private final Island handle;

	public IslandLevel(Island handle) {
		this.handle = handle;
	}

	/**
	 * @return Points of the Island from gathered materials
	 */
	public int getPoints() {
		return this.handle.getIsland().getLevel().getPoints();
	}

	/**
	 * @return Level of the Island from points
	 */
	public int getLevel() {
		return this.handle.getIsland().getLevel().getLevel();
	}

	/**
	 * @return Last calculated points of the Island
	 */
	public int getLastCalculatedPoints() {
		return this.handle.getIsland().getLevel().getLastCalculatedPoints();
	}

	/**
	 * @return Last calculated level of the Island
	 */
	public int getLastCalculatedLevel() {
		return this.handle.getIsland().getLevel().getLastCalculatedLevel();
	}

	/**
	 * Set the amount of a Material for the Island
	 */
	public void setMaterialAmount(Material material, int amount) {
		Preconditions.checkArgument(material != null, "Cannot set material amount to null material");
		this.handle.getIsland().getLevel().setMaterialAmount(Materials.fromString(material.name()).name(), amount);
	}

	/**
	 * Set the amount of a Material for the Island
	 */
	public void setMaterialAmount(Material material, byte data, int amount) {
		Preconditions.checkArgument(material != null, "Cannot set material amount to null material");
		this.handle.getIsland().getLevel().setMaterialAmount(Materials.requestMaterials(material.name(), data).name(),
				amount);
	}

	/**
	 * @return The amount of a Material from the Island
	 */
	public int getMaterialAmount(Material material) {
		Preconditions.checkArgument(material != null, "Cannot get material amount to null material");

		Materials materials = Materials.fromString(material.name());
		me.goodandevil.skyblock.island.IslandLevel level = this.handle.getIsland().getLevel();

		if (level.getMaterials().containsKey(materials.name())) {
			return level.getMaterials().get(materials.name());
		}

		return 0;
	}

	/**
	 * @return The amount of a Material from the Island
	 */
	public int getMaterialAmount(Material material, byte data) {
		Preconditions.checkArgument(material != null, "Cannot get material amount to null material");

		Materials materials = Materials.requestMaterials(material.name(), data);
		me.goodandevil.skyblock.island.IslandLevel level = this.handle.getIsland().getLevel();

		if (level.getMaterials().containsKey(materials.name())) {
			return level.getMaterials().get(materials.name());
		}

		return 0;
	}

	/**
	 * @return The points earned for a Material from the Island
	 */
	public int getMaterialPoints(Material material) {
		Preconditions.checkArgument(material != null, "Cannot get material points to null material");
		return this.handle.getIsland().getLevel().getMaterialPoints(Materials.fromString(material.name()).name());
	}

	/**
	 * @return The points earned for a Material from the Island
	 */
	public int getMaterialPoints(Material material, byte data) {
		Preconditions.checkArgument(material != null, "Cannot get material points to null material");
		return this.handle.getIsland().getLevel()
				.getMaterialPoints(Materials.requestMaterials(material.name(), data).name());
	}

	/**
	 * @return Implementation for the Island
	 */
	public Island getIsland() {
		return handle;
	}
}
