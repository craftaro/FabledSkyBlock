package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.utils.version.Sounds;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.HorseInventory;

import java.io.File;

public class Inventory implements Listener {

    private final SkyBlock skyblock;

    public Inventory(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

        if (!(event.getInventory() instanceof HorseInventory)) {
            return;
        }

        if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
            if (!skyblock.getIslandManager().hasPermission(player, "HorseInventory")) {
                event.setCancelled(true);

                skyblock.getMessageManager().sendMessage(player,
                        skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                .getFileConfiguration().getString("Island.Settings.Permission.Message"));
                skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
            }
        }
    }
}
