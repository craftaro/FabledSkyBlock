package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.permission.event.events.PlayerEnterPortalEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

@SuppressWarnings("deprecation")
public class Item implements Listener {

    private final SkyBlock skyblock;

    public Item(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        IslandManager islandManager = skyblock.getIslandManager();
        Player player = event.getPlayer();

        if (!skyblock.getWorldManager().isIslandWorld(player.getWorld())) return;

        // Check permissions.
        skyblock.getPermissionManager().processPermission(event, player,
                islandManager.getIslandAtLocation(event.getItemDrop().getLocation()));
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        IslandManager islandManager = skyblock.getIslandManager();
        Player player = event.getPlayer();

        if (!skyblock.getWorldManager().isIslandWorld(player.getWorld())) return;

        // Check permissions.
        skyblock.getPermissionManager().processPermission(event, player,
                islandManager.getIslandAtLocation(event.getItem().getLocation()));
    }
}
