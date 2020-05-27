package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import com.songoda.skyblock.permission.event.events.PlayerEnterPortalEvent;
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.utils.world.LocationUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PortalPermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public PortalPermission(SkyBlock plugin) {
        super("Portal", CompatibleMaterial.ENDER_PEARL, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onPortalEnter(PlayerEnterPortalEvent event) {
        Player player = (Player) event.getEntity();

        cancelAndMessage(event, player, plugin, messageManager);
    }

    @PermissionHandler
    public void onMove(PlayerMoveEvent event) {
        CompatibleMaterial toMaterial = CompatibleMaterial.getMaterial(event.getTo().getBlock().getType());

        if (toMaterial == CompatibleMaterial.NETHER_BRICK || toMaterial == CompatibleMaterial.END_PORTAL) {
            event.setTo(LocationUtil.getRandomLocation(event.getFrom().getWorld(), 5000, 5000, true, true));
            cancelAndMessage(event, event.getPlayer(), plugin, messageManager);
        }
    }

    @PermissionHandler
    public void onTeleport(PlayerTeleportEvent event) {
        boolean isCause = false;

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL || event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL || event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            isCause = true;
        } else {
            if (NMSUtil.getVersionNumber() > 9) {
                if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
                    isCause = true;
                }
            }
        }

        if (isCause)
            cancelAndMessage(event, event.getPlayer(), plugin, messageManager);
    }
}

