package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionPriority;
import com.songoda.skyblock.permission.PermissionType;
import com.songoda.skyblock.permission.event.events.ProjectileLaunchByPlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ProjectilePermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public ProjectilePermission(SkyBlock plugin) {
        super("Projectile", CompatibleMaterial.ARROW, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR)
            return;

        Player player = event.getPlayer();
        if (event.getItem() != null && CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.EGG)
            cancelAndMessage(event, player, plugin, messageManager);
    }

    @PermissionHandler(priority = PermissionPriority.LAST)
    public void onProjectileLaunch(ProjectileLaunchByPlayerEvent event) {
        event.setCancelled(true);
    }
}
