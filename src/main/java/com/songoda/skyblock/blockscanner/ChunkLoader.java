package com.songoda.skyblock.blockscanner;

import com.sk89q.worldedit.bukkit.paperlib.PaperLib;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChunkLoader extends BukkitRunnable {
    public final List<CompletableFuture<Chunk>> asyncPositions = new LinkedList<>();
    public final List<Chunk> syncPositions = new LinkedList<>();

    private ChunkScannerTask generalTask;
    private ChunkForChunkScannerTask chunkTask;
    private boolean chunkForChunk;
    private boolean paper;
    private World world;
    private int x;
    private int z;
    private int minZ;
    private int maxX;
    private int maxZ;

    private ChunkLoader(Island island, IslandWorld islandWorld, boolean paper, boolean chunkForChunk, ChunkForChunkScannerTask chunkTask) {
        this.chunkTask = chunkTask;
        this.chunkForChunk = chunkForChunk;
        this.paper = paper;
        Location islandLocation = island.getLocation(islandWorld, IslandEnvironment.Island);

        if (islandLocation == null) return;

        world = islandLocation.getWorld();

        Location minLocation = new Location(world, islandLocation.getBlockX() - island.getRadius(), 0, islandLocation.getBlockZ() - island.getRadius());
        Location maxLocation = new Location(world, islandLocation.getBlockX() + island.getRadius(), world.getMaxHeight(), islandLocation.getBlockZ() + island.getRadius());

        int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX());
        minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ());

        maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX());
        maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ());

        x = minX;
        z = minZ;

        if(paper){
            this.runTaskAsynchronously(SkyBlock.getInstance());
        } else {
            this.runTaskTimer(SkyBlock.getInstance(), 1L, 0L);
        }
    }

    private ChunkLoader(Island island, IslandWorld islandWorld, boolean paper, boolean chunkForChunk, ChunkScannerTask generalTask) {
        this.generalTask = generalTask;
        this.chunkForChunk = chunkForChunk;
        this.paper = paper;
        Location islandLocation = island.getLocation(islandWorld, IslandEnvironment.Island);

        if (islandLocation == null) return;

        world = islandLocation.getWorld();

        Location minLocation = new Location(world, islandLocation.getBlockX() - island.getRadius(), 0, islandLocation.getBlockZ() - island.getRadius());
        Location maxLocation = new Location(world, islandLocation.getBlockX() + island.getRadius(), world.getMaxHeight(), islandLocation.getBlockZ() + island.getRadius());

        int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX());
        minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ());

        maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX());
        maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ());

        x = minX;
        z = minZ;

        if(paper){
            this.runTaskAsynchronously(SkyBlock.getInstance());
        } else {
            this.runTaskTimer(SkyBlock.getInstance(), 1L, 0L);
        }
    }

    @Override
    public void run() {
        for(int i = 0; i < 50 || paper; i++){ // TODO Config for chunk per tick
            if(x < maxX){
                if(z < maxZ){
                    if(!chunkForChunk){
                        if(paper){
                            asyncPositions.add(PaperLib.getChunkAtAsync(world, x >> 4, z >> 4));
                        } else {
                            syncPositions.add(world.getChunkAt(x >> 4, z >> 4));
                        }
                    } else {
                        if(paper){
                            if(chunkTask != null) {
                                chunkTask.onChunkComplete(PaperLib.getChunkAtAsync(world, x >> 4, z >> 4), null);
                            }
                        } else {
                            if(chunkTask != null) {
                                chunkTask.onChunkComplete(null, world.getChunkAt(x >> 4, z >> 4));
                            }
                        }
                    }

                    z += 16;
                } else {
                    z = minZ;
                    x += 16;
                }
            } else {
                if(generalTask != null) {
                    generalTask.onComplete(asyncPositions, syncPositions);
                }
                this.cancel();
                return;
            }
        }
    }

    public static void startChunkLoading(Island island, IslandWorld islandWorld, boolean paper, ChunkScannerTask task){
        new ChunkLoader(island, islandWorld, paper, false, task);
    }

    public static void startChunkLoadingPerChunk(Island island, IslandWorld islandWorld, boolean paper, ChunkForChunkScannerTask task){
        new ChunkLoader(island, islandWorld, paper, true, task);
    }

    public interface ChunkScannerTask {

        void onComplete(List<CompletableFuture<Chunk>> asyncChunks, List<Chunk> syncChunks);

    }

    public interface ChunkForChunkScannerTask {

        void onChunkComplete(CompletableFuture<Chunk> asyncChunk, Chunk syncChunk);

    }
}
