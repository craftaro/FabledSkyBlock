package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class ExplosionsPermission extends ListeningPermission {
    private final SkyBlock plugin;

    public ExplosionsPermission(SkyBlock plugin) {
        super("Explosions", XMaterial.GUNPOWDER, PermissionType.ISLAND);
        this.plugin = plugin;
    }

    @PermissionHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

    @PermissionHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @PermissionHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (event.getAttacker() instanceof TNTPrimed
                || event.getAttacker() instanceof ExplosiveMinecart
                || event.getAttacker() instanceof Creeper) {
            event.setCancelled(true);
        }
    }

    @PermissionHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() instanceof TNTPrimed
                || event.getAttacker() instanceof ExplosiveMinecart
                || event.getAttacker() instanceof Creeper) {
            event.setCancelled(true);
        }
    }

    @PermissionHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                || event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                || event.getDamager() instanceof TNTPrimed
                || event.getDamager() instanceof ExplosiveMinecart
                || event.getDamager() instanceof Creeper) {
            event.setCancelled(true);
        }
    }

    @PermissionHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (event.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) {
            event.setCancelled(true);
        }
    }

    @PermissionHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if (event.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) {
            event.setCancelled(true);
        }
    }

    @PermissionHandler
    public void onTNTInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == XMaterial.FLINT_AND_STEEL.parseMaterial()
                && CompatibleMaterial.getMaterial(event.getClickedBlock().getType()).orElse(null) == XMaterial.TNT) {
            cancelAndMessage(event, event.getPlayer(), this.plugin, this.plugin.getMessageManager());
        }
    }
}
