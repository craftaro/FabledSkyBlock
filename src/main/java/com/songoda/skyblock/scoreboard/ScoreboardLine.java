package com.songoda.skyblock.scoreboard;

class ScoreboardLine {

    private final String prefix;
    private final String suffix;

    public ScoreboardLine(String prefix, String suffix, int limit) {
        if(prefix.length() > limit) {
            prefix = prefix.substring(0, limit);
        }
        if(suffix.length() > limit) {
            suffix = suffix.substring(0, limit);
        }
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
