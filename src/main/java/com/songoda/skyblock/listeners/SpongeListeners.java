package com.songoda.skyblock.listeners;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
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
