package com.songoda.skyblock.api.island;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.google.common.base.Preconditions;
import com.songoda.skyblock.SkyBlock;
import org.bukkit.Location;
import org.bukkit.Material;

public class IslandLevel {
    private final Island handle;

    public IslandLevel(Island handle) {
        this.handle = handle;
    }

    /**
     * @return Points of the Island from gathered materials
     */
    public double getPoints() {
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
    public double getLastCalculatedPoints() {
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
        this.handle.getIsland().getLevel().setMaterialAmount(CompatibleMaterial.getMaterial(material.name()).name(), amount);
    }

    /**
     * Set the amount of a Material for the Island
     */
    public void setMaterialAmount(Material material, byte data, int amount) {
        Preconditions.checkArgument(material != null, "Cannot set material amount to null material");
        //TODO: Add data support
        this.handle.getIsland().getLevel().setMaterialAmount(CompatibleMaterial.getMaterial(material.name()).name(),
                amount);
    }

    /**
     * @return The amount of a Material from the Island
     */
    public long getMaterialAmount(Material material) {
        Preconditions.checkArgument(material != null, "Cannot get material amount to null material");

        CompatibleMaterial materials = CompatibleMaterial.getMaterial(material.name());
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

        CompatibleMaterial materials = CompatibleMaterial.getMaterial(material.name());
        //TODO: data support
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
        return this.handle.getIsland().getLevel().getMaterialPoints(CompatibleMaterial.getMaterial(material.name()).name());
    }

    /**
     * @return The points earned for a Material from the Island
     */
    public long getMaterialPoints(Material material, byte data) {
        Preconditions.checkArgument(material != null, "Cannot get material points to null material");
        return this.handle.getIsland().getLevel()
                .getMaterialPoints(CompatibleMaterial.getMaterial(material.name()).name());
        //TODO: add data support
    }

    /**
     * @return Implementation for the Island
     */
    public Island getIsland() {
        return this.handle;
    }

    /**
     * Update the island level for a determined location
     */
    public void updateLevel(Location location) {
        Preconditions.checkArgument(location != null, "Cannot update level of a null island");
        SkyBlock.getPlugin(SkyBlock.class).getLevellingManager().updateLevel(this.handle.getIsland(), location);
    }
}
