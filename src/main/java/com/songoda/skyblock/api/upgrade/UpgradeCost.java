package com.songoda.skyblock.api.upgrade;

public class UpgradeCost {

    private final String type;
    private final int cost;

    public UpgradeCost(String type, int cost) {
        this.type = type;
        this.cost = cost;
    }

    public String getType() {
        return type;
    }

    public int getCost() {
        return cost;
    }

}
