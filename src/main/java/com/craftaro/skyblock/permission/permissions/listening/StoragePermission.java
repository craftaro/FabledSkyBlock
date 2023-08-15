package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class StoragePermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public StoragePermission(SkyBlock plugin) {
        super("Storage", XMaterial.CHEST, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Optional<XMaterial> material = CompatibleMaterial.getMaterial(event.getClickedBlock().getType());
            Player player = event.getPlayer();

            if (material.isPresent()) {
                switch (material.get()) {
                    case CHEST:
                    case TRAPPED_CHEST:
                    case DROPPER:
                    case DISPENSER:
                    case SHULKER_BOX:
                    case BLACK_SHULKER_BOX:
                    case BLUE_SHULKER_BOX:
                    case BROWN_SHULKER_BOX:
                    case CYAN_SHULKER_BOX:
                    case GRAY_SHULKER_BOX:
                    case GREEN_SHULKER_BOX:
                    case LIGHT_BLUE_SHULKER_BOX:
                    case LIGHT_GRAY_SHULKER_BOX:
                    case LIME_SHULKER_BOX:
                    case MAGENTA_SHULKER_BOX:
                    case ORANGE_SHULKER_BOX:
                    case PURPLE_SHULKER_BOX:
                    case PINK_SHULKER_BOX:
                    case RED_SHULKER_BOX:
                    case YELLOW_SHULKER_BOX:
                    case WHITE_SHULKER_BOX:
                    case BARREL:
                        cancelAndMessage(event, player, this.plugin, this.messageManager);
                }
            }
        }

    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked().getType().equals(EntityType.ITEM_FRAME)
                || event.getRightClicked() instanceof StorageMinecart) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
