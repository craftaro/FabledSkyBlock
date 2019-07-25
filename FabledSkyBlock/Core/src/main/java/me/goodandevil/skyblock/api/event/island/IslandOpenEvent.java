package me.goodandevil.skyblock.api.event.island;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class IslandOpenEvent extends IslandEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled = false;
	private final boolean open;

	public IslandOpenEvent(Island island, boolean open) {
		super(island);
		this.open = open;
	}

	public boolean isOpen() {
		return open;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public HandlerList getHandlerList() {
		return HANDLERS;
	}
}
