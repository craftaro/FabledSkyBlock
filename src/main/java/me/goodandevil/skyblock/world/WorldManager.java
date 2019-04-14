package me.goodandevil.skyblock.world;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.IslandWorld;
import me.goodandevil.skyblock.world.generator.VoidGenerator;

public class WorldManager {

	private final SkyBlock skyblock;

	private org.bukkit.World normalWorld;
	private org.bukkit.World netherWorld;
	private org.bukkit.World endWorld;

	public WorldManager(SkyBlock skyblock) {
		this.skyblock = skyblock;

		loadWorlds();
	}

	public void loadWorlds() {
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		String normalWorldName = configLoad.getString("Island.World.Normal.Name");
		String netherWorldName = configLoad.getString("Island.World.Nether.Name");
		String endWorldName = configLoad.getString("Island.World.End.Name");

		normalWorld = Bukkit.getServer().getWorld(normalWorldName);
		netherWorld = Bukkit.getServer().getWorld(netherWorldName);
		endWorld = Bukkit.getServer().getWorld(endWorldName);

		if (normalWorld == null) {
			Bukkit.getServer().getLogger().log(Level.INFO,
					"SkyBlock | Info: Generating VoidWorld '" + normalWorldName + "'.");
			normalWorld = WorldCreator.name(normalWorldName).type(WorldType.FLAT).environment(World.Environment.NORMAL)
					.generator(new VoidGenerator()).createWorld();

			Bukkit.getServer().getScheduler().runTask(skyblock, () -> registerMultiverse(normalWorldName, World.Environment.NORMAL));
		}

		if (netherWorld == null) {
			Bukkit.getServer().getLogger().log(Level.INFO,
					"SkyBlock | Info: Generating VoidWorld '" + netherWorldName + "'.");
			netherWorld = WorldCreator.name(netherWorldName).type(WorldType.FLAT).environment(World.Environment.NETHER)
					.generator(new VoidGenerator()).createWorld();

			Bukkit.getServer().getScheduler().runTask(skyblock, () -> registerMultiverse(netherWorldName, World.Environment.NETHER));
		}

		if (endWorld == null) {
			Bukkit.getServer().getLogger().log(Level.INFO,
					"SkyBlock | Info: Generating VoidWorld '" + endWorldName + "'.");
			endWorld = WorldCreator.name(endWorldName).type(WorldType.FLAT).environment(World.Environment.THE_END)
					.generator(new VoidGenerator()).createWorld();

			Bukkit.getServer().getScheduler().runTask(skyblock, () -> registerMultiverse(endWorldName, World.Environment.THE_END));
		}
	}

	public void registerMultiverse(String worldName, World.Environment environment) {
		if (Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core") != null) {
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
					"mv import " + worldName + " " + environment.name().toLowerCase() + " -g " + skyblock.getName());
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
					"mv modify set generator " + skyblock.getName() + " " + worldName);
		}
	}

	public World getWorld(IslandWorld world) {
		if (world == IslandWorld.Normal) {
			return normalWorld;
		} else if (world == IslandWorld.Nether) {
			return netherWorld;
		} else if (world == IslandWorld.End) {
			return endWorld;
		}

		return null;
	}

	public IslandWorld getIslandWorld(World world) {
		if (world == null) {
			return null;
		}

		if (normalWorld.getName().equals(world.getName())) {
			return IslandWorld.Normal;
		} else if (netherWorld.getName().equals(world.getName())) {
			return IslandWorld.Nether;
		} else if (endWorld.getName().equals(world.getName())) {
			return IslandWorld.End;
		}

		return null;
	}

	public boolean isIslandWorld(World world) {
		if (world == null) {
			return false;
		}

		if (normalWorld.getName().equals(world.getName()) || netherWorld.getName().equals(world.getName())
				|| endWorld.getName().equals(world.getName())) {
			return true;
		}

		return false;
	}

	public Location getLocation(Location location, IslandWorld world) {
		if (location != null && location.getWorld() == null) {
			location.setWorld(getWorld(world));
		}

		return location;
	}
}
