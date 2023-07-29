package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandBanEvent extends IslandEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final OfflinePlayer issuer, banned;
    private boolean cancelled = false;

    public IslandBanEvent(Island island, OfflinePlayer issuer, OfflinePlayer banned) {
        super(island);
        this.issuer = issuer;
        this.banned = banned;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public OfflinePlayer getIssuer() {
        return this.issuer;
    }

    public OfflinePlayer getBanned() {
        return this.banned;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
