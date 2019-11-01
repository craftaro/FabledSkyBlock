package com.songoda.skyblock.blockscanner;

import org.bukkit.World;

public final class BlockInfo {

    private final World world;

    private final int x;
    private final int y;
    private final int z;

    public BlockInfo(World world, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public World getWorld() {
        return world;
    }

}
