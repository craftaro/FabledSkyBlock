package com.songoda.skyblock.world;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.world.generator.VoidGenerator;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.logging.Level;

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
        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        String normalWorldName = configLoad.getString("Island.World.Normal.Name");
        String netherWorldName = configLoad.getString("Island.World.Nether.Name");
        String endWorldName = configLoad.getString("Island.World.End.Name");

        boolean netherWorldEnabled = configLoad.getBoolean("Island.World.Nether.Enable");
        boolean endWorldEnabled = configLoad.getBoolean("Island.World.End.Enable");

        World.Environment normalWorldEnvironment = World.Environment.valueOf(configLoad.getString("Island.World.Normal.Environment"));
        World.Environment netherWorldEnvironment = World.Environment.valueOf(configLoad.getString("Island.World.Nether.Environment"));
        World.Environment endWorldEnvironment = World.Environment.valueOf(configLoad.getString("Island.World.End.Environment"));

        normalWorld = Bukkit.getServer().getWorld(normalWorldName);
        netherWorld = Bukkit.getServer().getWorld(netherWorldName);
        endWorld = Bukkit.getServer().getWorld(endWorldName);

        if (normalWorld == null) {
            Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Generating VoidWorld '" + normalWorldName + "'.");
            normalWorld = WorldCreator.name(normalWorldName).type(WorldType.FLAT).environment(normalWorldEnvironment).generator(new VoidGenerator()).createWorld();

            Bukkit.getServer().getScheduler().runTask(skyblock, () -> registerMultiverse(normalWorldName, normalWorldEnvironment));
        }

        if (netherWorld == null && netherWorldEnabled) {
            Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Generating VoidWorld '" + netherWorldName + "'.");
            netherWorld = WorldCreator.name(netherWorldName).type(WorldType.FLAT).environment(netherWorldEnvironment).generator(new VoidGenerator()).createWorld();

            Bukkit.getServer().getScheduler().runTask(skyblock, () -> registerMultiverse(netherWorldName, netherWorldEnvironment));
        }

        if (endWorld == null && endWorldEnabled) {
            Bukkit.getServer().getLogger().log(Level.INFO, "SkyBlock | Info: Generating VoidWorld '" + endWorldName + "'.");
            endWorld = WorldCreator.name(endWorldName).type(WorldType.FLAT).environment(endWorldEnvironment).generator(new VoidGenerator()).createWorld();

            Bukkit.getServer().getScheduler().runTask(skyblock, () -> registerMultiverse(endWorldName, endWorldEnvironment));
        }

        if (normalWorld != null)
            normalWorld.setDifficulty(Difficulty.valueOf(configLoad.getString("Island.World.Normal.Difficulty")));

        if (netherWorld != null)
            netherWorld.setDifficulty(Difficulty.valueOf(configLoad.getString("Island.World.Nether.Difficulty")));

        if (endWorld != null)
            endWorld.setDifficulty(Difficulty.valueOf(configLoad.getString("Island.World.End.Difficulty")));
    }

    public void registerMultiverse(String worldName, World.Environment environment) {
        if (Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core") != null) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv import " + worldName + " " + environment.name().toLowerCase() + " -g " + skyblock.getName());
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv modify set generator " + skyblock.getName() + " " + worldName);
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
}
