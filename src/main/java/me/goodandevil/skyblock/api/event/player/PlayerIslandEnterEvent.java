package me.goodandevil.skyblock.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class PlayerIslandEnterEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public PlayerIslandEnterEvent(Player player, Island island) {
		super(player, island);
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
