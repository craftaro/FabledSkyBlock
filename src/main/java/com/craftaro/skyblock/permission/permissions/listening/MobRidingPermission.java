package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MobRidingPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public MobRidingPermission(SkyBlock plugin) {
        super("MobRiding", XMaterial.SADDLE, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {


        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity.getType() == EntityType.HORSE || entity.getType() == EntityType.PIG) {
            if (entity.getType() == EntityType.HORSE) {
                Horse horse = (Horse) event.getRightClicked();
                if (horse.getInventory().getSaddle() == null) {
                    cancelAndMessage(event, player, this.plugin, this.messageManager);
                }
            } else if (entity.getType() == EntityType.PIG) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
            }
        }
    }
}
