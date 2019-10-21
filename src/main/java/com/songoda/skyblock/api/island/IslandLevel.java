package com.songoda.skyblock.api.island;

import com.google.common.base.Preconditions;
import com.songoda.skyblock.utils.version.Materials;
import org.bukkit.Material;

public class IslandLevel {

    private final Island handle;

    public IslandLevel(Island handle) {
        this.handle = handle;
    }

    /**
     * @return Points of the Island from gathered materials
     */
    public long getPoints() {
        return this.handle.getIsland().getLevel().getPoints();
    }

    /**
     * @return Level of the Island from points
     */
    public long getLevel() {
        return this.handle.getIsland().getLevel().getLevel();
    }

    /**
     * @return Last calculated points of the Island
     */
    public long getLastCalculatedPoints() {
        return this.handle.getIsland().getLevel().getLastCalculatedPoints();
    }

    /**
     * @return Last calculated level of the Island
     */
    public long getLastCalculatedLevel() {
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
    public long getMaterialAmount(Material material) {
        Preconditions.checkArgument(material != null, "Cannot get material amount to null material");

        Materials materials = Materials.fromString(material.name());
        com.songoda.skyblock.island.IslandLevel level = this.handle.getIsland().getLevel();

        if (level.getMaterials().containsKey(materials.name())) {
            return level.getMaterials().get(materials.name());
        }

        return 0;
    }

    /**
     * @return The amount of a Material from the Island
     */
    public long getMaterialAmount(Material material, byte data) {
        Preconditions.checkArgument(material != null, "Cannot get material amount to null material");

        Materials materials = Materials.requestMaterials(material.name(), data);
        com.songoda.skyblock.island.IslandLevel level = this.handle.getIsland().getLevel();

        if (level.getMaterials().containsKey(materials.name())) {
            return level.getMaterials().get(materials.name());
        }

        return 0;
    }

    /**
     * @return The points earned for a Material from the Island
     */
    public long getMaterialPoints(Material material) {
        Preconditions.checkArgument(material != null, "Cannot get material points to null material");
        return this.handle.getIsland().getLevel().getMaterialPoints(Materials.fromString(material.name()).name());
    }

    /**
     * @return The points earned for a Material from the Island
     */
    public long getMaterialPoints(Material material, byte data) {
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
