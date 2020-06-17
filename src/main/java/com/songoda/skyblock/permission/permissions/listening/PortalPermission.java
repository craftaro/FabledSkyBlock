package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.*;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import com.songoda.skyblock.permission.event.events.PlayerEnterPortalEvent;
import com.songoda.skyblock.utils.world.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
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
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();

            cancelAndMessage(event, player, plugin, messageManager);
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.teleport(getToLocation(event.getLocation(), player));
            });
        }
    }

    @PermissionHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
                || event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)
                || event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)
                || (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)
                    && event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_GATEWAY))){
            event.getPlayer().teleport(getToLocation(event.getFrom(), event.getPlayer()));
            Location to = getToLocation(event.getFrom(), event.getPlayer());
            Bukkit.getScheduler().runTask(plugin, () -> {
                event.getPlayer().teleport(to);
            });
            event.setTo(to);

            cancelAndMessage(event, event.getPlayer(), plugin, messageManager);
        }
    }

    private Location getToLocation(Location from, Player player) {
        IslandManager islandManager = plugin.getIslandManager();
        Island island = islandManager.getIslandAtLocation(from);
        Location to = island.getLocation(IslandWorld.Normal, IslandEnvironment.Main);
        if(island.hasRole(IslandRole.Visitor, player.getUniqueId())){
            to = LocationUtil.getSafeLocation(island.getLocation(IslandWorld.Normal, IslandEnvironment.Visitor));
            if(to == null){
                to = LocationUtil.getSpawnLocation();
            }
        }
        return to;
    }
}

