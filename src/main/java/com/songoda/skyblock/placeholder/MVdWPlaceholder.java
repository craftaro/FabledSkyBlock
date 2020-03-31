package com.songoda.skyblock.placeholder;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.leaderboard.Leaderboard;
import com.songoda.skyblock.leaderboard.LeaderboardManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.visit.Visit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class MVdWPlaceholder {

    private final SkyBlock skyblock;

    public MVdWPlaceholder(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    public void register() {
        PlaceholderManager placeholderManager = skyblock.getPlaceholderManager();
        LeaderboardManager leaderboardManager = skyblock.getLeaderboardManager();

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        List<Leaderboard> leaderboardLevelPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.Level);
        List<Leaderboard> leaderboardBankPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.Bank);
        List<Leaderboard> leaderboardVotesPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.Votes);

        PlaceholderAPI.registerPlaceholder(skyblock, "fabledskyblock_islands", event -> "" + skyblock.getVisitManager().getIslands().size());

        for (int i = 0; i < 10; i++) {
            PlaceholderAPI.registerPlaceholder(skyblock, "fabledskyblock_leaderboard_votes_" + (i + 1),
                    event -> {
                        int index = Integer.valueOf(event.getPlaceholder().replace("fabledskyblock_leaderboard_votes_", ""));

                        if (index < leaderboardVotesPlayers.size()) {
                            Leaderboard leaderboard = leaderboardVotesPlayers.get(index);
                            Visit visit = leaderboard.getVisit();

                            Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
                            String islandOwnerName;

                            if (targetPlayer == null) {
                                islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
                            } else {
                                islandOwnerName = targetPlayer.getName();
                            }

                            return ChatColor.translateAlternateColorCodes('&',
                                    configLoad.getString("Placeholder.fabledskyblock_leaderboard_votes.Non-empty.Message")
                                            .replace("%position", "" + (index + 1))
                                            .replace("%player", islandOwnerName)
                                            .replace("%votes", NumberUtil.formatNumberByDecimal(visit.getVoters().size())));
                        }

                        return ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Placeholder.fabledskyblock_leaderboard_votes.Empty.Message"));
                    });

            PlaceholderAPI.registerPlaceholder(skyblock, "fabledskyblock_leaderboard_bank_" + (i + 1),
                    event -> {
                        int index = Integer.valueOf(event.getPlaceholder().replace("fabledskyblock_leaderboard_bank_", ""));

                        if (index < leaderboardBankPlayers.size()) {
                            Leaderboard leaderboard = leaderboardBankPlayers.get(index);
                            Visit visit = leaderboard.getVisit();

                            Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
                            String islandOwnerName;

                            if (targetPlayer == null) {
                                islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
                            } else {
                                islandOwnerName = targetPlayer.getName();
                            }

                            return ChatColor.translateAlternateColorCodes('&',
                                    configLoad.getString("Placeholder.fabledskyblock_leaderboard_bank.Non-empty.Message")
                                            .replace("%position", "" + (index + 1))
                                            .replace("%player", islandOwnerName)
                                            .replace("%balance", NumberUtil.formatNumberByDecimal(visit.getBankBalance())));
                        }

                        return ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Placeholder.fabledskyblock_leaderboard_bank.Empty.Message"));
                    });

            PlaceholderAPI.registerPlaceholder(skyblock, "fabledskyblock_leaderboard_level_" + (i + 1),
                    event -> {
                        int index = Integer.valueOf(event.getPlaceholder().replace("fabledskyblock_leaderboard_level_", ""));

                        if (index < leaderboardLevelPlayers.size()) {
                            Leaderboard leaderboard = leaderboardLevelPlayers.get(index);
                            Visit visit = leaderboard.getVisit();
                            IslandLevel level = visit.getLevel();

                            Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
                            String islandOwnerName;

                            if (targetPlayer == null) {
                                islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
                            } else {
                                islandOwnerName = targetPlayer.getName();
                            }

                            return ChatColor.translateAlternateColorCodes('&', configLoad
                                    .getString("Placeholder.fabledskyblock_leaderboard_level.Non-empty.Message")
                                    .replace("%position", "" + (index + 1)).replace("%player", islandOwnerName)
                                    .replace("%level", NumberUtil.formatNumberByDecimal(level.getLevel()))
                                    .replace("%points", NumberUtil.formatNumberByDecimal(level.getPoints())));
                        }

                        return ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Placeholder.fabledskyblock_leaderboard_level.Empty.Message"));
                    });
        }

        for (String placeholderList : placeholderManager.getPlaceholders()) {
            PlaceholderAPI.registerPlaceholder(skyblock, placeholderList, event -> {
            	try {
            		Player player = event.getPlayer();
            		
            		if (player == null) {
            			return null;
            		}
            		
            		return placeholderManager.getPlaceholder(player, event.getPlaceholder());
            	} catch (Exception ex) {
            		Bukkit.getLogger().log(Level.WARNING, "[FabledSkyBlock] Exception while retrieving placeholder {}:", event.getPlaceholder());
            		Bukkit.getLogger().log(Level.WARNING, "", ex);
            		return event.getPlaceholder();
            	}
            });
        }
    }
}
