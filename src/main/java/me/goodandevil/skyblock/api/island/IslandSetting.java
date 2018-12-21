package me.goodandevil.skyblock.api.island;

public class IslandSetting {

	private final me.goodandevil.skyblock.island.IslandSetting handle;

	public IslandSetting(me.goodandevil.skyblock.island.IslandSetting handle) {
		this.handle = handle;
	}

	/**
	 * @return The name of the Setting
	 */
	public String getName() {
		return this.handle.getName();
	}

	/**
	 * @param status condition for the Setting
	 */
	public void setStatus(boolean status) {
		this.handle.setStatus(status);
	}

	/**
	 * @return The status condition of the Setting
	 */
	public boolean getStatus() {
		return this.handle.getStatus();
	}
}
