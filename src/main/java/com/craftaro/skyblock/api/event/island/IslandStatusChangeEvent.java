package com.craftaro.skyblock.api.event.island;

import com.craftaro.skyblock.api.island.Island;
import com.craftaro.skyblock.api.island.IslandStatus;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandStatusChangeEvent extends IslandEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final IslandStatus status;
    private boolean cancelled = false;

    public IslandStatusChangeEvent(Island island, IslandStatus status) {
        super(island);
        this.status = status;
    }

    public IslandStatus getStatus() {
        return this.status;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public HandlerList getHandlerList() {
        return HANDLERS;
    }
}
