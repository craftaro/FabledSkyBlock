package me.goodandevil.skyblock.api.event.island;

import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.level.Level;

public class IslandLevelChangeEvent extends IslandEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final Level level;

	public IslandLevelChangeEvent(Island island, Level level) {
		super(island);
		this.level = level;
	}

	public Level getLevelData() {
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
