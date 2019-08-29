package com.songoda.skyblock.utils.structure;

import org.bukkit.block.Block;
import org.bukkit.entity.*;

import java.util.LinkedHashMap;

public final class SelectionLocation {

    public static LinkedHashMap<Entity, Location> getEntities(org.bukkit.Location originLocation,
                                                              org.bukkit.Location location1, org.bukkit.Location location2) throws Exception {
        LinkedHashMap<org.bukkit.Location, Location> locations = getLocations(originLocation, location1, location2);
        LinkedHashMap<Entity, Location> entities = new LinkedHashMap<>();

        for (Entity entityList : location2.getWorld().getEntities()) {
            for (org.bukkit.Location locationList : locations.keySet()) {
                if (locationList.getBlockX() == entityList.getLocation().getBlockX()
                        && locationList.getBlockY() == entityList.getLocation().getBlockY()
                        && locationList.getBlockZ() == entityList.getLocation().getBlockZ()) {
                    if (entityList instanceof Player || !(entityList instanceof LivingEntity
                            || entityList instanceof Vehicle || entityList instanceof Hanging)) {
                        continue;
                    }

                    entities.put(entityList, locations.get(locationList));
                }
            }
        }

        return entities;
    }

    public static LinkedHashMap<Block, Location> getBlocks(org.bukkit.Location originLocation,
                                                           org.bukkit.Location location1, org.bukkit.Location location2) throws Exception {
        LinkedHashMap<org.bukkit.Location, Location> locations = getLocations(originLocation, location1, location2);
        LinkedHashMap<Block, Location> blocks = new LinkedHashMap<>();

        for (org.bukkit.Location locationList : locations.keySet()) {
            blocks.put(locationList.getBlock(), locations.get(locationList));
        }

        return blocks;
    }

    private static LinkedHashMap<org.bukkit.Location, Location> getLocations(org.bukkit.Location originLocation,
                                                                             org.bukkit.Location location1, org.bukkit.Location location2) throws Exception {
        LinkedHashMap<org.bukkit.Location, Location> locations = new LinkedHashMap<>();

        int MinX = Math.min(location2.getBlockX(), location1.getBlockX());
        int MinY = Math.min(location2.getBlockY(), location1.getBlockY());
        int MinZ = Math.min(location2.getBlockZ(), location1.getBlockZ());

        int MaxX = Math.max(location2.getBlockX(), location1.getBlockX());
        int MaxY = Math.max(location2.getBlockY(), location1.getBlockY());
        int MaxZ = Math.max(location2.getBlockZ(), location1.getBlockZ());

        for (int x = MinX; x <= MaxX; x++) {
            for (int y = MinY; y <= MaxY; y++) {
                for (int z = MinZ; z <= MaxZ; z++) {
                    Block block = location1.getWorld().getBlockAt(x, y, z);

                    int offsetX = x - (int) location1.getX();
                    int offsetY = y - (int) location1.getY();
                    int offsetZ = z - (int) location1.getZ();

                    boolean isOriginLocation = false;

                    if (block.getX() == originLocation.getBlockX() && block.getY() == originLocation.getBlockY()
                            && block.getZ() == originLocation.getBlockZ()) {
                        isOriginLocation = true;
                    }

                    locations.put(block.getLocation(), new Location(offsetX, offsetY, offsetZ, isOriginLocation));
                }
            }
        }

        return locations;
    }
}
