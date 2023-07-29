package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.IslandRole;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListeners implements Listener {
    private final SkyBlock plugin;

    public DeathListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (this.plugin.getWorldManager().isIslandWorld(player.getWorld())) {
            FileConfiguration configLoad = this.plugin.getConfiguration();

            boolean keepInventory = false;

            if (configLoad.getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
                if (this.plugin.getPermissionManager().hasPermission(player.getLocation(), "KeepItemsOnDeath",
                        IslandRole.OWNER)) {
                    keepInventory = true;
                }
            } else {
                keepInventory = configLoad.getBoolean("Island.KeepItemsOnDeath.Enable");
            }

            if (keepInventory) {
                event.setKeepInventory(true);
                event.getDrops().clear();
                event.setKeepLevel(true);
                event.setDroppedExp(0);
            }

            if (configLoad.getBoolean("Island.Death.AutoRespawn")) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                    player.spigot().respawn();
                    player.setFallDistance(0.0F);
                    player.setFireTicks(0);
                }, 1L);
            }
        }
    }
}
