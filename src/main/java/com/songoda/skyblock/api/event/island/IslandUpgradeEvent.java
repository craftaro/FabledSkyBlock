package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.api.island.IslandUpgrade;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandUpgradeEvent extends IslandEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final IslandUpgrade upgrade;

    public IslandUpgradeEvent(Island island, Player player, IslandUpgrade upgrade) {
        super(island);
        this.player = player;
        this.upgrade = upgrade;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public IslandUpgrade getUpgrade() {
        return this.upgrade;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
