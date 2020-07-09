package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.IslandRole;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;

public class Death implements Listener {

    private final SkyBlock plugin;

    public Death(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (plugin.getWorldManager().isIslandWorld(player.getWorld())) {
            Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            boolean keepInventory = false;

            if (configLoad.getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
                if (plugin.getPermissionManager().hasPermission(player.getLocation(),"KeepItemsOnDeath",
                        IslandRole.Owner)) {
                    keepInventory = true;
                }
            } else keepInventory = configLoad.getBoolean("Island.KeepItemsOnDeath.Enable");

            if (keepInventory) {
                event.setKeepInventory(true);
                event.getDrops().clear();
                event.setKeepLevel(true);
                event.setDroppedExp(0);
            }

            if (configLoad.getBoolean("Island.Death.AutoRespawn")) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    player.spigot().respawn();
                    player.setFallDistance(0.0F);
                    player.setFireTicks(0);
                }, 1L);
            }
        }
    }
}
