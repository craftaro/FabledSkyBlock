package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
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
        super("Bucket", XMaterial.BUCKET, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.getGameMode() == GameMode.SURVIVAL
                    && CompatibleMaterial.getMaterial(event.getClickedBlock().getType()).orElse(null) == XMaterial.OBSIDIAN
                    && event.getItem() != null
                    && !CompatibleMaterial.isAir(CompatibleMaterial.getMaterial(event.getItem().getType()).orElse(XMaterial.STONE))
                    && XMaterial.BUCKET.isSimilar(event.getItem())) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
            }
        } else if (event.getItem() != null && !CompatibleMaterial.isAir(CompatibleMaterial.getMaterial(event.getItem().getType()).orElse(XMaterial.STONE))) {
            if (XMaterial.BUCKET.isSimilar(event.getItem())
                    || XMaterial.WATER_BUCKET.isSimilar(event.getItem())
                    || XMaterial.LAVA_BUCKET.isSimilar(event.getItem())) {
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
