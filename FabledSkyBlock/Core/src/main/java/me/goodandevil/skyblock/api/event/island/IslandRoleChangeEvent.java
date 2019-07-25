package me.goodandevil.skyblock.api.event.island;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandRole;

public class IslandRoleChangeEvent extends IslandEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final OfflinePlayer player;
	private final IslandRole role;

	public IslandRoleChangeEvent(Island island, OfflinePlayer player, IslandRole role) {
		super(island);
		this.player = player;
		this.role = role;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public IslandRole getRole() {
		return role;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
