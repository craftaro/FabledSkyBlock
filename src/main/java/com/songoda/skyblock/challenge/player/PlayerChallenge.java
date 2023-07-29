package com.songoda.skyblock.challenge.player;

import com.songoda.skyblock.challenge.challenge.Challenge;

import java.util.HashMap;
import java.util.UUID;

public class PlayerChallenge {
    private final UUID uuid;
    private final HashMap<Challenge, Integer> challenges;

    public PlayerChallenge(UUID uuid, HashMap<Challenge, Integer> challenges) {
        this.uuid = uuid;
        this.challenges = challenges;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public HashMap<Challenge, Integer> getChallenges() {
        return this.challenges;
    }
}
