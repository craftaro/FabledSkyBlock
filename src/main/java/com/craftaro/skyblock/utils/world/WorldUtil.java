package com.craftaro.skyblock.utils.world;

import org.bukkit.World;

public final class WorldUtil {
    private WorldUtil() {
    }

    public static int getMinHeight(World world) {
        try {
            return world.getMinHeight();
        } catch (NoSuchMethodError e) {
            return 0;
        }
    }
}
