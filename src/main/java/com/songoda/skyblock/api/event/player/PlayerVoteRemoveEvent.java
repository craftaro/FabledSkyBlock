package com.songoda.skyblock.api.event.player;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerVoteRemoveEvent extends PlayerEvent {
    private static HandlerList HANDLERS = new HandlerList();

    public PlayerVoteRemoveEvent(Player player, Island island) {
        super(player, island);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}