package me.goodandevil.skyblock.island;

import org.bukkit.Location;

import me.goodandevil.skyblock.SkyBlock;

public class IslandLocation {

	private IslandWorld world;
	private IslandEnvironment environment;

	private double x;
	private double y;
	private double z;

	private float yaw;
	private float pitch;

	public IslandLocation(IslandWorld world, IslandEnvironment environment, org.bukkit.Location location) {
		this.world = world;
		this.environment = environment;

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

	public void setLocation(Location location) {
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();

		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}

	public Location getLocation() {
		return new Location(SkyBlock.getInstance().getWorldManager().getWorld(world), x, y, z, yaw, pitch);
	}
}
