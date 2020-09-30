package com.songoda.skyblock.levelling;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.island.IslandLevelChangeEvent;
import com.songoda.skyblock.blockscanner.BlockInfo;
import com.songoda.skyblock.blockscanner.BlockScanner;
import com.songoda.skyblock.blockscanner.CachedChunk;
import com.songoda.skyblock.blockscanner.ChunkLoader;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.levelling.amount.AmountMaterialPair;
import com.songoda.skyblock.levelling.amount.BlockAmount;
import com.songoda.skyblock.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;

public final class IslandScan extends BukkitRunnable {


    private final Set<Location> doubleBlocks;
    private final Island island;
    private final IslandWorld world;
    private final Map<CompatibleMaterial, BlockAmount> amounts;
    private final Configuration language;
    private final int runEveryX;
    private final SkyBlock plugin;

    private int totalScanned;
    private int blocksSize;
    private Queue<BlockInfo> blocks;

    public IslandScan(SkyBlock plugin, Island island, IslandWorld world) {
        if (island == null) throw new IllegalArgumentException("island cannot be null");
        this.plugin = plugin;
        this.island = island;
        this.world = world;
        this.amounts = new EnumMap<>(CompatibleMaterial.class);
        this.language = this.plugin.getLanguage();
        this.runEveryX = language.getInt("Command.Island.Level.Scanning.Progress.Display-Every-X-Scan");
        this.doubleBlocks = new HashSet<>();
    }

    public IslandScan start() {
        final SkyBlock plugin = SkyBlock.getInstance();

        final FileConfiguration config = this.plugin.getConfiguration();
        final FileConfiguration islandData = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), this.island.getOwnerUUID().toString() + ".yml")).getFileConfiguration();

        final boolean hasNether = config.getBoolean("Island.World.Nether.Enable") && islandData.getBoolean("Unlocked.Nether", false);
        final boolean hasEnd = config.getBoolean("Island.World.End.Enable") && islandData.getBoolean("Unlocked.End", false);

        final Map<World, List<ChunkSnapshot>> snapshots = new HashMap<>(3);


        if (plugin.isPaperAsync()) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                initScan(plugin);
            });
        } else {
            initScan(plugin);
        }


        return this;
    }

    private void initScan(SkyBlock plugin) {

        final Map<World, List<CachedChunk>> cachedChunk = new HashMap<>(3);

        populate(cachedChunk, plugin.isPaperAsync(), () -> {
            BlockScanner.startScanner(cachedChunk, island, true, true, true, false, (blocks) -> {
                this.blocks = blocks;
                this.blocksSize = blocks.size();
                this.runTaskTimer(SkyBlock.getInstance(), 20, 20);
            });
        });
    }

    private int executions;

    @Override
    public void run() {
        executions += 1;

        int scanned = 0;

        for (Iterator<BlockInfo> it = blocks.iterator(); it.hasNext(); ) {

            final BlockInfo info = it.next();

            if (scanned == 8500) break;

            final AmountMaterialPair pair = SkyBlock.getInstance().getLevellingManager().getAmountAndType(this, info);

            if (pair.getType() != null) {

                BlockAmount cachedAmount = amounts.get(pair.getType());

                if (cachedAmount == null) {
                    cachedAmount = new BlockAmount(pair.getAmount());
                } else {
                    cachedAmount.increaseAmount(pair.getAmount());
                }

                amounts.put(pair.getType(), cachedAmount);
            }

            scanned += 1;
            it.remove();
        }

        totalScanned += scanned;

        if (blocks.isEmpty()) {
            cancel();
            SkyBlock.getInstance().getLevellingManager().stopScan(island);
        }
    }

    private void populate(Map<World, List<CachedChunk>> cachedChunks, boolean paper, PopulateTask task) {

        final SkyBlock plugin = SkyBlock.getInstance();
        List<CachedChunk> positions = new LinkedList<>();

        ChunkLoader.startChunkLoadingPerChunk(island, world, paper, positions::add,
                value -> {
                    cachedChunks.put(plugin.getWorldManager().getWorld(world), positions);
                    task.onComplete();
                });
    }

    private interface PopulateTask {
        void onComplete();
    }

    public Set<Location> getDoubleBlocks() {
        return doubleBlocks;
    }

    public Map<CompatibleMaterial, BlockAmount> getAmounts() {
        return Collections.unmodifiableMap(amounts);
    }

    public int getTotalScanned() {
        return totalScanned;
    }

    public int getBlocksSize() {
        return blocksSize;
    }

    public int getExecutions() {
        return executions;
    }
}
