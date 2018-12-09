package me.goodandevil.skyblock.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import me.goodandevil.skyblock.api.island.Island;

public abstract class PlayerEvent extends Event {

	private final Player player;
	private final Island island;

	protected PlayerEvent(Player player, Island island) {
		this.player = player;
		this.island = island;
	}

	public Player getPlayer() {
		return player;
	}

	public Island getIsland() {
		return island;
	}
}
