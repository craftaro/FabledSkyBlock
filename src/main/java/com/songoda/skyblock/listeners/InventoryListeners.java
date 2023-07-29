package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryListeners implements Listener {
    private final SkyBlock plugin;

    public InventoryListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if (this.plugin.getWorldManager().isIslandWorld(player.getWorld())) {
            // Check permissions.
            this.plugin.getPermissionManager().processPermission(event, player, this.plugin.getIslandManager().getIsland(player));
        }
    }
}
