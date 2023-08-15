package com.craftaro.skyblock.api.event.island;

import com.craftaro.skyblock.api.island.Island;
import com.craftaro.skyblock.api.island.IslandLevel;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandLevelChangeEvent extends IslandEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final IslandLevel level;

    public IslandLevelChangeEvent(Island island, IslandLevel level) {
        super(island);
        this.level = level;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public IslandLevel getLevel() {
        return this.level;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
