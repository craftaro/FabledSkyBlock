package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class LeashPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public LeashPermission(SkyBlock plugin) {
        super("Leash", XMaterial.LEAD, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack is = player.getItemInHand();

        if (!CompatibleMaterial.isAir(CompatibleMaterial.getMaterial(is.getType()).orElse(XMaterial.STONE))) {
            if (XMaterial.LEAD.isSimilar(is)) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
            }
        }
    }
}
