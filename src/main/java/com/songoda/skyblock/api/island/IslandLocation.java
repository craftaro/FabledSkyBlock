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
        return environment;
    }

    public IslandWorld getWorld() {
        return world;
    }

    public Location getLocation() {
        return location;
    }

    public World getBukkitWorld() {
        return location.getWorld();
    }

    public int getX() {
        return location.getBlockX();
    }

    public int getY() {
        return location.getBlockY();
    }

    public int getZ() {
        return location.getBlockZ();
    }
}
