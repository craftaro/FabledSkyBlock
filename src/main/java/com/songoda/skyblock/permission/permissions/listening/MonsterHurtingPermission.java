package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionPriority;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MonsterHurtingPermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public MonsterHurtingPermission(SkyBlock plugin) {
        super("MonsterHurting", CompatibleMaterial.BONE, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler(priority = PermissionPriority.FIRST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player player;
        if (event.getDamager() instanceof Player)
            player = (Player)event.getDamager();
        else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player)
            player = (Player) ((Projectile) event.getDamager()).getShooter();
        else return;

        Entity entity = event.getEntity();

        if (entity instanceof Monster){
            cancelAndMessage(event, player, plugin, messageManager);
        }
    }
}
