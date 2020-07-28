package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ProtectorDamagePermission extends ListeningPermission  {
    
    private final SkyBlock plugin;
    
    public ProtectorDamagePermission(SkyBlock plugin) {
        super("ProtectorDamage", CompatibleMaterial.CARVED_PUMPKIN, PermissionType.ISLAND);
        this.plugin = plugin;
    }
    
    @PermissionHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof IronGolem ||
            event.getDamager() instanceof Snowman ||
                (event.getDamager() instanceof Wolf &&
                        ((Wolf) event.getDamager()).isTamed())) {
            event.setCancelled(true);
        }
    }
}
