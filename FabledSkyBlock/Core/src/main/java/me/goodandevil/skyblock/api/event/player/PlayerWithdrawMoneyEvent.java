package me.goodandevil.skyblock.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerWithdrawMoneyEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	private Player player;
	private double money;

	public PlayerWithdrawMoneyEvent(Player player, double money) {
		this.player = player;
		this.money = money;
	}

	public Player getPlayer() {
		return player;
	}

	public double getMoney() {
		return money;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
