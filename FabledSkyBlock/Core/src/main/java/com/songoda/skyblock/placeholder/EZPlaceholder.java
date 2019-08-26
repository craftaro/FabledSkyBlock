package com.songoda.skyblock.placeholder;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.leaderboard.Leaderboard;
import com.songoda.skyblock.leaderboard.LeaderboardManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.visit.Visit;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.List;

public class EZPlaceholder extends PlaceholderExpansion implements Listener {

    private final SkyBlock skyblock;

    public EZPlaceholder(SkyBlock skyblock) {
        this.skyblock = skyblock;
        Bukkit.getPluginManager().registerEvents(this, skyblock);
    }

    public String getIdentifier() {
        return "fabledskyblock";
    }

    public String getPlugin() {
        return null;
    }

    public String getAuthor() {
        return skyblock.getDescription().getAuthors().get(0);
    }

    public String getVersion() {
        return skyblock.getDescription().getVersion();
    }

    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        PlaceholderManager placeholderManager = skyblock.getPlaceholderManager();
        LeaderboardManager leaderboardManager = skyblock.getLeaderboardManager();

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        List<Leaderboard> leaderboardLevelPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.Level);
        List<Leaderboard> leaderboardBankPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.Bank);
        List<Leaderboard> leaderboardVotesPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.Votes);


        if (identifier.equalsIgnoreCase("islands")) {
            return "" + skyblock.getVisitManager().getIslands().size();
        } else {
            for (int i = 0; i < 10; i++) {
                if (identifier.equalsIgnoreCase("leaderboard_votes_" + (i + 1))) {
                    if (i < leaderboardVotesPlayers.size()) {
                        Leaderboard leaderboard = leaderboardVotesPlayers.get(i);
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
                                        .replace("%position", "" + (i + 1)).replace("%player", islandOwnerName)
                                        .replace("%votes", NumberUtil.formatNumberByDecimal(visit.getVoters().size())));
                    }

                    return ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Placeholder.fabledskyblock_leaderboard_votes.Empty.Message"));
                } else if (identifier.equalsIgnoreCase("leaderboard_bank_" + (i + 1))) {
                    if (i < leaderboardBankPlayers.size()) {
                        Leaderboard leaderboard = leaderboardBankPlayers.get(i);
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
                                        .replace("%position", "" + (i + 1)).replace("%player", islandOwnerName)
                                        .replace("%balance", NumberUtil.formatNumberByDecimal(visit.getBankBalance())));
                    }

                    return ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Placeholder.fabledskyblock_leaderboard_bank.Empty.Message"));
                } else if (identifier.equalsIgnoreCase("leaderboard_level_" + (i + 1))) {
                    if (i < leaderboardLevelPlayers.size()) {
                        Leaderboard leaderboard = leaderboardLevelPlayers.get(i);
                        Visit visit = leaderboard.getVisit();
                        IslandLevel level = visit.getLevel();

                        Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
                        String islandOwnerName;

                        if (targetPlayer == null) {
                            islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
                        } else {
                            islandOwnerName = targetPlayer.getName();
                        }

                        return ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Placeholder.fabledskyblock_leaderboard_level.Non-empty.Message")
                                        .replace("%position", "" + (i + 1)).replace("%player", islandOwnerName)
                                        .replace("%level", NumberUtil.formatNumberByDecimal(level.getLevel()))
                                        .replace("%points", NumberUtil.formatNumberByDecimal(level.getPoints())));
                    }

                    return ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Placeholder.fabledskyblock_leaderboard_level.Empty.Message"));
                }
            }
        }

        if (player == null) {
            return "";
        }

        return placeholderManager.getPlaceholder(player, "fabledskyblock_" + identifier);
    }

}
