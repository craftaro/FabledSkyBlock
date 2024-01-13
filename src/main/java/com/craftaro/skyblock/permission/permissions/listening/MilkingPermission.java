package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MilkingPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public MilkingPermission(SkyBlock plugin) {
        super("Milking", XMaterial.MILK_BUCKET, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {


        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity.getType() == EntityType.COW) {
            if (XMaterial.BUCKET.isSimilar(player.getItemInHand())) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
            }
        } else if (entity.getType() == EntityType.MUSHROOM_COW) {
            if (XMaterial.BUCKET.isSimilar(player.getItemInHand())
                    || XMaterial.BOWL.isSimilar(player.getItemInHand())) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
            }
        }
    }
}
