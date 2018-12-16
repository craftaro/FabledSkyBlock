package me.goodandevil.skyblock.island;

public class Location {

	private IslandWorld world;
	private IslandEnvironment environment;
	private org.bukkit.Location location;

	public Location(IslandWorld world, IslandEnvironment environment, org.bukkit.Location location) {
		this.world = world;
		this.environment = environment;
		this.location = location;
	}

	public IslandWorld getWorld() {
		return world;
	}

	public IslandEnvironment getEnvironment() {
		return environment;
	}

	public org.bukkit.Location getLocation() {
		return location;
	}

	public void setLocation(org.bukkit.Location location) {
		this.location = location;
	}
}
