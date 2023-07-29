package com.songoda.skyblock.blockscanner;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;

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
        this.chunkPerTick = SkyBlock.getPlugin(SkyBlock.class).getConfiguration().getInt("Island.Performance.ChunkPerTick", 25);

        this.completeTask = complete;
        this.chunkTask = chunkTask;
        this.chunkForChunk = chunkForChunk;
        this.paper = paper;
        this.island = island;
        Location islandLocation = island.getLocation(islandWorld, IslandEnvironment.ISLAND);

        if (islandLocation == null) {
            return;
        }

        this.world = islandLocation.getWorld();

        Location minLocation = new Location(this.world, islandLocation.getBlockX() - island.getRadius(), 0, islandLocation.getBlockZ() - island.getRadius());
        Location maxLocation = new Location(this.world, islandLocation.getBlockX() + island.getRadius(), this.world.getMaxHeight(), islandLocation.getBlockZ() + island.getRadius());

        int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX()) >> 4 << 4;
        this.minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ()) >> 4 << 4;

        this.maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX()) >> 4 << 4 | 15;
        this.maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ()) >> 4 << 4 | 15;

        this.x = minX;
        this.z = this.minZ;

        if (paper) {
            this.runTaskAsynchronously(SkyBlock.getPlugin(SkyBlock.class));
        } else {
            this.runTaskTimer(SkyBlock.getPlugin(SkyBlock.class), 1L, 0L);
        }
    }

    private ChunkLoader(Island island,
                        IslandWorld islandWorld,
                        boolean paper,
                        boolean chunkForChunk,
                        ChunkScannerTask generalTask,
                        CompleteTask complete) {
        this.chunkPerTick = SkyBlock.getPlugin(SkyBlock.class).getConfiguration().getInt("Island.Performance.ChunkPerTick", 25);

        this.completeTask = complete;
        this.generalTask = generalTask;
        this.chunkForChunk = chunkForChunk;
        this.paper = paper;
        this.island = island;
        Location islandLocation = island.getLocation(islandWorld, IslandEnvironment.ISLAND);
        if (islandLocation == null) {
            return;
        }

        this.world = islandLocation.getWorld();

        Location minLocation = new Location(
                this.world,
                islandLocation.getBlockX() - island.getRadius(),
                0,
                islandLocation.getBlockZ() - island.getRadius());
        Location maxLocation = new Location(
                this.world,
                islandLocation.getBlockX() + island.getRadius(),
                this.world.getMaxHeight(),
                islandLocation.getBlockZ() + island.getRadius());

        int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX()) >> 4 << 4;
        this.minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ()) >> 4 << 4;

        this.maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX()) >> 4 << 4 | 15;
        this.maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ()) >> 4 << 4 | 15;

        this.x = minX;
        this.z = this.minZ;

        if (paper) {
            this.runTaskAsynchronously(SkyBlock.getPlugin(SkyBlock.class));
        } else {
            this.runTaskTimer(SkyBlock.getPlugin(SkyBlock.class), 1L, 0L);
        }
    }

    @Override
    public void run() { // TODO New algorithm that start from the center of the island
        for (int i = 0; i < this.chunkPerTick || this.paper; i++) {
            if (this.x <= this.maxX) {
                if (this.z <= this.maxZ) {
                    if (!this.chunkForChunk) {
                        this.positions.add(new CachedChunk(this.world, this.x >> 4, this.z >> 4));
                    } else {
                        if (this.chunkTask != null) {
                            this.chunkTask.onChunkComplete(new CachedChunk(this.world, this.x >> 4, this.z >> 4));
                        }
                    }

                    this.z += 16;
                } else {
                    this.z = this.minZ;
                    this.x += 16;
                }
            } else {
                if (this.generalTask != null) {
                    this.generalTask.onComplete(this.positions);
                }
                if (this.completeTask != null) {
                    this.completeTask.onComplete(this.island);
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
