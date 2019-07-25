package me.goodandevil.skyblock.api.levelling;

import com.google.common.base.Preconditions;

import me.goodandevil.skyblock.api.island.Island;

public class LevellingManager {

	private final me.goodandevil.skyblock.levelling.LevellingManager levellingManager;

	public LevellingManager(me.goodandevil.skyblock.levelling.LevellingManager levellingManager) {
		this.levellingManager = levellingManager;
	}

	/**
	 * Calculates the points of an Island to determine what the Island level is
	 */
	public void calculatePoints(Island island) {
		Preconditions.checkArgument(island != null, "Cannot calculate points to null island");
		this.levellingManager.calculatePoints(null, island.getIsland());
	}
}
