package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.WeatherType;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandWeatherChangeEvent extends IslandEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final boolean sync;
    private final WeatherType weather;
    private final int time;

    public IslandWeatherChangeEvent(Island island, WeatherType weather, int time, boolean sync) {
        super(island);
        this.weather = weather;
        this.time = time;
        this.sync = sync;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public WeatherType getWeather() {
        return this.weather;
    }

    public int getTime() {
        return this.time;
    }

    public boolean isSync() {
        return this.sync;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
