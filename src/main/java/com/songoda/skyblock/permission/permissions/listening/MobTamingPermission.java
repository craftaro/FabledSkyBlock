package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public class MobTamingPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public MobTamingPermission(SkyBlock plugin) {
        super("MobTaming", CompatibleMaterial.POPPY, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        Player player = (Player) event.getAttacker();
        cancelAndMessage(event, player, this.plugin, this.messageManager);
    }

    @PermissionHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Player player = (Player) event.getAttacker();
        cancelAndMessage(event, player, this.plugin, this.messageManager);
    }

    @PermissionHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        EntityType type = event.getEntityType();

        if (type == EntityType.ARMOR_STAND || type == EntityType.PLAYER || entity instanceof Monster) {
            return;
        }

        Player player;
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            player = (Player) ((Projectile) event.getDamager()).getShooter();
        } else {
            return;
        }

        cancelAndMessage(event, player, this.plugin, this.messageManager);
    }
}
