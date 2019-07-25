package me.goodandevil.skyblock.api.event.island;

import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class IslandPasswordChangeEvent extends IslandEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private String password;

	public IslandPasswordChangeEvent(Island island, String password) {
		super(island);
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
