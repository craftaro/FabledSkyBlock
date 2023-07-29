package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.event.Event;

public abstract class IslandEvent extends Event {
    private final Island island;

    protected IslandEvent(Island island) {
        this.island = island;
    }

    protected IslandEvent(Island island, boolean isAsync) {
        super(isAsync);
        this.island = island;
    }

    public Island getIsland() {
        return this.island;
    }
}
