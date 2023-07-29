package com.songoda.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.ListeningPermission;
import com.songoda.skyblock.permission.PermissionHandler;
import com.songoda.skyblock.permission.PermissionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class DestroyPermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public DestroyPermission(SkyBlock plugin) {
        super("Destroy", CompatibleMaterial.DIAMOND_PICKAXE, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        CompatibleMaterial material = CompatibleMaterial.getMaterial(event.getClickedBlock());
        Player player = event.getPlayer();

        if (material == CompatibleMaterial.SWEET_BERRY_BUSH || material == CompatibleMaterial.TNT
                || material == CompatibleMaterial.END_PORTAL_FRAME) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }

    @PermissionHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player player = null;
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        }
        if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            player = (Player) ((Projectile) event.getDamager()).getShooter();
        }
        if (player != null) {
            Entity entity = event.getEntity();

            switch (entity.getType()) {
                case ARMOR_STAND:
                case PAINTING:
                case ITEM_FRAME:
                    cancelAndMessage(event, player, this.plugin, this.messageManager);
                    break;
            }
        }
    }

    @PermissionHandler
    public void onBlockBreak(BlockBreakEvent event) {
        cancelAndMessage(event, event.getPlayer(), this.plugin, this.messageManager);
    }
}
