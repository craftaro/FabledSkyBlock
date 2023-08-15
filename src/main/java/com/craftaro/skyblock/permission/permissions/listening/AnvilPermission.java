package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AnvilPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public AnvilPermission(SkyBlock plugin) {
        super("Anvil", XMaterial.ANVIL, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        XMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock().getType()).orElse(null);
        if (material == XMaterial.ANVIL) {
            cancelAndMessage(event, event.getPlayer(), this.plugin, this.messageManager);
        }
    }
}
