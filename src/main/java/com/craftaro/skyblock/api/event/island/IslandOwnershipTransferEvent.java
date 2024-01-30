package com.craftaro.skyblock.api.event.island;

import com.craftaro.skyblock.api.island.Island;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class IslandOwnershipTransferEvent extends IslandEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final OfflinePlayer owner;
    private final UUID previousOwnerId;

    public IslandOwnershipTransferEvent(Island island, OfflinePlayer owner, UUID previousOwnerId) {
        super(island);
        this.owner = owner;
        this.previousOwnerId = previousOwnerId;
    }

    public OfflinePlayer getOwner() {
        return this.owner;
    }

    public UUID getPreviousOwnerId() {
        return this.previousOwnerId;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
