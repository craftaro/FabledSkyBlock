package me.goodandevil.skyblock.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;

public class IslandChatEvent extends Event {
	
	private Player player;
	private Island island;
	private String message, format;
	private boolean cancel = false;
	
	public IslandChatEvent(Player player, Island island, String message, String format) {
		this.player = player;
		this.island = island;
		this.message = message;
		this.format = format;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Island getIsland() {
		return island;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
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
