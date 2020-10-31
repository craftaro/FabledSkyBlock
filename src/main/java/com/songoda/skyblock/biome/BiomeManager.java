package com.songoda.skyblock.biome;

import com.songoda.core.compatibility.CompatibleBiome;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.blockscanner.ChunkLoader;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.utils.version.NMSUtil;
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

    private final SkyBlock plugin;
    private final List<Island> updatingIslands;
    private final FileConfiguration language;
    private final int runEveryX;

    public BiomeManager(SkyBlock plugin) {
        this.plugin = plugin;
        this.updatingIslands = new ArrayList<>();
        this.language = SkyBlock.getInstance().getLanguage();
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

    public void setBiome(Island island, IslandWorld world, CompatibleBiome biome, CompleteTask task) {
        addUpdatingIsland(island);

        if (island.getLocation(world, IslandEnvironment.Island) == null) return;
    
        // We keep it sequentially in order to use less RAM
        int chunkAmount = (int) Math.ceil(Math.pow(island.getSize()/16d, 2d));
        AtomicInteger progress = new AtomicInteger();
    
        ChunkLoader.startChunkLoadingPerChunk(island, world, plugin.isPaperAsync(), (cachedChunk) -> {
            // I don't like this. But CompletableFuture#join causes a crash on some setups.
            cachedChunk.getChunk().thenAccept(chunk -> {
                try {
                    if (chunk != null)
                        biome.setBiome(chunk);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                progress.getAndIncrement();

                if (language.getBoolean("Command.Island.Biome.Progress.Should-Display-Message") &&
                        progress.get() == 1 || progress.get() == chunkAmount || progress.get() % runEveryX == 0) {
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
            });
        }, (island1 -> {
            removeUpdatingIsland(island1);
            if(task != null) {
                task.onCompleteUpdate();
            }
        }));
    }

    public interface CompleteTask {
        void onCompleteUpdate();
    }
}
