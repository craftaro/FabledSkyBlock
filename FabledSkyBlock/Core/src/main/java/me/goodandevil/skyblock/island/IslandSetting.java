package me.goodandevil.skyblock.island;

public class IslandSetting {

	private String name;
	private boolean status;

	public IslandSetting(String name, boolean status) {
		this.name = name;
		this.status = status;
	}

	public String getName() {
		return this.name;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean getStatus() {
		return this.status;
	}
}
