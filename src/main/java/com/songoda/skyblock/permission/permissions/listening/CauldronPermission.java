package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CauldronPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public CauldronPermission(SkyBlock plugin) {
        super("Cauldron", XMaterial.CAULDRON, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getItem() != null && !event.getItem().getType().isBlock()) {
                Player player = event.getPlayer();
                Block block = event.getClickedBlock();


                if (CompatibleMaterial.getMaterial(block.getType()).orElse(null) == XMaterial.CAULDRON) {
                    cancelAndMessage(event, player, this.plugin, this.messageManager);
                }
            }
        }
    }
}
