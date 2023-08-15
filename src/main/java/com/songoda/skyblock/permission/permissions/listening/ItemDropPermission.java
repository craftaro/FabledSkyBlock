package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
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
