package com.songoda.skyblock.levelling;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.sk89q.worldedit.bukkit.paperlib.PaperLib;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;

public class ChunkUtil {
    public final List<CompletableFuture<Chunk>> asyncPositions = new LinkedList<>();
    public final List<Chunk> syncPositions = new LinkedList<>();

    public void getChunksToScan(Island island, IslandWorld islandWorld, boolean paper) {
        Location islandLocation = island.getLocation(islandWorld, IslandEnvironment.Island);

        if (islandLocation == null) return;

        World world = islandLocation.getWorld();

        Location minLocation = new Location(world, islandLocation.getBlockX() - island.getRadius(), 0, islandLocation.getBlockZ() - island.getRadius());
        Location maxLocation = new Location(world, islandLocation.getBlockX() + island.getRadius(), world.getMaxHeight(), islandLocation.getBlockZ() + island.getRadius());

        int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX());
        int minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ());

        int maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX());
        int maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ());


        for (int x = minX; x < maxX + 16; x += 16) {
            for (int z = minZ; z < maxZ + 16; z += 16) {
                if(paper){
                    asyncPositions.add(PaperLib.getChunkAtAsync(world, x >> 4, z >> 4));
                } else {
                    syncPositions.add(world.getChunkAt(x >> 4, z >> 4));
                }
            }
        }
    }
}
