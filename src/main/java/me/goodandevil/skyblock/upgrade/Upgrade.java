package me.goodandevil.skyblock.upgrade;

public class Upgrade {

	private double cost;
	private int value;
	private boolean enabled = true;

	public Upgrade(double cost) {
		this.cost = cost;
	}

	public Upgrade(double cost, int value) {
		this.cost = cost;
		this.value = value;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public enum Type {

		Crop, Spawner, Fly, Drops, Size, Speed, Jump;

	}
}
