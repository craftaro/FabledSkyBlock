package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class WaterCollectionPermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public WaterCollectionPermission(SkyBlock plugin) {
        super("WaterCollection", CompatibleMaterial.POTION, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {


        Player player = event.getPlayer();
        CompatibleMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock());
        if (event.getItem() != null && CompatibleMaterial.getMaterial(event.getItem()) != CompatibleMaterial.AIR) {
            if (CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.GLASS_BOTTLE) {
                if (material == CompatibleMaterial.WATER || material == CompatibleMaterial.CAULDRON) {
                    cancelAndMessage(event, player, plugin, messageManager);
                    player.updateInventory();
                }
            }
        }
    }
}
