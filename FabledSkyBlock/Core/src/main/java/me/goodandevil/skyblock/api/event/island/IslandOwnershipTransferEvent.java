package me.goodandevil.skyblock.api.event.island;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class IslandOwnershipTransferEvent extends IslandEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final OfflinePlayer owner;

	public IslandOwnershipTransferEvent(Island island, OfflinePlayer owner) {
		super(island);
		this.owner = owner;
	}

	public OfflinePlayer getOwner() {
		return owner;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
