package com.songoda.skyblock.world;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.limit.LimitationInstanceHandler;
import com.songoda.skyblock.world.generator.VoidGenerator;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.logging.Level;

public class WorldManager {

    private final SkyBlock plugin;

    private org.bukkit.World normalWorld;
    private org.bukkit.World netherWorld;
    private org.bukkit.World endWorld;
    private ChunkGenerator normalWorldWorldGenerator;
    private ChunkGenerator netherWorldWorldGenerator;
    private ChunkGenerator endWorldWorldGenerator;

    public WorldManager(SkyBlock plugin) {
        this.plugin = plugin;

        loadWorlds();
    }

    public void loadWorlds() {
        FileConfiguration configLoad = plugin.getConfiguration();

        String normalWorldName = configLoad.getString("Island.World.Normal.Name");
        String netherWorldName = configLoad.getString("Island.World.Nether.Name");
        String endWorldName = configLoad.getString("Island.World.End.Name");

        boolean netherWorldEnabled = configLoad.getBoolean("Island.World.Nether.Enable");
        boolean endWorldEnabled = configLoad.getBoolean("Island.World.End.Enable");

        World.Environment normalWorldEnvironment = World.Environment.valueOf(configLoad.getString("Island.World.Normal.Environment"));
        World.Environment netherWorldEnvironment = World.Environment.valueOf(configLoad.getString("Island.World.Nether.Environment"));
        World.Environment endWorldEnvironment = World.Environment.valueOf(configLoad.getString("Island.World.End.Environment"));

        String normalWorldGeneratorName = configLoad.getString("Island.World.Normal.CustomWorldGenerator");
        String netherWorldGeneratorName = configLoad.getString("Island.World.End.CustomWorldGenerator");
        String endWorldGeneratorName = configLoad.getString("Island.World.End.CustomWorldGenerator");

        normalWorldWorldGenerator = getWorldGenerator(normalWorldName, normalWorldGeneratorName, IslandWorld.Normal);
        netherWorldWorldGenerator = getWorldGenerator(netherWorldName, netherWorldGeneratorName, IslandWorld.Nether);
        endWorldWorldGenerator = getWorldGenerator(endWorldName, endWorldGeneratorName, IslandWorld.End);

        normalWorld = Bukkit.getServer().getWorld(normalWorldName);
        netherWorld = Bukkit.getServer().getWorld(netherWorldName);
        endWorld = Bukkit.getServer().getWorld(endWorldName);

        if (normalWorld == null) {
            Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Generating Normal World '" + normalWorldName + "'.");
            normalWorld = WorldCreator.name(normalWorldName).type(WorldType.FLAT).environment(normalWorldEnvironment).generator(normalWorldWorldGenerator).createWorld();
            registerMultiverse(normalWorldName, normalWorldEnvironment, normalWorldGeneratorName);
        }

        if (netherWorld == null && netherWorldEnabled) {
            Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Generating Nether World '" + netherWorldName + "'.");
            netherWorld = WorldCreator.name(netherWorldName).type(WorldType.FLAT).environment(netherWorldEnvironment).generator(netherWorldWorldGenerator).createWorld();
            registerMultiverse(netherWorldName, netherWorldEnvironment, netherWorldGeneratorName);
        }

        if (endWorld == null && endWorldEnabled) {
            Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Generating Void World '" + endWorldName + "'.");
            endWorld = WorldCreator.name(endWorldName).type(WorldType.FLAT).environment(endWorldEnvironment).generator(endWorldWorldGenerator).createWorld();
            registerMultiverse(endWorldName, endWorldEnvironment, endWorldGeneratorName);
        }

        if (normalWorld != null)
            normalWorld.setDifficulty(Difficulty.valueOf(configLoad.getString("Island.World.Normal.Difficulty")));

        if (netherWorld != null)
            netherWorld.setDifficulty(Difficulty.valueOf(configLoad.getString("Island.World.Nether.Difficulty")));

        if (endWorld != null)
            endWorld.setDifficulty(Difficulty.valueOf(configLoad.getString("Island.World.End.Difficulty")));
    }

    public void registerMultiverse(String worldName, World.Environment environment, String worldGeneratorName) {
        if (Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core") == null) {
            return;
        }
        if (worldGeneratorName.toLowerCase().equals("default") || worldGeneratorName == null) {
            worldGeneratorName = plugin.getName();
        }
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + worldName + " " + environment.name().toLowerCase() + " -g " + plugin.getName());
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + worldGeneratorName + " " + worldName);
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

        if (normalWorld != null && normalWorld.getName().equals(world.getName())) return IslandWorld.Normal;

        if (netherWorld != null && netherWorld.getName().equals(world.getName())) return IslandWorld.Nether;

        if (endWorld != null && endWorld.getName().equals(world.getName())) return IslandWorld.End;

        return null;
    }

    public boolean isIslandWorld(World world) {
        if (world == null) return false;

        if (normalWorld != null && normalWorld.getName().equals(world.getName())) return true;

        if (netherWorld != null && netherWorld.getName().equals(world.getName())) return true;

        return endWorld != null && endWorld.getName().equals(world.getName());
    }

    public Location getLocation(Location location, IslandWorld world) {
        if (location != null && location.getWorld() == null) {
            location.setWorld(getWorld(world));
        }

        return location;
    }

    private ChunkGenerator getWorldGenerator(String mapName, String worldGeneratorName, IslandWorld islandWorld) {
        if (worldGeneratorName == null || worldGeneratorName.equalsIgnoreCase("default") || worldGeneratorName.length() == 0) {
            return new VoidGenerator(islandWorld);
        }

        ChunkGenerator customWorldGenerator = WorldCreator.getGeneratorForName(mapName, worldGeneratorName, null);

        if (customWorldGenerator != null) {
            return customWorldGenerator;
        }

        return new VoidGenerator(islandWorld);
    }

    public ChunkGenerator getWorldGeneratorForMapName(String mapName) {
        if (normalWorld != null && normalWorld.getName().equals(mapName)) return normalWorldWorldGenerator;

        if (netherWorld != null && netherWorld.getName().equals(mapName)) return netherWorldWorldGenerator;

        if (endWorld != null && endWorld.getName().equals(mapName)) return endWorldWorldGenerator;

        return new VoidGenerator(IslandWorld.Normal);
    }
}
