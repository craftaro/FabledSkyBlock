package me.goodandevil.skyblock.levelling;

import org.bukkit.World;

public class ChunkPosition {
    private World world;
    private int x, z;

    public ChunkPosition(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public World getWorld() {
        return this.world;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }
}
