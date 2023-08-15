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

public class BedPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public BedPermission(SkyBlock plugin) {
        super("Bed", XMaterial.RED_BED, PermissionType.GENERIC);
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

        if (material == XMaterial.WHITE_BED || material == XMaterial.ORANGE_BED
                || material == XMaterial.MAGENTA_BED || material == XMaterial.LIGHT_BLUE_BED
                || material == XMaterial.YELLOW_BED || material == XMaterial.LIME_BED
                || material == XMaterial.PINK_BED || material == XMaterial.GRAY_BED
                || material == XMaterial.LIGHT_GRAY_BED || material == XMaterial.CYAN_BED
                || material == XMaterial.PURPLE_BED || material == XMaterial.BLUE_BED
                || material == XMaterial.BROWN_BED || material == XMaterial.GREEN_BED
                || material == XMaterial.RED_BED || material == XMaterial.BLACK_BED) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
