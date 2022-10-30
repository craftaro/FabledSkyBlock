package com.songoda.skyblock.api.island;

import com.songoda.skyblock.playerdata.PlayerData;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IslandMember {

    /**
     * @return The player if the player is online, otherwise null
     */
    Player getPlayer();

    /**
     * @return The player's name
     */
    String getName();

    /**
     * @return The player's UUID
     */
    UUID getUniqueId();

    /**
     * @return The player's role on the island
     */
    IslandRole getIslandRole();

    void setRole(IslandRole role);

    /**
     * @return The player's data
     */
    PlayerData getPlayerData();

}
