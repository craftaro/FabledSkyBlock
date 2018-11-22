package me.goodandevil.skyblock.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Location;

public class IslandLocationChangeEvent extends Event {
	
	private Location oldLocation, newLocation;
	private Island island;
	
	public IslandLocationChangeEvent(Island island, Location oldLocation, Location newLocation) {
		this.island = island;
		this.oldLocation = oldLocation;
		this.newLocation = newLocation;
	}
	
	public Location getOldLocation() {
		return oldLocation;
	}
	
	public Location getNewLocation() {
		return newLocation;
	}
	
	public Island getIsland() {
		return island;
	}
	
    private static final HandlerList handlers = new HandlerList();
    
	public HandlerList getHandlers() {
		return handlers;
	}
}
