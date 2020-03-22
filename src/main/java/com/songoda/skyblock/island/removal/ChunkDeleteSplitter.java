package com.songoda.skyblock.island.removal;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.songoda.skyblock.SkyBlock;

public final class ChunkDeleteSplitter extends BukkitRunnable {

    private int completedNum;

    private final World world;
    private final int threadCount;
    private final Queue<XYZPair> toRemove;

    private ChunkDeleteSplitter(World world, List<ChunkSnapshot> snapshots) {
        this.toRemove = new ConcurrentLinkedQueue<>();
        this.world = world;

        final List<List<ChunkSnapshot>> parts = Lists.partition(snapshots, 32);

        this.threadCount = parts.size();

        for (List<ChunkSnapshot> sub : parts) {
            queueWork(sub);
        }
    }

    private void queueWork(List<ChunkSnapshot> subList) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(SkyBlock.getInstance(), () -> {
            for (ChunkSnapshot shot : subList) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 256; y++) {
                            final Material type = shot.getBlockType(x, y, z);

                            if (type == Material.AIR) continue;

                            toRemove.add(new XYZPair(x, y, z));
                        }
                    }
                }
            }
            increment();
        });
    }

    private synchronized void increment() {
        completedNum++;
    }

    private synchronized int get() {
        return completedNum;
    }

    @Override
    public void run() {
        if (get() != threadCount) return;

        int deleteAmount = 0;

        for (Iterator<XYZPair> it = toRemove.iterator(); it.hasNext();) {

            if (deleteAmount == 10000) break;

            final XYZPair pair = it.next();

            world.getBlockAt(pair.getX(), pair.getY(), pair.getZ()).setType(Material.AIR);

            deleteAmount++;
            it.remove();
        }

        if (toRemove.isEmpty()) {
            cancel();
        }
    }

    public static void startDeleting(World world, List<ChunkSnapshot> snapshots) {

        final ChunkDeleteSplitter splitter = new ChunkDeleteSplitter(world, snapshots);

        splitter.runTaskTimer(SkyBlock.getInstance(), 5, 5);
    }

}
