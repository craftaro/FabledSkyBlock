package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ArmorStandUsePermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public ArmorStandUsePermission(SkyBlock plugin) {
        super("ArmorStandUse", CompatibleMaterial.ARMOR_STAND, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked() instanceof ArmorStand)
            cancelAndMessage(event, player, this.plugin, this.messageManager);
    }

    @PermissionHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        cancelAndMessage(event, event.getPlayer(), this.plugin, this.messageManager);
    }
}
