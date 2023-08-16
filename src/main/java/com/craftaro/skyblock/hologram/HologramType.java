package com.craftaro.skyblock.hologram;

public enum HologramType {
    LEVEL("Level"),
    BANK("Bank"),
    VOTES("Votes");

    private final String friendlyName;

    HologramType(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }
}
