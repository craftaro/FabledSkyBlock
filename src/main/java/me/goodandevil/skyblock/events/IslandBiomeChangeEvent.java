package me.goodandevil.skyblock.events;

import org.bukkit.block.Biome;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.island.Island;

public class IslandBiomeChangeEvent extends Event {
	
	private Biome oldBiome, newBiome;
	private Island island;
	
	public IslandBiomeChangeEvent(Island island, Biome oldBiome, Biome newBiome) {
		this.island = island;
		this.oldBiome = oldBiome;
		this.newBiome = newBiome;
	}
	
	public Biome getOldBiome() {
		return oldBiome;
	}
	
	public Biome getNewBiome() {
		return newBiome;
	}
	
	public Island getIsland() {
		return island;
	}
	
    private static final HandlerList handlers = new HandlerList();
    
	public HandlerList getHandlers() {
		return handlers;
	}
}
