 package me.goodandevil.skyblock.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandRole;

public class IslandRoleChangeEvent extends Event {

	private UUID uuid;
	private Island island;
	private IslandRole oldRole, newRole;
	
	public IslandRoleChangeEvent(UUID uuid, Island island, IslandRole oldRole, IslandRole newRole) {
		this.uuid = uuid;
		this.island = island;
		this.oldRole = oldRole;
		this.newRole = newRole;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	public IslandRole getOldRole() {
		return oldRole;
	}
	
	public IslandRole getNewRole() {
		return newRole;
	}
	
	public Island getIsland() {
		return island;
	}
	
    private static final HandlerList handlers = new HandlerList();
    
	public HandlerList getHandlers() {
		return handlers;
	}
}
