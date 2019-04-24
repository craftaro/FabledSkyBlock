package me.goodandevil.skyblock.api.event.island;

import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandLocation;

public class IslandLocationChangeEvent extends IslandEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final IslandLocation location;

	public IslandLocationChangeEvent(Island island, IslandLocation location) {
		super(island, true);
		this.location = location;
	}

	public IslandLocation getLocation() {
		return location;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
