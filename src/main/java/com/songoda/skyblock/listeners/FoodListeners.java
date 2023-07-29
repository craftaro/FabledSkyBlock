package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodListeners implements Listener {
    private final SkyBlock plugin;

    public FoodListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();

        if (this.plugin.getWorldManager().isIslandWorld(player.getWorld())) {
            // Check permissions.
            this.plugin.getPermissionManager().processPermission(event, player, player.getLocation());
        }
    }
}
