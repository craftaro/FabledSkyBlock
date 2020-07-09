package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.permission.event.events.ProjectileLaunchByPlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

public class Projectile implements Listener {

    private final SkyBlock plugin;

    public Projectile(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        org.bukkit.entity.Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();
        if (!(shooter instanceof Player))
            return;

        Player player = (Player) shooter;

        if (!plugin.getWorldManager().isIslandWorld(player.getWorld()))
            return;

        // Check permissions.
        plugin.getPermissionManager().processPermission(new ProjectileLaunchByPlayerEvent(event.getEntity()), player, player.getLocation());
    }

}
