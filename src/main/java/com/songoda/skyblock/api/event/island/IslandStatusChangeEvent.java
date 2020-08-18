package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.api.island.IslandStatus;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class IslandStatusChangeEvent extends IslandEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final IslandStatus status;
    private boolean cancelled = false;

    public IslandStatusChangeEvent(Island island, IslandStatus status) {
        super(island);
        this.status = status;
    }

    public IslandStatus getStatus() {
        return status;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public HandlerList getHandlerList() {
        return HANDLERS;
    }
}
