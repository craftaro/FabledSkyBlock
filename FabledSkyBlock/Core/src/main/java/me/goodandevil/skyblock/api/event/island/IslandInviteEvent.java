package me.goodandevil.skyblock.api.event.island;

import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.invite.IslandInvitation;
import me.goodandevil.skyblock.api.island.Island;

public class IslandInviteEvent extends IslandEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final IslandInvitation invite;

	public IslandInviteEvent(Island island, IslandInvitation invite) {
		super(island);
		this.invite = invite;
	}

	public IslandInvitation getInvite() {
		return invite;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public HandlerList getHandlerList() {
		return HANDLERS;
	}
}
