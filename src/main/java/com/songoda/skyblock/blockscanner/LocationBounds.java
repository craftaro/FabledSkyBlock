package com.songoda.skyblock.blockscanner;

public class LocationBounds {
    private final int minX, minZ, maxX, maxZ;

    public LocationBounds(int minX, int minZ, int maxX, int maxZ) {
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMinZ() {
        return this.minZ;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMaxZ() {
        return this.maxZ;
    }
}
