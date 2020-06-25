package com.songoda.skyblock.levelling;

import com.songoda.core.compatibility.CompatibleMaterial;
import org.bukkit.inventory.ItemStack;

 

public final class LevellingMaterial {

    private CompatibleMaterial materials;
    private long points;

    public LevellingMaterial(CompatibleMaterial materials, long points) {
        this.materials = materials;
        this.points = points;
    }

    public CompatibleMaterial getMaterials() {
        return materials;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public ItemStack getItemStack() {
        return materials.getItem();
    }
}
