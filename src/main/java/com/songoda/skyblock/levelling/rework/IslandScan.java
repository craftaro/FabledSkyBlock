package com.songoda.skyblock.levelling.rework;

import java.io.File;
import java.text.NumberFormat;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.island.IslandLevelChangeEvent;
import com.songoda.skyblock.blockscanner.BlockInfo;
import com.songoda.skyblock.blockscanner.BlockScanner;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.levelling.ChunkUtil;
import com.songoda.skyblock.levelling.rework.amount.AmountMaterialPair;
import com.songoda.skyblock.levelling.rework.amount.BlockAmount;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.utils.version.Materials;

public final class IslandScan extends BukkitRunnable {

    private static final NumberFormat FORMATTER = NumberFormat.getInstance();;

    private final Island island;
    private final Map<Materials, BlockAmount> amounts;
    private final Configuration config;
    private final int runEveryX;

    private int totalScanned;
    private int blocksSize;
    private Queue<BlockInfo> blocks;

    public IslandScan(Island island) {
        if (island == null) throw new IllegalArgumentException("island cannot be null");
        this.island = island;
        this.amounts = new EnumMap<>(Materials.class);
        this.config = SkyBlock.getInstance().getFileManager().getConfig(new File(SkyBlock.getInstance().getDataFolder(), "language.yml")).getFileConfiguration();
        this.runEveryX = config.getInt("Command.Island.Level.Scanning.Progress.Display-Every-X-Scan");
    }

    public IslandScan start() {
        final SkyBlock skyblock = SkyBlock.getInstance();

        final FileConfiguration config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration();
        final FileConfiguration islandData = skyblock.getFileManager()
                .getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"), this.island.getOwnerUUID().toString() + ".yml")).getFileConfiguration();

        final boolean hasNether = config.getBoolean("Island.World.Nether.Enable") && islandData.getBoolean("Unlocked.Nether", false);
        final boolean hasEnd = config.getBoolean("Island.World.End.Enable") && islandData.getBoolean("Unlocked.End", false);

        final Map<World, List<ChunkSnapshot>> snapshots = new HashMap<>(3);

        populate(snapshots, IslandWorld.Normal);
        if (hasNether) populate(snapshots, IslandWorld.Nether);
        if (hasEnd) populate(snapshots, IslandWorld.End);

        BlockScanner.startScanner(snapshots, (blocks) -> {
            this.blocks = blocks;
            this.blocksSize = blocks.size();
            this.runTaskTimer(SkyBlock.getInstance(), 20, 20);

        });
        return this;
    }

    private void finalizeBlocks() {

        final Map<String, Long> materials = new HashMap<>(amounts.size());

        for (Entry<Materials, BlockAmount> entry : amounts.entrySet()) {
            materials.put(entry.getKey().name(), entry.getValue().getAmount());
        }

        final IslandLevel level = island.getLevel();

        level.setMaterials(materials);
        level.setLastCalculatedLevel(level.getLevel());
        level.setLastCalculatedPoints(level.getPoints());

        Bukkit.getServer().getPluginManager().callEvent(new IslandLevelChangeEvent(island.getAPIWrapper(), island.getAPIWrapper().getLevel()));
    }

    private int executions;

    @Override
    public void run() {
        executions += 1;

        int scanned = 0;

        for (Iterator<BlockInfo> it = blocks.iterator(); it.hasNext();) {

            final BlockInfo info = it.next();

            if (scanned == 8500) break;

            final AmountMaterialPair pair = SkyBlock.getInstance().getLevellingManager().getAmountAndType(info);

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
            finalizeBlocks();
            cancel();
            SkyBlock.getInstance().getLevellingManager().stopScan(island);
        }

        if (config.getBoolean("Command.Island.Level.Scanning.Progress.Should-Display-Message") && executions == 1 || totalScanned == blocksSize || executions % runEveryX == 0) {

            String message = config.getString("Command.Island.Level.Scanning.Progress.Message");
            message = message.replace("%current_scanned_blocks%", String.valueOf(totalScanned));
            message = message.replace("%max_blocks%", String.valueOf(blocksSize));
            message = message.replace("%percent%", FORMATTER.format(((double) totalScanned / (double) blocksSize) * 100));

            final boolean displayComplete = totalScanned == blocksSize && config.getBoolean("Command.Island.Level.Scanning.Finished.Should-Display-Message");
            final MessageManager messageManager = SkyBlock.getInstance().getMessageManager();

            for (Player player : SkyBlock.getInstance().getIslandManager().getPlayersAtIsland(island)) {
                messageManager.sendMessage(player, message);
                if (displayComplete) messageManager.sendMessage(player, config.getString("Command.Island.Level.Scanning.Finished.Message"));
            }
        }

    }

    private void populate(Map<World, List<ChunkSnapshot>> snapshots, IslandWorld world) {

        final SkyBlock skyblock = SkyBlock.getInstance();

        snapshots.put(skyblock.getWorldManager().getWorld(world), ChunkUtil.getChunksToScan(island, world).stream().map(org.bukkit.Chunk::getChunkSnapshot).collect(Collectors.toList()));
    }

}
