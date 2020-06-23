package com.songoda.skyblock.biome;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.blockscanner.ChunkLoader;
import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.*;
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

        if (location == null) return;

        if(skyblock.isPaperAsync()){
            // We keep it sequentially in order to use less RAM
            ChunkLoader.startChunkLoadingPerChunk(island, IslandWorld.Normal, skyblock.isPaperAsync(), (asyncChunk, syncChunk) -> {
                Chunk chunk = asyncChunk.join();
                setChunkBiome(biome, chunk);
                updateBiomePacket(island, chunk);
            });
        } else {
            ChunkLoader.startChunkLoading(island, IslandWorld.Normal, skyblock.isPaperAsync(), (asyncChunks, syncChunks) -> {
                Bukkit.getScheduler().runTaskAsynchronously(skyblock, () -> {
                    syncChunks.forEach(chunk -> {
                        setChunkBiome(biome, chunk);
                        updateBiomePacket(island, chunk);
                    });
                });
            });
        }
    }

    private void setChunkBiome(Biome biome, Chunk chunk) {
        for(int xx = 0; xx < 16; xx++){
            for(int zz = 0; zz < 16; zz++){
                chunk.getBlock(xx, 0, zz).setBiome(biome);
            }
        }
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
