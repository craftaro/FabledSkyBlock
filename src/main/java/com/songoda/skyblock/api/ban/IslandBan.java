package com.songoda.skyblock.api.ban;

import java.util.UUID;

public interface IslandBan {

    /**
     * @return The player's name who was banned
     */
    String getPlayerName();

    /**
     * @return The player's UUID who was banned
     */
    UUID getPlayerUUID();

    /**
     * @return The player's name who banned the player
     */
    String getBannedBy();

    /**
     * @return The player's UUID who banned the player
     */
    UUID getBannedByUUID();

    /**
     * @return The reason for the ban
     */
    String getReason();

    /**
     * @return The timestamp when the player was banned
     */
    long getTime();

}
