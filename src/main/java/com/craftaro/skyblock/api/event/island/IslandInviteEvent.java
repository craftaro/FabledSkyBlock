package com.craftaro.skyblock.api.event.island;

import com.craftaro.skyblock.api.invite.IslandInvitation;
import com.craftaro.skyblock.api.island.Island;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandInviteEvent extends IslandEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final IslandInvitation invite;

    public IslandInviteEvent(Island island, IslandInvitation invite) {
        super(island);
        this.invite = invite;
    }

    public IslandInvitation getInvite() {
        return this.invite;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
