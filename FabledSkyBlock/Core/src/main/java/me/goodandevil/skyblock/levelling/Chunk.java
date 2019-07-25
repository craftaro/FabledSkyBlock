package me.goodandevil.skyblock.levelling;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import me.goodandevil.skyblock.utils.version.NMSUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandEnvironment;
import me.goodandevil.skyblock.island.IslandWorld;
import org.bukkit.entity.Player;

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
					this.getChunksToScan(islandWorld);
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

			int percentComplete = (int)((1 - ((double)this.chunkPositions.size() / this.initialNumberOfChunks)) * 100);
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

	private void getChunksToScan(IslandWorld islandWorld) {
		Location islandLocation = this.island.getLocation(islandWorld, IslandEnvironment.Island);
		World world = islandLocation.getWorld();

		Location minLocation = new Location(world, islandLocation.getBlockX() - this.island.getRadius(), 0, islandLocation.getBlockZ() - this.island.getRadius());
		Location maxLocation = new Location(world, islandLocation.getBlockX() + this.island.getRadius(), world.getMaxHeight(), islandLocation.getBlockZ() + this.island.getRadius());

		int minX = Math.min(maxLocation.getBlockX(), minLocation.getBlockX());
		int minZ = Math.min(maxLocation.getBlockZ(), minLocation.getBlockZ());

		int maxX = Math.max(maxLocation.getBlockX(), minLocation.getBlockX());
		int maxZ = Math.max(maxLocation.getBlockZ(), minLocation.getBlockZ());

		for (int x = minX; x < maxX + 16; x += 16) {
			for (int z = minZ; z < maxZ + 16; z += 16) {
				this.chunkPositions.add(new ChunkPosition(world, x >> 4, z >> 4));
			}
		}
	}
}
