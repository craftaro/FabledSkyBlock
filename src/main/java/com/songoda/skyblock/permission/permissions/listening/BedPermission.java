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

public class BedPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public BedPermission(SkyBlock plugin) {
        super("Bed", CompatibleMaterial.RED_BED, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        CompatibleMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock());
        Player player = event.getPlayer();

        if (material == CompatibleMaterial.WHITE_BED || material == CompatibleMaterial.ORANGE_BED
                || material == CompatibleMaterial.MAGENTA_BED || material == CompatibleMaterial.LIGHT_BLUE_BED
                || material == CompatibleMaterial.YELLOW_BED || material == CompatibleMaterial.LIME_BED
                || material == CompatibleMaterial.PINK_BED || material == CompatibleMaterial.GRAY_BED
                || material == CompatibleMaterial.LIGHT_GRAY_BED || material == CompatibleMaterial.CYAN_BED
                || material == CompatibleMaterial.PURPLE_BED || material == CompatibleMaterial.BLUE_BED
                || material == CompatibleMaterial.BROWN_BED || material == CompatibleMaterial.GREEN_BED
                || material == CompatibleMaterial.RED_BED || material == CompatibleMaterial.BLACK_BED) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
