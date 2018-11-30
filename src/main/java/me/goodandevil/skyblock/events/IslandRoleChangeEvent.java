 package me.goodandevil.skyblock.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Role;

public class IslandRoleChangeEvent extends Event {

	private UUID uuid;
	private Island island;
	private Role oldRole, newRole;
	
	public IslandRoleChangeEvent(UUID uuid, Island island, Role oldRole, Role newRole) {
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
	
	public Role getOldRole() {
		return oldRole;
	}
	
	public Role getNewRole() {
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
