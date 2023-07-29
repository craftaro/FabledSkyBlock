package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import com.songoda.skyblock.permission.event.events.PlayerEnterPortalEvent;
import com.songoda.skyblock.utils.world.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;

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
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }

    @PermissionHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() != null) {
            CompatibleMaterial toMaterial = CompatibleMaterial.getMaterial(event.getTo().getBlock().getType());

            if (toMaterial == CompatibleMaterial.NETHER_PORTAL || toMaterial == CompatibleMaterial.END_PORTAL) {
                //event.setTo(LocationUtil.getRandomLocation(event.getFrom().getWorld(), 5000, 5000, true, true));
                cancelAndMessage(event, event.getPlayer(), this.plugin, this.messageManager);
            }
        }
    }


    @PermissionHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL
                || event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL
                || event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL
                || (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)
                && event.getCause() == PlayerTeleportEvent.TeleportCause.END_GATEWAY)) {
            /*event.getPlayer().teleport(getToLocation(event.getFrom(), event.getPlayer()));
            Location to = getToLocation(event.getFrom(), event.getPlayer());
            Bukkit.getScheduler().runTask(plugin, () -> {
                event.getPlayer().teleport(to);
            });
            event.setTo(to);*/

            cancelAndMessage(event, event.getPlayer(), this.plugin, this.messageManager);
        }
    }

    private Location getToLocation(Location from, Player player) {
        IslandManager islandManager = this.plugin.getIslandManager();
        Island island = islandManager.getIslandAtLocation(from);
        Location to = island.getLocation(IslandWorld.NORMAL, IslandEnvironment.MAIN);
        if (island.hasRole(IslandRole.VISITOR, player.getUniqueId())) {
            if (this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "config.yml"))
                    .getFileConfiguration().getBoolean("Island.Teleport.SafetyCheck", true)) {
                Location isLoc = island.getLocation(IslandWorld.NORMAL, IslandEnvironment.VISITOR);

                if (isLoc != null) {
                    Location safeLoc = LocationUtil.getSafeLocation(isLoc);
                    if (safeLoc != null) {
                        to = safeLoc;
                    }
                }
            }
            if (to == null) {
                to = LocationUtil.getSpawnLocation();
            }
        }
        return to;
    }
}
