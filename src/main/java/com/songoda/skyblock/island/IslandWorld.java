package com.songoda.skyblock.island;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.World.Environment;

import java.util.ArrayList;
import java.util.List;

public enum IslandWorld {

    Normal, Nether, End;

    public static List<IslandWorld> getIslandWorlds() {
        List<IslandWorld> islandWorlds = new ArrayList<>();

        WorldManager worldManager = SkyBlock.getInstance().getWorldManager();
        if (worldManager.getWorld(Normal) != null)
            islandWorlds.add(Normal);

        if (worldManager.getWorld(Nether) != null)
            islandWorlds.add(Nether);

        if (worldManager.getWorld(End) != null)
            islandWorlds.add(End);

        return islandWorlds;
    }

    public Environment getUncheckedEnvironment() {
        switch (this) {
            case Normal:
                return Environment.NORMAL;
            case Nether:
                return Environment.NETHER;
            case End:
                return Environment.THE_END;
        }

        return null;
    }

    public Environment getEnvironment() {
        WorldManager worldManager = SkyBlock.getInstance().getWorldManager();
        if (worldManager.getWorld(Normal) != null)
            return Environment.NORMAL;

        if (worldManager.getWorld(Nether) != null)
            return Environment.NETHER;

        if (worldManager.getWorld(End) != null)
            return Environment.THE_END;

        return null;
    }
}
