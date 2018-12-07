package me.goodandevil.skyblock.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;

public class IslandChatSwitchEvent extends Event {

	private Player player;
	private Island island;
	private boolean chat;

	public IslandChatSwitchEvent(Player player, Island island, boolean chat) {
		this.player = player;
		this.island = island;
		this.chat = chat;
	}

	public Player getPlayer() {
		return player;
	}

	public Island getIsland() {
		return island;
	}

	public boolean isChat() {
		return chat;
	}

	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}
}
