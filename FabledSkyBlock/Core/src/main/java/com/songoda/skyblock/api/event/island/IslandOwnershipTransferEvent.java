package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;

public class IslandOwnershipTransferEvent extends IslandEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final OfflinePlayer owner;

    public IslandOwnershipTransferEvent(Island island, OfflinePlayer owner) {
        super(island);
        this.owner = owner;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
