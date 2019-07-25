package me.goodandevil.skyblock.api.event.island;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class IslandUnbanEvent extends IslandEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final OfflinePlayer unbanned;

	public IslandUnbanEvent(Island island, OfflinePlayer unbanned) {
		super(island);
		this.unbanned = unbanned;
	}

	public OfflinePlayer getUnbanned() {
		return unbanned;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
