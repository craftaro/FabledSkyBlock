package com.songoda.skyblock.leaderboard;

import com.songoda.skyblock.visit.Visit;

public class Leaderboard {
    private final Visit visit;
    private final Type type;
    private final int position;

    public Leaderboard(Type type, Visit visit, int position) {
        this.type = type;
        this.visit = visit;
        this.position = position;
    }

    public Type getType() {
        return this.type;
    }

    public Visit getVisit() {
        return this.visit;
    }

    public int getPosition() {
        return this.position;
    }

    public enum Type {
        LEVEL, BANK, VOTES
    }
}
