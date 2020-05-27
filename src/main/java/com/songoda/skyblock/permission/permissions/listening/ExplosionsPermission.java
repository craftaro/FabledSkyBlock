package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

public class ExplosionsPermission extends ListeningPermission {

    public ExplosionsPermission() {
        super("Explosions", CompatibleMaterial.GUNPOWDER, PermissionType.GENERIC);
    }

    @PermissionHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (event.getDamager() instanceof TNTPrimed)
            event.setCancelled(true);
        if (entity.getType() == EntityType.PLAYER
                && event.getDamager() instanceof TNTPrimed)
            event.setCancelled(true);
    }

    @PermissionHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (event.getCause() != HangingBreakEvent.RemoveCause.EXPLOSION)
            return;

        event.setCancelled(true);
    }
}
