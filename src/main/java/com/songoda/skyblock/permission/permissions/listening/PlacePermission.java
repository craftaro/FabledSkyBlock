package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlacePermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public PlacePermission(SkyBlock plugin) {
        super("Place", CompatibleMaterial.DIRT, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();

        if (event.getItem() != null && CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.BONE_MEAL)
            cancelAndMessage(event, player, plugin, messageManager);
    }

    @PermissionHandler
    public void onPlace(BlockPlaceEvent event) {
        cancelAndMessage(event, event.getPlayer(), plugin, messageManager);
    }
}
