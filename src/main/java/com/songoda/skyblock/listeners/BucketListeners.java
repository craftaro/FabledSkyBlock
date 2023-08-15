package com.songoda.skyblock.listeners;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BucketListeners implements Listener {
    private final SkyBlock plugin;

    public BucketListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlockClicked();

        IslandManager islandManager = this.plugin.getIslandManager();

        XMaterial clickedBlock = CompatibleMaterial.getMaterial(event.getBlockClicked().getType()).orElse(null);

        if (clickedBlock == XMaterial.WATER || clickedBlock == XMaterial.LAVA) {
            if (this.plugin.getWorldManager().isIslandWorld(block.getWorld())) {
                Island island = islandManager.getIslandAtLocation(block.getLocation());
                // Check permissions.
                if (!this.plugin.getPermissionManager().processPermission(event, player, island)) {
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlockClicked().getRelative(event.getBlockFace());

        WorldManager worldManager = this.plugin.getWorldManager();
        IslandManager islandManager = this.plugin.getIslandManager();

        if (this.plugin.getWorldManager().isIslandWorld(block.getWorld())) {
            Island island = islandManager.getIslandAtLocation(block.getLocation());
            // Check permissions.
            if (!this.plugin.getPermissionManager().processPermission(event, player, island)) {
                return;
            }
        }

        if (!this.plugin.getConfiguration().getBoolean("Island.Spawn.Protection")) {
            return;
        }

        Island island = islandManager.getIslandAtLocation(block.getLocation());

        if (island == null) {
            return;
        }

        // Check spawn block protection
        IslandWorld world = worldManager.getIslandWorld(block.getWorld());
        if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
            event.setCancelled(true);
            this.plugin.getMessageManager().sendMessage(player, this.plugin.getLanguage().getString("Island.SpawnProtection.Place.Message"));
            this.plugin.getSoundManager().playSound(player, XSound.ENTITY_VILLAGER_NO);
        }
    }
}
