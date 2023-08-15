package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDropPermission extends ListeningPermission {
    public ItemDropPermission() {
        super("ItemDrop", XMaterial.PUMPKIN_SEEDS, PermissionType.GENERIC);
    }

    @PermissionHandler
    public void ondropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
