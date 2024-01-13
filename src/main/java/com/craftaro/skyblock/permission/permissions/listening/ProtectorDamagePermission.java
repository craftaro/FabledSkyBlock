package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ProtectorDamagePermission extends ListeningPermission {
    public ProtectorDamagePermission(SkyBlock plugin) {
        super("ProtectorDamage", XMaterial.CARVED_PUMPKIN, PermissionType.ISLAND);
    }

    @PermissionHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof IronGolem ||
                event.getDamager() instanceof Snowman ||
                (event.getDamager() instanceof Wolf && ((Wolf) event.getDamager()).isTamed())) {
            event.setCancelled(true);
        }
    }
}
