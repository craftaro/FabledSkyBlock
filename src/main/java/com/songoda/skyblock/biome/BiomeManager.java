package com.songoda.skyblock.biome;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.utils.version.NMSUtil;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

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

                    if(skyblock.isPaperAsync()){
                        PaperLib.getChunkAtAsync(location.getWorld(), finalX >> 4, finalZ >> 4).thenAccept(chunk -> {
                            setChunkBiome(island, biome, chunk);
                        });
                    } else {
                        Bukkit.getScheduler().runTaskLater(skyblock, () -> {
                            Chunk chunk = location.getWorld().getChunkAt(finalX >> 4, finalZ >> 4);
                            setChunkBiome(island, biome, chunk);
                        }, i);
                        i++;
                    }
                }
            }
        });

    }

    private void setChunkBiome(Island island, Biome biome, Chunk chunk) {
        //location.getWorld().loadChunk(chunk);
        for(int xx = 0; xx < 16; xx++){
            for(int zz = 0; zz < 16; zz++){
                if(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_15)){
                    for(int y = 0; y<256; y++){
                        chunk.getBlock(xx, y, zz).setBiome(biome);
                    }
                } else {
                    chunk.getBlock(xx, 128, zz).setBiome(biome);
                }
            }
        }

        updateBiomePacket(island, chunk);
    }

    private Class<?> packetPlayOutMapChunkClass;
    private Class<?> chunkClass;

    private void updateBiomePacket(Island island, Chunk chunk) {
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
