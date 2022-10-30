package com.songoda.skyblock.api.upgrade;

public interface IslandUpgrade {

    /**
     * Name of the upgrade. Used for registering and getting the upgrade.
     * @return The upgrade's name
     */
    String getKey();

    /**
     * The upgrade's display name. Used for displaying the upgrade's name.
     * @return The upgrade's display name
     */
    String getDisplayName();

    /**
     * The upgrade's description. Used for displaying the upgrade's description.
     * @return The upgrade's description
     */
    String getDescription();

    /**
     * Add a level to the upgrade if the upgrade is not maxed out
     */
    void addLevel();

    /**
     * @return The level of the upgrade
     */
    int getLevel();

    /**
     * Set the level of the upgrade. Can't be lower than 0 and higher than the max level.
     */
    void setLevel(int level);

    /**
     * @return Max level of the upgrade
     */
    int getMaxLevel();

    /**
     * @return true if the upgrade is its max level, otherwise false
     */
    boolean isMaxLevel();

    /**
     * The upgrade's cost. Used for displaying the upgrade's cost.
     * @return The upgrade's cost
     */
    UpgradeCost getCost();

}
