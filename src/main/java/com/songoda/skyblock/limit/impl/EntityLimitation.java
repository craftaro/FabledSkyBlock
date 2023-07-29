package com.songoda.skyblock.limit.impl;

import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.limit.EnumLimitation;
import com.songoda.skyblock.limit.LimitationInstanceHandler;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public final class EntityLimitation extends EnumLimitation<EntityType> {
    private final LimitationInstanceHandler limitationInstanceHandler;

    public EntityLimitation(LimitationInstanceHandler limitationInstanceHandler) {
        super(EntityType.class);
        this.limitationInstanceHandler = limitationInstanceHandler;
    }

    public long getEntityCount(Island island, IslandWorld islandWorld, EntityType type) {
        final Location islandLocation = island.getLocation(islandWorld, IslandEnvironment.ISLAND);
        final World world = islandLocation.getWorld();

        final Location minLocation = new Location(world, islandLocation.getBlockX() - island.getRadius(), 0,
                islandLocation.getBlockZ() - island.getRadius());
        final Location maxLocation = new Location(world, islandLocation.getBlockX() + island.getRadius(), world.getMaxHeight(),
                islandLocation.getBlockZ() + island.getRadius());

        final int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX());
        final int minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ());

        final int maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX());
        final int maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ());

        int count = 0;

        for (int x = minX; x < maxX + 16; x += 16) {
            for (int z = minZ; z < maxZ + 16; z += 16) {
                if (this.limitationInstanceHandler.isLoadChunks() || world.isChunkLoaded(x >> 4, z >> 4)) {
                    final Chunk chunk = world.getChunkAt(x >> 4, z >> 4);

                    for (Entity ent : chunk.getEntities()) {
                        if (ent.getType() == type) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }

    @Override
    public String getSectionName() {
        return "entity";
    }
}
