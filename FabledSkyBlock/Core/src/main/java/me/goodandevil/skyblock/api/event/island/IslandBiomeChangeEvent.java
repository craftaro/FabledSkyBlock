package me.goodandevil.skyblock.api.event.island;

import org.bukkit.block.Biome;
import org.bukkit.event.HandlerList;

import me.goodandevil.skyblock.api.island.Island;

public class IslandBiomeChangeEvent extends IslandEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private Biome biome;

	public IslandBiomeChangeEvent(Island island, Biome biome) {
		super(island);
		this.biome = biome;
	}

	public Biome getBiome() {
		return biome;
	}

	public void setBiome(Biome biome) {
		this.biome = biome;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
