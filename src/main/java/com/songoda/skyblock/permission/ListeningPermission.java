package com.songoda.skyblock.permission;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.event.events.PlayerEnterPortalEvent;
import com.songoda.skyblock.permission.event.events.ProjectileLaunchByPlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import java.io.File;

public abstract class ListeningPermission extends BasicPermission {

    protected ListeningPermission(String name, CompatibleMaterial icon, PermissionType type) {
        super(name, icon, type);
    }

    public void onInteract(PlayerInteractEvent event) {}

    public void onInteractEntity(PlayerInteractEntityEvent event) {}

    public void onShear(PlayerShearEntityEvent event) {}

    public void onBreak(BlockBreakEvent event) {}

    public void onPlace(BlockPlaceEvent event) {}

    public void onVehicleDamage(VehicleDamageEvent event) {}

    public void onVehicleDestroy(VehicleDestroyEvent event) {}

    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {}

    public void onEntityDamage(EntityDamageEvent event) {}

    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {}

    public void onHangingPlace(HangingPlaceEvent event) {}

    public void onHangingBreak(HangingBreakEvent event) {}

    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {}

    public void onEntityTame(EntityTameEvent event) {}

    public void onTargetEntity(EntityTargetLivingEntityEvent event) {}

    public void onBucketEmpty(PlayerBucketEmptyEvent event) {}

    public void onBucketFill(PlayerBucketFillEvent event) {}

    public void onInventoryOpen(InventoryOpenEvent event) {}

    public void onFoodLevelChange(FoodLevelChangeEvent event) {}

    public void onPortalEnter(PlayerEnterPortalEvent event) {}

    public void onPickupItem(PlayerPickupItemEvent event) {}

    public void onDropItem(PlayerDropItemEvent event) {}

    public void onMove(PlayerMoveEvent event) {}

    public void onTeleport(PlayerTeleportEvent event) {}

    public void onProjectileLaunch(ProjectileLaunchByPlayerEvent event) {}

    protected void noPermsMessage(Player player, SkyBlock plugin, MessageManager messageManager) {
        messageManager.sendMessage(player,
                plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"))
                        .getFileConfiguration().getString("Island.Settings.Permission.Message"));
        CompatibleSound.ENTITY_VILLAGER_NO.play(player);
    }

    protected void cancelAndMessage(Cancellable cancellable, Player player,
                                    SkyBlock plugin, MessageManager messageManager) {
        cancellable.setCancelled(true);
        noPermsMessage(player, plugin, messageManager);

    }
}
