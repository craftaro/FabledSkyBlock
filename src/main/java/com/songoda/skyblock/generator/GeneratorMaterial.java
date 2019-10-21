package com.songoda.skyblock.generator;

import com.songoda.skyblock.utils.version.Materials;

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
