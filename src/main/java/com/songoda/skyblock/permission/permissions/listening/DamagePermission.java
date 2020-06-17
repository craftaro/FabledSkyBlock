package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.io.File;

public class DamagePermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final FileManager fileManager;

    public DamagePermission(SkyBlock plugin) {
        super("Damage", CompatibleMaterial.RED_DYE, PermissionType.GENERIC);
        this.plugin = plugin;
        this.fileManager = plugin.getFileManager();
    }

    @PermissionHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            return;
        } else if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;

            if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                return;
            }
        } else {
            if (NMSUtil.getVersionNumber() > 11) {
                if (event.getCause() == EntityDamageEvent.DamageCause.valueOf("ENTITY_SWEEP_ATTACK")) {
                    EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;

                    if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                        return;
                    }
                }
            }
        }

        FileManager.Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getBoolean("Island.Settings.Damage.Enable", false)
                || !configLoad.getBoolean("Island.Damage.Enable", false)) {
            event.setCancelled(true);
        }
    }

    @PermissionHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() != EntityType.PLAYER) return;

        FileManager.Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getBoolean("Island.Settings.Damage.Enable", false)
                || !configLoad.getBoolean("Island.Damage.Enable", false)) {
            event.setCancelled(true);
        }
    }
}
