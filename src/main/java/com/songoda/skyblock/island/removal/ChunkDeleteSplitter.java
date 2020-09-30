package com.songoda.skyblock.island.removal;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.blockscanner.BlockInfo;
import com.songoda.skyblock.blockscanner.BlockScanner;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ChunkDeleteSplitter extends BukkitRunnable {

    private final Map<World, List<CachedChunk>> snapshots;
    private Queue<BlockInfo> blocks;

    private ChunkDeleteSplitter(Map<World, List<CachedChunk>> snapshots) {
        this.snapshots = snapshots;
        start();
    }

    private void start() {
        BlockScanner.startScanner(snapshots, null, false, true, true, false, (blocks) -> {
            this.blocks = blocks;
            this.runTaskTimer(SkyBlock.getInstance(), 20, 20);
        });
    }

    @Override
    public void run() {

        int deleteAmount = 0;

        for (Iterator<BlockInfo> it = blocks.iterator(); it.hasNext(); ) {

            if (deleteAmount == 3500) break;

            final BlockInfo pair = it.next();
            final Block block = pair.getWorld().getBlockAt(pair.getX(), pair.getY(), pair.getZ());

            block.setType(CompatibleMaterial.AIR.getBlockMaterial());

            deleteAmount++;
            it.remove();
        }

        if (blocks.isEmpty()) {
            cancel();
        }
    }

    public static void startDeletion(Map<World, List<CachedChunk>> snapshots) {
        new ChunkDeleteSplitter(snapshots);
    }
}