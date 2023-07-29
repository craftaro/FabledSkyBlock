package com.songoda.skyblock.generator;

import com.songoda.core.compatibility.CompatibleMaterial;

public class GeneratorMaterial {
    private final CompatibleMaterial materials;
    private double chance;

    public GeneratorMaterial(CompatibleMaterial materials, double chance) {
        this.materials = materials;
        this.chance = chance;
    }

    public CompatibleMaterial getMaterials() {
        return this.materials;
    }

    public double getChance() {
        return this.chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }
}
