package com.songoda.skyblock.leaderboard;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.leaderboard.leaderheads.TopBank;
import com.songoda.skyblock.leaderboard.leaderheads.TopLevel;
import com.songoda.skyblock.leaderboard.leaderheads.TopVotes;
import com.songoda.skyblock.utils.VaultPermissions;
import com.songoda.skyblock.visit.Visit;
import com.songoda.skyblock.visit.VisitManager;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.*;

public class LeaderboardManager {

    private final SkyBlock plugin;

    private final List<Leaderboard> leaderboardStorage = new ArrayList<>();

    public LeaderboardManager(SkyBlock plugin) {
        this.plugin = plugin;

        new LeaderboardTask(plugin).runTaskTimerAsynchronously(plugin, 0L,
                this.plugin.getConfiguration().getInt("Island.Leaderboard.Reset.Time") * 20);

        resetLeaderboard();
        setupLeaderHeads();
    }

    public void resetLeaderboard() {
        VisitManager visitManager = plugin.getVisitManager();
        WorldManager worldManager = plugin.getWorldManager();

        visitManager.loadIslands();

        int arraySize = visitManager.getIslands().size();

        List<LeaderboardPlayer> islandLevels = new ArrayList<>(arraySize);
        List<LeaderboardPlayer> islandBanks = new ArrayList<>(arraySize);
        List<LeaderboardPlayer> islandVotes = new ArrayList<>(arraySize);

        boolean enableExemptions = this.plugin.getConfiguration()
                .getBoolean("Island.Leaderboard.Exemptions.Enable");

        for (UUID ownerUUID : new LinkedHashSet<>(visitManager.getIslands().keySet())) {
            if (enableExemptions && VaultPermissions.hasPermission(worldManager.getWorld(IslandWorld.Normal).getName(), Bukkit.getOfflinePlayer(ownerUUID), "fabledskyblock.top.exempt"))
                continue;

            Visit visit = visitManager.getIslands().get(ownerUUID);
            islandLevels.add(new LeaderboardPlayer(ownerUUID, visit.getLevel().getLevel()));
            islandBanks.add(new LeaderboardPlayer(ownerUUID, (long) visit.getBankBalance()));
            islandVotes.add(new LeaderboardPlayer(ownerUUID, visit.getVoters().size()));
        }

        islandLevels.sort(Comparator.comparingLong(LeaderboardPlayer::getValue).reversed());
        islandBanks.sort(Comparator.comparingLong(LeaderboardPlayer::getValue).reversed());
        islandVotes.sort(Comparator.comparingLong(LeaderboardPlayer::getValue).reversed());

        for (int i = 0; i < 10; i++) {
            if (!islandVotes.isEmpty() && i < islandVotes.size()) {
                Leaderboard leaderboard = new Leaderboard(Leaderboard.Type.Votes, visitManager.getIsland(islandVotes.get(i).getUUID()), i);
                leaderboardStorage.add(leaderboard);
            }

            if (!islandBanks.isEmpty() && i < islandBanks.size()) {
                Leaderboard leaderboard = new Leaderboard(Leaderboard.Type.Bank, visitManager.getIsland(islandBanks.get(i).getUUID()), i);
                leaderboardStorage.add(leaderboard);
            }

            if (!islandLevels.isEmpty() && i < islandLevels.size()) {
                Leaderboard leaderboard = new Leaderboard(Leaderboard.Type.Level, visitManager.getIsland(islandLevels.get(i).getUUID()), i);
                leaderboardStorage.add(leaderboard);
            }
        }
    }

    public int getPlayerIslandLeaderboardPosition(OfflinePlayer offlinePlayer, Leaderboard.Type type) {
        VisitManager visitManager = plugin.getVisitManager();
        visitManager.loadIslands();

        List<LeaderboardPlayer> leaderboardPlayers = new ArrayList<>(visitManager.getIslands().size());

        switch (type) {
            case Level:
                for (UUID ownerUUID : visitManager.getIslands().keySet()) {
                    Visit visit = visitManager.getIslands().get(ownerUUID);
                    leaderboardPlayers.add(new LeaderboardPlayer(ownerUUID, visit.getLevel().getLevel()));
                }
                break;
            case Bank:
                for (UUID ownerUUID : visitManager.getIslands().keySet()) {
                    Visit visit = visitManager.getIslands().get(ownerUUID);
                    leaderboardPlayers.add(new LeaderboardPlayer(ownerUUID, (long) visit.getBankBalance()));
                }
            case Votes:
                for (UUID ownerUUID : visitManager.getIslands().keySet()) {
                    Visit visit = visitManager.getIslands().get(ownerUUID);
                    leaderboardPlayers.add(new LeaderboardPlayer(ownerUUID, visit.getVoters().size()));
                }
                break;
        }

        leaderboardPlayers.sort(Comparator.comparingLong(LeaderboardPlayer::getValue).reversed());

        for (int i = 0; i < leaderboardPlayers.size(); i++) {
            if (leaderboardPlayers.get(i).getUUID().equals(offlinePlayer.getUniqueId())) {
                return i + 1;
            }
        }

        return -1;
    }

    public void setupLeaderHeads() {
        if (Bukkit.getServer().getPluginManager().getPlugin("LeaderHeads") != null) {
            new TopLevel(plugin);
            new TopBank(plugin);
            new TopVotes(plugin);
        }
    }

    public void clearLeaderboard() {
        leaderboardStorage.clear();
    }

    public List<Leaderboard> getLeaderboard(Leaderboard.Type type) {
        List<Leaderboard> leaderboardIslands = new ArrayList<>();

        for (Leaderboard leaderboardList : leaderboardStorage) {
            if (leaderboardList.getType() == type) {
                leaderboardIslands.add(leaderboardList);
            }
        }

        return leaderboardIslands;
    }

    public Leaderboard getLeaderboardFromPosition(Leaderboard.Type type, int position) {
        for (Leaderboard leaderboardPlayerList : leaderboardStorage) {
            if (leaderboardPlayerList.getType() == type && leaderboardPlayerList.getPosition() == position) {
                return leaderboardPlayerList;
            }
        }
        return null;
    }

    public List<Leaderboard> getLeaderboards() {
        return leaderboardStorage;
    }
}
