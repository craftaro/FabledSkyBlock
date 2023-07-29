package com.songoda.skyblock.listeners;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
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

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FallBreakListeners implements Listener {
    private final SkyBlock plugin;

    private final Set<FallingBlock> fallingBlocks;

    public FallBreakListeners(SkyBlock plugin) {
        this.plugin = plugin;
        this.fallingBlocks = new HashSet<>();

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!this.fallingBlocks.isEmpty()) {
                int counter = 0;
                IslandManager islandManager = plugin.getIslandManager();
                WorldManager worldManager = plugin.getWorldManager();
                FileConfiguration configLoad = plugin.getConfiguration();
                Iterator<FallingBlock> iterator = this.fallingBlocks.iterator();
                while (iterator.hasNext()) {
                    FallingBlock ent = iterator.next();
                    if (ent.isDead()) {
                        if (worldManager.isIslandWorld(ent.getLocation().getWorld()) && configLoad.getBoolean("Island.Block.Level.Enable")) {
                            Island island = islandManager.getIslandAtLocation(ent.getLocation());

                            if (island != null) {
                                CompatibleMaterial material = null;
                                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                                    material = CompatibleMaterial.getMaterial(ent.getBlockData().getMaterial());
                                } else {
                                    try {
                                        material = CompatibleMaterial.getMaterial((Material) ent.getClass().getMethod("getMaterial").invoke(ent));
                                    } catch (NoSuchMethodException | InvocationTargetException |
                                             IllegalAccessException ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                if (material != null) {
                                    IslandLevel level = island.getLevel();

                                    if (level.hasMaterial(material.name())) {
                                        long materialAmount = level.getMaterialAmount(material.name());

                                        if (materialAmount <= 1) {
                                            level.removeMaterial(material.name());
                                        } else {
                                            level.setMaterialAmount(material.name(), materialAmount - 1);
                                        }
                                    }
                                }
                            }
                        }
                        iterator.remove();
                    }
                    if (++counter > 50) { // Limit 50 checks per tick
                        break;
                    }
                }
            }
        }, 2L, 2L);
    }

    @EventHandler
    public void onFallingBlockModify(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            WorldManager worldManager = this.plugin.getWorldManager();
            if (worldManager.isIslandWorld(event.getEntity().getLocation().getWorld())) {
                if (event.getTo() != CompatibleMaterial.AIR.getMaterial()) {
                    this.fallingBlocks.remove(event.getEntity());
                } else if (!event.isCancelled()) {
                    this.fallingBlocks.add((FallingBlock) event.getEntity());
                }
            }
        }
    }
}
