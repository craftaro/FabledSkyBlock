package com.songoda.skyblock.biome;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.blockscanner.BlockInfo;
import com.songoda.skyblock.blockscanner.BlockScanner;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class ChunkBiomeSplitter extends BukkitRunnable {

    private final Map<World, List<ChunkSnapshot>> snapshots;
    private Queue<BlockInfo> blocks;
    private final Biome biome;
    private Chunk lastChunk;
    private ChunkBiomeTask task;

    private ChunkBiomeSplitter(Map<World, List<ChunkSnapshot>> snapshots, Biome biome, ChunkBiomeTask task) {
        this.task = task;
        this.snapshots = snapshots;
        this.biome = biome;
        lastChunk = null;
        start();
    }

    private void start() {
        Bukkit.getScheduler().runTaskAsynchronously(SkyBlock.getInstance(), () -> {
            BlockScanner.startScanner(snapshots, true, true, true, (blocks) -> {
                this.blocks = blocks;
                this.runTaskTimer(SkyBlock.getInstance(), 2L, 2L);
            });
        });
    }

    @Override
    public void run() {

        int updateAmount = 0;

        for (Iterator<BlockInfo> it = blocks.iterator(); it.hasNext();) {

            if (updateAmount == 3500) break;

            final BlockInfo pair = it.next();
            final Block block = pair.getWorld().getBlockAt(pair.getX(), pair.getY(), pair.getZ());

            if(!block.getChunk().equals(lastChunk)){
                lastChunk = block.getChunk();
                task.onChunkComplete(lastChunk);
            }

            block.setBiome(biome);

            updateAmount++;
            it.remove();
        }

        Bukkit.broadcastMessage("Amount: " + blocks.size() + " Empty: " + blocks.isEmpty());

        if (blocks.isEmpty()) {
            super.cancel();
        }
    }

    public static void startUpdating(Map<World, List<ChunkSnapshot>> snapshots, Biome biome, ChunkBiomeTask task) {
        new ChunkBiomeSplitter(snapshots, biome, task);
    }

    public interface ChunkBiomeTask {

        void onChunkComplete(Chunk chunk);

    }

}
