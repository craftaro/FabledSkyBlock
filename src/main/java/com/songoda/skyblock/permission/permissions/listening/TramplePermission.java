package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TramplePermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public TramplePermission(SkyBlock plugin) {
        super("Trample", XMaterial.WHEAT_SEEDS, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        Player player = event.getPlayer();
        XMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock().getType()).orElse(null);

        if (material == XMaterial.TURTLE_EGG || material == XMaterial.FARMLAND) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
