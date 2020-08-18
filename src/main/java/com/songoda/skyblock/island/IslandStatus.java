package com.songoda.skyblock.island;

public enum IslandStatus {
    OPEN,
    CLOSED,
    WHITELISTED;
    
    public static IslandStatus getEnum(String value) {
        return valueOf(value.toUpperCase());
    }
}
