package com.songoda.skyblock.api.island;

import com.google.common.base.Preconditions;
import com.songoda.skyblock.api.island.Island;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IslandVisitManager {

    /**
     * @return The Safe Level for the Island
     */
    public int getSafeLevel();

    /**
     * Set the Safe Level for the Island
     */
    public void setSafeLevel(int safeLevel);

    /**
     * @return true of conditions met, false otherwise
     */
    public boolean isVisitor(UUID playerUUID);

    /**
     * @return A Set of players that have visited the Island
     */
    public Set<UUID> getVisitors();

    /**
     * Add a player to the visited players for the Island
     */
    public void addVisitor(UUID playerUUID);

    /**
     * Remove a player from the visited players for the Island
     */
    public void removeVisitor(UUID playerUUID);

    /**
     * @return true of conditions met, false otherwise
     */
    public boolean isVoter(UUID playerUUID);

    /**
     * @return A Set of players that have voted for the Island
     */
    public Set<UUID> getVoters();

    /**
     * Add a player to the voted players for the Island
     */
    public void addVoter(UUID playerUUID);

    /**
     * Remove a player from the voted players for the Island
     */
    public void removeVoter(UUID playerUUID);

    /**
     * @return All players on the island from all worlds
     */
    List<Player> getPlayers();

    /**
     * @param world The world to get the players from
     * @return All players on the island
     */
    List<Player> getPlayers(IslandWorld world);

}
