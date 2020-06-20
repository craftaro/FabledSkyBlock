package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class MobGriefingPermission extends ListeningPermission {

    private final SkyBlock plugin;

    public MobGriefingPermission(SkyBlock plugin) {
        super("MobGriefing", CompatibleMaterial.IRON_SHOVEL, PermissionType.ISLAND);
        this.plugin = plugin;
    }

    @PermissionHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof Player)
                && (!(event.getEntity() instanceof org.bukkit.entity.Projectile)
                || !(((org.bukkit.entity.Projectile) event.getEntity()).getShooter() instanceof Player))
                && !(event.getEntity() instanceof TNTPrimed)
                && !(event.getEntity() instanceof ExplosiveMinecart)) {
            event.setCancelled(true);
        }
    }

    @PermissionHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player)
                && (!(event.getAttacker() instanceof org.bukkit.entity.Projectile)
                || !(((org.bukkit.entity.Projectile) event.getAttacker()).getShooter() instanceof Player))
                && !(event.getAttacker() instanceof TNTPrimed)
                && !(event.getAttacker() instanceof ExplosiveMinecart)) {
            event.setCancelled(true);
        }
    }

    @PermissionHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player)
                && (!(event.getAttacker() instanceof org.bukkit.entity.Projectile)
                || !(((org.bukkit.entity.Projectile) event.getAttacker()).getShooter() instanceof Player))
                && !(event.getAttacker() instanceof TNTPrimed)
                && !(event.getAttacker() instanceof ExplosiveMinecart)) {
            event.setCancelled(true);
        }
    }

    @PermissionHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)
                && (!(event.getDamager() instanceof org.bukkit.entity.Projectile)
                || !(((org.bukkit.entity.Projectile) event.getDamager()).getShooter() instanceof Player))
                && !(event.getDamager() instanceof TNTPrimed)
                && !(event.getDamager() instanceof ExplosiveMinecart)) {
            event.setCancelled(true);
        }
    }

    @PermissionHandler
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if(!(event.getRemover() instanceof Player)){
            event.setCancelled(true);
        }
    }

    /*@PermissionHandler // TODO ? - Fabrimat
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        EntityType type = event.getEntityType();

        if (type == EntityType.ARMOR_STAND || type == EntityType.PLAYER || entity instanceof Monster) return;

        Player player;
        if (event.getDamager() instanceof Player)
            player = (Player)event.getDamager();
        else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player)
            player = (Player) ((Projectile) event.getDamager()).getShooter();
        else return;

        cancelAndMessage(event, player, plugin, messageManager);
    }*/
}

