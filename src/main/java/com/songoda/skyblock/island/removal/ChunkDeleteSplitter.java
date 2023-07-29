package com.songoda.skyblock.island.removal;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.blockscanner.BlockInfo;
import com.songoda.skyblock.blockscanner.BlockScanner;
import com.songoda.skyblock.blockscanner.CachedChunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ChunkDeleteSplitter extends BukkitRunnable {
    private final Map<World, List<CachedChunk>> cachedChunks;
    private Queue<BlockInfo> blocks;

    private ChunkDeleteSplitter(Map<World, List<CachedChunk>> cachedChunks) {
        this.cachedChunks = cachedChunks;
        start();
    }

    private void start() {
        BlockScanner.startScanner(this.cachedChunks, null, false, true, true, false, (blocks) -> {
            this.blocks = blocks;
            this.runTaskTimer(SkyBlock.getPlugin(SkyBlock.class), 20, 20);
        });
    }

    @Override
    public void run() {
        int deleteAmount = 0;

        for (Iterator<BlockInfo> it = this.blocks.iterator(); it.hasNext(); ) {
            if (deleteAmount == 3500) {
                break;
            }

            final BlockInfo pair = it.next();
            final Block block = pair.getWorld().getBlockAt(pair.getX(), pair.getY(), pair.getZ());

            block.setType(CompatibleMaterial.AIR.getBlockMaterial());

            deleteAmount++;
            it.remove();
        }

        if (this.blocks.isEmpty()) {
            cancel();
        }
    }

    public static void startDeletion(Map<World, List<CachedChunk>> snapshots) {
        new ChunkDeleteSplitter(snapshots);
    }
}
