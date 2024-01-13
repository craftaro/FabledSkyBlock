package com.craftaro.skyblock.listeners;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandLevel;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.levelling.IslandLevelManager;
import com.craftaro.skyblock.stackable.StackableManager;
import com.craftaro.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SpongeAbsorbEvent;

public class SpongeListeners implements Listener {
    private final SkyBlock plugin;

    public SpongeListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSponge(SpongeAbsorbEvent event) {
        IslandLevelManager islandLevelManager = this.plugin.getLevellingManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        StackableManager stackableManager = this.plugin.getStackableManager();
        WorldManager worldManager = this.plugin.getWorldManager();

        org.bukkit.block.Block block = event.getBlock();

        if (worldManager.isIslandWorld(block.getWorld())) {
            Location blockLocation = block.getLocation();

            Island island = islandManager.getIslandAtLocation(blockLocation);
            if (island != null) {
                if (this.plugin.getPermissionManager().processPermission(event, island) && !event.isCancelled()) {
                    if (stackableManager == null || !stackableManager.isStacked(blockLocation)) {
                        IslandLevel level = island.getLevel();

                        XMaterial material = XMaterial.SPONGE;
                        if (level.hasMaterial(material.name())) {
                            long materialAmount = level.getMaterialAmount(material.name());

                            if (materialAmount - 1 <= 0) {
                                level.removeMaterial(material.name());
                            } else {
                                level.setMaterialAmount(material.name(), materialAmount - 1);
                            }

                            Bukkit.getScheduler().runTask(this.plugin, () -> islandLevelManager.updateLevel(island, blockLocation));
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
