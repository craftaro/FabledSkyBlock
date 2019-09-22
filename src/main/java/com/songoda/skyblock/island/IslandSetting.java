package com.songoda.skyblock.island;

public class IslandSetting {

    private String name;
    private boolean status;

    public IslandSetting(String name, boolean status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return this.name;
    }

    public boolean getStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
