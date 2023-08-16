package com.craftaro.skyblock.island;

public enum IslandMessage {
    WELCOME("Welcome"),
    SIGNATURE("Signature"),
    SIGN("Sign");

    private final String friendlyName;

    IslandMessage(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }
}
