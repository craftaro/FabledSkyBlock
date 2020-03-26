package com.songoda.skyblock.listeners;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.utils.version.Materials;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

import java.io.File;

public class Piston implements Listener {

    private final SkyBlock skyblock;

    public Piston(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    // Prevent point farming with Dragon Egg
    @EventHandler
    public void onPistonMove(BlockPistonExtendEvent event) {

        Block block = event.getBlock().getRelative(event.getDirection());

        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();

        if (!worldManager.isIslandWorld(block.getWorld())) return;

        Island island = islandManager.getIslandAtLocation(block.getLocation());

        if (island == null ||
                CompatibleMaterial.DRAGON_EGG != CompatibleMaterial.getMaterial(block)) return;

        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (!configLoad.getBoolean("Island.Block.Level.Enable")) return;

        final Materials materials;

        materials = Materials.getMaterials(block.getType(), block.getData());

        if (materials == null) return;

        IslandLevel level = island.getLevel();

        if (!level.hasMaterial(materials.name())) return;

        long materialAmount = level.getMaterialAmount(materials.name());

        if (materialAmount <= 1)
            level.removeMaterial(materials.name());
        else
            level.setMaterialAmount(materials.name(), materialAmount - 1);
    }
}