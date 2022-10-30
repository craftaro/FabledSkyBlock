package com.songoda.skyblock.database.sources;

import com.eatthepath.uuid.FastUUID;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.database.DataProvider;
import com.songoda.skyblock.database.exceptions.NoPlayerDataException;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.playerdata.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FlatFileDataProvider implements DataProvider {

    private SkyBlock plugin;
    private Map<UUID, PlayerData> playerDataStorage;
    private Map<UUID, Island> islandStorage;

    @Override
    public void init(SkyBlock plugin) {
        this.plugin = plugin;
        this.playerDataStorage = new HashMap<>();
        this.islandStorage = new HashMap<>();
    }

    @Override
    public boolean hasPlayerData(UUID player) {
        File file = new File(new File(plugin.getDataFolder().toString() + "/player-data"), FastUUID.toString(player) + ".yml");
        return file.exists();
    }

    @Override
    public PlayerData loadPlayerData(UUID playerUUID) {
        File data = new File(new File(plugin.getDataFolder().toString() + "/player-data"), FastUUID.toString(playerUUID) + ".yml");
        if (data.exists()) {
            PlayerData playerData = new PlayerData(playerUUID);
            playerDataStorage.put(playerUUID, playerData);
            return playerData;
        }
        try {
            throw new NoPlayerDataException();
        } catch (NoPlayerDataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isPlayerDataLoaded(UUID playerUUID) {
        return playerDataStorage.containsKey(playerUUID);
    }

    @Override
    public PlayerData getPlayerData(UUID playerUUID) {
        return playerDataStorage.get(playerUUID);
    }

    @Override
    public void unloadPlayerData(UUID playerUUID) {
        playerDataStorage.remove(playerUUID);
    }

    @Override
    public void createPlayerData(UUID playerUUID) {

    }

    @Override
    public void savePlayerData(PlayerData playerData) {

    }

    @Override
    public void savePlayerData(UUID playerUUID) {
        savePlayerData(playerDataStorage.get(playerUUID));
    }

    @Override
    public boolean hasIslandData(UUID islandUUID) {
        return false;
    }

    @Override
    public Set<PlayerData> getLoadedPlayers() {
        return new HashSet<>(playerDataStorage.values());
    }

    @Override
    public boolean hasIsland(UUID playerUUID) {
        return playerDataStorage.get(playerUUID).getIsland() != null;
    }

    @Override
    public Island getPlayerIsland(UUID playerUUID) {
        return getIsland(playerDataStorage.get(playerUUID).getIsland());
    }

    @Override
    public boolean isPlayerIslandLoaded(UUID playerUUID) {
        return islandStorage.containsKey(playerDataStorage.get(playerUUID).getIsland());
    }

    @Override
    public Island loadIsland(UUID islandUUID) {
        return null;
    }

    @Override
    public boolean isIslandDataLoaded(UUID islandUUID) {
        return islandStorage.containsKey(islandUUID);
    }

    @Override
    public Island getIsland(UUID islandUUID) {
        return islandStorage.get(islandUUID);
    }

    @Override
    public void unloadIslandData(UUID islandUUID) {
        islandStorage.remove(islandUUID);
    }

    @Override
    public void createIslandData(UUID islandUUID) {

    }

    @Override
    public void saveIsland(UUID islandUUID) {

    }

    @Override
    public Set<Island> getLoadedIslands() {
        return new HashSet<>(islandStorage.values());
    }
}
