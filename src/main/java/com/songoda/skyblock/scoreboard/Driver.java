package com.songoda.skyblock.scoreboard;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Driver extends BukkitRunnable {
    private final Row title;
    private final List<Row> rows;
    private final List<Holder> holders;
    private final ScoreboardType boardType;

    Driver(SkyBlock plugin, ScoreboardType boardType) {
        FileManager fileManager = plugin.getFileManager();
        FileConfiguration scoreboardLoad = fileManager.getConfig(
                new File(plugin.getDataFolder(), "scoreboard.yml")).getFileConfiguration();

        this.rows = new ArrayList<>();
        this.holders = new ArrayList<>();
        this.boardType = boardType;

        ConfigurationSection config = scoreboardLoad.getConfigurationSection(boardType.getConfigSection());

        if (config != null) {
            List<String> lines = config.getStringList("Title.Content");
            int interval = config.getInt("Title.Interval");
            this.title = new Row(lines, interval);

            for (int i = 1; i < 16; i++) {
                List<String> rowLines = config.getStringList("Rows." + i + ".Content");
                if (!rowLines.isEmpty()) {
                    Row row = new Row(rowLines, config.getInt("Rows." + i + ".Interval"));
                    this.rows.add(row);
                }
            }
        } else {
            this.title = new Row(new ArrayList<>(), -1);
        }
    }

    List<Row> getRows() {
        return this.rows;
    }

    Row getTitle() {
        return this.title;
    }

    void registerHolder(Holder holder) {
        synchronized (this.holders) {
            this.holders.add(holder);
        }
    }

    void unregisterHolder(Holder holder) {
        synchronized (this.holders) {
            this.holders.remove(holder);
        }
    }

    void unregisterHolder(Player player) {
        synchronized (this.holders) {
            Iterator<Holder> it = this.holders.iterator();
            while (it.hasNext()) {
                Holder holder = it.next();
                if (holder.getPlayer().equals(player)) {
                    it.remove();
                    break;
                }
            }
        }
    }

    @Override
    public void run() {
        this.title.update();
        for (Row row : this.rows) {
            row.update();
        }

        synchronized (this.holders) {
            for (Holder holder : this.holders) {
                holder.update();
            }
        }
    }

    ScoreboardType getBoardType() {
        return this.boardType;
    }
}
