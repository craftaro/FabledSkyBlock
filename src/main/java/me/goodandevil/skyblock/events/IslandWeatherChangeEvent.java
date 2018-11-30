package me.goodandevil.skyblock.events;

import org.bukkit.WeatherType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;

public class IslandWeatherChangeEvent extends Event {
	
	private Island island;
	private WeatherType weather;
	private int time;
	private boolean synchronised;
	
	public IslandWeatherChangeEvent(Island island, WeatherType weather, int time, boolean synchronised) {
		this.island = island;
		this.weather = weather;
		this.time = time;
		this.synchronised = synchronised;
	}
	
	public Island getIsland() {
		return island;
	}
	
	public WeatherType getWeather() {
		return weather;
	}
	
	public int getTime() {
		return time;
	}
	
	public boolean isSynchornised() {
		return synchronised;
	}
	
    private static final HandlerList handlers = new HandlerList();
    
	public HandlerList getHandlers() {
		return handlers;
	}
}
