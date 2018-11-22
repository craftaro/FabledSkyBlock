package me.goodandevil.skyblock.events;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Role;

public class IslandKickEvent extends Event {
	
	private Player kicker;
	private UUID kicked;
	private Island island;
	private Role role;
	private boolean cancel = false;
	
	public IslandKickEvent(Island island, Role role, UUID kicked, Player kicker) {
		this.island = island;
		this.role = role;
		this.kicked = kicked;
		this.kicker = kicker;
	}
	
	public UUID getKicked() {
		return kicked;
	}
	
	public Player getKicker() {
		return kicker;
	}
	
	public Role getRole() {
		return role;
	}
	
	public Island getIsland() {
		return island;
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
