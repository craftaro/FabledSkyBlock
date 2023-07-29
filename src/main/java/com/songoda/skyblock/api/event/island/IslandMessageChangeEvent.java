package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.api.island.IslandMessage;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IslandMessageChangeEvent extends IslandEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final IslandMessage message;
    private boolean cancelled = false;
    private List<String> lines;
    private String author;

    public IslandMessageChangeEvent(Island island, IslandMessage message, List<String> lines, String author) {
        super(island);
        this.message = message;
        this.lines = lines;
        this.author = author;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public IslandMessage getMessage() {
        return this.message;
    }

    public List<String> getLines() {
        return this.lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
