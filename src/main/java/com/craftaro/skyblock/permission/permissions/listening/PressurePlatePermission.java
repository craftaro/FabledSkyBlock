package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PressurePlatePermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public PressurePlatePermission(SkyBlock plugin) {
        super("PressurePlate", XMaterial.OAK_PRESSURE_PLATE, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        XMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock().getType()).orElse(null);

        if (material == XMaterial.STONE_PRESSURE_PLATE || material == XMaterial.OAK_PRESSURE_PLATE
                || material == XMaterial.SPRUCE_PRESSURE_PLATE || material == XMaterial.BIRCH_PRESSURE_PLATE
                || material == XMaterial.JUNGLE_PRESSURE_PLATE || material == XMaterial.ACACIA_PRESSURE_PLATE
                || material == XMaterial.DARK_OAK_PRESSURE_PLATE || material == XMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE
                || material == XMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            cancelAndMessage(event, event.getPlayer(), this.plugin, this.messageManager);
        }
    }
}
