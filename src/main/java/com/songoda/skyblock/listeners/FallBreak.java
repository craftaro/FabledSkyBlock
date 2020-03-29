package com.songoda.skyblock.listeners;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

import java.io.File;
import java.util.List;

public class FallBreak implements Listener {

    private final SkyBlock skyblock;

    public FallBreak(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    /*
     * Removes island points when a block is broken by falling.
     * Checks for items spawning (because there's no other event called)
     * Then looks for the falling block (in a radius of 1), which should still be present on the event call.
     *
     * Couldn't find any other way to do this.
     * */
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {

        // Basic world and island checks
        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();

        if (!worldManager.isIslandWorld(event.getEntity().getWorld())) return;

        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (!configLoad.getBoolean("Island.Block.Level.Enable")) return;

        Island island = islandManager.getIslandAtLocation(event.getLocation());

        if (island == null) return;

        // Get entities in radius and look for our block
        List<Entity> entities = event.getEntity().getNearbyEntities(1, 1, 1);

        for (Entity e : entities) {
            if (!(e instanceof FallingBlock)) continue;

            FallingBlock fallingBlock = (FallingBlock) e;

            // Get the block material
            CompatibleMaterial material = CompatibleMaterial.getMaterial(fallingBlock.getMaterial());

            if (material == null) continue;

            // Update count in the level
            IslandLevel level = island.getLevel();

            if (!level.hasMaterial(material.name())) continue;

            long materialAmount = level.getMaterialAmount(material.name());

            if (materialAmount <= 1)
                level.removeMaterial(material.name());
            else
                level.setMaterialAmount(material.name(), materialAmount - 1);
        }
    }
}