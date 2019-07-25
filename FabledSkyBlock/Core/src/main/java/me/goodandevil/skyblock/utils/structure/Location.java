package me.goodandevil.skyblock.utils.structure;

public class Location {

	private int x;
	private int y;
	private int z;

	private boolean originLocation;

	public Location(int x, int y, int z, boolean originLocation) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.originLocation = originLocation;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public boolean isOriginLocation() {
		return originLocation;
	}
}
