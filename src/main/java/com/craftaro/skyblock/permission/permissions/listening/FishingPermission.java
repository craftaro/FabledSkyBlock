package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionPriority;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class FishingPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public FishingPermission(SkyBlock plugin) {
        super("Fishing", XMaterial.FISHING_ROD, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler(priority = PermissionPriority.LAST)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        org.bukkit.entity.Projectile projectile = event.getEntity();
        if (projectile instanceof FishHook && projectile.getShooter() instanceof Player) {
            Player shooter = (Player) projectile.getShooter();
            cancelAndMessage(event, shooter, this.plugin, this.messageManager);
        }
    }
}
