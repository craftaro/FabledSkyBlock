package com.songoda.skyblock.island;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.Location;

public class IslandLocation {
    private final IslandWorld world;
    private final IslandEnvironment environment;

    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    public IslandLocation(IslandWorld world, IslandEnvironment environment, org.bukkit.Location location) {
        this.world = world;
        this.environment = environment;

        if (location == null) {
            return;
        }

        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();

        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public IslandWorld getWorld() {
        return this.world;
    }

    public IslandEnvironment getEnvironment() {
        return this.environment;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Location getLocation() {
        return new Location(SkyBlock.getPlugin(SkyBlock.class).getWorldManager().getWorld(this.world), this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public void setLocation(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();

        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }
}
