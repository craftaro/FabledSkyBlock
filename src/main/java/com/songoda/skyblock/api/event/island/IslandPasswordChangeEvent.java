package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandPasswordChangeEvent extends IslandEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private String password;

    public IslandPasswordChangeEvent(Island island, String password) {
        super(island);
        this.password = password;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
