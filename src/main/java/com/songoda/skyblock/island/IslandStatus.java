package com.songoda.skyblock.island;

import java.util.Arrays;

public enum IslandStatus {
    OPEN,
    CLOSED,
    WHITELISTED;

    public static IslandStatus getEnum(String value) {
        return Arrays.stream(values())
                .filter(status -> value.toUpperCase().equals(status.name()))
                .findFirst()
                .orElse(OPEN);
    }
}
