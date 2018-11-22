package me.goodandevil.skyblock.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;

public class IslandSwitchEvent extends Event {
	
	private Player player;
	private Island oldIsland, newIsland;
	
	public IslandSwitchEvent(Player player, Island oldIsland, Island newIsland) {
		this.player = player;
		this.oldIsland = oldIsland;
		this.newIsland = newIsland;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Island getOldIsland() {
		return oldIsland;
	}
	
	public Island getNewIsland() {
		return newIsland;
	}
	
    private static final HandlerList handlers = new HandlerList();
    
	public HandlerList getHandlers() {
		return handlers;
	}
}
