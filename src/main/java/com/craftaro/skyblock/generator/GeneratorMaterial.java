package com.craftaro.skyblock.generator;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;

public class GeneratorMaterial {
    private final XMaterial materials;
    private double chance;

    public GeneratorMaterial(XMaterial materials, double chance) {
        this.materials = materials;
        this.chance = chance;
    }

    public XMaterial getMaterials() {
        return this.materials;
    }

    public double getChance() {
        return this.chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }
}
