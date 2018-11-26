package me.goodandevil.skyblock.world;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.world.generator.VoidGenerator;

public class WorldManager {
	
	private final SkyBlock skyblock;

	private org.bukkit.World normalWorld;
	private org.bukkit.World netherWorld;
	
	public WorldManager(SkyBlock skyblock) {
		this.skyblock = skyblock;
		
		loadWorlds();
	}
	
	public void loadWorlds() {
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		String netherWorldName = configLoad.getString("Island.World.Nether.Name");
		String normalWorldName = configLoad.getString("Island.World.Normal.Name");
		
		normalWorld = Bukkit.getServer().getWorld(normalWorldName);
		netherWorld = Bukkit.getServer().getWorld(netherWorldName);
		
		if (normalWorld == null) {
			Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Generating VoidWorld '" + normalWorldName + "'.");
			normalWorld = WorldCreator.name(normalWorldName).type(WorldType.FLAT).environment(World.Environment.NORMAL).generator(new VoidGenerator()).createWorld();
			
			Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
				@Override
				public void run() {
					registerMultiverse(normalWorldName, World.Environment.NORMAL);
				}
			});
		}
		
		if (netherWorld == null) {
			Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Generating VoidWorld '" + netherWorldName + "'.");
			netherWorld = WorldCreator.name(netherWorldName).type(WorldType.FLAT).environment(World.Environment.NETHER).generator(new VoidGenerator()).createWorld();
			
			Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
				@Override
				public void run() {
					registerMultiverse(netherWorldName, World.Environment.NETHER);
				}
			});
		}
	}
	
	public void registerMultiverse(String worldName, World.Environment environment) {
		if (Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core") != null) {
	        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + worldName + " " + environment.name().toLowerCase() + " -g " + skyblock.getName());
	        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + skyblock.getName() + " " + worldName);
		}
	}
	
	public org.bukkit.World getWorld(Location.World world) {
		if (world == Location.World.Normal) {
			return normalWorld;
		} else if (world == Location.World.Nether) {
			return netherWorld;
		}
		
		return null;
	}
}
