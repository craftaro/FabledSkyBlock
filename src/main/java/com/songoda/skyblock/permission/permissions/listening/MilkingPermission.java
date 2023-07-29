package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MilkingPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public MilkingPermission(SkyBlock plugin) {
        super("Milking", CompatibleMaterial.MILK_BUCKET, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {


        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity.getType() == EntityType.COW) {
            if (CompatibleMaterial.getMaterial(player.getItemInHand()) == CompatibleMaterial.BUCKET) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
            }
        } else if (entity.getType() == EntityType.MUSHROOM_COW) {
            if (CompatibleMaterial.getMaterial(player.getItemInHand()) == CompatibleMaterial.BUCKET
                    || CompatibleMaterial.getMaterial(player.getItemInHand()) == CompatibleMaterial.BOWL) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
            }
        }
    }
}
