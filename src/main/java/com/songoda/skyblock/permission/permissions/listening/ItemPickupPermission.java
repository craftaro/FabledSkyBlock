package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemPickupPermission extends ListeningPermission {

    public ItemPickupPermission() {
        super("ItemPickup", CompatibleMaterial.MELON_SEEDS, PermissionType.GENERIC);
    }

    @PermissionHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }
}
