package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class Inventory implements Listener {

    private final SkyBlock skyblock;

    public Inventory(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
            // Check permissions.
            skyblock.getPermissionManager().processPermission(event, player,
                    skyblock.getIslandManager().getIsland(player));
        }
    }
}
