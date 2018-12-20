package me.goodandevil.skyblock.biome;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandEnvironment;
import me.goodandevil.skyblock.island.IslandWorld;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class BiomeManager {

	private final SkyBlock skyblock;

	public BiomeManager(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	public void setBiome(Island island, Biome biome) {
		Bukkit.getServer().getScheduler().runTaskAsynchronously(skyblock, new Runnable() {
			@Override
			public void run() {
				Location location = island.getLocation(IslandWorld.Normal, IslandEnvironment.Island);

				for (Location locationList : LocationUtil.getLocations(
						new Location(location.getWorld(), location.getBlockX() - island.getRadius(), 0,
								location.getBlockZ() - island.getRadius()),
						new Location(location.getWorld(), location.getBlockX() + island.getRadius(), 0,
								location.getBlockZ() + island.getRadius()))) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(skyblock, new Runnable() {
						@Override
						public void run() {
							location.getWorld().setBiome(locationList.getBlockX(), locationList.getBlockZ(), biome);
						}
					});
				}
			}
		});
	}

	public void updateBiome(Island island, List<Chunk> chunks) {
		Class<?> packetPlayOutMapChunkClass = NMSUtil.getNMSClass("PacketPlayOutMapChunk");
		Class<?> chunkClass = NMSUtil.getNMSClass("Chunk");

		for (Player all : skyblock.getIslandManager().getPlayersAtIsland(island, IslandWorld.Normal)) {
			for (Chunk chunkList : chunks) {
				try {
					if (NMSUtil.getVersionNumber() < 10) {
						NMSUtil.sendPacket(all,
								packetPlayOutMapChunkClass.getConstructor(chunkClass, boolean.class, int.class)
										.newInstance(all.getLocation().getChunk().getClass().getMethod("getHandle")
												.invoke(chunkList), true, 20));
					} else {
						NMSUtil.sendPacket(all,
								packetPlayOutMapChunkClass.getConstructor(chunkClass, int.class).newInstance(all
										.getLocation().getChunk().getClass().getMethod("getHandle").invoke(chunkList),
										65535));
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
