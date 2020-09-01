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

        rows = new ArrayList<>();
        holders = new ArrayList<>();
        this.boardType = boardType;

        ConfigurationSection config = scoreboardLoad.getConfigurationSection(boardType.getConfigSection());

        if(config != null) {
            List<String> lines = config.getStringList("Title.Content");
            int interval = config.getInt("Title.Interval");
            title = new Row(lines, interval);

            for(int i = 1; i<16; i++) {
                List<String> rowLines = config.getStringList("Rows." + i + ".Content");
                if(!rowLines.isEmpty()) {
                    Row row = new Row(rowLines, config.getInt("Rows." + i + ".Interval"));
                    rows.add(row);
                }
            }
        } else {
            title = new Row(new ArrayList<>(), -1);
        }
    }

    List<Row> getRows() {
        return rows;
    }

    Row getTitle() {
        return title;
    }

    void registerHolder(Holder holder) {
        holders.add(holder);
    }

    void unregisterHolder(Holder holder) {
        holders.remove(holder);
    }

    void unregisterHolder(Player player) {
        Iterator<Holder> it = holders.iterator();
        while(it.hasNext()) {
            Holder holder = it.next();
            if(holder.getPlayer().equals(player)) {
                it.remove();
                break;
            }
        }
    }
    
    @Override
    public void run() {
        title.update();
        for(Row row : rows) {
            row.update();
        }

        for(Holder holder : holders) {
            holder.update();
        }
    }

    ScoreboardType getBoardType() {
        return boardType;
    }
}
