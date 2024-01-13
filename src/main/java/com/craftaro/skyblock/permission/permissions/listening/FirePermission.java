package com.craftaro.skyblock.permission.permissions.listening;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.ListeningPermission;
import com.craftaro.skyblock.permission.PermissionHandler;
import com.craftaro.skyblock.permission.PermissionType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;

public class FirePermission extends ListeningPermission {
    private final SkyBlock plugin;
    private final MessageManager messageManager;

    public FirePermission(SkyBlock plugin) {
        super("Fire", XMaterial.FLINT_AND_STEEL, PermissionType.GENERIC);
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    @PermissionHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();

        Optional<XMaterial> targetMaterial = CompatibleMaterial.getMaterial(player.getTargetBlock(null, 5).getType());
        if (targetMaterial.orElse(null) == XMaterial.FIRE) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }

    @PermissionHandler
    public void onProjectileHit(BlockIgniteEvent event) {
        Player player = null;
        if (event.getPlayer() != null) {
            player = event.getPlayer();
        } else if (event.getIgnitingEntity() instanceof Projectile && ((Projectile) event.getIgnitingEntity()).getShooter() instanceof Player) {
            player = (Player) ((Projectile) event.getIgnitingEntity()).getShooter();
        }

        if (player != null) {
            cancelAndMessage(event, player, this.plugin, this.messageManager);
        }
    }
}
