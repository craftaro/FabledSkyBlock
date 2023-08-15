package com.craftaro.skyblock.api.event.island;

import com.craftaro.skyblock.api.island.Island;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandDeleteEvent extends IslandEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public IslandDeleteEvent(Island island) {
        super(island);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
