package me.goodandevil.skyblock.api.event.island;

import me.goodandevil.skyblock.api.island.Island;

import org.bukkit.event.Event;

public abstract class IslandEvent extends Event {

	private final Island island;

	protected IslandEvent(Island island) {
		this.island = island;
	}

	public Island getIsland() {
		return island;
	}
}