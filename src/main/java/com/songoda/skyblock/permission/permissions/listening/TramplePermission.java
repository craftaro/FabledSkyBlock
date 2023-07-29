package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
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
        super("Trample", CompatibleMaterial.WHEAT_SEEDS, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        Player player = event.getPlayer();
        CompatibleMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock());

        if (material == CompatibleMaterial.TURTLE_EGG || material == CompatibleMaterial.FARMLAND) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
