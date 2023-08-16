package com.craftaro.skyblock.island;

public enum IslandEnvironment {
    ISLAND("Island"),
    VISITOR("Visitor"),
    MAIN("Main");

    private final String friendlyName;

    IslandEnvironment(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }
}
