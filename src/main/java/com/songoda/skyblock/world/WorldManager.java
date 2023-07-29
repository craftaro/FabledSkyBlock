package com.songoda.skyblock.world;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.world.generator.VoidGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;

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
    }

    public void loadWorlds() {
        FileConfiguration configLoad = this.plugin.getConfiguration();

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

        this.normalWorldWorldGenerator = getWorldGenerator(normalWorldName, normalWorldGeneratorName, IslandWorld.NORMAL);
        this.netherWorldWorldGenerator = getWorldGenerator(netherWorldName, netherWorldGeneratorName, IslandWorld.NETHER);
        this.endWorldWorldGenerator = getWorldGenerator(endWorldName, endWorldGeneratorName, IslandWorld.END);

        this.normalWorld = Bukkit.getServer().getWorld(normalWorldName);
        this.netherWorld = Bukkit.getServer().getWorld(netherWorldName);
        this.endWorld = Bukkit.getServer().getWorld(endWorldName);

        if (this.normalWorld == null) {
            Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Generating Normal World '" + normalWorldName + "'.");
            this.normalWorld = WorldCreator.name(normalWorldName).type(WorldType.FLAT).environment(normalWorldEnvironment).generator(this.normalWorldWorldGenerator).createWorld();
            registerMultiverse(normalWorldName, normalWorldEnvironment, normalWorldGeneratorName);
        }

        if (this.netherWorld == null && netherWorldEnabled) {
            Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Generating Nether World '" + netherWorldName + "'.");
            this.netherWorld = WorldCreator.name(netherWorldName).type(WorldType.FLAT).environment(netherWorldEnvironment).generator(this.netherWorldWorldGenerator).createWorld();
            registerMultiverse(netherWorldName, netherWorldEnvironment, netherWorldGeneratorName);
        }

        if (this.endWorld == null && endWorldEnabled) {
            Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Generating Void World '" + endWorldName + "'.");
            this.endWorld = WorldCreator.name(endWorldName).type(WorldType.FLAT).environment(endWorldEnvironment).generator(this.endWorldWorldGenerator).createWorld();
            registerMultiverse(endWorldName, endWorldEnvironment, endWorldGeneratorName);
        }

        if (this.normalWorld != null) {
            this.normalWorld.setDifficulty(Difficulty.valueOf(configLoad.getString("Island.World.Normal.Difficulty")));
        }

        if (this.netherWorld != null) {
            this.netherWorld.setDifficulty(Difficulty.valueOf(configLoad.getString("Island.World.Nether.Difficulty")));
        }

        if (this.endWorld != null) {
            this.endWorld.setDifficulty(Difficulty.valueOf(configLoad.getString("Island.World.End.Difficulty")));
        }
    }

    public void registerMultiverse(String worldName, World.Environment environment, String worldGeneratorName) {
        if (Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core") == null) {
            return;
        }
        if (worldGeneratorName == null || worldGeneratorName.equalsIgnoreCase("default")) {
            worldGeneratorName = this.plugin.getName();
        }
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + worldName + " " + environment.name().toLowerCase() + " -g " + this.plugin.getName());
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + worldGeneratorName + " " + worldName);
    }

    public World getWorld(IslandWorld world) {
        if (world == IslandWorld.NORMAL) {
            return this.normalWorld;
        } else if (world == IslandWorld.NETHER) {
            return this.netherWorld;
        } else if (world == IslandWorld.END) {
            return this.endWorld;
        }

        return null;
    }

    public IslandWorld getIslandWorld(World world) {
        if (world == null) {
            return null;
        }

        if (this.normalWorld != null && this.normalWorld.getName().equals(world.getName())) {
            return IslandWorld.NORMAL;
        }

        if (this.netherWorld != null && this.netherWorld.getName().equals(world.getName())) {
            return IslandWorld.NETHER;
        }

        if (this.endWorld != null && this.endWorld.getName().equals(world.getName())) {
            return IslandWorld.END;
        }

        return null;
    }

    public boolean isIslandWorld(World world) {
        if (world == null) {
            return false;
        }

        if (this.normalWorld != null && this.normalWorld.getName().equals(world.getName())) {
            return true;
        }

        if (this.netherWorld != null && this.netherWorld.getName().equals(world.getName())) {
            return true;
        }

        return this.endWorld != null && this.endWorld.getName().equals(world.getName());
    }

    public Location getLocation(Location location, IslandWorld world) {
        if (location != null && location.getWorld() == null) {
            location.setWorld(getWorld(world));
        }

        return location;
    }

    private ChunkGenerator getWorldGenerator(String mapName, String worldGeneratorName, IslandWorld islandWorld) {
        if (worldGeneratorName == null || worldGeneratorName.isEmpty() || worldGeneratorName.equalsIgnoreCase("default")) {
            return new VoidGenerator(islandWorld, this.plugin);
        }

        ChunkGenerator customWorldGenerator = WorldCreator.getGeneratorForName(mapName, worldGeneratorName, null);
        if (customWorldGenerator != null) {
            return customWorldGenerator;
        }

        return new VoidGenerator(islandWorld, this.plugin);
    }

    public ChunkGenerator getWorldGeneratorForMapName(String mapName) {
        if (this.normalWorld != null && this.normalWorld.getName().equals(mapName)) {
            return this.normalWorldWorldGenerator;
        }
        if (this.netherWorld != null && this.netherWorld.getName().equals(mapName)) {
            return this.netherWorldWorldGenerator;
        }
        if (this.endWorld != null && this.endWorld.getName().equals(mapName)) {
            return this.endWorldWorldGenerator;
        }

        return new VoidGenerator(IslandWorld.NORMAL, this.plugin);
    }
}
