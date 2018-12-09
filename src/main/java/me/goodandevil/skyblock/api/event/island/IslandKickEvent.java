package me.goodandevil.skyblock.api.event.island;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandRole;

public class IslandKickEvent extends IslandEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled = false;

	private final Player kicker;
	private final OfflinePlayer kicked;
	private final IslandRole role;

	public IslandKickEvent(Island island, IslandRole role, OfflinePlayer kicked, Player kicker) {
		super(island);
		this.role = role;
		this.kicked = kicked;
		this.kicker = kicker;
	}

	public OfflinePlayer getKicked() {
		return kicked;
	}

	public Player getKicker() {
		return kicker;
	}

	public IslandRole getRole() {
		return role;
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
