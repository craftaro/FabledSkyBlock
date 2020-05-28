package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.io.File;

public class PvpPermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;
    private final FileManager fileManager;

    public PvpPermission(SkyBlock plugin) {
        super("PvP", CompatibleMaterial.DIAMOND_SWORD, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
        this.fileManager = plugin.getFileManager();
    }

    @PermissionHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        Player player;
        if (event.getDamager() instanceof Player)
            player = (Player) event.getDamager();
        else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player)
            player = (Player) ((Projectile) event.getDamager()).getShooter();
        else return;

        FileManager.Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getBoolean("Island.Settings.PvP.Enable")) {
            event.setCancelled(true);
        } else if (!configLoad.getBoolean("Island.PvP.Enable")) {
            event.setCancelled(true);
        }

        if (entity.getType() == EntityType.ARMOR_STAND || !(entity instanceof Monster)) return;

        cancelAndMessage(event, player, plugin, messageManager);
    }
}
