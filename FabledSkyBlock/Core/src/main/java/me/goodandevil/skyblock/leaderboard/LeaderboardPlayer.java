package me.goodandevil.skyblock.leaderboard;

import java.util.UUID;

public class LeaderboardPlayer {

	private UUID uuid;
	private long value;

	public LeaderboardPlayer(UUID uuid, long value) {
		this.uuid = uuid;
		this.value = value;
	}

	public UUID getUUID() {
		return uuid;
	}

	public long getValue() {
		return value;
	}
}
