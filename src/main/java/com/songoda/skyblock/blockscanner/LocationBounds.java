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
        return minX;
    }
    
    public int getMinZ() {
        return minZ;
    }
    
    public int getMaxX() {
        return maxX;
    }
    
    public int getMaxZ() {
        return maxZ;
    }
}
