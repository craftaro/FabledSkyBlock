package me.goodandevil.skyblock.api.setting;

public class Setting {

	private final me.goodandevil.skyblock.island.Setting handle;

	public Setting(me.goodandevil.skyblock.island.Setting handle) {
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
