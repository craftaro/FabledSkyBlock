package me.goodandevil.skyblock.api.event.island;

import org.bukkit.WeatherType;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class IslandWeatherChangeEvent extends IslandEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private WeatherType weather;
	private int time;

	private final boolean sync;

	public IslandWeatherChangeEvent(Island island, WeatherType weather, int time, boolean sync) {
		super(island);
		this.weather = weather;
		this.time = time;
		this.sync = sync;
	}

	public WeatherType getWeather() {
		return weather;
	}

	public int getTime() {
		return time;
	}

	public boolean isSync() {
		return sync;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
