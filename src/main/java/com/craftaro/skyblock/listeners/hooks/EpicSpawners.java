package com.craftaro.skyblock.listeners.hooks;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandLevel;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.utils.version.CompatibleSpawners;
import com.craftaro.skyblock.world.WorldManager;
import com.songoda.epicspawners.api.events.SpawnerBreakEvent;
import com.songoda.epicspawners.api.events.SpawnerChangeEvent;
import com.songoda.epicspawners.api.events.SpawnerPlaceEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EpicSpawners implements Listener {
    private final SkyBlock plugin;

    public EpicSpawners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawnerPlace(SpawnerPlaceEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SkyBlock.getPlugin(SkyBlock.class), () -> {
            IslandManager islandManager = this.plugin.getIslandManager();
            WorldManager worldManager = this.plugin.getWorldManager();

            Location location = event.getSpawner().getLocation();
            if (!worldManager.isIslandWorld(location.getWorld())) {
                return;
            }

            Island island = islandManager.getIslandAtLocation(location);

            int amount = event.getSpawner().getStackSize();
            EntityType spawnerType = event.getSpawner().getCreatureSpawner().getSpawnedType();

            FileConfiguration configLoad = this.plugin.getConfiguration();

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
        if (event.getChange() != SpawnerChangeEvent.ChangeType.STACK_SIZE) {
            return;
        }

        IslandManager islandManager = this.plugin.getIslandManager();
        WorldManager worldManager = this.plugin.getWorldManager();

        Location location = event.getSpawner().getLocation();
        if (!worldManager.isIslandWorld(location.getWorld())) {
            return;
        }

        Island island = islandManager.getIslandAtLocation(location);

        int amount = event.getStackSize() - event.getOldStackSize();
        EntityType spawnerType = event.getSpawner().getCreatureSpawner().getSpawnedType();

        FileConfiguration configLoad = this.plugin.getConfiguration();

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
        IslandManager islandManager = this.plugin.getIslandManager();
        WorldManager worldManager = this.plugin.getWorldManager();

        Location location = event.getSpawner().getLocation();
        if (!worldManager.isIslandWorld(location.getWorld())) {
            return;
        }

        Island island = islandManager.getIslandAtLocation(location);

        int amount = event.getSpawner().getStackSize();
        EntityType spawnerType = event.getSpawner().getCreatureSpawner().getSpawnedType();

        FileConfiguration configLoad = this.plugin.getConfiguration();

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
