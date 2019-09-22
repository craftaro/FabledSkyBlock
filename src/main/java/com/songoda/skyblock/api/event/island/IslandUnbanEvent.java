package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;

public class IslandUnbanEvent extends IslandEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final OfflinePlayer unbanned;

    public IslandUnbanEvent(Island island, OfflinePlayer unbanned) {
        super(island);
        this.unbanned = unbanned;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public OfflinePlayer getUnbanned() {
        return unbanned;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
