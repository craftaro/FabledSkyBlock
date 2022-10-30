package com.songoda.skyblock.api.upgrade;

import java.util.List;
import java.util.Set;

public interface UpgradeManager {

    void registerUpgrade(IslandUpgrade upgrade);

    void unregisterUpgrade(IslandUpgrade upgrade);

    boolean isUpgradeRegistered(String key);

    /**
     * @param key The key of the upgrade
     * @return The upgrade if the upgrade is registered, otherwise null
     */
    IslandUpgrade getUpgrade(String key);

    /**
     * @return All registered upgrades
     */
    List<IslandUpgrade> getUpgrades();

}
