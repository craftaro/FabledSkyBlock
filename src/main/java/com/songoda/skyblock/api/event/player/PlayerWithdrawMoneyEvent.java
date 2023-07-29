package com.songoda.skyblock.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerWithdrawMoneyEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final double money;

    public PlayerWithdrawMoneyEvent(Player player, double money) {
        this.player = player;
        this.money = money;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getMoney() {
        return this.money;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
