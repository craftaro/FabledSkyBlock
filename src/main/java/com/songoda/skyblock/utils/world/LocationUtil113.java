package com.songoda.skyblock.utils.world;

import org.bukkit.Location;

public class LocationUtil113 {
    public static void removeWaterLoggedFromLocation(Location loc) {
        if (loc.getBlock().getBlockData() instanceof org.bukkit.block.data.Waterlogged) {
            org.bukkit.block.data.Waterlogged blockData = (org.bukkit.block.data.Waterlogged) loc.getBlock().getBlockData();
            blockData.setWaterlogged(false);
            loc.getBlock().setBlockData(blockData);
        }
    }
}
