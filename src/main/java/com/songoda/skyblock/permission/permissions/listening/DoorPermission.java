package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Openable;

public class DoorPermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public DoorPermission(SkyBlock plugin) {
        super("Door", CompatibleMaterial.OAK_DOOR, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();

        CompatibleMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock());
        if (material == CompatibleMaterial.BIRCH_DOOR || material == CompatibleMaterial.ACACIA_DOOR
                || material == CompatibleMaterial.DARK_OAK_DOOR || material == CompatibleMaterial.JUNGLE_DOOR
                || material == CompatibleMaterial.SPRUCE_DOOR || material == CompatibleMaterial.OAK_DOOR)
            cancelAndMessage(event, player, plugin, messageManager);



    }
}

