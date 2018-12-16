package me.goodandevil.skyblock.island;

public class IslandLocation {

	private IslandWorld world;

	private double x;
	private double z;

	public IslandLocation(IslandWorld world, double x, double z) {
		this.world = world;
		this.x = x;
		this.z = z;
	}

	public IslandWorld getWorld() {
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
