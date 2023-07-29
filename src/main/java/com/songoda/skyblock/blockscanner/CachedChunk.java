package com.songoda.skyblock.blockscanner;

import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CachedChunk {
    private final String world;
    private final int x;
    private final int z;
    private ChunkSnapshot latestSnapshot;

    public CachedChunk(Chunk chunk) {
        this(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public CachedChunk(String world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public CachedChunk(World world, int x, int z) {
        this(world.getName(), x, z);
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

    public CompletableFuture<Chunk> getChunk() {
        World world = Bukkit.getWorld(this.world);
        if (world == null) {
            return null;
        }
        return PaperLib.getChunkAtAsync(world, this.x, this.z);
    }

    public boolean isSnapshotAvailable() {
        return this.latestSnapshot != null;
    }

    public ChunkSnapshot getSnapshot() {
        if (this.latestSnapshot == null) {
            return takeSnapshot();
        }
        return this.latestSnapshot;
    }

    public ChunkSnapshot takeSnapshot() {
        return this.latestSnapshot = getChunk().join().getChunkSnapshot();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Chunk)) {
            return false;
        }

        Chunk other = (Chunk) obj;
        return this.world.equals(other.getWorld().getName()) &&
                this.x == other.getX() &&
                this.z == other.getZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.world, this.x, this.z);
    }
}
