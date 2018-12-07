package me.goodandevil.skyblock.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.invite.Invite;
import me.goodandevil.skyblock.island.Island;

public class IslandInviteEvent extends Event {

	private Player invited, inviter;
	private Island island;
	private Invite invite;

	public IslandInviteEvent(Player invited, Player inviter, Island island, Invite invite) {
		this.invited = invited;
		this.inviter = inviter;
		this.island = island;
		this.invite = invite;
	}

	public Player getInvited() {
		return invited;
	}

	public Player getInviter() {
		return inviter;
	}

	public Island getIsland() {
		return island;
	}

	public Invite getInvite() {
		return invite;
	}

	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}
}
