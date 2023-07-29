package com.songoda.skyblock.api.event.player;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerIslandSwitchEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Island lastIsland;

    public PlayerIslandSwitchEvent(Player player, Island lastIsland, Island island) {
        super(player, island);
        this.lastIsland = lastIsland;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Island getLastIsland() {
        return this.lastIsland;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
