package me.goodandevil.skyblock.api.event.island;

import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandLevel;

public class IslandLevelChangeEvent extends IslandEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final IslandLevel level;

	public IslandLevelChangeEvent(Island island, IslandLevel level) {
		super(island, true);
		this.level = level;
	}

	public IslandLevel getLevel() {
		return level;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
