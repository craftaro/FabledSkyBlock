package com.songoda.skyblock.listeners;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FallBreak implements Listener {

    private final SkyBlock plugin;
    
    private final Set<FallingBlock> fallingBlocks;

    public FallBreak(SkyBlock plugin) {
        this.plugin = plugin;
        this.fallingBlocks = new HashSet<>();
        
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(!fallingBlocks.isEmpty()) {
                int counter = 0;
                IslandManager islandManager = plugin.getIslandManager();
                WorldManager worldManager = plugin.getWorldManager();
                FileManager.Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
                FileConfiguration configLoad = config.getFileConfiguration();
                Iterator<FallingBlock> iterator = fallingBlocks.iterator();
                while(iterator.hasNext()) {
                    FallingBlock ent = iterator.next();
                    if(ent.isDead()) {
                        if (worldManager.isIslandWorld(ent.getLocation().getWorld()) && configLoad.getBoolean("Island.Block.Level.Enable")) {
                            Island island = islandManager.getIslandAtLocation(ent.getLocation());
        
                            if (island != null) {
                                CompatibleMaterial material = null;
                                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                                    material = CompatibleMaterial.getMaterial(ent.getBlockData().getMaterial());
                                } else {
                                    try {
                                        material = CompatibleMaterial.getMaterial((Material) ent.getClass().getMethod("getMaterial").invoke(ent));
                                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
    
                                if (material != null) {
                                    IslandLevel level = island.getLevel();
    
                                    if (level.hasMaterial(material.name())) {
                                        long materialAmount = level.getMaterialAmount(material.name());
        
                                        if (materialAmount <= 1)
                                            level.removeMaterial(material.name());
                                        else
                                            level.setMaterialAmount(material.name(), materialAmount - 1);
                                    }
                                }
                            }
                        }
                        iterator.remove();
                    }
                    if(++counter > 50) { // Limit 50 checks per tick
                        break;
                    }
                }
            }
        }, 2L, 2L);
    }
    
    @EventHandler
    public void onFallingBlockModify(EntityChangeBlockEvent event) {
        if(event.getEntity() instanceof FallingBlock) {
            WorldManager worldManager = plugin.getWorldManager();
            if (worldManager.isIslandWorld(event.getEntity().getLocation().getWorld())) {
                if (!event.getTo().equals(CompatibleMaterial.AIR.getMaterial())){
                    fallingBlocks.remove((FallingBlock) event.getEntity());
                } else if(!event.isCancelled()) {
                    fallingBlocks.add((FallingBlock) event.getEntity());
                }
            }
        }
    }
}