package com.songoda.skyblock.levelling;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.island.IslandLevelChangeEvent;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.levelling.amount.BlockAmount;
import com.songoda.skyblock.message.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.*;

public class QueuedIslandScan {

    private final SkyBlock plugin;
    private final Island island;
    private IslandScan currentScan = null;
    private final Queue<IslandWorld> toScan = new LinkedList<>();

    private static final NumberFormat FORMATTER = NumberFormat.getInstance();
    private final Configuration language;

    private int executions;
    private final int runEveryX;
    private final Map<CompatibleMaterial, BlockAmount> amounts = new EnumMap<>(CompatibleMaterial.class);
    private int totalScanned;
    private int blocksSize;

    public QueuedIslandScan(SkyBlock plugin, Island island) {
        this.plugin = plugin;
        this.island = island;
        this.language = plugin.getLanguage();
        this.runEveryX = language.getInt("Command.Island.Level.Scanning.Progress.Display-Every-X-Scan");
    }

    public void addToScan(IslandWorld world) {
        toScan.add(world);
    }

    public void update() {
        this.executions += currentScan.getExecutions();
        this.totalScanned += currentScan.getTotalScanned();
        this.blocksSize += currentScan.getBlocksSize();

        for (Map.Entry<CompatibleMaterial, BlockAmount> entry : currentScan.getAmounts().entrySet()) {
            if (amounts.containsKey(entry.getKey())) {
                amounts.get(entry.getKey()).increaseAmount(entry.getValue().getAmount());
            } else {
                amounts.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public boolean scan() {
        if (currentScan != null)
            update();

        if (toScan.isEmpty()) {
            finalizeScan();
            return false;
        }
        IslandWorld world = toScan.poll();
        currentScan = new IslandScan(plugin, island, world).start();
        return true;
    }

    public void finalizeScan() {
        final Map<String, Long> materials = new HashMap<>(amounts.size());

        for (Map.Entry<CompatibleMaterial, BlockAmount> entry : amounts.entrySet()) {
            materials.put(entry.getKey().name(), entry.getValue().getAmount());
        }

        final IslandLevel level = island.getLevel();

        level.setMaterials(materials);
        level.setLastCalculatedLevel(level.getLevel());
        level.setLastCalculatedPoints(level.getPoints());

        Bukkit.getServer().getPluginManager().callEvent(new IslandLevelChangeEvent(island.getAPIWrapper(), island.getAPIWrapper().getLevel()));

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (language.getBoolean("Command.Island.Level.Scanning.Progress.Should-Display-Message") && executions == 1 || totalScanned == blocksSize || executions % runEveryX == 0) {

                double percent = ((double) totalScanned / (double) blocksSize) * 100;

                if (Double.isNaN(percent)) {
                    percent = 0d;
                }

                String message = language.getString("Command.Island.Level.Scanning.Progress.Message");
                message = message.replace("%current_scanned_blocks%", String.valueOf(totalScanned));
                message = message.replace("%max_blocks%", String.valueOf(blocksSize));
                message = message.replace("%percent_whole%", String.valueOf((int) percent));
                message = message.replace("%percent%", FORMATTER.format(percent));

                final boolean displayComplete = totalScanned == blocksSize && language.getBoolean("Command.Island.Level.Scanning.Finished.Should-Display-Message");
                final MessageManager messageManager = SkyBlock.getInstance().getMessageManager();

                for (Player player : SkyBlock.getInstance().getIslandManager().getPlayersAtIsland(island)) {

                    messageManager.sendMessage(player, message);
                    if (displayComplete)
                        messageManager.sendMessage(player, language.getString("Command.Island.Level.Scanning.Finished.Message"));

                    // Check for level ups
                    island.getLevel().checkLevelUp();
                }
            }
        });
    }
}
