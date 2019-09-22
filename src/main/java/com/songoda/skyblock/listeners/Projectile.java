package com.songoda.skyblock.listeners;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.version.Sounds;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.io.File;

public class Projectile implements Listener {

    private final SkyBlock skyblock;

    public Projectile(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        MessageManager messageManager = skyblock.getMessageManager();
        SoundManager soundManager = skyblock.getSoundManager();

        if (skyblock.getWorldManager().isIslandWorld(player.getWorld())) {
            if (event.getEntity() instanceof FishHook) {
                if (!skyblock.getIslandManager().hasPermission(player, "Fishing")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                    .getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
                }

                return;
            }

            if (!skyblock.getIslandManager().hasPermission(player, "Projectile")) {
                event.setCancelled(true);

                messageManager.sendMessage(player,
                        skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                .getFileConfiguration().getString("Island.Settings.Permission.Message"));
                soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
            }
        }
    }
}
