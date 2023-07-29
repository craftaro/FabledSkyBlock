package com.songoda.skyblock.api.island;

import org.bukkit.Location;
import org.bukkit.World;

public class IslandLocation {
    private final IslandEnvironment environment;
    private final IslandWorld world;
    private final Location location;

    public IslandLocation(IslandEnvironment environment, IslandWorld world, Location location) {
        this.environment = environment;
        this.world = world;
        this.location = location;
    }

    public IslandEnvironment getEnvironment() {
        return this.environment;
    }

    public IslandWorld getWorld() {
        return this.world;
    }

    public Location getLocation() {
        return this.location;
    }

    public World getBukkitWorld() {
        return this.location.getWorld();
    }

    public int getX() {
        return this.location.getBlockX();
    }

    public int getY() {
        return this.location.getBlockY();
    }

    public int getZ() {
        return this.location.getBlockZ();
    }
}
