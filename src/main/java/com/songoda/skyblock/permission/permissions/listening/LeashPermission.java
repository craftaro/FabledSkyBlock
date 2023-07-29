package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class LeashPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public LeashPermission(SkyBlock plugin) {
        super("Leash", CompatibleMaterial.LEAD, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack is = player.getItemInHand();

        if (CompatibleMaterial.getMaterial(is) != CompatibleMaterial.AIR) {
            if (CompatibleMaterial.getMaterial(is) == CompatibleMaterial.LEAD) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
            }
        }
    }
}
