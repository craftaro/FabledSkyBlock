package com.craftaro.skyblock.levelling;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.blockscanner.BlockInfo;
import com.craftaro.skyblock.blockscanner.BlockScanner;
import com.craftaro.skyblock.blockscanner.CachedChunk;
import com.craftaro.skyblock.blockscanner.ChunkLoader;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.levelling.amount.AmountMaterialPair;
import com.craftaro.skyblock.levelling.amount.BlockAmount;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class IslandScan extends BukkitRunnable {
    private final Set<Location> doubleBlocks;
    private final Island island;
    private final IslandWorld world;
    private final Map<XMaterial, BlockAmount> amounts;
    private final SkyBlock plugin;

    private int totalScanned;
    private int blocksSize;
    private Queue<BlockInfo> blocks;

    public IslandScan(SkyBlock plugin, Island island, IslandWorld world) {
        if (island == null) {
            throw new IllegalArgumentException("island cannot be null");
        }

        this.plugin = plugin;
        this.island = island;
        this.world = world;
        this.amounts = new EnumMap<>(XMaterial.class);
        this.doubleBlocks = new HashSet<>();
    }

    public IslandScan start() {
        if (this.plugin.isPaperAsync()) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, this::initScan);
        } else {
            initScan();
        }
        return this;
    }

    private void initScan() {
        final Map<World, List<CachedChunk>> cachedChunk = new HashMap<>(3);

        populate(cachedChunk, this.plugin.isPaperAsync(), () -> {
            BlockScanner.startScanner(cachedChunk, this.island, true, true, true, false, (blocks) -> {
                this.blocks = blocks;
                this.blocksSize = blocks.size();
                this.runTaskTimer(this.plugin, 20, 20);
            });
        });
    }

    private int executions;

    @Override
    public void run() {
        this.executions += 1;

        int scanned = 0;

        for (Iterator<BlockInfo> it = this.blocks.iterator(); it.hasNext(); ) {

            final BlockInfo info = it.next();

            if (scanned == 8500) {
                break;
            }

            final AmountMaterialPair pair = this.plugin.getLevellingManager().getAmountAndType(this, info);

            if (pair.getType() != null) {
                BlockAmount cachedAmount = this.amounts.get(pair.getType());

                if (cachedAmount == null) {
                    cachedAmount = new BlockAmount(pair.getAmount());
                } else {
                    cachedAmount.increaseAmount(pair.getAmount());
                }

                this.amounts.put(pair.getType(), cachedAmount);
            }

            scanned += 1;
            it.remove();
        }

        this.totalScanned += scanned;

        if (this.blocks.isEmpty()) {
            cancel();
            this.plugin.getLevellingManager().stopScan(this.island);
        }
    }

    private void populate(Map<World, List<CachedChunk>> cachedChunks, boolean paper, PopulateTask task) {
        List<CachedChunk> positions = new LinkedList<>();

        ChunkLoader.startChunkLoadingPerChunk(this.island, this.world, paper, positions::add,
                value -> {
                    cachedChunks.put(this.plugin.getWorldManager().getWorld(this.world), positions);
                    task.onComplete();
                });
    }

    private interface PopulateTask {
        void onComplete();
    }

    public Set<Location> getDoubleBlocks() {
        return this.doubleBlocks;
    }

    public Map<XMaterial, BlockAmount> getAmounts() {
        return Collections.unmodifiableMap(this.amounts);
    }

    public int getTotalScanned() {
        return this.totalScanned;
    }

    public int getBlocksSize() {
        return this.blocksSize;
    }

    public int getExecutions() {
        return this.executions;
    }
}
