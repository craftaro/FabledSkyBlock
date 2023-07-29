package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.api.island.IslandLocation;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandLocationChangeEvent extends IslandEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final IslandLocation location;

    public IslandLocationChangeEvent(Island island, IslandLocation location) {
        super(island, true);
        this.location = location;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public IslandLocation getLocation() {
        return this.location;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
