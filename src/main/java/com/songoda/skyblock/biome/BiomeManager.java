package com.songoda.skyblock.biome;

import com.songoda.core.compatibility.ServerVersion;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BiomeManager {

    private final SkyBlock skyblock;

    public BiomeManager(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    public void setBiome(Island island, Biome biome) {
        Location location = island.getLocation(IslandWorld.Normal, IslandEnvironment.Island);
        int radius = (int) Math.ceil(island.getRadius());

        final List<ChunkSnapshot> snapshotList = new ArrayList<>(3);

        if (location == null) return;

        final World world = location.getWorld();


            new ChunkLoader(island, IslandWorld.Normal, skyblock.isPaperAsync(), (asyncPositions, syncPositions) -> {
                if (skyblock.isPaperAsync()) {
                    Bukkit.getScheduler().runTaskAsynchronously(skyblock, () -> {
                        List<Chunk> positions = new LinkedList<>();
                        for (CompletableFuture<Chunk> chunk : asyncPositions) {
                            positions.add(chunk.join());
                        }
                        for(Chunk chunk : positions){
                            setChunkBiome(biome, chunk);
                            updateBiomePacket(island, chunk);
                        }
                        //positions.stream().map(Chunk::getChunkSnapshot).forEach(snapshotList::add);
                    });

                    //Map<World, List<ChunkSnapshot>> snapshots = new HashMap<>(3);
                    //snapshots.put(world, snapshotList);
                    Bukkit.getScheduler().runTaskAsynchronously(skyblock, () -> {
                        //ChunkBiomeSplitter.startUpdating(snapshots, biome, (chunk) -> {
                        //    updateBiomePacket(island, chunk);
                        //});

                    });
                } else {
                    //syncPositions.stream().map(Chunk::getChunkSnapshot).forEach(snapshotList::add);

                    //Map<World, List<ChunkSnapshot>> snapshots = new HashMap<>(3);
                    //snapshots.put(world, snapshotList);
                    Bukkit.getScheduler().runTaskAsynchronously(skyblock, () -> {
                        //ChunkBiomeSplitter.startUpdating(snapshots, biome, (chunk) -> {
                        //    updateBiomePacket(island, chunk);
                        //});
                        for(Chunk chunk : syncPositions){
                            setChunkBiome(biome, chunk);
                            updateBiomePacket(island, chunk);
                        }
                    });
                }
            });

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
        /*Bukkit.getScheduler().runTask(skyblock, () -> {
            List<Chunk> chunks = new ArrayList<>();

            if(skyblock.isPaperAsync()){
                Bukkit.getScheduler().runTaskAsynchronously(skyblock, () -> {
                    for (int x = location.getBlockX() - radius; x < location.getBlockX() + radius; x += 16) {
                        for (int z = location.getBlockZ() - radius; z < location.getBlockZ() + radius; z += 16) {
                            try {
                                Chunk chunk = PaperLib.getChunkAtAsync(location.getWorld(), x >> 4, z >> 4).get();
                                setChunkBiome(biome, chunk);
                                chunks.add(chunk);
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Bukkit.getScheduler().runTask(skyblock, () -> {
                        for(Chunk chunk : chunks){
                            updateBiomePacket(island, chunk);
                        }
                    });
                });
            } else {
                int x = location.getBlockX() - radius;
                Bukkit.getScheduler().runTaskTimer(skyblock, () -> {

                }, 2L, 2L);
                while (x < location.getBlockX() + radius) {
                    int z = location.getBlockZ() - radius;
                    while (z < location.getBlockZ() + radius) {
                        chunks.add(location.getWorld().getChunkAt(x >> 4, z >> 4));
                        z += 16;
                    }
                    x += 16;
                }
                Bukkit.getScheduler().runTaskAsynchronously(skyblock, () -> {
                    for(Chunk chunk : chunks){
                        setChunkBiome(biome, chunk);
                    }
                    Bukkit.getScheduler().runTask(skyblock, () -> {
                        for(Chunk chunk : chunks){
                            updateBiomePacket(island, chunk);
                        }
                    });
                });
            }
        });*/
    }

    private void setChunkBiome(Biome biome, Chunk chunk) {
        //location.getWorld().loadChunk(chunk);
        for(int xx = 0; xx < 16; xx++){
            for(int zz = 0; zz < 16; zz++){
                if(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_15)){
                    for(int y = 0; y<256; y++){
                        chunk.getBlock(xx, y, zz).setBiome(biome);
                    }
                } else {
                    chunk.getBlock(xx, 0, zz).setBiome(biome);
                }
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
