package com.songoda.skyblock.listeners;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SpongeAbsorbEvent;

public class Sponge implements Listener {
    
    private final SkyBlock plugin;
    
    public Sponge(SkyBlock plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onSponge(SpongeAbsorbEvent event) {
        IslandLevelManager islandLevelManager = plugin.getLevellingManager();
        IslandManager islandManager = plugin.getIslandManager();
        StackableManager stackableManager = plugin.getStackableManager();
        WorldManager worldManager = plugin.getWorldManager();
        
        org.bukkit.block.Block block = event.getBlock();
        
        if (worldManager.isIslandWorld(block.getWorld())) {
            Location blockLocation = block.getLocation();
            
            Island island = islandManager.getIslandAtLocation(blockLocation);
            if (island != null) {
                if (plugin.getPermissionManager().processPermission(event, island) && !event.isCancelled()) {
                    if (stackableManager == null || !stackableManager.isStacked(blockLocation)) {
                        IslandLevel level = island.getLevel();
                        
                        CompatibleMaterial material = CompatibleMaterial.SPONGE;
                        if (level.hasMaterial(material.name())) {
                            long materialAmount = level.getMaterialAmount(material.name());
                            
                            if (materialAmount - 1 <= 0) {
                                level.removeMaterial(material.name());
                            } else {
                                level.setMaterialAmount(material.name(), materialAmount - 1);
                            }
                            
                            Bukkit.getScheduler().runTask(plugin, () -> islandLevelManager.updateLevel(island, blockLocation));
                        }
                    } else {
                        event.setCancelled(true);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
        
    }
}
