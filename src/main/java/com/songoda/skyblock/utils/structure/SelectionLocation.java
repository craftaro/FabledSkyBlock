package com.songoda.skyblock.utils.structure;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SelectionLocation {
    public static Map<Entity, Location> getEntities(org.bukkit.Location originLocation, org.bukkit.Location location1, org.bukkit.Location location2) {
        Map<org.bukkit.Location, Location> locations = getLocations(originLocation, location1, location2);
        Map<Entity, Location> entities = new LinkedHashMap<>();

        for (Entity entityList : location2.getWorld().getEntities()) {
            for (org.bukkit.Location locationList : locations.keySet()) {
                if (locationList.getBlockX() == entityList.getLocation().getBlockX()
                        && locationList.getBlockY() == entityList.getLocation().getBlockY()
                        && locationList.getBlockZ() == entityList.getLocation().getBlockZ()) {
                    if (entityList instanceof Player || !(entityList instanceof LivingEntity || entityList instanceof Vehicle || entityList instanceof Hanging)) {
                        continue;
                    }

                    entities.put(entityList, locations.get(locationList));
                }
            }
        }

        return entities;
    }

    public static Map<Block, Location> getBlocks(org.bukkit.Location originLocation, org.bukkit.Location location1, org.bukkit.Location location2) {
        Map<org.bukkit.Location, Location> locations = getLocations(originLocation, location1, location2);
        Map<Block, Location> blocks = new LinkedHashMap<>();

        for (org.bukkit.Location locationList : locations.keySet()) {
            blocks.put(locationList.getBlock(), locations.get(locationList));
        }

        return blocks;
    }

    private static Map<org.bukkit.Location, Location> getLocations(org.bukkit.Location originLocation, org.bukkit.Location location1, org.bukkit.Location location2) {
        LinkedHashMap<org.bukkit.Location, Location> locations = new LinkedHashMap<>();

        int minX = Math.min(location2.getBlockX(), location1.getBlockX());
        int minY = Math.min(location2.getBlockY(), location1.getBlockY());
        int minZ = Math.min(location2.getBlockZ(), location1.getBlockZ());

        int maxX = Math.max(location2.getBlockX(), location1.getBlockX());
        int maxY = Math.max(location2.getBlockY(), location1.getBlockY());
        int maxZ = Math.max(location2.getBlockZ(), location1.getBlockZ());

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    Block block = location1.getWorld().getBlockAt(x, y, z);

                    int offsetX = x - (int) location1.getX();
                    int offsetY = y - (int) location1.getY();
                    int offsetZ = z - (int) location1.getZ();

                    boolean isOriginLocation = block.getX() == originLocation.getBlockX() &&
                            block.getY() == originLocation.getBlockY() &&
                            block.getZ() == originLocation.getBlockZ();
                    locations.put(block.getLocation(), new Location(offsetX, offsetY, offsetZ, isOriginLocation));
                }
            }
        }

        return locations;
    }
}
