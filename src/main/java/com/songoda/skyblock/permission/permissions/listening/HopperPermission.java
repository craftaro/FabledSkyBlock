package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class HopperPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public HopperPermission(SkyBlock plugin) {
        super("Hopper", CompatibleMaterial.HOPPER, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (CompatibleMaterial.getMaterial(block) == CompatibleMaterial.HOPPER) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.MINECART_HOPPER) {
            cancelAndMessage(event, event.getPlayer(), this.plugin, this.messageManager);
        }
    }
}
