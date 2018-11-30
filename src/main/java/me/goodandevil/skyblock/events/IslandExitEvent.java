package me.goodandevil.skyblock.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;

public class IslandExitEvent extends Event {
	
	private Player player;
	private Island island;
	
	public IslandExitEvent(Player player, Island island) {
		this.player = player;
		this.island = island;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Island getIsland() {
		return island;
	}
	
    private static final HandlerList handlers = new HandlerList();
    
	public HandlerList getHandlers() {
		return handlers;
	}
}
