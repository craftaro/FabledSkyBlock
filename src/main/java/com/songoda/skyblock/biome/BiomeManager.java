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
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BiomeManager {
    
    final ServerVersion ASYNC_OBFUSCATOR_VERSION = ServerVersion.V1_9;

    private final SkyBlock plugin;
    private final List<Island> updatingIslands;
    private final FileConfiguration language;
    private final int runEveryX;

    public BiomeManager(SkyBlock plugin) {
        this.plugin = plugin;
        this.updatingIslands = new ArrayList<>();
        this.language = SkyBlock.getInstance().getFileManager().getConfig(new File(SkyBlock.getInstance().getDataFolder(), "language.yml")).getFileConfiguration();
        this.runEveryX = language.getInt("Command.Island.Biome.Progress.Display-Every-X-Updates");
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

        if(plugin.isPaperAsync()){
            // We keep it sequentially in order to use less RAM
            int chunkAmount = (int) Math.ceil(Math.pow(island.getSize()/16d, 2d));
            AtomicInteger progress = new AtomicInteger();
            
            ChunkLoader.startChunkLoadingPerChunk(island, IslandWorld.Normal, plugin.isPaperAsync(), (asyncChunk, syncChunk) -> {
                Chunk chunk = asyncChunk.join();
                if(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)){ // TODO Should be 1.15 but it works fine there
                    setChunkBiome3D(biome, chunk); // 2D for the moment
                } else {
                    try {
                        setChunkBiome2D(biome, chunk);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                updateBiomePacket(island, chunk);
                
                progress.getAndIncrement();
                
                if(language.getBoolean("Command.Island.Biome.Progress.Should-Display-Message") &&
                        progress.get() == 1 || progress.get() == chunkAmount || progress.get() % runEveryX == 0){
                    final double percent = ((double) progress.get() / (double) chunkAmount) * 100;
    
                    String message = language.getString("Command.Island.Biome.Progress.Message");
                    message = message.replace("%current_updated_chunks%", String.valueOf(progress.get()));
                    message = message.replace("%max_chunks%", String.valueOf(chunkAmount));
                    message = message.replace("%percent_whole%", String.valueOf((int) percent));
                    message = message.replace("%percent%", NumberFormat.getInstance().format(percent));
    
                    for (Player player : SkyBlock.getInstance().getIslandManager().getPlayersAtIsland(island)) {
                        plugin.getMessageManager().sendMessage(player, message);
                    }
                }
            }, (island1 -> {
                removeUpdatingIsland(island1);
                if(task != null) {
                    task.onCompleteUpdate();
                }
            }));
        } else {
            ChunkLoader.startChunkLoading(island, IslandWorld.Normal, plugin.isPaperAsync(), (asyncChunks, syncChunks) -> {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    int progress = 0;
                    for(Chunk chunk : syncChunks){
                        if(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)){ // TODO Should be 1.15 but it works fine there
                            setChunkBiome3D(biome, chunk); // 2D for the moment
                        } else {
                            try {
                                setChunkBiome2D(biome, chunk);
                            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        if(ServerVersion.isServerVersionAtLeast(ASYNC_OBFUSCATOR_VERSION)) {
                            updateBiomePacket(island, chunk);
                        }
                        progress++;
    
                        if(language.getBoolean("Command.Island.Biome.Progress.Should-Display-Message") &&
                                progress == 1 || progress == syncChunks.size() || progress % runEveryX == 0){
                            final double percent = ((double) progress / (double) syncChunks.size()) * 100;
        
                            String message = language.getString("Command.Island.Biome.Progress.Message");
                            message = message.replace("%current_updated_chunks%", String.valueOf(progress));
                            message = message.replace("%max_chunks%", String.valueOf(syncChunks.size()));
                            message = message.replace("%percent_whole%", String.valueOf((int) percent));
                            message = message.replace("%percent%", NumberFormat.getInstance().format(percent));
        
                            for (Player player : SkyBlock.getInstance().getIslandManager().getPlayersAtIsland(island)) {
                                plugin.getMessageManager().sendMessage(player, message);
                            }
                        }
                    }
                    if(ServerVersion.isServerVersionBelow(ASYNC_OBFUSCATOR_VERSION)) {
                        for(Chunk chunk : syncChunks){
                            updateBiomePacket(island, chunk);
                        }
                    }
                });
            }, (island1 -> {
                removeUpdatingIsland(island1);
                if(task != null) {
                    task.onCompleteUpdate();
                }
            }));
        }
    }

    private void setChunkBiome2D(Biome biome, Chunk chunk) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for(int x = chunk.getX() << 4; x < (chunk.getX()<< 4)+16; x++){
            for(int z = chunk.getZ() << 4; z < (chunk.getZ()<< 4)+16; z++){
                World.class.getMethod("setBiome", int.class, int.class, Biome.class).invoke(chunk.getWorld(), x, z, biome);
            }
        }
    }
    
    // Do not use - Too laggy
    private void setChunkBiome3D(Biome biome, Chunk chunk) {
        for(int x = chunk.getX() << 4; x < (chunk.getX()<< 4)+16; x++){
            for(int z = chunk.getZ() << 4; z < (chunk.getZ()<< 4)+16; z++){
                for(int y = 0; y < chunk.getWorld().getMaxHeight(); ++y) {
                    chunk.getWorld().setBiome(x, y, z, biome);
                }
            }
        }
    }

    

    private void updateBiomePacket(Island island, Chunk chunk) {
        Class<?> packetPlayOutMapChunkClass;
        Class<?> chunkClass;
    
        packetPlayOutMapChunkClass = NMSUtil.getNMSClass("PacketPlayOutMapChunk");
        chunkClass = NMSUtil.getNMSClass("Chunk");
    
        for (Player player : plugin.getIslandManager().getPlayersAtIsland(island, IslandWorld.Normal)) {
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
    
    public interface CompleteTask {
        void onCompleteUpdate();
    }
}
