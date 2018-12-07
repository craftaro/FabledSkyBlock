package me.goodandevil.skyblock.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;

public class IslandOwnershipTransferEvent extends Event {

	private UUID oldOwnerUUID, newOwnerUUID;
	private Island island;

	public IslandOwnershipTransferEvent(Island island, UUID oldOwnerUUID, UUID newOwnerUUID) {
		this.island = island;
		this.oldOwnerUUID = oldOwnerUUID;
		this.newOwnerUUID = newOwnerUUID;
	}

	public UUID getOldOwnerUUID() {
		return oldOwnerUUID;
	}

	public UUID getNewOwnerUUID() {
		return newOwnerUUID;
	}

	public Island getIsland() {
		return island;
	}

	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}
}
