package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandEnvironment;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import com.craftaro.skyblock.permission.event.events.PlayerEnterPortalEvent;
import com.craftaro.skyblock.utils.world.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;

public class PortalPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public PortalPermission(SkyBlock plugin) {
        super("Portal", XMaterial.ENDER_PEARL, PermissionType.GENERIC);
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
            XMaterial toMaterial = CompatibleMaterial.getMaterial(event.getTo().getBlock().getType()).orElse(null);

            if (toMaterial == XMaterial.NETHER_PORTAL || toMaterial == XMaterial.END_PORTAL) {
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
