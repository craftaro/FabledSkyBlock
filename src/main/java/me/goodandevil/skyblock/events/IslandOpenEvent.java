package me.goodandevil.skyblock.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;

public class IslandOpenEvent extends Event {
	
	private Island island;
	private boolean open, cancel = false;
	
	public IslandOpenEvent(Island island, boolean open) {
		this.island = island;
		this.open = open;
	}
	
	public Island getIsland() {
		return island;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public boolean isCancelled() {
		return cancel;
	}
	
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
    private static final HandlerList handlers = new HandlerList();
    
	public HandlerList getHandlers() {
		return handlers;
	}
}
