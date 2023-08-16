package com.craftaro.skyblock.leaderboard;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.utils.VaultPermissions;
import com.craftaro.skyblock.visit.Visit;
import com.craftaro.skyblock.visit.VisitManager;
import com.craftaro.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public class LeaderboardManager {
    private final SkyBlock plugin;

    private final List<Leaderboard> leaderboardStorage = new ArrayList<>();

    public LeaderboardManager(SkyBlock plugin) {
        this.plugin = plugin;

        new LeaderboardTask(plugin).runTaskTimerAsynchronously(plugin, 0L, this.plugin.getConfiguration().getInt("Island.Leaderboard.Reset.Time") * 20);

        resetLeaderboard();
    }

    public void resetLeaderboard() {
        VisitManager visitManager = this.plugin.getVisitManager();
        WorldManager worldManager = this.plugin.getWorldManager();

        visitManager.loadIslands();

        int arraySize = visitManager.getIslands().size();

        List<LeaderboardPlayer> islandLevels = new ArrayList<>(arraySize);
        List<LeaderboardPlayer> islandBanks = new ArrayList<>(arraySize);
        List<LeaderboardPlayer> islandVotes = new ArrayList<>(arraySize);

        boolean enableExemptions = this.plugin.getConfiguration().getBoolean("Island.Leaderboard.Exemptions.Enable");

        for (UUID ownerUUID : new LinkedHashSet<>(visitManager.getIslands().keySet())) {
            if (enableExemptions && VaultPermissions.hasPermission(worldManager.getWorld(IslandWorld.NORMAL).getName(), Bukkit.getOfflinePlayer(ownerUUID), "fabledskyblock.top.exempt")) {
                continue;
            }

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
                Leaderboard leaderboard = new Leaderboard(Leaderboard.Type.VOTES, visitManager.getIsland(islandVotes.get(i).getUUID()), i);
                this.leaderboardStorage.add(leaderboard);
            }

            if (!islandBanks.isEmpty() && i < islandBanks.size()) {
                Leaderboard leaderboard = new Leaderboard(Leaderboard.Type.BANK, visitManager.getIsland(islandBanks.get(i).getUUID()), i);
                this.leaderboardStorage.add(leaderboard);
            }

            if (!islandLevels.isEmpty() && i < islandLevels.size()) {
                Leaderboard leaderboard = new Leaderboard(Leaderboard.Type.LEVEL, visitManager.getIsland(islandLevels.get(i).getUUID()), i);
                this.leaderboardStorage.add(leaderboard);
            }
        }
    }

    public int getPlayerIslandLeaderboardPosition(OfflinePlayer offlinePlayer, Leaderboard.Type type) {
        VisitManager visitManager = this.plugin.getVisitManager();
        visitManager.loadIslands();

        List<LeaderboardPlayer> leaderboardPlayers = new ArrayList<>(visitManager.getIslands().size());

        switch (type) {
            case LEVEL:
                for (UUID ownerUUID : visitManager.getIslands().keySet()) {
                    Visit visit = visitManager.getIslands().get(ownerUUID);
                    leaderboardPlayers.add(new LeaderboardPlayer(ownerUUID, visit.getLevel().getLevel()));
                }
                break;
            case BANK:
                for (UUID ownerUUID : visitManager.getIslands().keySet()) {
                    Visit visit = visitManager.getIslands().get(ownerUUID);
                    leaderboardPlayers.add(new LeaderboardPlayer(ownerUUID, (long) visit.getBankBalance()));
                }
            case VOTES:
                for (UUID ownerUUID : visitManager.getIslands().keySet()) {
                    Visit visit = visitManager.getIslands().get(ownerUUID);
                    leaderboardPlayers.add(new LeaderboardPlayer(ownerUUID, visit.getVoters().size()));
                }
                break;
        }

        leaderboardPlayers.sort(Comparator.comparingLong(LeaderboardPlayer::getValue).reversed());

        for (int i = 0; i < leaderboardPlayers.size(); ++i) {
            if (leaderboardPlayers.get(i).getUUID().equals(offlinePlayer.getUniqueId())) {
                return i + 1;
            }
        }

        return -1;
    }

    public void clearLeaderboard() {
        this.leaderboardStorage.clear();
    }

    public List<Leaderboard> getLeaderboard(Leaderboard.Type type) {
        List<Leaderboard> leaderboardIslands = new ArrayList<>();

        for (Leaderboard leaderboardList : this.leaderboardStorage) {
            if (leaderboardList.getType() == type) {
                leaderboardIslands.add(leaderboardList);
            }
        }

        return leaderboardIslands;
    }

    public Leaderboard getLeaderboardFromPosition(Leaderboard.Type type, int position) {
        for (Leaderboard leaderboardPlayerList : this.leaderboardStorage) {
            if (leaderboardPlayerList.getType() == type && leaderboardPlayerList.getPosition() == position) {
                return leaderboardPlayerList;
            }
        }
        return null;
    }

    public List<Leaderboard> getLeaderboards() {
        return this.leaderboardStorage;
    }
}
