package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class Food implements Listener {

    private final SkyBlock skyblock;

    public Food(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();

        if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
            // Check permissions.
            skyblock.getPermissionManager().processPermission(event, player, player.getLocation());
        }
    }
}
