package com.songoda.skyblock.api.upgrade;

import java.util.Set;

public interface IslandUpgradeManager {

    /**
     * @param key The key of the upgrade
     * @return true if the island has the specified upgrade, otherwise false
     */
    boolean hasUpgrade(String key);

    default void addUpgrade(String key) {
        addUpgrade(getUpgrade(key));
    }

    void addUpgrade(IslandUpgrade upgrade);

    /**
     * Remove an upgrade from the island if it has it
     * @param key The upgrade's key to remove
     */
    default void removeUpgrade(String key) {
        removeUpgrade(getUpgrade(key));
    }

    /**
     * @param upgrade The upgrade to remove
     */
    void removeUpgrade(IslandUpgrade upgrade);

    /**
     * @return The upgrade by its key if the island has the upgrade, otherwise null
     */
    IslandUpgrade getUpgrade(String key);

    /**
     * @return All upgrades the island has
     */
    Set<IslandUpgrade> getUpgrades();

}
