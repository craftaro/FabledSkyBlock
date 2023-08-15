package com.craftaro.skyblock.api.visit;

import com.craftaro.skyblock.api.island.Island;
import com.google.common.base.Preconditions;

import java.util.Set;
import java.util.UUID;

public class Visit {
    private final Island handle;

    public Visit(Island handle) {
        this.handle = handle;
    }

    /**
     * @return The Safe Level for the Island
     */
    public int getSafeLevel() {
        return this.handle.getIsland().getVisit().getSafeLevel();
    }

    /**
     * Set the Safe Level for the Island
     */
    public void setSafeLevel(int safeLevel) {
        this.handle.getIsland().getVisit().setSafeLevel(safeLevel);
    }

    /**
     * @return true of conditions met, false otherwise
     */
    public boolean isVisitor(UUID uuid) {
        Preconditions.checkArgument(uuid != null, "Cannot return condition to null uuid");
        return getVisitors().contains(uuid);
    }

    /**
     * @return A Set of players that have visited the Island
     */
    public Set<UUID> getVisitors() {
        return this.handle.getIsland().getVisit().getVisitors();
    }

    /**
     * Add a player to the visited players for the Island
     */
    public void addVisitor(UUID uuid) {
        Preconditions.checkArgument(uuid != null, "Cannot add visitor to null uuid");
        this.handle.getIsland().getVisit().addVisitor(uuid);
    }

    /**
     * Remove a player from the visited players for the Island
     */
    public void removeVisitor(UUID uuid) {
        Preconditions.checkArgument(uuid != null, "Cannot remove visitor to null uuid");
        this.handle.getIsland().getVisit().removeVisitor(uuid);
    }

    /**
     * @return true of conditions met, false otherwise
     */
    public boolean isVoter(UUID uuid) {
        Preconditions.checkArgument(uuid != null, "Cannot return condition to null uuid");
        return getVoters().contains(uuid);
    }

    /**
     * @return A Set of players that have voted for the Island
     */
    public Set<UUID> getVoters() {
        return this.handle.getIsland().getVisit().getVoters();
    }

    /**
     * Add a player to the voted players for the Island
     */
    public void addVoter(UUID uuid) {
        Preconditions.checkArgument(uuid != null, "Cannot add voter to null uuid");
        this.handle.getIsland().getVisit().addVoter(uuid);
    }

    /**
     * Remove a player from the voted players for the Island
     */
    public void removeVoter(UUID uuid) {
        Preconditions.checkArgument(uuid != null, "Cannot remove voter to null uuid");
        this.handle.getIsland().getVisit().removeVoter(uuid);
    }

    /**
     * @return Implementation for the Island
     */
    public Island getIsland() {
        return this.handle;
    }
}
