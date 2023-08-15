package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LeverButtonPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public LeverButtonPermission(SkyBlock plugin) {
        super("LeverButton", XMaterial.LEVER, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        XMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock().getType()).orElse(null);
        Player player = event.getPlayer();

        if (material == XMaterial.STONE_BUTTON || material == XMaterial.OAK_BUTTON
                || material == XMaterial.SPRUCE_BUTTON || material == XMaterial.BIRCH_BUTTON
                || material == XMaterial.JUNGLE_BUTTON || material == XMaterial.ACACIA_BUTTON
                || material == XMaterial.DARK_OAK_BUTTON || material == XMaterial.LEVER) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
