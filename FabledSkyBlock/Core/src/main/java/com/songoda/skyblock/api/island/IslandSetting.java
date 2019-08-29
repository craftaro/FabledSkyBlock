package com.songoda.skyblock.api.island;

public class IslandSetting {

    private final com.songoda.skyblock.island.IslandSetting handle;

    public IslandSetting(com.songoda.skyblock.island.IslandSetting handle) {
        this.handle = handle;
    }

    /**
     * @return The name of the Setting
     */
    public String getName() {
        return this.handle.getName();
    }

    /**
     * @return The status condition of the Setting
     */
    public boolean getStatus() {
        return this.handle.getStatus();
    }

    /**
     * @param status condition for the Setting
     */
    public void setStatus(boolean status) {
        this.handle.setStatus(status);
    }
}
