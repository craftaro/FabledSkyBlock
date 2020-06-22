package com.songoda.skyblock.biome;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class BiomeManager {

    private final SkyBlock skyblock;

    public BiomeManager(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    public void setBiome(Island island, Biome biome) {
        Location location = island.getLocation(IslandWorld.Normal, IslandEnvironment.Island);
        int radius = (int) Math.ceil(island.getRadius());

        /*Bukkit.getScheduler().runTaskAsynchronously(skyblock, () -> {
            for (int x = location.getBlockX() - radius; x < location.getBlockX() + radius; x++) {
                for (int z = location.getBlockZ() - radius; z < location.getBlockZ() + radius; z++) {
                    location.getWorld().setBiome(x, z, biome);
                }
            }
            Bukkit.getScheduler().runTask(skyblock, () -> {
                for (int x = location.getBlockX() - radius; x < location.getBlockX() + radius; x += 16) {
                    for (int z = location.getBlockZ() - radius; z < location.getBlockZ() + radius; z += 16) {
                        Chunk chunk = location.getWorld().getChunkAt(x >> 4, z >> 4);
                        updateBiome(island, chunk);
                    }
                }
            });
        });*/

        Bukkit.getScheduler().runTaskAsynchronously(skyblock, () -> {
            long i = 0;
            for (int x = location.getBlockX() - radius; x < location.getBlockX() + radius; x += 16) {
                for (int z = location.getBlockZ() - radius; z < location.getBlockZ() + radius; z += 16) {
                    int finalX = x;
                    int finalZ = z;
                    Bukkit.getScheduler().runTaskLater(skyblock, () -> {
                        Chunk chunk = location.getWorld().getChunkAt(finalX >> 4, finalZ >> 4);
                        location.getWorld().loadChunk(chunk);
                        for(int xx = 0; xx < 16; xx++){
                            for(int zz = 0; zz < 16; zz++){
                                chunk.getBlock(xx, 0, zz).setBiome(biome);
                            }
                        }
                        updateBiome(island, chunk);
                    }, i);
                    i++;
                }
            }
        });

    }

    private Class<?> packetPlayOutMapChunkClass;
    private Class<?> chunkClass;

    private void updateBiome(Island island, Chunk chunk) {
        if (packetPlayOutMapChunkClass == null) {
            packetPlayOutMapChunkClass = NMSUtil.getNMSClass("PacketPlayOutMapChunk");
            chunkClass = NMSUtil.getNMSClass("Chunk");
        }

        for (Player all : skyblock.getIslandManager().getPlayersAtIsland(island, IslandWorld.Normal)) {
            try {
                if (NMSUtil.getVersionNumber() < 9) {
                    NMSUtil.sendPacket(all,
                            packetPlayOutMapChunkClass.getConstructor(chunkClass, boolean.class, int.class)
                                    .newInstance(all.getLocation().getChunk().getClass().getMethod("getHandle")
                                            .invoke(chunk), true, 20));
                    return;
                }
                NMSUtil.sendPacket(all,
                        packetPlayOutMapChunkClass.getConstructor(chunkClass, int.class).newInstance(all
                                        .getLocation().getChunk().getClass().getMethod("getHandle").invoke(chunk),
                                65535));
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }
}
