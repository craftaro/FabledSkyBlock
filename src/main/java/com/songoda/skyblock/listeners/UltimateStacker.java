package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.utils.version.CompatibleSpawners;
import com.songoda.skyblock.world.WorldManager;
import com.songoda.ultimatestacker.events.SpawnerBreakEvent;
import com.songoda.ultimatestacker.events.SpawnerPlaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

public class UltimateStacker implements Listener {

    private final SkyBlock plugin;

    public UltimateStacker(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawnerPlace(SpawnerPlaceEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SkyBlock.getInstance(), () -> {
            IslandManager islandManager = plugin.getIslandManager();
            WorldManager worldManager = plugin.getWorldManager();

            Location location = event.getBlock().getLocation();
            if (!worldManager.isIslandWorld(location.getWorld())) return;

            Island island = islandManager.getIslandAtLocation(location);

            FileConfiguration configLoad = plugin.getConfiguration();

            if (configLoad.getBoolean("Island.Block.Level.Enable")) {
                CompatibleSpawners materials = CompatibleSpawners.getSpawner(event.getSpawnerType());
                if (materials != null) {
                    IslandLevel level = island.getLevel();

                    long materialAmount = 0;
                    if (level.hasMaterial(materials.name())) {
                        materialAmount = level.getMaterialAmount(materials.name());
                    }

                    level.setMaterialAmount(materials.name(), materialAmount + event.getAmount() - 1); // Normal place event still goes off
                }
            }
        });
    }

    @EventHandler
    public void onSpawnerBreak(SpawnerBreakEvent event) {
        IslandManager islandManager = plugin.getIslandManager();
        WorldManager worldManager = plugin.getWorldManager();

        Location location = event.getBlock().getLocation();
        if (!worldManager.isIslandWorld(location.getWorld())) return;

        Island island = islandManager.getIslandAtLocation(location);

        FileConfiguration configLoad = plugin.getConfiguration();

        if (configLoad.getBoolean("Island.Block.Level.Enable")) {
            CompatibleSpawners materials = CompatibleSpawners.getSpawner(event.getSpawnerType());
            if (materials != null) {
                IslandLevel level = island.getLevel();

                if (level.hasMaterial(materials.name())) {
                    long materialAmount = level.getMaterialAmount(materials.name());

                    if (materialAmount - event.getAmount() <= 0) {
                        level.removeMaterial(materials.name());
                    } else {
                        level.setMaterialAmount(materials.name(), materialAmount - event.getAmount());
                    }
                }
            }
        }
    }

}
