package com.songoda.skyblock.levelling;

import com.songoda.core.compatibility.CompatibleMaterial;
import org.bukkit.inventory.ItemStack;

public final class LevellingMaterial {
    private final CompatibleMaterial materials;
    private double points;

    public LevellingMaterial(CompatibleMaterial materials, double points) {
        this.materials = materials;
        this.points = points;
    }

    public CompatibleMaterial getMaterials() {
        return this.materials;
    }

    public double getPoints() {
        return this.points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public ItemStack getItemStack() {
        return this.materials.getItem();
    }
}
