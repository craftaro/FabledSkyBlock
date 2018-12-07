package me.goodandevil.skyblock.api.level;

import me.goodandevil.skyblock.api.island.Island;

public class Level {

	private final Island handle;
	
	public Level(Island handle) {
		this.handle = handle;
	}
	
	public Island getIsland() {
		return handle;
	}
}
