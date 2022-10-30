package com.songoda.skyblock.database;

import com.bekvon.bukkit.residence.commands.check;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.database.exceptions.NoIslandDataException;
import com.songoda.skyblock.database.exceptions.NoPlayerDataException;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.playerdata.PlayerData;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface DataProvider {

    void init(SkyBlock plugin);

    /**
     * Check if a player has data in the database
     * @param player The player to check
     */
    default boolean hasPlayerData(Player player) {
        return hasPlayerData(player.getUniqueId());
    }

    /**
     * Check if a player has data in the database
     * @param player The player to check
     */
    default boolean hasPlayerData(OfflinePlayer player) {
        return hasPlayerData(player.getUniqueId());
    }

    /**
     * Check if a player has data in the database
     * @param playerUUID The player's UUID to check
     */
    boolean hasPlayerData(UUID playerUUID);

    /**
     * Load a player's data from the database into memory
     * @param player The player to load
     * @throws NoPlayerDataException when the player has no data in the database
     */
    default PlayerData loadPlayerData(Player player) {
        return loadPlayerData(player.getUniqueId());
    }

    /**
     * Load a player's data from the database into memory
     * @param offlinePlayer The player to load
     * @throws NoPlayerDataException when the player has no data in the database
     */
    default PlayerData loadPlayerData(OfflinePlayer offlinePlayer) {
        return loadPlayerData(offlinePlayer.getUniqueId());
    }

    /**
     * Load a player's data from the database into memory
     * @param playerUUID The player's UUID
     * @throws NoPlayerDataException when the player has no data in the database
     */
    PlayerData loadPlayerData(UUID playerUUID);

    /**
     * Check if a player's data is loaded in memory
     * @param player Player to check
     * @return true if loaded false otherwise
     */
    default boolean isPlayerDataLoaded(Player player) {
        return isPlayerDataLoaded(player.getUniqueId());
    }

    /**
     * Check if a player's data is loaded in memory
     * @param player Player to check
     * @return true if loaded false otherwise
     */
    default boolean isPlayerDataLoaded(OfflinePlayer player) {
        return isPlayerDataLoaded(player.getUniqueId());
    }

    /**
     * Check if a player's data is loaded in memory
     * @param playerUUID Player's UUID to check
     * @return true if loaded false otherwise
     */
    boolean isPlayerDataLoaded(UUID playerUUID);

    /**
     * Get a player's data from memory
     * @param player Player to get its data
     */
    default PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    /**
     * Get a player's data from memory
     * @param player Player to get its data
     */
    default PlayerData getPlayerData(OfflinePlayer player) {
        return getPlayerData(player.getUniqueId());
    }

    /**
     * Get a player's data from memory
     * @param playerUUID Player's UUID to get its data
     */
    PlayerData getPlayerData(UUID playerUUID);

    /**
     * unload a player's data from memory without saving
     * @param player The player to unload its data from memory
     */
    default void unloadPlayerData(Player player) {
        unloadPlayerData(player.getUniqueId());
    }

    /**
     * unload a player's data from memory without saving
     * @param player The player to unload its data from memory
     */
    default void unloadPlayerData(OfflinePlayer player) {
        unloadPlayerData(player.getUniqueId());
    }

    /**
     * unload a player's data from memory without saving
     * @param playerUUID The player's UUID to unload its data from memory
     */
    void unloadPlayerData(UUID playerUUID);

    /**
     * Creates the player's data in the database
     * @param player The player to create its data
     */
    default void createPlayerData(Player player) {
        createPlayerData(player.getUniqueId());
    }

    /**
     * Creates the player's data in the database
     * @param player The player to create its data
     */
    default void createPlayerData(OfflinePlayer player) {
        createPlayerData(player.getUniqueId());
    }

    /**
     * Creates the player's data in the database
     * @param playerUUID The player to create its data
     */
    void createPlayerData(UUID playerUUID);

    /**
     * Save a player's data to the database from memory
     * @param playerData The player's data to save
     */
    void savePlayerData(PlayerData playerData);

    /**
     * Save a player's data to the database from memory
     * If the player's data is not cached in memory, it won't save anything
     * @param player The player to save its data to the database
     */
    default void savePlayerData(Player player) {
        savePlayerData(player.getUniqueId());
    }

    /**
     * Save a player's data to the database from memory
     * If the player's data is not cached in memory, it won't save anything
     * @param offlinePlayer The player to save its data to the database
     */
    default void savePlayerData(OfflinePlayer offlinePlayer) {
        savePlayerData(offlinePlayer.getUniqueId());
    }

    /**
     * Save a player's data to the database from memory
     * If the player's data is not cached in memory, it won't save anything
     * @param playerUUID The player's UUID to save its data to the database
     */
    default void savePlayerData(UUID playerUUID) {
        if (isPlayerDataLoaded(playerUUID)) {
            savePlayerData(loadPlayerData(playerUUID));
        }
    }

    /**
     * @return all loaded player data
     */
    Set<PlayerData> getLoadedPlayers();

    /**
     * Check if a player is member of any island
     * @param player The player to check
     * @return true if member of any island false otherwise
     */
    default boolean hasIsland(Player player) {
        return hasIsland(player.getUniqueId());
    }

    /**
     * Check if a player is member of any island
     * @param player The player to check
     * @return true if member of any island false otherwise
     */
    default boolean hasIsland(OfflinePlayer player) {
        return hasIsland(player.getUniqueId());
    }

    /**
     * Check if a player is member of any island
     * @param playerUUID The player's UUID to check
     * @return true if member of any island false otherwise
     */
    boolean hasIsland(UUID playerUUID);

    /**
     * Get the island of a player
     * @param player The player to get its island
     * @return The island of the player if the island loaded in memory, null otherwise
     */
    default Island getPlayerIsland(Player player) {
        return getPlayerIsland(player.getUniqueId());
    }

    /**
     * Get the island of a player
     * @param player The player to get its island
     * @return The island of the player if the island loaded in memory, null otherwise
     */
    default Island getPlayerIsland(OfflinePlayer player) {
        return getPlayerIsland(player.getUniqueId());
    }

    /**
     * Get the island of a player
     * @param playerUUID The player's UUID to get its island
     * @return The island of the player if the island loaded in memory, null otherwise
     */
    Island getPlayerIsland(UUID playerUUID);

    /**
     * Check if a player's island is loaded in memory
     * @param player The player to check
     * @return true if the island is loaded false otherwise
     */
    default boolean isPlayerIslandLoaded(Player player) {
        return isPlayerIslandLoaded(player.getUniqueId());
    }

    /**
     * Check if a player's island is loaded in memory
     * @param player The player to check
     * @return true if the island is loaded false otherwise
     */
    default boolean isPlayerIslandLoaded(OfflinePlayer player) {
        return isPlayerIslandLoaded(player.getUniqueId());
    }

    /**
     * Check if a player's island is loaded in memory
     * @param playerUUID The player's UUID to check
     * @return true if the island is loaded false otherwise
     */
    boolean isPlayerIslandLoaded(UUID playerUUID);

    /**
     * Check if an island has data in the database
     * @param islandUUID The island's UUID to check
     */
    boolean hasIslandData(UUID islandUUID);

    /**
     * Load an island from the database
     * @param islandUUID The island to load by UUID
     * @throws NoIslandDataException if the island does not exist in the database
     */
    Island loadIsland(UUID islandUUID);

    /**
     * Checks if the island is cached in memory
     * @param islandUUID The island's UUID to check
     */
    boolean isIslandDataLoaded(UUID islandUUID);

    /**
     * Get an island from memory
     * @param islandUUID The island's UUID to get
     * @return The island if it is loaded in memory, null otherwise
     */
    Island getIsland(UUID islandUUID);

    /**
     * Unload an island from memory without saving
     * @param island The island to unload
     */
    default void unloadIslandData(Island island) {
        unloadIslandData(island.getIslandUUID());
    }

    /**
     * Unload an island from memory without saving
     * @param islandUUID The island's UUID to unload
     */
    void unloadIslandData(UUID islandUUID);

    /**
     * Creates the island's data in the database if it does not exist
     * @param islandUUID The island's UUID to create
     */
    void createIslandData(UUID islandUUID);

    /**
     * Save an island to the database from memory
     * @param island The island to save
     */
    default void saveIsland(Island island) {
        saveIsland(island.getIslandUUID());
    }

    /**
     * Save an island to the database from memory
     * @param islandUUID The island to save by UUID
     */
    void saveIsland(UUID islandUUID);

    /**
     * @return all loaded island data
     */
    Set<Island> getLoadedIslands();

}
