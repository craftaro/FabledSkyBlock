package me.goodandevil.skyblock.island;

public class Location {

	private World world;
	private Environment environment;
	private org.bukkit.Location location;
	
	public Location(World world, Environment environment, org.bukkit.Location location) {
		this.world = world;
		this.environment = environment;
		this.location = location;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Environment getEnvironment() {
		return environment;
	}
	
	public org.bukkit.Location getLocation() {
		return location;
	}
	
	public void setLocation(org.bukkit.Location location) {
		this.location = location;
	}
	
	public enum Environment {
		Island,
		Visitor,
		Main;
	}
	
	public enum World {
		Normal,
		Nether;
	}
}
