package com.songoda.skyblock.api.island;

import com.google.common.base.Preconditions;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import org.bukkit.Location;
import org.bukkit.Material;

public interface IslandLevel {

    /**
     * @return Points of the Island from gathered materials
     */
    double getPoints();

    /**
     * @return Level of the Island from points
     */
    long getLevel();

    /**
     * @return Last calculated points of the Island
     */
    double getLastCalculatedPoints();

    /**
     * @return Last calculated level of the Island
     */
    long getLastCalculatedLevel();

    /**
     * Set the amount of a Material for the Island
     */
    void setMaterialAmount(Material material, int amount);

    /**
     * Set the amount of a Material for the Island
     */
    void setMaterialAmount(Material material, byte data, int amount);

    /**
     * @return The amount of a Material from the Island
     */
    long getMaterialAmount(Material material);

    /**
     * @return The amount of a Material from the Island
     */
    long getMaterialAmount(Material material, byte data);

    /**
     * @return The points earned for a Material from the Island
     */
    long getMaterialPoints(Material material);

    /**
     * @return The points earned for a Material from the Island
     */
    long getMaterialPoints(Material material, byte data);
    
    /**
     * Update the island level for a determined location
     * @param location of the island
     */
    void updateLevel(Location location);
}
