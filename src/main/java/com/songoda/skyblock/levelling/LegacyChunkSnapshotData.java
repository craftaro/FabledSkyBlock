package com.songoda.skyblock.levelling;

import org.bukkit.Material;

public class LegacyChunkSnapshotData {

    private Material material;
    private int data;

    public LegacyChunkSnapshotData(Material material, int data) {
        this.material = material;
        this.data = data;
    }

    public Material getMaterial() {
        return this.material;
    }

    public int getData() {
        return this.data;
    }

}
