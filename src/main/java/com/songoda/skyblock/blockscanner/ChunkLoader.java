package com.songoda.skyblock.blockscanner;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChunkLoader extends BukkitRunnable {
    public final List<CachedChunk> positions = new LinkedList<>();

    private ChunkScannerTask generalTask;
    private ChunkForChunkScannerTask chunkTask;
    private final boolean chunkForChunk;
    private final boolean paper;
    private World world;
    private final Island island;
    private int x;
    private int z;
    private int minZ;
    private int maxX;
    private int maxZ;
    private final int chunkPerTick;
    private final CompleteTask completeTask;

    private ChunkLoader(Island island,
                        IslandWorld islandWorld,
                        boolean paper,
                        boolean chunkForChunk,
                        ChunkForChunkScannerTask chunkTask,
                        CompleteTask complete) {
        chunkPerTick = SkyBlock.getInstance().getConfiguration().getInt("Island.Performance.ChunkPerTick", 25);

        this.completeTask = complete;
        this.chunkTask = chunkTask;
        this.chunkForChunk = chunkForChunk;
        this.paper = paper;
        this.island = island;
        Location islandLocation = island.getLocation(islandWorld, IslandEnvironment.Island);

        if (islandLocation == null) return;

        world = islandLocation.getWorld();

        Location minLocation = new Location(world, islandLocation.getBlockX() - island.getRadius(), 0, islandLocation.getBlockZ() - island.getRadius());
        Location maxLocation = new Location(world, islandLocation.getBlockX() + island.getRadius(), world.getMaxHeight(), islandLocation.getBlockZ() + island.getRadius());

        int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX()) >> 4 << 4;
        minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ()) >> 4 << 4;

        maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX()) >> 4 << 4 | 15;
        maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ()) >> 4 << 4 | 15;

        x = minX;
        z = minZ;

        if (paper) {
            this.runTaskAsynchronously(SkyBlock.getInstance());
        } else {
            this.runTaskTimer(SkyBlock.getInstance(), 1L, 0L);
        }
    }

    private ChunkLoader(Island island,
                        IslandWorld islandWorld,
                        boolean paper,
                        boolean chunkForChunk,
                        ChunkScannerTask generalTask,
                        CompleteTask complete) {
        chunkPerTick = SkyBlock.getInstance().getConfiguration().getInt("Island.Performance.ChunkPerTick", 25);

        this.completeTask = complete;
        this.generalTask = generalTask;
        this.chunkForChunk = chunkForChunk;
        this.paper = paper;
        this.island = island;
        Location islandLocation = island.getLocation(islandWorld, IslandEnvironment.Island);

        if (islandLocation == null) return;

        world = islandLocation.getWorld();

        Location minLocation = new Location(
                world,
                islandLocation.getBlockX() - island.getRadius(),
                0,
                islandLocation.getBlockZ() - island.getRadius());
        Location maxLocation = new Location(
                world,
                islandLocation.getBlockX() + island.getRadius(),
                world.getMaxHeight(),
                islandLocation.getBlockZ() + island.getRadius());

        int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX()) >> 4 << 4;
        minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ()) >> 4 << 4;

        maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX()) >> 4 << 4 | 15;
        maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ()) >> 4 << 4 | 15;

        x = minX;
        z = minZ;

        if (paper) {
            this.runTaskAsynchronously(SkyBlock.getInstance());
        } else {
            this.runTaskTimer(SkyBlock.getInstance(), 1L, 0L);
        }
    }

    @Override
    public void run() { // TODO New algorithm that start from the center of the island
        for (int i = 0; i < chunkPerTick || paper; i++) {
            if (x <= maxX) {
                if (z <= maxZ) {
                    if (!chunkForChunk) {
                        positions.add(new CachedChunk(world, x >> 4, z >> 4));
                    } else {
                        if (chunkTask != null) {
                            chunkTask.onChunkComplete(new CachedChunk(world, x >> 4, z >> 4));
                        }
                    }

                    z += 16;
                } else {
                    z = minZ;
                    x += 16;
                }
            } else {
                if (generalTask != null) {
                    generalTask.onComplete(positions);
                }
                if (completeTask != null) {
                    completeTask.onComplete(island);
                }
                this.cancel();
                return;
            }
        }
    }

    public static void startChunkLoading(Island island, IslandWorld islandWorld, boolean paper, ChunkScannerTask task, CompleteTask complete) {
        new ChunkLoader(island, islandWorld, paper, false, task, complete);
    }

    public static void startChunkLoadingPerChunk(Island island, IslandWorld islandWorld, boolean paper, ChunkForChunkScannerTask task, CompleteTask complete) {
        new ChunkLoader(island, islandWorld, paper, true, task, complete);
    }

    public interface ChunkScannerTask {
        void onComplete(List<CachedChunk> chunks);
    }

    public interface ChunkForChunkScannerTask {
        void onChunkComplete(CachedChunk chunk);
    }

    public interface CompleteTask {
        void onComplete(Island island);
    }
}
