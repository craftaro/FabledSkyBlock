package com.songoda.skyblock.levelling;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;

public class ChunkUtil {

    public static List<Chunk> getChunksToScan(Island island, IslandWorld islandWorld) {
        Location islandLocation = island.getLocation(islandWorld, IslandEnvironment.Island);

        if (islandLocation == null) return new ArrayList<>(0);

        World world = islandLocation.getWorld();

        Location minLocation = new Location(world, islandLocation.getBlockX() - island.getRadius(), 0, islandLocation.getBlockZ() - island.getRadius());
        Location maxLocation = new Location(world, islandLocation.getBlockX() + island.getRadius(), world.getMaxHeight(), islandLocation.getBlockZ() + island.getRadius());

        int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX());
        int minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ());

        int maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX());
        int maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ());

        final List<Chunk> positions = new LinkedList<>();

        for (int x = minX; x < maxX + 16; x += 16) {
            for (int z = minZ; z < maxZ + 16; z += 16) {
                positions.add(world.getChunkAt(x >> 4, z >> 4));
            }
        }

        return positions;
    }
}
