package me.goodandevil.skyblock.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class PlayerIslandChatSwitchEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean chat;

	public PlayerIslandChatSwitchEvent(Player player, Island island, boolean chat) {
		super(player, island);
		this.chat = chat;
	}

	public boolean isChat() {
		return chat;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
