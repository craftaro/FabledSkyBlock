package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;

public class IslandOwnershipTransferEvent extends IslandEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final OfflinePlayer owner;
    private final OfflinePlayer oldOwner;

    public IslandOwnershipTransferEvent(Island island, OfflinePlayer owner, OfflinePlayer oldOwner) {
        super(island);
        this.owner = owner;
        this.oldOwner = oldOwner;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public OfflinePlayer getOldOwner() {
        return oldOwner;   
    }
    
    public OfflinePlayer getOwner() {
        return owner;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
