package me.goodandevil.skyblock.island;

public class IslandPosition {

	private IslandWorld world;

	private double x;
	private double z;

	public IslandPosition(IslandWorld world, double x, double z) {
		this.world = world;
		this.x = x;
		this.z = z;
	}

	public IslandWorld getWorld() {
		return this.world;
	}

	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getZ() {
		return this.z;
	}

	public void setZ(double z) {
		this.z = z;
	}
}
