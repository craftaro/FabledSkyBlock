package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class NoteblockPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public NoteblockPermission(SkyBlock plugin) {
        super("Noteblock", XMaterial.NOTE_BLOCK, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (CompatibleMaterial.getMaterial(block.getType()).orElse(null) == XMaterial.NOTE_BLOCK) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
