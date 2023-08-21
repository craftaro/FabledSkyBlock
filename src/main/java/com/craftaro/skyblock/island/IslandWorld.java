package com.craftaro.skyblock.island;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.world.WorldManager;
import org.bukkit.World.Environment;

import java.util.ArrayList;
import java.util.List;

public enum IslandWorld {
    NORMAL("Normal"), NETHER("Nether"), END("End");

    private final String friendlyName;

    IslandWorld(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    public Environment getUncheckedEnvironment() {
        switch (this) {
            case NORMAL:
                return Environment.NORMAL;
            case NETHER:
                return Environment.NETHER;
            case END:
                return Environment.THE_END;
        }

        return null;
    }

    public Environment getEnvironment() {
        WorldManager worldManager = SkyBlock.getPlugin(SkyBlock.class).getWorldManager();
        if (worldManager.getWorld(NORMAL) != null) {
            return Environment.NORMAL;
        }
        if (worldManager.getWorld(NETHER) != null) {
            return Environment.NETHER;
        }
        if (worldManager.getWorld(END) != null) {
            return Environment.THE_END;
        }

        return null;
    }

    public static List<IslandWorld> getIslandWorlds() {
        List<IslandWorld> islandWorlds = new ArrayList<>(3);

        WorldManager worldManager = SkyBlock.getPlugin(SkyBlock.class).getWorldManager();
        if (worldManager.getWorld(NORMAL) != null) {
            islandWorlds.add(NORMAL);
        }
        if (worldManager.getWorld(NETHER) != null) {
            islandWorlds.add(NETHER);
        }
        if (worldManager.getWorld(END) != null) {
            islandWorlds.add(END);
        }

        return islandWorlds;
    }

    public static IslandWorld getByEnvironment(Environment environment) {
        switch (environment) {
            case NORMAL:
                return IslandWorld.NORMAL;
            case NETHER:
                return IslandWorld.NETHER;
            case THE_END:
                return IslandWorld.END;
            default:
                return null;
        }
    }
}
