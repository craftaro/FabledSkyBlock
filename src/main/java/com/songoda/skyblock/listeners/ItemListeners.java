package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.IslandManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemListeners implements Listener {
    private final SkyBlock plugin;

    public ItemListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        IslandManager islandManager = this.plugin.getIslandManager();
        Player player = event.getPlayer();

        if (!this.plugin.getWorldManager().isIslandWorld(player.getWorld())) {
            return;
        }

        // Check permissions.
        this.plugin.getPermissionManager().processPermission(event, player, islandManager.getIslandAtLocation(event.getItemDrop().getLocation()));
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        IslandManager islandManager = this.plugin.getIslandManager();
        Player player = event.getPlayer();

        if (!this.plugin.getWorldManager().isIslandWorld(player.getWorld())) {
            return;
        }

        // Check permissions.
        this.plugin.getPermissionManager().processPermission(event, player, islandManager.getIslandAtLocation(event.getItem().getLocation()));
    }
}
