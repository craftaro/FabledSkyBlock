package com.songoda.skyblock.permission.permissions.listening;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class HangingDestroyPermission extends ListeningPermission {

    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public HangingDestroyPermission(SkyBlock plugin) {
        super("HangingDestroy", CompatibleMaterial.ITEM_FRAME, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Hanging) {

            Player player;
            if (event.getDamager() instanceof Player)
                player = (Player)event.getDamager();
            else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player)
                player = (Player) ((Projectile) event.getDamager()).getShooter();
            else return;

            cancelAndMessage(event, player, plugin, messageManager);

        }
    }

    @PermissionHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player))
            return;

        cancelAndMessage(event, (Player) event.getRemover(), plugin, messageManager);
    }

    @PermissionHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Hanging))
            return;

        cancelAndMessage(event, event.getPlayer(), plugin, messageManager);
    }
}
