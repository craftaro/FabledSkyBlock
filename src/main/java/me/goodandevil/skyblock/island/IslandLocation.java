package me.goodandevil.skyblock.island;

public class IslandLocation {

	private Location.World world;

	private double x;
	private double z;

	public IslandLocation(Location.World world, double x, double z) {
		this.world = world;
		this.x = x;
		this.z = z;
	}

	public Location.World getWorld() {
		return world;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
}
