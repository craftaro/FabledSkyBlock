package me.goodandevil.skyblock.api.ban;

import java.util.Set;
import java.util.UUID;

import me.goodandevil.skyblock.api.island.Island;

public class Ban {

	private final Island handle;

	public Ban(Island handle) {
		this.handle = handle;
	}

	public boolean isBanned(UUID uuid) {
		return getBans().contains(uuid);
	}

	public Set<UUID> getBans() {
		return handle.getIsland().getBan().getBans();
	}

	public void addBan(UUID uuid) {
		handle.getIsland().getBan().addBan(uuid);
	}

	public void removeBan(UUID uuid) {
		handle.getIsland().getBan().removeBan(uuid);
	}

	/**
	 * @return Implementation for the Island
	 */
	public Island getIsland() {
		return handle;
	}
}
