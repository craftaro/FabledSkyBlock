package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class EntityPlacementPermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public EntityPlacementPermission(SkyBlock plugin) {
        super("EntityPlacement", CompatibleMaterial.SHEEP_SPAWN_EGG, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {


        Player player = event.getPlayer();
        if (event.getItem() != null && CompatibleMaterial.getMaterial(event.getItem()) != CompatibleMaterial.AIR) {
            if (CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.ARMOR_STAND
                    || event.getItem().getType().name().contains("BOAT")
                    || event.getItem().getType().name().contains("MINECART")) {
                cancelAndMessage(event, player, plugin, messageManager);
                player.updateInventory();
            }
        }
    }
}
