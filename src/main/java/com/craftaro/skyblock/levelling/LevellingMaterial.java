package com.craftaro.skyblock.levelling;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.ItemStack;

public final class LevellingMaterial {
    private final XMaterial materials;
    private double points;

    public LevellingMaterial(XMaterial materials, double points) {
        this.materials = materials;
        this.points = points;
    }

    public XMaterial getMaterials() {
        return this.materials;
    }

    public double getPoints() {
        return this.points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public ItemStack getItemStack() {
        return this.materials.parseItem();
    }
}
