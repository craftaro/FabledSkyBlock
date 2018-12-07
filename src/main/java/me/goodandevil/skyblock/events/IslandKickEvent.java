package me.goodandevil.skyblock.events;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandRole;

public class IslandKickEvent extends Event {
	
	private Player kicker;
	private UUID kicked;
	private Island island;
	private IslandRole role;
	private boolean cancel = false;
	
	public IslandKickEvent(Island island, IslandRole role, UUID kicked, Player kicker) {
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
	
	public IslandRole getRole() {
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
