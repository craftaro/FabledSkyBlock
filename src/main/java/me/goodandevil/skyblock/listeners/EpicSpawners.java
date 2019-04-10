package me.goodandevil.skyblock.listeners;

import com.songoda.epicspawners.api.events.SpawnerBreakEvent;
import com.songoda.epicspawners.api.events.SpawnerPlaceEvent;
import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandLevel;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.world.WorldManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

public class EpicSpawners implements Listener {

    private final SkyBlock skyblock;

    public EpicSpawners(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onSpawnerPlace(SpawnerPlaceEvent event) {
        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();

        Location location = event.getSpawner().getLocation();
        if (!worldManager.isIslandWorld(location.getWorld())) return;

        Island island = islandManager.getIslandAtLocation(location);

        int amount = event.getSpawner().getFirstStack().getStackSize();
        EntityType spawnerType = event.getSpawner().getCreatureSpawner().getSpawnedType();

        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getBoolean("Island.Block.Level.Enable")) {
            Materials materials = Materials.getSpawner(spawnerType);
            if (materials != null) {
                IslandLevel level = island.getLevel();

                long materialAmount = 0;
                if (level.hasMaterial(materials.name())) {
                    materialAmount = level.getMaterialAmount(materials.name());
                }

                level.setMaterialAmount(materials.name(), materialAmount + amount);
            }
        }
    }

    @EventHandler
    public void onSpawnerBreak(SpawnerBreakEvent event) {
        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();

        Location location = event.getSpawner().getLocation();
        if (!worldManager.isIslandWorld(location.getWorld())) return;

        Island island = islandManager.getIslandAtLocation(location);

        int amount = event.getSpawner().getFirstStack().getStackSize();
        EntityType spawnerType = event.getSpawner().getCreatureSpawner().getSpawnedType();

        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getBoolean("Island.Block.Level.Enable")) {
            Materials materials = Materials.getSpawner(spawnerType);
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
