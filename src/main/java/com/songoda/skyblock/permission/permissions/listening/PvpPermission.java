package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
        Player attacker = null;
        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            attacker = (Player) ((Projectile) event.getDamager()).getShooter();
        }

        if(attacker != null && event.getEntity() instanceof Player){

            FileManager.Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getBoolean("Island.Settings.PvP.Enable") || !configLoad.getBoolean("Island.PvP.Enable")) {
                event.setCancelled(true);
            }

            cancelAndMessage(event, attacker, plugin, messageManager);
        }

    }
}
