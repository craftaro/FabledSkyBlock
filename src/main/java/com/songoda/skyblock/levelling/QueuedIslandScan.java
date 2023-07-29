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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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
        this.runEveryX = this.language.getInt("Command.Island.Level.Scanning.Progress.Display-Every-X-Scan");
    }

    public void addToScan(IslandWorld world) {
        this.toScan.add(world);
    }

    public void update() {
        this.executions += this.currentScan.getExecutions();
        this.totalScanned += this.currentScan.getTotalScanned();
        this.blocksSize += this.currentScan.getBlocksSize();

        for (Map.Entry<CompatibleMaterial, BlockAmount> entry : this.currentScan.getAmounts().entrySet()) {
            if (this.amounts.containsKey(entry.getKey())) {
                this.amounts.get(entry.getKey()).increaseAmount(entry.getValue().getAmount());
            } else {
                this.amounts.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public boolean scan() {
        if (this.currentScan != null) {
            update();
        }

        if (this.toScan.isEmpty()) {
            finalizeScan();
            return false;
        }
        IslandWorld world = this.toScan.poll();
        this.currentScan = new IslandScan(this.plugin, this.island, world).start();
        return true;
    }

    public void finalizeScan() {
        final Map<String, Long> materials = new HashMap<>(this.amounts.size());

        for (Map.Entry<CompatibleMaterial, BlockAmount> entry : this.amounts.entrySet()) {
            materials.put(entry.getKey().name(), entry.getValue().getAmount());
        }

        final IslandLevel level = this.island.getLevel();

        level.setMaterials(materials);
        level.setLastCalculatedLevel(level.getLevel());
        level.setLastCalculatedPoints(level.getPoints());

        Bukkit.getServer().getPluginManager().callEvent(new IslandLevelChangeEvent(this.island.getAPIWrapper(), this.island.getAPIWrapper().getLevel()));

        Bukkit.getScheduler().runTask(this.plugin, () -> {
            if (this.language.getBoolean("Command.Island.Level.Scanning.Progress.Should-Display-Message") && this.executions == 1 || this.totalScanned == this.blocksSize || this.executions % this.runEveryX == 0) {
                double percent = ((double) this.totalScanned / (double) this.blocksSize) * 100;

                if (Double.isNaN(percent)) {
                    percent = 0d;
                }

                String message = this.language.getString("Command.Island.Level.Scanning.Progress.Message");
                message = message.replace("%current_scanned_blocks%", String.valueOf(this.totalScanned));
                message = message.replace("%max_blocks%", String.valueOf(this.blocksSize));
                message = message.replace("%percent_whole%", String.valueOf((int) percent));
                message = message.replace("%percent%", FORMATTER.format(percent));

                final boolean displayComplete = this.totalScanned == this.blocksSize && this.language.getBoolean("Command.Island.Level.Scanning.Finished.Should-Display-Message");
                final MessageManager messageManager = SkyBlock.getPlugin(SkyBlock.class).getMessageManager();

                for (Player player : SkyBlock.getPlugin(SkyBlock.class).getIslandManager().getPlayersAtIsland(this.island)) {

                    messageManager.sendMessage(player, message);
                    if (displayComplete) {
                        messageManager.sendMessage(player, this.language.getString("Command.Island.Level.Scanning.Finished.Message"));
                    }

                    // Check for level ups
                    this.island.getLevel().checkLevelUp();
                }
            }
        });
    }
}
