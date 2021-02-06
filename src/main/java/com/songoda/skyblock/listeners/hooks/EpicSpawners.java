package com.songoda.skyblock.listeners.hooks;

import com.songoda.epicspawners.api.events.SpawnerBreakEvent;
import com.songoda.epicspawners.api.events.SpawnerChangeEvent;
import com.songoda.epicspawners.api.events.SpawnerPlaceEvent;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.utils.version.CompatibleSpawners;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.File;

public class EpicSpawners implements Listener {

    private final SkyBlock plugin;

    public EpicSpawners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawnerPlace(SpawnerPlaceEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SkyBlock.getInstance(), () -> {
            IslandManager islandManager = plugin.getIslandManager();
            WorldManager worldManager = plugin.getWorldManager();

            Location location = event.getSpawner().getLocation();
            if (!worldManager.isIslandWorld(location.getWorld())) return;

            Island island = islandManager.getIslandAtLocation(location);

            int amount = event.getSpawner().getStackSize();
            EntityType spawnerType = event.getSpawner().getCreatureSpawner().getSpawnedType();

            FileConfiguration configLoad = plugin.getConfiguration();

            if (configLoad.getBoolean("Island.Block.Level.Enable")) {
                CompatibleSpawners materials = CompatibleSpawners.getSpawner(spawnerType);
                if (materials != null) {
                    IslandLevel level = island.getLevel();

                    long materialAmount = 0;
                    if (level.hasMaterial(materials.name())) {
                        materialAmount = level.getMaterialAmount(materials.name());
                    }

                    level.setMaterialAmount(materials.name(), materialAmount + amount);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawnerChange(SpawnerChangeEvent event) {
        if (event.getChange() != SpawnerChangeEvent.ChangeType.STACK_SIZE)
            return;

        IslandManager islandManager = plugin.getIslandManager();
        WorldManager worldManager = plugin.getWorldManager();

        Location location = event.getSpawner().getLocation();
        if (!worldManager.isIslandWorld(location.getWorld())) return;

        Island island = islandManager.getIslandAtLocation(location);

        int amount = event.getStackSize() - event.getOldStackSize();
        EntityType spawnerType = event.getSpawner().getCreatureSpawner().getSpawnedType();

        FileConfiguration configLoad = plugin.getConfiguration();

        if (configLoad.getBoolean("Island.Block.Level.Enable")) {
            CompatibleSpawners materials = CompatibleSpawners.getSpawner(spawnerType);
            if (materials != null) {
                IslandLevel level = island.getLevel();

                long materialAmount = 0;
                if (level.hasMaterial(materials.name())) {
                    materialAmount = level.getMaterialAmount(materials.name());
                }

                if (materialAmount + amount <= 0) {
                    level.removeMaterial(materials.name());
                } else {
                    level.setMaterialAmount(materials.name(), materialAmount + amount);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawnerBreak(SpawnerBreakEvent event) {
        IslandManager islandManager = plugin.getIslandManager();
        WorldManager worldManager = plugin.getWorldManager();

        Location location = event.getSpawner().getLocation();
        if (!worldManager.isIslandWorld(location.getWorld())) return;

        Island island = islandManager.getIslandAtLocation(location);

        int amount = event.getSpawner().getStackSize();
        EntityType spawnerType = event.getSpawner().getCreatureSpawner().getSpawnedType();

        FileConfiguration configLoad = plugin.getConfiguration();

        if (configLoad.getBoolean("Island.Block.Level.Enable")) {
            CompatibleSpawners materials = CompatibleSpawners.getSpawner(spawnerType);
            if (materials != null) {
                IslandLevel level = island.getLevel();

                if (level.hasMaterial(materials.name())) {
                    long materialAmount = level.getMaterialAmount(materials.name());

                    if (materialAmount - amount <= 0) {
                        level.removeMaterial(materials.name());
                    } else {
                        level.setMaterialAmount(materials.name(), materialAmount - amount);
                    }
                }
            }
        }
    }

}
