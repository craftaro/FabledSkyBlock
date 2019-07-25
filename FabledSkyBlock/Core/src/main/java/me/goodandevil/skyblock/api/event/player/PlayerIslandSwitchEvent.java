package me.goodandevil.skyblock.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class PlayerIslandSwitchEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final Island lastIsland;

	public PlayerIslandSwitchEvent(Player player, Island lastIsland, Island island) {
		super(player, island);
		this.lastIsland = lastIsland;
	}

	public Island getLastIsland() {
		return lastIsland;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
