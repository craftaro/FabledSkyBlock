package com.songoda.skyblock.levelling;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.utils.version.NMSUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Chunk {

    private static final int MAX_CHUNKS = 150;
    private final SkyBlock skyblock;
    private Island island;

    private int initialNumberOfChunks = -1;
    private Set<ChunkPosition> chunkPositions = new HashSet<>();
    private Set<LevelChunkSnapshotWrapper> chunkSnapshots = new HashSet<>();
    private boolean isReady = false;
    private boolean isFinished = false;

    public Chunk(SkyBlock skyblock, Island island) {
        this.skyblock = skyblock;
        this.island = island;
    }

    public void prepareInitial() {
        Bukkit.getScheduler().runTask(this.skyblock, () -> {
            FileConfiguration config = this.skyblock.getFileManager().getConfig(new File(this.skyblock.getDataFolder(), "config.yml")).getFileConfiguration();
            FileConfiguration islandData = this.skyblock.getFileManager().getConfig(new File(new File(this.skyblock.getDataFolder().toString() + "/island-data"), this.island.getOwnerUUID().toString() + ".yml")).getFileConfiguration();

            boolean hasNether = config.getBoolean("Island.World.Nether.Enable") && islandData.getBoolean("Unlocked.Nether", false);
            boolean hasEnd = config.getBoolean("Island.World.End.Enable") && islandData.getBoolean("Unlocked.End", false);

            for (IslandWorld islandWorld : IslandWorld.getIslandWorlds()) {
                if (islandWorld == IslandWorld.Normal || (islandWorld == IslandWorld.Nether && hasNether) || (islandWorld == IslandWorld.End && hasEnd)) {
                    chunkPositions.addAll(getChunksToScan(island, islandWorld));
                }
            }

            this.initialNumberOfChunks = this.chunkPositions.size();

            this.prepareNextChunkSnapshots();
        });
    }

    public boolean isReadyToScan() {
        return this.isReady;
    }

    public Set<LevelChunkSnapshotWrapper> getAvailableChunkSnapshots() {
        this.isReady = false;
        return this.chunkSnapshots;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public void prepareNextChunkSnapshots() {
        boolean isWildStackerEnabled = Bukkit.getPluginManager().isPluginEnabled("WildStacker");

        Bukkit.getScheduler().runTask(this.skyblock, () -> {
            this.chunkSnapshots.clear();

            Iterator<ChunkPosition> it = this.chunkPositions.iterator();
            if (!it.hasNext()) {
                this.isReady = true;
                this.isFinished = true;
                this.sendFinishedMessage();
                return;
            }

            int percentComplete = (int) ((1 - ((double) this.chunkPositions.size() / this.initialNumberOfChunks)) * 100);
            this.sendPercentMessage(percentComplete);

            while (it.hasNext() && this.chunkSnapshots.size() < MAX_CHUNKS) {
                ChunkPosition chunkPosition = it.next();
                World world = chunkPosition.getWorld();
                int x = chunkPosition.getX();
                int z = chunkPosition.getZ();

                // Try to load the chunk, but don't generate anything and ignore if we couldn't get it
                if (world.isChunkLoaded(x, z) || world.loadChunk(x, z, false)) {
                    org.bukkit.Chunk chunk = world.getChunkAt(x, z);
                    ChunkSnapshot chunkSnapshot = chunk.getChunkSnapshot();
                    if (isWildStackerEnabled) {
                        this.chunkSnapshots.add(new WildStackerChunkSnapshotWrapper(chunkSnapshot, com.bgsoftware.wildstacker.api.WildStackerAPI.getWildStacker().getSystemManager().getStackedSnapshot(chunk, true)));
                    } else {
                        this.chunkSnapshots.add(new ChunkSnapshotWrapper(chunkSnapshot));
                    }
                }
                it.remove();
            }

            this.isReady = true;
        });
    }

    private void sendPercentMessage(int percent) {
        if (NMSUtil.getVersionNumber() > 8) {
            String message = ChatColor.translateAlternateColorCodes('&',
                    this.skyblock.getFileManager()
                            .getConfig(new File(this.skyblock.getDataFolder(), "language.yml"))
                            .getFileConfiguration().getString("Command.Island.Level.Scanning.Progress.Message")
                            .replace("%percent", String.valueOf(percent)));
            for (Player player : this.skyblock.getIslandManager().getPlayersAtIsland(this.island)) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            }
        }
    }

    private void sendFinishedMessage() {
        if (NMSUtil.getVersionNumber() > 8) {
            String message = ChatColor.translateAlternateColorCodes('&', this.skyblock.getFileManager()
                    .getConfig(new File(this.skyblock.getDataFolder(), "language.yml"))
                    .getFileConfiguration().getString("Command.Island.Level.Scanning.Finished.Message"));
            for (Player player : this.skyblock.getIslandManager().getPlayersAtIsland(this.island)) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            }
        }
    }

    public static List<ChunkPosition> getChunksToScan(Island island, IslandWorld islandWorld) {
        Location islandLocation = island.getLocation(islandWorld, IslandEnvironment.Island);
        
        if (islandLocation == null) return new ArrayList<>(0);
        
        World world = islandLocation.getWorld();

        Location minLocation = new Location(world, islandLocation.getBlockX() - island.getRadius(), 0, islandLocation.getBlockZ() - island.getRadius());
        Location maxLocation = new Location(world, islandLocation.getBlockX() + island.getRadius(), world.getMaxHeight(), islandLocation.getBlockZ() + island.getRadius());

        int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX());
        int minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ());

        int maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX());
        int maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ());

        List<ChunkPosition> positions = new LinkedList<>();
        
        for (int x = minX; x < maxX + 16; x += 16) {
            for (int z = minZ; z < maxZ + 16; z += 16) {
                positions.add(new ChunkPosition(world, x >> 4, z >> 4));
            }
        }
        
        return positions;
    }
}
