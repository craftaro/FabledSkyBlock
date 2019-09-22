package com.songoda.skyblock.api.ban;

import com.google.common.base.Preconditions;
import com.songoda.skyblock.api.island.Island;

import java.util.Set;
import java.util.UUID;

public class Ban {

    private final Island handle;

    public Ban(Island handle) {
        this.handle = handle;
    }

    /**
     * @return true of conditions met, false otherwise
     */
    public boolean isBanned(UUID uuid) {
        Preconditions.checkArgument(uuid != null, "Cannot return condition to null uuid");
        return getBans().contains(uuid);
    }

    /**
     * @return A Set of players that have banned from the Island
     */
    public Set<UUID> getBans() {
        return handle.getIsland().getBan().getBans();
    }

    /**
     * Add a player to the banned players for the Island
     */
    public void addBan(UUID issuer, UUID banned) {
        Preconditions.checkArgument(banned != null, "Cannot add ban to null banned uuid");
        handle.getIsland().getBan().addBan(issuer, banned);
    }

    /**
     * Remove a player from the banned players for the Island
     */
    public void removeBan(UUID uuid) {
        Preconditions.checkArgument(uuid != null, "Cannot remove ban to null uuid");
        handle.getIsland().getBan().removeBan(uuid);
    }

    /**
     * @return Implementation for the Island
     */
    public Island getIsland() {
        return handle;
    }
}
