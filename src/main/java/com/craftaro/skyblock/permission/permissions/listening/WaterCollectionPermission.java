package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class WaterCollectionPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public WaterCollectionPermission(SkyBlock plugin) {
        super("WaterCollection", XMaterial.POTION, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        XMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock().getType()).orElse(null);
        if (event.getItem() != null && !CompatibleMaterial.isAir(CompatibleMaterial.getMaterial(event.getItem().getType()).orElse(XMaterial.STONE))) {
            if (XMaterial.GLASS_BOTTLE.isSimilar(event.getItem())) {
                if (material == XMaterial.WATER || material == XMaterial.CAULDRON) {
                    cancelAndMessage(event, player, this.plugin, this.messageManager);
                    player.updateInventory();
                }
            }
        }
    }
}
