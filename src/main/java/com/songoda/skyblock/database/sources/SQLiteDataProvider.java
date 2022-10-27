package com.songoda.skyblock.database.sources;

import com.songoda.core.database.DataManagerAbstract;
import com.songoda.core.database.DatabaseConnector;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.database.DataProvider;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.playerdata.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;

public class SQLiteDataProvider extends DataManagerAbstract implements DataProvider {

    public SQLiteDataProvider(DatabaseConnector databaseConnector, Plugin plugin) {
        super(databaseConnector, plugin);
    }


    @Override
    public void init(SkyBlock plugin) {

    }

    @Override
    public boolean hasPlayerData(UUID playerUUID) {
        return false;
    }

    @Override
    public PlayerData loadPlayerData(UUID playerUUID) {
        return null;
    }

    @Override
    public boolean isPlayerDataLoaded(UUID playerUUID) {
        return false;
    }

    @Override
    public PlayerData getPlayerData(UUID playerUUID) {
        return null;
    }

    @Override
    public void unloadPlayerData(UUID playerUUID) {

    }

    @Override
    public void createPlayerData(UUID playerUUID) {

    }

    @Override
    public void savePlayerData(PlayerData playerData) {

    }

    @Override
    public Set<PlayerData> getLoadedPlayers() {
        return null;
    }

    @Override
    public boolean hasIslandData(UUID islandUUID) {
        return false;
    }

    @Override
    public Island loadIsland(UUID islandUUID) {
        return null;
    }

    @Override
    public boolean isIslandDataLoaded(UUID islandUUID) {
        return false;
    }

    @Override
    public Island getIsland(UUID islandUUID) {
        return null;
    }

    @Override
    public void unloadIslandData(UUID islandUUID) {

    }

    @Override
    public void createIslandData(UUID islandUUID) {

    }

    @Override
    public void saveIsland(UUID islandUUID) {

    }

    @Override
    public Set<Island> getLoadedIslands() {
        return null;
    }
}
