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

public class RedstonePermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public RedstonePermission(SkyBlock plugin) {
        super("Redstone", CompatibleMaterial.REDSTONE, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {


        Player player = event.getPlayer();
        CompatibleMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock());

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (material == CompatibleMaterial.COMPARATOR || material == CompatibleMaterial.REPEATER)
                cancelAndMessage(event, player, plugin, messageManager);
        } else if (event.getAction() == Action.PHYSICAL) {
            if (material == CompatibleMaterial.TRIPWIRE)
                cancelAndMessage(event, player, plugin, messageManager);
        }
    }
}
