package com.songoda.skyblock.blockscanner;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.Lists;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.world.WorldManager;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class BlockScanner extends BukkitRunnable {
    private static final Method ID_FIELD;
    private static final int MAX_CHUNKS_PER_ITERATION = 2;
    private static final int MAX_EMPTY_ITERATIONS = 20;

    static {
        Method temp = null;

        try {
            temp = ChunkSnapshot.class.getMethod("getBlockTypeId", int.class, int.class, int.class);
        } catch (NoSuchMethodException ignored) {
        }

        ID_FIELD = temp;
    }

    public static int getBlockTypeID(CachedChunk chunk, int x, int y, int z) {

        int id = 0;

        try {
            id = (Integer) ID_FIELD.invoke(chunk.getSnapshot(), x, y, z);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace();
        }

        return id;
    }

    private final AtomicInteger completedNum;

    private final int threadCount;
    private final Queue<BlockInfo> blocks;
    private final ScannerTasks tasks;

    private final Island island;

    private final boolean ignoreLiquids;
    private final boolean ignoreAir;

    private BlockScanner(Map<World, List<CachedChunk>> snapshots,
                         Island island,
                         boolean ignoreLiquids,
                         boolean ignoreLiquidsY,
                         boolean ignoreAir,
                         boolean ignoreY,
                         ScannerTasks tasks) {
        this.ignoreLiquids = ignoreLiquids;
        this.ignoreAir = ignoreAir;
        this.blocks = new ConcurrentLinkedQueue<>();
        this.tasks = tasks;
        this.completedNum = new AtomicInteger();
        this.island = island;

        FileConfiguration config = SkyBlock.getPlugin(SkyBlock.class).getConfiguration();

        int threadCount = 0;

        for (Entry<World, List<CachedChunk>> entry : snapshots.entrySet()) {

            final List<List<CachedChunk>> parts = Lists.partition(entry.getValue(), 16);

            threadCount += parts.size();

            World world = entry.getKey();
            final String env;

            switch (world.getEnvironment()) {
                case NETHER:
                    env = "Nether";
                    break;
                case THE_END:
                    env = "End";
                    break;
                default:
                    env = "Normal";
                    break;
            }

            final ConfigurationSection liquidSection = config.getConfigurationSection("Island.World." + env + ".Liquid");

            int startY;
            if (ignoreY) {
                startY = 255;
            } else {
                startY = !ignoreLiquidsY && liquidSection.getBoolean("Enable") && !config.getBoolean("Island.Levelling.ScanLiquid") ? liquidSection.getInt("Height") + 1 : 0;
            }

            for (List<CachedChunk> sub : parts) {
                queueWork(world, startY, sub);
            }
        }

        this.threadCount = threadCount;
    }

    private void queueWork(World world, int scanY, List<CachedChunk> subList) {
        WorldManager worldManager = SkyBlock.getPlugin(SkyBlock.class).getWorldManager();

        // The chunks that couldn't be taken snapshot async
        List<CachedChunk> pendingChunks = new ArrayList<>();

        // The chunks that are ready to be processed asynchronously
        List<CachedChunk> readyChunks = new ArrayList<>();

        // This lock will help to make the bukkit task wait after all the chunks that could be processed async are processed
        Lock lock = new ReentrantLock();

        // This is the actual object that we will use to wait
        Condition emptyCondition = lock.newCondition();

        Bukkit.getServer().getScheduler().runTaskAsynchronously(SkyBlock.getPlugin(SkyBlock.class), () -> {
            // We need to hold the lock on the thread calling the await
            lock.lock();

            LocationBounds bounds = null;
            if (this.island != null) {
                Location islandLocation = this.island.getLocation(worldManager.getIslandWorld(world), IslandEnvironment.ISLAND);

                Location minLocation = new Location(world, islandLocation.getBlockX() - this.island.getRadius(), 0, islandLocation.getBlockZ() - this.island.getRadius());
                Location maxLocation = new Location(world, islandLocation.getBlockX() + this.island.getRadius(), world.getMaxHeight(), islandLocation.getBlockZ() + this.island.getRadius());

                int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX());
                int minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ());

                int maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX());
                int maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ());

                bounds = new LocationBounds(minX, minZ, maxX, maxZ);
            }

            for (CachedChunk shot : subList) {
                if (!shot.isSnapshotAvailable() && !areAsyncChunksAvailable()) {
                    pendingChunks.add(shot);

                    continue;
                }

                processCachedChunk(world, scanY, shot, bounds);
            }

            // Don't wait for the condition if the async chunks are available, since it would never be signalled
            if (areAsyncChunksAvailable()) {
                increment();

                lock.unlock();
                return;
            }

            try {
                emptyCondition.await();
            } catch (InterruptedException e) {
                // Pass the interruption
                Thread.currentThread().interrupt();
            }

            // process the pending chunks
            for (CachedChunk shot : readyChunks) {
                processCachedChunk(world, scanY, shot, bounds);
            }

            lock.unlock();
            increment();
        });

        if (!areAsyncChunksAvailable()) {
            startChunkSnapshotTask(pendingChunks, readyChunks, emptyCondition, lock);
        }
    }

    private boolean areAsyncChunksAvailable() {
        return PaperLib.isVersion(9) && PaperLib.isPaper();
    }

    private void startChunkSnapshotTask(List<CachedChunk> pendingChunks, List<CachedChunk> readyChunks, Condition emptyCondition, Lock lock) {
        new BukkitRunnable() {
            // The number of iterations with the pendingChunks list empty
            private int emptyIterations = 0;

            @Override
            public void run() {
                lock.lock();
                int updatedChunks = 0;

                Iterator<CachedChunk> chunkIterator = pendingChunks.iterator();

                try {
                    while (chunkIterator.hasNext()) {
                        CachedChunk pendingChunk = chunkIterator.next();

                        if (updatedChunks >= MAX_CHUNKS_PER_ITERATION) {
                            break;
                        }

                        // take the snapshot
                        pendingChunk.takeSnapshot();

                        chunkIterator.remove();
                        readyChunks.add(pendingChunk);

                        updatedChunks++;
                    }

                    if (pendingChunks.isEmpty()) {
                        if (this.emptyIterations >= MAX_EMPTY_ITERATIONS) {
                            // Send the signal to unlock the async thread and continue with the processing
                            emptyCondition.signalAll();
                            this.cancel();

                            return;
                        }

                        this.emptyIterations++;
                    }
                } finally {
                    lock.unlock();
                }
            }
        }.runTaskTimer(SkyBlock.getPlugin(SkyBlock.class), 1, 1);
    }

    private void processCachedChunk(World world, int scanY, CachedChunk shot, LocationBounds bounds) {
        final int cX = shot.getX() << 4;
        final int cZ = shot.getZ() << 4;

        int initX = 0;
        int initZ = 0;
        int lastX = 15;
        int lastZ = 15;

        if (bounds != null) {
            initX = Math.max(cX, bounds.getMinX()) & 0x000F;
            initZ = Math.max(cZ, bounds.getMinZ()) & 0x000F;

            lastX = Math.min(cX | 15, bounds.getMaxX() - 1) & 0x000F;
            lastZ = Math.min(cZ | 15, bounds.getMaxZ() - 1) & 0x000F;
        }

        for (int x = initX; x <= lastX; x++) {
            for (int z = initZ; z <= lastZ; z++) {
                for (int y = scanY; y < world.getMaxHeight(); y++) {
                    final Optional<XMaterial> type = CompatibleMaterial.getMaterial(
                            ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                                    ? shot.getSnapshot().getBlockType(x, y, z) :
                                    MaterialIDHelper.getLegacyMaterial(getBlockTypeID(shot, x, y, z)));

                    if (!type.isPresent()) {
                        continue;

                    } else if (CompatibleMaterial.isAir(type.get()) && this.ignoreAir) {
                        continue;
                    } else if (type.get() == XMaterial.WATER && this.ignoreLiquids) {
                        continue;
                    }

                    this.blocks.add(new BlockInfo(world, x + (cX), y, z + (cZ)));
                }
            }
        }
    }

    private synchronized int increment() {
        return this.completedNum.getAndIncrement();
    }

    private synchronized int get() {
        return this.completedNum.get();
    }

    @Override
    public void run() {
        if (get() != this.threadCount) {
            return;
        }

        this.tasks.onComplete(this.blocks);
        cancel();
    }

    public static void startScanner(Map<World, List<CachedChunk>> snapshots, Island island, boolean ignoreLiquids, boolean ignoreLiquidsY, boolean ignoreAir, boolean ignoreY, ScannerTasks tasks) {
        if (snapshots == null) {
            throw new IllegalArgumentException("snapshots cannot be null");
        }
        if (tasks == null) {
            throw new IllegalArgumentException("tasks cannot be null");
        }

        final BlockScanner scanner = new BlockScanner(snapshots, island, ignoreLiquids, ignoreLiquidsY, ignoreAir, ignoreY, tasks);

        scanner.runTaskTimer(SkyBlock.getPlugin(SkyBlock.class), 5, 5);
    }

    public interface ScannerTasks {
        void onComplete(Queue<BlockInfo> blocks);
    }
}
