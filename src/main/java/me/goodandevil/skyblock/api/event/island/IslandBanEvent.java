package me.goodandevil.skyblock.api.event.island;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class IslandBanEvent extends IslandEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled = false;
	private final OfflinePlayer issuer, banned;

	public IslandBanEvent(Island island, OfflinePlayer issuer, OfflinePlayer banned) {
		super(island);
		this.issuer = issuer;
		this.banned = banned;
	}

	public OfflinePlayer getIssuer() {
		return issuer;
	}

	public OfflinePlayer getBanned() {
		return banned;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
