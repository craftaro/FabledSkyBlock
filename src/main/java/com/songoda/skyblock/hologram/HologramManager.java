package com.songoda.skyblock.hologram;

import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.leaderboard.Leaderboard;
import com.songoda.skyblock.leaderboard.LeaderboardManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.visit.Visit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HologramManager {

    private final SkyBlock skyblock;
    private List<Hologram> hologramStorage = new ArrayList<>();

    public HologramManager(SkyBlock skyblock) {
        this.skyblock = skyblock;

        FileManager fileManager = skyblock.getFileManager();

        Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> {
            for (HologramType hologramTypeList : HologramType.values()) {
                if (hologramTypeList == HologramType.Votes) {
                    if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                            .getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
                        continue;
                    }
                }

                spawnHologram(hologramTypeList);
            }
        }, 200L);
    }

    public void onDisable() {
        removeHolograms();
    }

    public void spawnHologram(HologramType type, Location location, List<String> lines) {
        hologramStorage.add(new Hologram(type, location, lines));
    }

    public void spawnHologram(HologramType type) {
        FileManager fileManager = skyblock.getFileManager();

        Config locationsConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "locations.yml"));
        FileConfiguration locationsConfigLoad = locationsConfig.getFileConfiguration();

        if (locationsConfigLoad.getString("Location.Hologram.Leaderboard." + type) != null)
            spawnHologram(type, skyblock.getFileManager().getLocation(locationsConfig,
                    "Location.Hologram.Leaderboard." + type, true), getHologramLines(type));
    }

    private List<String> getHologramLines(HologramType type) {
        FileManager fileManager = skyblock.getFileManager();
        LeaderboardManager leaderboardManager = skyblock.getLeaderboardManager();
        MessageManager messageManager = skyblock.getMessageManager();

        FileConfiguration languageConfigLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                .getFileConfiguration();

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
                                .replace("%level", NumberUtil.formatNumberByDecimal(level.getLevel()))
                                .replace("%points", NumberUtil.formatNumberByDecimal(level.getPoints()))));
            } else if (type == HologramType.Bank) {
                hologramLines.add(TextUtils.formatText(
                        languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Claimed")
                                .replace("%position", "" + (i + 1))
                                .replace("%player", islandOwnerName)
                                .replace("%balance",
                                        "" + NumberUtil.formatNumberByDecimal(visit.getBankBalance()))));
            } else if (type == HologramType.Votes) {
                hologramLines.add(TextUtils.formatText(
                        languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Claimed")
                                .replace("%position", "" + (i + 1))
                                .replace("%player", islandOwnerName)
                                .replace("%votes",
                                        "" + NumberUtil.formatNumberByDecimal(visit.getVoters().size()))));
            }
        }

        String hologramFooter = languageConfigLoad.getString("Hologram.Leaderboard." + type.name() + ".Footer");

        if (!hologramFooter.isEmpty())
            hologramLines.add(TextUtils.formatText(hologramFooter));

        return hologramLines;
    }

    public void removeHolograms() {
        for (Hologram hologramList : hologramStorage) {
            hologramList.remove();
        }
    }

    public Hologram getHologram(HologramType type) {
        for (Hologram hologramList : hologramStorage) {
            if (hologramList.getType() == type) {
                return hologramList;
            }
        }

        return null;
    }

    public void updateHologram() {
        for (Hologram hologramList : hologramStorage) {
            hologramList.update(getHologramLines(hologramList.getType()));
        }
    }
}
