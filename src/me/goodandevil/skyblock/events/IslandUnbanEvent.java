package me.goodandevil.skyblock.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.ban.Ban;

public class IslandUnbanEvent extends Event {
	
	private Ban ban;
	private UUID uuid;
	
	public IslandUnbanEvent(UUID uuid, Ban ban) {
		this.uuid = uuid;
		this.ban = ban;
	}
	
	public UUID getUniqueId() {
		return uuid;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public Ban getBan() {
		return ban;
	}
	
    private static final HandlerList handlers = new HandlerList();
    
	public HandlerList getHandlers() {
		return handlers;
	}
}
