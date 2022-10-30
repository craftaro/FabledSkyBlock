package com.songoda.skyblock.api.ban;

import java.util.Set;
import java.util.UUID;

public interface IslandBanManager {

    /**
     * @return All records of bans from the island
     */
    Set<IslandBan> getBans();

    /**
     * Bans the player from the island
     */
    void banPlayer(UUID playerUUID);

    /**
     * Unbans the player from the island
     */
    void unbanPlayer(UUID playerUUID);

    /**
     * @param playerUUID The player's UUID to check
     * @return true if the player is banned from the island, false otherwise
     */
    boolean isBanned(UUID playerUUID);

}
