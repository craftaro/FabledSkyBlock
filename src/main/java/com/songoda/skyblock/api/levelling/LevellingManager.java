package com.songoda.skyblock.api.levelling;

import com.google.common.base.Preconditions;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.levelling.calculator.Calculator;
import com.songoda.skyblock.levelling.calculator.CalculatorRegistry;
import org.bukkit.Material;

public class LevellingManager {
    private final IslandLevelManager levellingManager;

    public LevellingManager(IslandLevelManager levellingManager) {
        this.levellingManager = levellingManager;
    }

    /**
     * Calculates the points of an Island to determine what the Island level is
     */
    public void calculatePoints(Island island) {
        Preconditions.checkArgument(island != null, "Cannot calculate points to null island");
        this.levellingManager.startScan(null, island.getIsland());
    }

    /**
     * Register a new material calculator for a determined material
     */
    public void registerCalculator(Calculator calculator, Material material) {
        Preconditions.checkArgument(calculator != null, "Cannot use a null calculator");
        Preconditions.checkArgument(material != null, "Cannot use a null material");
        CalculatorRegistry.registerCalculator(calculator, CompatibleMaterial.getMaterial(material));
    }
}
