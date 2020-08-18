package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@Deprecated
public class IslandOpenEvent extends IslandEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final boolean open;
    private boolean cancelled = false;
    
    @Deprecated
    public IslandOpenEvent(Island island, boolean open) {
        super(island);
        this.open = open;
    }
    
    @Deprecated
    public boolean isOpen() {
        return open;
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
