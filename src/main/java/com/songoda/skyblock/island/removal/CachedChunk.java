package com.songoda.skyblock.island.removal;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

import java.util.Objects;

public class CachedChunk {

    private final String world;
    private final int x;
    private final int z;

    public CachedChunk(Chunk chunk) {
        this(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public CachedChunk(World world, int x, int z) {
        this(world.getName(), x, z);
    }

    public CachedChunk(String world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public String getWorld() {
        return this.world;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public Chunk getChunk() {
        World world = Bukkit.getWorld(this.world);
        if (world == null)
            return null;
        return world.getChunkAt(this.x, this.z);
    }

    public ChunkSnapshot getSnapshot() {
        Chunk chunk = getChunk();
        return chunk == null ? null : chunk.getChunkSnapshot();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Chunk) {
            Chunk other = (Chunk) o;
            return this.world.equals(other.getWorld().getName()) && this.x == other.getX() && this.z == other.getZ();
        } else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.world, this.x, this.z);
    }
}