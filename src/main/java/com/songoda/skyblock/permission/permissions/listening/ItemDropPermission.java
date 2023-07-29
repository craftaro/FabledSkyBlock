package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDropPermission extends ListeningPermission {
    public ItemDropPermission() {
        super("ItemDrop", CompatibleMaterial.PUMPKIN_SEEDS, PermissionType.GENERIC);
    }

    @PermissionHandler
    public void ondropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
