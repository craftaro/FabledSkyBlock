package me.goodandevil.skyblock.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Level;

public class IslandLevelChangeEvent extends Event {

	private Island island;
	private Level level;

	public IslandLevelChangeEvent(Island island, Level level) {
		this.island = island;
		this.level = level;
	}

	public Island getIsland() {
		return island;
	}

	public Level getLevel() {
		return level;
	}

	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}
}
