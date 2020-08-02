package com.songoda.skyblock.scoreboard;

import javax.annotation.Nonnull;

enum ScoreboardType {
    NOISLAND("Scoreboards.NoIsland"),
    ISLAND_SOLO_EMPTY("Scoreboards.Island.Solo.Empty"),
    ISLAND_SOLO_VISITORS("Scoreboards.Island.Solo.Occupied"),
    ISLAND_TEAM_EMPTY("Scoreboards.Island.Team.Empty"),
    ISLAND_TEAM_VISITORS("Scoreboards.Island.Team.Occupied");

    private final String configSection;

    ScoreboardType(@Nonnull String configSection) {
        this.configSection = configSection;
    }

    public @Nonnull String getConfigSection() {
        return this.configSection;
    }
}
