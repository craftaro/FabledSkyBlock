package me.goodandevil.skyblock.leaderboard;

import java.util.UUID;

public class LeaderboardPlayer {

	private UUID uuid;
	private int value;

	public LeaderboardPlayer(UUID uuid, int value) {
		this.uuid = uuid;
		this.value = value;
	}

	public UUID getUUID() {
		return uuid;
	}

	public int getValue() {
		return value;
	}
}
