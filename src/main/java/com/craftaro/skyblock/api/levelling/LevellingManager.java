package com.craftaro.skyblock.api.levelling;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.skyblock.api.island.Island;
import com.craftaro.skyblock.levelling.IslandLevelManager;
import com.craftaro.skyblock.levelling.calculator.Calculator;
import com.craftaro.skyblock.levelling.calculator.CalculatorRegistry;
import com.google.common.base.Preconditions;
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
        CalculatorRegistry.registerCalculator(calculator, CompatibleMaterial.getMaterial(material).get());
    }
}
