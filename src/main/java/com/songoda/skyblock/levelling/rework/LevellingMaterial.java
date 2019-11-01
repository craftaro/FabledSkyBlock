package com.songoda.skyblock.levelling.rework;

import org.bukkit.inventory.ItemStack;

import com.songoda.skyblock.utils.version.Materials;

public final class LevellingMaterial {

    private Materials materials;
    private long points;

    public LevellingMaterial(Materials materials, long points) {
        this.materials = materials;
        this.points = points;
    }

    public Materials getMaterials() {
        return materials;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public ItemStack getItemStack() {
        return materials.parseItem();
    }
}
