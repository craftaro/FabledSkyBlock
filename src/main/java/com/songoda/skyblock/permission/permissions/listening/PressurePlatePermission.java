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

public class PressurePlatePermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public PressurePlatePermission(SkyBlock plugin) {
        super("PressurePlate", CompatibleMaterial.OAK_PRESSURE_PLATE, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {


        if (event.getAction() != Action.PHYSICAL)
            return;

        Player player = event.getPlayer();
        CompatibleMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock());

        if (material == CompatibleMaterial.STONE_PRESSURE_PLATE || material == CompatibleMaterial.OAK_PRESSURE_PLATE
                || material == CompatibleMaterial.SPRUCE_PRESSURE_PLATE || material == CompatibleMaterial.BIRCH_PRESSURE_PLATE
                || material == CompatibleMaterial.JUNGLE_PRESSURE_PLATE || material == CompatibleMaterial.ACACIA_PRESSURE_PLATE
                || material == CompatibleMaterial.DARK_OAK_PRESSURE_PLATE || material == CompatibleMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE
                || material == CompatibleMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE)
            cancelAndMessage(event, player, plugin, messageManager);
    }
}
