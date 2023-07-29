package com.songoda.skyblock.api.event.island;

import com.songoda.skyblock.api.island.Island;
import org.bukkit.block.Biome;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class IslandBiomeChangeEvent extends IslandEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    private Biome biome;

    public IslandBiomeChangeEvent(Island island, Biome biome) {
        super(island);
        this.biome = biome;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Biome getBiome() {
        return this.biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
