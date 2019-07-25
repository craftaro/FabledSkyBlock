package me.goodandevil.skyblock.api.event.island;

import java.util.List;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandMessage;

public class IslandMessageChangeEvent extends IslandEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled = false;
	private final IslandMessage message;
	private List<String> lines;
	private String author;

	public IslandMessageChangeEvent(Island island, IslandMessage message, List<String> lines, String author) {
		super(island);
		this.message = message;
		this.lines = lines;
		this.author = author;
	}

	public IslandMessage getMessage() {
		return message;
	}

	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
