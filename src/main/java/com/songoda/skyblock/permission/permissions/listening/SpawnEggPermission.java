package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpawnEggPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public SpawnEggPermission(SkyBlock plugin) {
        super("SpawnEgg", XMaterial.EGG, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && !CompatibleMaterial.isAir(CompatibleMaterial.getMaterial(event.getItem().getType()).orElse(XMaterial.STONE))) {
            if (event.getItem().getType().name().contains("SPAWN_EGG") || event.getItem().getType().name().equals("MONSTER_EGG")) {
                Player player = event.getPlayer();
                cancelAndMessage(event, player, this.plugin, this.messageManager);
                player.updateInventory();
            }
        }
    }
}
