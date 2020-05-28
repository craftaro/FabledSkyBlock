package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class StoragePermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public StoragePermission(SkyBlock plugin) {
        super("Storage", CompatibleMaterial.CHEST, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        CompatibleMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock());
        Player player = event.getPlayer();

        if (material == CompatibleMaterial.CHEST || material == CompatibleMaterial.TRAPPED_CHEST
                || (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9) && (material == CompatibleMaterial.SHULKER_BOX
                || material == CompatibleMaterial.BLACK_SHULKER_BOX || material == CompatibleMaterial.BLUE_SHULKER_BOX
                || material == CompatibleMaterial.BROWN_SHULKER_BOX || material == CompatibleMaterial.CYAN_SHULKER_BOX
                || material == CompatibleMaterial.GRAY_SHULKER_BOX || material == CompatibleMaterial.GREEN_SHULKER_BOX
                || material == CompatibleMaterial.LIGHT_BLUE_SHULKER_BOX || material == CompatibleMaterial.LIGHT_GRAY_SHULKER_BOX
                || material == CompatibleMaterial.LIME_SHULKER_BOX || material == CompatibleMaterial.MAGENTA_SHULKER_BOX
                || material == CompatibleMaterial.ORANGE_SHULKER_BOX || material == CompatibleMaterial.PINK_SHULKER_BOX
                || material == CompatibleMaterial.PURPLE_SHULKER_BOX || material == CompatibleMaterial.RED_SHULKER_BOX
                || material == CompatibleMaterial.WHITE_SHULKER_BOX || material == CompatibleMaterial.YELLOW_SHULKER_BOX))
                || ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14) && material == CompatibleMaterial.BARREL)
            cancelAndMessage(event, player, plugin, messageManager);
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked().getType() == EntityType.ITEM_FRAME
                || event.getRightClicked() instanceof StorageMinecart)
            cancelAndMessage(event, player, plugin, messageManager);

    }
}
