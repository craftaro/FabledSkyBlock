package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.Bukkit;
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

    private SkyBlock plugin;

    public ExplosionsPermission(SkyBlock plugin) {
        super("Explosions", CompatibleMaterial.GUNPOWDER, PermissionType.ISLAND);
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
                || event.getAttacker() instanceof Creeper)
            event.setCancelled(true);
    }

    @PermissionHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() instanceof TNTPrimed
                || event.getAttacker() instanceof ExplosiveMinecart
                || event.getAttacker() instanceof Creeper)
            event.setCancelled(true);
    }

    @PermissionHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                || event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
                || event.getDamager() instanceof TNTPrimed
                || event.getDamager() instanceof ExplosiveMinecart
                || event.getDamager() instanceof Creeper)
            event.setCancelled(true);
    }

    @PermissionHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (event.getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION)) {
            event.setCancelled(true);
        }
    }

    @PermissionHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if(event.getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION)){
            event.setCancelled(true);
        }
    }

    @PermissionHandler
    public void onTNTInteract(PlayerInteractEvent event) {
        if(event.getItem().getType().equals(CompatibleMaterial.FLINT_AND_STEEL.getMaterial())
                && event.getClickedBlock().getType().equals(CompatibleMaterial.TNT.getBlockMaterial())){
            cancelAndMessage(event, event.getPlayer(), plugin, plugin.getMessageManager());
        }
    }
}
