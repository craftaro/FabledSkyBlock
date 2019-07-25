package me.goodandevil.skyblock.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class PlayerIslandJoinEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled = false;

	public PlayerIslandJoinEvent(Player player, Island island) {
		super(player, island);
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
