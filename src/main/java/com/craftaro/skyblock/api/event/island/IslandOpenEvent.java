package com.craftaro.skyblock.api.event.island;

import com.craftaro.skyblock.api.island.Island;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

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
        return this.open;
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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
