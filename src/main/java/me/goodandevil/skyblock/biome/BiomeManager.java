package me.goodandevil.skyblock.biome;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
				List<Chunk> chunks = new ArrayList<>();
				Location location = island.getLocation(IslandWorld.Normal, IslandEnvironment.Island);
				int radius = (int) Math.ceil(island.getRadius());

				for(int x = location.getBlockX() - radius; x < location.getBlockX() + radius; x++){
					for(int z = location.getBlockX() - radius; z < location.getBlockX() + radius; z++){
						location.getWorld().setBiome(x, z, biome);
						Chunk chunk = location.getWorld().getChunkAt(x >> 4, z >> 4);
						if(!chunks.contains(chunk))
							chunks.add(chunk);
					}
				}

				for(Chunk chunk : chunks)
					updateBiome(island, chunk);
			}
		});
	}

	public void updateBiome(Island island, Chunk chunk) {
		Class<?> packetPlayOutMapChunkClass = NMSUtil.getNMSClass("PacketPlayOutMapChunk");
		Class<?> chunkClass = NMSUtil.getNMSClass("Chunk");

		for (Player all : skyblock.getIslandManager().getPlayersAtIsland(island, IslandWorld.Normal)) {
			try {
				if (NMSUtil.getVersionNumber() < 10) {
					NMSUtil.sendPacket(all,
							packetPlayOutMapChunkClass.getConstructor(chunkClass, boolean.class, int.class)
									.newInstance(all.getLocation().getChunk().getClass().getMethod("getHandle")
											.invoke(chunk), true, 20));
				} else {
					NMSUtil.sendPacket(all,
							packetPlayOutMapChunkClass.getConstructor(chunkClass, int.class).newInstance(all
											.getLocation().getChunk().getClass().getMethod("getHandle").invoke(chunk),
									65535));
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
}
