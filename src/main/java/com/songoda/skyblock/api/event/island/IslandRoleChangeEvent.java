package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.api.island.IslandRole;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandRoleChangeEvent extends IslandEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final OfflinePlayer player;
    private final IslandRole role;

    public IslandRoleChangeEvent(Island island, OfflinePlayer player, IslandRole role) {
        super(island);
        this.player = player;
        this.role = role;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public OfflinePlayer getPlayer() {
        return this.player;
    }

    public IslandRole getRole() {
        return this.role;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
