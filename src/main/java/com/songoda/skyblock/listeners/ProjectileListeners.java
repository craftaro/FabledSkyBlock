package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

public class ProjectileListeners implements Listener {
    private final SkyBlock plugin;

    public ProjectileListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        org.bukkit.entity.Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();
        if (shooter instanceof Player) {
            Player player = (Player) shooter;

            if (this.plugin.getWorldManager().isIslandWorld(player.getWorld())) {
                this.plugin.getPermissionManager().processPermission(event, player, player.getLocation());
            }
        }
    }
}
