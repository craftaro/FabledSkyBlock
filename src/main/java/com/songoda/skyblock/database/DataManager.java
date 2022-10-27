package com.songoda.skyblock.database;

import com.eatthepath.uuid.FastUUID;
import com.songoda.core.database.DataManagerAbstract;
import com.songoda.core.database.DatabaseConnector;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager {

    private final PlayerDataManager playerDataManager;
    private final IslandManager islandManager;
    private final DataProvider dataProvider;

    private final DatabaseType databaseType;

    public DataManager(DataProvider provider, DatabaseType type, Plugin plugin) {
        this.dataProvider = provider;
        this.islandManager = new IslandManager((SkyBlock) plugin);
        this.playerDataManager = new PlayerDataManager((SkyBlock) plugin);
        this.databaseType = type;
    }

    public void loadOnline() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            dataProvider.loadPlayerData(player);
            dataProvider.loadIsland(playerDataManager.getPlayerData(player).getIsland());
        }
    }

    public void save() {
        islandManager.onDisable();
        playerDataManager.save();
    }

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }
}
