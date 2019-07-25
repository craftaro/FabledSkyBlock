package me.goodandevil.skyblock.api.event.island;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class IslandCreateEvent extends IslandEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final Player player;

	public IslandCreateEvent(Island island, Player player) {
		super(island, true);
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
