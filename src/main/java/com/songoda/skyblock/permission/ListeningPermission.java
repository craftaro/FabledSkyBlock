package com.songoda.skyblock.permission;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.event.events.PlayerEnterPortalEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

public abstract class ListeningPermission extends BasicPermission {
    protected ListeningPermission(String name, CompatibleMaterial icon, PermissionType type) {
        super(name, icon, type);
    }

    public void onInteract(PlayerInteractEvent event) {
    }

    public void onInteractEntity(PlayerInteractEntityEvent event) {
    }

    public void onShear(PlayerShearEntityEvent event) {
    }

    public void onBreak(BlockBreakEvent event) {
    }

    public void onPlace(BlockPlaceEvent event) {
    }

    public void onMultiPlace(BlockMultiPlaceEvent event) {
    }

    public void onVehicleDamage(VehicleDamageEvent event) {
    }

    public void onVehicleDestroy(VehicleDestroyEvent event) {
    }

    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    }

    public void onEntityDamage(EntityDamageEvent event) {
    }

    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
    }

    public void onHangingPlace(HangingPlaceEvent event) {
    }

    public void onHangingBreak(HangingBreakEvent event) {
    }

    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
    }

    public void onEntityTame(EntityTameEvent event) {
    }

    public void onTargetEntity(EntityTargetLivingEntityEvent event) {
    }

    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    }

    public void onBucketFill(PlayerBucketFillEvent event) {
    }

    public void onInventoryOpen(InventoryOpenEvent event) {
    }

    public void onFoodLevelChange(FoodLevelChangeEvent event) {
    }

    public void onPortalEnter(PlayerEnterPortalEvent event) {
    }

    public void onPickupItem(PlayerPickupItemEvent event) {
    }

    public void onDropItem(PlayerDropItemEvent event) {
    }

    public void onMove(PlayerMoveEvent event) {
    }

    public void onTeleport(PlayerTeleportEvent event) {
    }

    public void onProjectileLaunch(ProjectileLaunchEvent event) {
    }

    public void onBlockIgnite(BlockIgniteEvent event) {
    }

    protected void noPermsMessage(Player player, SkyBlock plugin, MessageManager messageManager) {
        if (messageManager == null) { // TODO Check why this is null - Fabrimat
            messageManager = SkyBlock.getPlugin(SkyBlock.class).getMessageManager();
        }

        messageManager.sendMessage(player, plugin.getLanguage().getString("Island.Settings.Permission.Message"));
        plugin.getSoundManager().playSound(player, XSound.ENTITY_VILLAGER_NO);
    }

    protected void cancelAndMessage(Cancellable cancellable, Player player, SkyBlock plugin, MessageManager messageManager) {
        cancellable.setCancelled(true);
        noPermsMessage(player, plugin, messageManager);
    }
}
