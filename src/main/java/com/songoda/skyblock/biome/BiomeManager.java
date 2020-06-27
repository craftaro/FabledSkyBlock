package com.songoda.skyblock.biome;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.blockscanner.ChunkLoader;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class BiomeManager {

    private final SkyBlock skyblock;
    private final List<Island> updatingIslands;
    private final List<ExecutorService> pools;

    public BiomeManager(SkyBlock skyblock) {
        this.skyblock = skyblock;
        this.updatingIslands = new ArrayList<>();
        this.pools = new ArrayList<>();
    }
    
    public boolean isUpdating(Island island) {
        return updatingIslands.contains(island);
    }
    
    public void addUpdatingIsland(Island island) {
        updatingIslands.add(island);
    }
    
    public void removeUpdatingIsland(Island island) {
        updatingIslands.remove(island);
    }

    public void setBiome(Island island, Biome biome, CompleteTask task) {
        addUpdatingIsland(island);

        if (island.getLocation(IslandWorld.Normal, IslandEnvironment.Island) == null) return;

        if(skyblock.isPaperAsync()){
            // We keep it sequentially in order to use less RAM
            ExecutorService threadPool = Executors.newFixedThreadPool(4);
            pools.add(threadPool);
            ChunkLoader.startChunkLoadingPerChunk(island, IslandWorld.Normal, skyblock.isPaperAsync(), (asyncChunk, syncChunk) -> {
                Chunk chunk = asyncChunk.join();
                if(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)){ // TODO Should be 1.15 but it works fine there
                    setChunkBiome3D(island, biome, chunk, threadPool);
                } else {
                    setChunkBiome2D(island, biome, chunk);
                }
            }, (island1 -> {
                removeUpdatingIsland(island1);
                if(task != null) {
                    task.onCompleteUpdate();
                }
                threadPool.shutdown();
                pools.remove(threadPool);
            }));
        } else {
            ExecutorService threadPool = Executors.newFixedThreadPool(4);
            pools.add(threadPool);
            ChunkLoader.startChunkLoadingPerChunk(island, IslandWorld.Normal, skyblock.isPaperAsync(), (asyncChunk, syncChunk) -> {
                if(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)){ // TODO Should be 1.15 but it works fine there
                    setChunkBiome3D(island, biome, syncChunk, threadPool);
                } else {
                    setChunkBiome2D(island, biome, syncChunk);
                }
            }, (island1 -> {
                removeUpdatingIsland(island1);
                if(task != null) {
                    task.onCompleteUpdate();
                }
                threadPool.shutdown();
                pools.remove(threadPool);
            }));
        }
    }

    private void setChunkBiome2D(Island island, Biome biome, Chunk chunk) {
        for(int xx = 0; xx < 16; xx++){
            for(int zz = 0; zz < 16; zz++){
                if(!chunk.getWorld().getBiome(xx, zz).equals(biome)){
                    chunk.getWorld().setBiome(xx, zz, biome);
                }
            }
        }
        updateBiomePacket(island, chunk);
    }
    
    private void setChunkBiome3D(Island island, Biome biome, Chunk chunk, ExecutorService pool) {
        for(int i = 0; i<256; i+=16){
            int finalI = i;
            pool.execute(() -> {
                for(int x = 0; x < 16; x++){
                    for(int z = 0; z < 16; z++){
                        for(int y = 0; y<16; y++){
                            chunk.getWorld().setBiome(x, y * finalI, z, biome);
                            if(!chunk.getWorld().getBiome(x, y * finalI, z).equals(biome)){
                            }
                        }
                    }
                }
            });
        }
        updateBiomePacket(island, chunk);
    }

    

    private void updateBiomePacket(Island island, Chunk chunk) {
        Class<?> packetPlayOutMapChunkClass;
        Class<?> chunkClass;
    
        packetPlayOutMapChunkClass = NMSUtil.getNMSClass("PacketPlayOutMapChunk");
        chunkClass = NMSUtil.getNMSClass("Chunk");
    
        for (Player player : skyblock.getIslandManager().getPlayersAtIsland(island, IslandWorld.Normal)) {
            try {
                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
                    if(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)) {
                        NMSUtil.sendPacket(player,
                                packetPlayOutMapChunkClass.getConstructor(chunkClass, int.class, boolean.class).newInstance(player
                                                .getLocation().getChunk().getClass().getMethod("getHandle").invoke(chunk),
                                        65535, true));
                    } else {
                        NMSUtil.sendPacket(player,
                                packetPlayOutMapChunkClass.getConstructor(chunkClass, int.class).newInstance(player
                                                .getLocation().getChunk().getClass().getMethod("getHandle").invoke(chunk),
                                        65535));
                    }
                } else {
                    NMSUtil.sendPacket(player,
                            packetPlayOutMapChunkClass.getConstructor(chunkClass, boolean.class, int.class)
                                    .newInstance(player.getLocation().getChunk().getClass().getMethod("getHandle")
                                            .invoke(chunk), true, 20));
                }
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void onDisable() {
        for(ExecutorService pool : pools){
            pool.shutdownNow();
        }
    }
    
    public interface CompleteTask {
        void onCompleteUpdate();
    }
}
