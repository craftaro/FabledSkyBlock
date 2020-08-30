package com.songoda.skyblock.leaderboard;

import java.util.UUID;

public class LeaderboardPlayer {

    private final UUID uuid;
    private final long value;

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
