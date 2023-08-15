package com.craftaro.skyblock.api.event.player;

import com.craftaro.skyblock.api.island.Island;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class PlayerEvent extends Event {
    private final Player player;
    private final Island island;

    protected PlayerEvent(Player player, Island island) {
        this.player = player;
        this.island = island;
    }

    protected PlayerEvent(Player player, Island island, boolean isAsync) {
        super(isAsync);
        this.player = player;
        this.island = island;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Island getIsland() {
        return this.island;
    }
}
