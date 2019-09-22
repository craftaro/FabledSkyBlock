package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.event.HandlerList;

public class IslandUnloadEvent extends IslandEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public IslandUnloadEvent(Island island) {
        super(island);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
