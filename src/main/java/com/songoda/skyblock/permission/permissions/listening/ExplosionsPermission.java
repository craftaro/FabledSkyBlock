package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ExplosionsPermission extends ListeningPermission {

    private SkyBlock plugin;

    public ExplosionsPermission(SkyBlock plugin) {
        super("Explosions", CompatibleMaterial.GUNPOWDER, PermissionType.GENERIC);
        this.plugin = plugin;
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

    @PermissionHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (CompatibleMaterial.getMaterial(event.getPlayer().getItemInHand()) != CompatibleMaterial.FLINT_AND_STEEL)
            return;

        CompatibleMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock());
        if (material == CompatibleMaterial.TNT)
            cancelAndMessage(event, event.getPlayer(), plugin, plugin.getMessageManager());
    }
}
