package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemPickupPermission extends ListeningPermission {
    public ItemPickupPermission() {
        super("ItemPickup", XMaterial.MELON_SEEDS, PermissionType.GENERIC);
    }

    @PermissionHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }
}
