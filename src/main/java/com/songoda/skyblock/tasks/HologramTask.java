package com.songoda.skyblock.tasks;

import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.hologram.Hologram;
import com.songoda.skyblock.hologram.HologramType;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.leaderboard.Leaderboard;
import com.songoda.skyblock.leaderboard.LeaderboardManager;
import com.songoda.core.utils.NumberUtils;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.visit.Visit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HologramTask extends BukkitRunnable {

    private static HologramTask instance;
    private static SkyBlock plugin;

    private final List<Hologram> hologramStorage = new ArrayList<>();

    public HologramTask(SkyBlock plug) {
        plugin = plug;
    }

    public static HologramTask startTask(SkyBlock plug) {
        plugin = plug;
        if (instance == null) {
            instance = new HologramTask(plugin);
            instance.runTaskTimerAsynchronously(plugin, 0, 20 * 60);
        }

        return instance;
    }

    @Override
    public void run() {
        for (HologramType hologramTypeList : HologramType.values()) {
            if (hologramTypeList == HologramType.Votes) {
                if (!plugin.getConfiguration().getBoolean("Island.Visitor.Vote")) {
                    continue;
                }
            }
            spawnHologram(hologramTypeList);
        }
    }

    public void onDisable() {
    }

    public void spawnHologram(HologramType type, Location location, List<String> lines) {
        Hologram hologram = hologramStorage.stream()
                .filter(h -> LocationUtil.isLocationLocation(h.getLocation(), location)).findFirst().orElse(null);
        if (hologram == null)
            hologramStorage.add(new Hologram(type, location, lines));
        else
            hologram.update(lines);
    }

    public void spawnHologram(HologramType type) {
        FileManager fileManager = plugin.getFileManager();

        Config locationsConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "locations.yml"));
        FileConfiguration locationsConfigLoad = locationsConfig.getFileConfiguration();

        if (locationsConfigLoad.getString("Location.Hologram.Leaderboard." + type) != null)
            spawnHologram(type, plugin.getFileManager().getLocation(locationsConfig,
                    "Location.Hologram.Leaderboard." + type, true), getHologramLines(type));
    }

    private List<String> getHologramLines(HologramType type) {
        LeaderboardManager leaderboardManager = plugin.getLeaderboardManager();

        FileConfiguration languageConfigLoad = plugin.getLanguage();

        List<String> hologramLines = new ArrayList<>();
        Leaderboard.Type leaderboardType = null;

        switch (type) {
            case Level:
                leaderboardType = Leaderboard.Type.Level;
                break;
            case Bank:
                leaderboardType = Leaderboard.Type.Bank;
                break;
            case Votes:
                leaderboardType = Leaderboard.Type.Votes;
                break;
        }

        hologramLines.add(TextUtils.formatText(
                languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Header")));

        for (int i = 0; i < 10; i++) {
            Leaderboard leaderboard = leaderboardManager.getLeaderboardFromPosition(leaderboardType, i);

            if (leaderboard == null) continue;

            Visit visit = leaderboard.getVisit();

            Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
            String islandOwnerName = targetPlayer == null
                    ? new OfflinePlayer(visit.getOwnerUUID()).getName() : targetPlayer.getName();

            if (type == HologramType.Level) {
                IslandLevel level = visit.getLevel();
                hologramLines.add(TextUtils.formatText(
                        languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Claimed")
                                .replace("%position", "" + (i + 1))
                                .replace("%player", islandOwnerName)
                                .replace("%level", NumberUtils.formatNumber(level.getLevel()))
                                .replace("%points", NumberUtils.formatNumber(level.getPoints()))));
            } else if (type == HologramType.Bank) {
                hologramLines.add(TextUtils.formatText(
                        languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Claimed")
                                .replace("%position", "" + (i + 1))
                                .replace("%player", islandOwnerName)
                                .replace("%balance",
                                        "" + NumberUtils.formatNumber(visit.getBankBalance()))));
            } else if (type == HologramType.Votes) {
                hologramLines.add(TextUtils.formatText(
                        languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Claimed")
                                .replace("%position", "" + (i + 1))
                                .replace("%player", islandOwnerName)
                                .replace("%votes",
                                        "" + NumberUtils.formatNumber(visit.getVoters().size()))));
            }
        }

        String hologramFooter = languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Footer");

        if (!hologramFooter.isEmpty())
            hologramLines.add(TextUtils.formatText(hologramFooter));

        return hologramLines;
    }

    public Hologram getHologram(HologramType type) {
        return hologramStorage.stream().filter(hologramList -> hologramList.getType() == type).findFirst().orElse(null);

    }

    public void updateHologram() {
        new ArrayList<>(hologramStorage).forEach(hologramList -> hologramList.update(getHologramLines(hologramList.getType())));
    }

    public void removeHologram(Hologram hologram) {
        hologramStorage.remove(hologram);
        hologram.remove();
    }
}
