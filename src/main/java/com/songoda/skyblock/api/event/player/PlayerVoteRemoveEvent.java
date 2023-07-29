package com.songoda.skyblock.api.event.player;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerVoteRemoveEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public PlayerVoteRemoveEvent(Player player, Island island) {
        super(player, island);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
