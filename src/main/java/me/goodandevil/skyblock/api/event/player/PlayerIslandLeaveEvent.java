package me.goodandevil.skyblock.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class PlayerIslandLeaveEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled = false;

	public PlayerIslandLeaveEvent(Player player, Island island) {
		super(player, island);
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
