package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BucketPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public BucketPermission(SkyBlock plugin) {
        super("Bucket", CompatibleMaterial.BUCKET, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.getGameMode() == GameMode.SURVIVAL
                    && CompatibleMaterial.getMaterial(event.getClickedBlock()) == CompatibleMaterial.OBSIDIAN
                    && event.getItem() != null
                    && CompatibleMaterial.getMaterial(event.getItem()) != CompatibleMaterial.AIR
                    && CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.BUCKET) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
            }
        } else if (event.getItem() != null && CompatibleMaterial.getMaterial(event.getItem()) != CompatibleMaterial.AIR) {
            if (CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.BUCKET
                    || CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.WATER_BUCKET
                    || CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.LAVA_BUCKET) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
                player.updateInventory();
            }
        }
    }

    @PermissionHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        cancelAndMessage(event, event.getPlayer(), this.plugin, this.messageManager);
    }

    @PermissionHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        cancelAndMessage(event, event.getPlayer(), this.plugin, this.messageManager);
    }
}
