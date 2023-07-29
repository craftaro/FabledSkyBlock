package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class ShearingPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public ShearingPermission(SkyBlock plugin) {
        super("Shearing", CompatibleMaterial.SHEARS, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onShear(PlayerShearEntityEvent event) {
        Player player = event.getPlayer();
        cancelAndMessage(event, player, this.plugin, this.messageManager);
    }
}
