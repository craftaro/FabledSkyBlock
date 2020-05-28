package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionPriority;
import com.songoda.skyblock.permission.PermissionType;
import com.songoda.skyblock.permission.event.events.ProjectileLaunchByPlayerEvent;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class FishingPermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public FishingPermission(SkyBlock plugin) {
        super("Fishing", CompatibleMaterial.FISHING_ROD, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler(priority = PermissionPriority.LAST)
    public void onProjectileLaunch(ProjectileLaunchByPlayerEvent event) {
        org.bukkit.entity.Projectile projectile = event.getEntity();
        Player shooter = (Player) projectile.getShooter();
        if (projectile instanceof FishHook) {
            cancelAndMessage(event, shooter, plugin, messageManager);
            event.setStopped(true);
        }
    }
}
