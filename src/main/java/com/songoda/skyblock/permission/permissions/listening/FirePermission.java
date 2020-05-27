package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class FirePermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public FirePermission(SkyBlock plugin) {
        super("Fire", CompatibleMaterial.FLINT_AND_STEEL, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {


        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (CompatibleMaterial.getMaterial(player.getTargetBlock(null, 5)) == CompatibleMaterial.FIRE)
            cancelAndMessage(event, player, plugin, messageManager);
    }
}
