package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.HorseInventory;

public class HorseInventoryPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public HorseInventoryPermission(SkyBlock plugin) {
        super("HorseInventory", XMaterial.DIAMOND_HORSE_ARMOR, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity.getType() == EntityType.HORSE) {
            Horse horse = (Horse) event.getRightClicked();
            if (horse.getInventory().getSaddle() != null && player.isSneaking()) {
                cancelAndMessage(event, player, this.plugin, this.messageManager);
            }
        }
    }

    @PermissionHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory() instanceof HorseInventory)) {
            return;
        }
        cancelAndMessage(event, (Player) event.getPlayer(), this.plugin, this.messageManager);
    }
}
