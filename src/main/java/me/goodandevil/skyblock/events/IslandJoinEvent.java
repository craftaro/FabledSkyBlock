package me.goodandevil.skyblock.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;

public class IslandJoinEvent extends Event {
	
	private Player player;
	private Island island;
	private boolean cancel = false;
	
	public IslandJoinEvent(Player player, Island island) {
		this.player = player;
		this.island = island;
	}
	
	public Player getPlayer() {
		return player;
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
