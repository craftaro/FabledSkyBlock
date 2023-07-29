package com.songoda.skyblock.api.event.player;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerIslandChatSwitchEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private final boolean chat;

    public PlayerIslandChatSwitchEvent(Player player, Island island, boolean chat) {
        super(player, island);
        this.chat = chat;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public boolean isChat() {
        return this.chat;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
