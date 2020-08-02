package com.songoda.skyblock.scoreboard;

class ScoreboardLine {

    private final String prefix;
    private final String suffix;

    public ScoreboardLine(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
}
