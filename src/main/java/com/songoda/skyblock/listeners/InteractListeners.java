package com.songoda.skyblock.listeners;

import com.songoda.core.compatibility.CompatibleHand;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.hooks.LogManager;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.NumberUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.limit.impl.BlockLimitation;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.stackable.Stackable;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.utils.structure.StructureUtil;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.world.WorldManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.util.HashMap;

public class InteractListeners implements Listener {

    private final SkyBlock plugin;

    public InteractListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onWaterPlace(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock().getRelative(event.getBlockFace());

        CompatibleMaterial material = CompatibleMaterial.getMaterial(block);
        IslandManager islandManager = plugin.getIslandManager();
        WorldManager worldManager = plugin.getWorldManager();
        IslandLevelManager levellingManager = plugin.getLevellingManager();
        if (!worldManager.isIslandWorld(block.getWorld())) return;

        CompatibleMaterial itemMaterial = CompatibleMaterial.getMaterial(event.getItem());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK
                && worldManager.getIslandWorld(block.getWorld()).equals(IslandWorld.Nether)
                && (itemMaterial.equals(CompatibleMaterial.WATER_BUCKET)
                || itemMaterial.equals(CompatibleMaterial.TROPICAL_FISH_BUCKET)
                || itemMaterial.equals(CompatibleMaterial.COD_BUCKET)
                || itemMaterial.equals(CompatibleMaterial.SALMON_BUCKET)
                || itemMaterial.equals(CompatibleMaterial.PUFFERFISH_BUCKET))) {
            Location blockLoc = block.getLocation();

            Island island = islandManager.getIslandAtLocation(blockLoc);

            // Check permissions.
            if (!plugin.getPermissionManager().processPermission(event, player, island))
                return;

            if (island == null) {
                event.setCancelled(true);
                return;
            }

            if (levellingManager.isScanning(island)) {
                plugin.getMessageManager().sendMessage(player,
                        plugin.getLanguage().getString("Command.Island.Level.Scanning.BlockPlacing.Message"));
                event.setCancelled(true);
                return;
            }

            CompatibleMaterial type = CompatibleMaterial.getMaterial(block);

            if (type.name().contains("SLAB")
                    || type == CompatibleMaterial.BROWN_MUSHROOM
                    || type == CompatibleMaterial.RED_MUSHROOM
                    || type == CompatibleMaterial.CHEST
                    || type == CompatibleMaterial.ENDER_CHEST
                    || type == CompatibleMaterial.TRAPPED_CHEST
                    || type == CompatibleMaterial.END_PORTAL
                    || type == CompatibleMaterial.ENCHANTING_TABLE) {
                event.setCancelled(true);
                return;
            }

            FileManager.Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();
            IslandWorld world = worldManager.getIslandWorld(block.getWorld());

            // Check spawn protection
            if (configLoad.getBoolean("Island.Spawn.Protection")) {
                boolean isObstructing = false;
                // Directly on the block
                if (LocationUtil.isLocationAffectingIslandSpawn(blockLoc, island, world)) {
                    isObstructing = true;
                }

                if (isObstructing) {
                    plugin.getMessageManager().sendMessage(player, plugin.getLanguage().getString("Island.SpawnProtection.Place.Message"));
                    plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    event.setCancelled(true);
                    return;
                }
            }

            BlockLimitation limits = plugin.getLimitationHandler().getInstance(BlockLimitation.class);

            long limit = limits.getBlockLimit(player, Material.WATER);

            if (limits.isBlockLimitExceeded(itemMaterial, block.getLocation(), limit)) {

                plugin.getMessageManager().sendMessage(player, plugin.getLanguage().getString("Island.Limit.Block.Exceeded.Message")
                        .replace("%type", WordUtils.capitalizeFully(itemMaterial.name().replace("_", " "))).replace("%limit", NumberUtils.formatNumber(limit)));
                plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                event.setCancelled(true);
                return;
            }

            if (configLoad.getBoolean("Island.Nether.AllowNetherWater", false))
                block.setType(Material.WATER, true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getClickedBlock();

        if (block != null && !plugin.getWorldManager().isIslandWorld(block.getWorld())) {
            return;
        }

        IslandManager islandManager = plugin.getIslandManager();
        StackableManager stackableManager = plugin.getStackableManager();
        IslandLevelManager levellingManager = plugin.getLevellingManager();

        Island island = (block != null) ?
                islandManager.getIslandAtLocation(block.getLocation()) :
                islandManager.getIslandAtLocation(player.getLocation());
        if (island == null) {
            event.setCancelled(true);
            return;
        }

        CompatibleMaterial material = block == null ? null : CompatibleMaterial.getMaterial(block.getType());

        // Check permissions.
        if (!plugin.getPermissionManager().processPermission(event, player, island))
            return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final CompatibleMaterial blockType = CompatibleMaterial.getBlockMaterial(event.getClickedBlock().getType());
            final CompatibleMaterial heldType;
            final ItemStack item = event.getItem();

            if (item != null && CompatibleMaterial.getMaterial(item.getType()) != CompatibleMaterial.AIR) {
                heldType = CompatibleMaterial.getMaterial(event.getItem());
            } else {
                heldType = CompatibleMaterial.AIR;
            }

            if (stackableManager != null && block != null && stackableManager.isStacked(block.getLocation())) {
                if (blockType.equals(CompatibleMaterial.DRAGON_EGG)) {
                    event.setCancelled(true);
                }
            }

            if (stackableManager != null && stackableManager.isStackableMaterial(heldType) && blockType == heldType
                    && !player.isSneaking() && plugin.getPermissionManager().hasPermission(player, island, "Place")
                    && (!this.plugin.getConfiguration().getBoolean("Island.Stackable.RequirePermission")
                    || player.hasPermission("fabledskyblock.stackable"))) {

                if (levellingManager.isScanning(island)) {
                    plugin.getMessageManager().sendMessage(player,
                            plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Command.Island.Level.Scanning.BlockPlacing.Message"));
                    event.setCancelled(true);
                    return;
                }

                BlockLimitation limits = plugin.getLimitationHandler().getInstance(BlockLimitation.class);

                long limit = limits.getBlockLimit(player, block.getType());

                if (limits.isBlockLimitExceeded(block, limit)) {
                    plugin.getMessageManager().sendMessage(player,
                            plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Limit.Block.Exceeded.Message")
                                    .replace("%type", WordUtils.capitalizeFully(material.name().replace("_", " "))).replace("%limit", NumberUtils.formatNumber(limit)));
                    plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    event.setCancelled(true);
                    return;
                }

                Location location = event.getClickedBlock().getLocation();
                Stackable stackable = stackableManager.getStack(location, blockType);
                int itemAmount = event.getItem().getAmount();

                FileManager.Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
                FileConfiguration configLoad = config.getFileConfiguration();

                if (configLoad.getBoolean("Island.Stackable.Limit.Enable")) {
                    // Add block to stackable
                    int maxStackSize = getStackLimit(player, material);

                    if (stackable == null) {
                        stackableManager.addStack(stackable = new Stackable(location, blockType, maxStackSize));
                        stackable.setSize(itemAmount + 1);
                        if (stackable.isMaxSize()) {
                            stackable.setSize(stackable.getMaxSize());
                            event.setCancelled(true);
                            return;
                        }
                    } else {
                        stackable.setMaxSize(maxStackSize);
                        stackable.setSize(stackable.getSize() + itemAmount);
                        if (stackable.isMaxSize()) {
                            stackable.setSize(stackable.getMaxSize());
                            event.setCancelled(true);
                            return;
                        }
                    }

                    // Disables interaction
                    event.setCancelled(true);

                } else {
                    if (stackable == null) {
                        stackableManager.addStack(stackable = new Stackable(location, blockType));
                        stackable.setSize(itemAmount + 1);
                    } else {
                        stackable.setSize(stackable.getSize() + itemAmount);
                    }

                    event.setCancelled(true);
                }

                if (LogManager.isEnabled() && material != null)
                    LogManager.logPlacement(player, block);

                if (player.getGameMode() != GameMode.CREATIVE)
                    ItemUtils.takeActiveItem(player, CompatibleHand.getHand(event), itemAmount);

                if (!configLoad.getBoolean("Island.Block.Level.Enable")) {
                    return;
                }

                long materialAmmount = 0;
                IslandLevel level = island.getLevel();

                if (material == null) {
                    return;
                }

                if (level.hasMaterial(material.name())) {
                    materialAmmount = level.getMaterialAmount(material.name());
                }

                level.setMaterialAmount(material.name(), materialAmmount + itemAmount);
                return;

            }

            // Check if the clicked block is outside of the border.
            WorldManager worldManager = plugin.getWorldManager();
            org.bukkit.block.Block clickedBlock = event.getClickedBlock();
            IslandWorld world = worldManager.getIslandWorld(clickedBlock.getWorld());
            if (!islandManager.isLocationAtIsland(island, clickedBlock.getLocation(), world)) {
                event.setCancelled(true);
                return;
            }

            if (player.getGameMode() == GameMode.SURVIVAL
                    && material == CompatibleMaterial.OBSIDIAN
                    && event.getItem() != null
                    && CompatibleMaterial.getMaterial(event.getItem()) != CompatibleMaterial.AIR
                    && CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.BUCKET) {
                if (plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"))
                        .getFileConfiguration().getBoolean("Island.Block.Obsidian.Enable")) {

                    plugin.getSoundManager().playSound(block.getLocation(), CompatibleSound.BLOCK_FIRE_EXTINGUISH.getSound(), 1.0F, 1.0F);
                    block.setType(CompatibleMaterial.AIR.getBlockMaterial());

                    ItemUtils.takeActiveItem(player, CompatibleHand.getHand(event));
                    HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(CompatibleMaterial.LAVA_BUCKET.getItem());
                    for (ItemStack i : overflow.values())
                        block.getWorld().dropItemNaturally(block.getLocation(), i);

                    event.setCancelled(true);
                    return;
                }
            } else if (material == CompatibleMaterial.END_PORTAL_FRAME) {
                if (plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"))
                        .getFileConfiguration().getBoolean("Island.Block.EndFrame.Enable")) {

                    if (CompatibleHand.getHand(event) == CompatibleHand.OFF_HAND) return;

                    ItemStack is = event.getPlayer().getItemInHand();
                    boolean hasEye = ((block.getData() >> 2) & 1) == 1;

                    if (CompatibleMaterial.getMaterial(is.getType()) == CompatibleMaterial.AIR) {
                        int size = 1;

                        if (stackableManager != null && stackableManager.isStacked(block.getLocation())) {
                            Stackable stackable = stackableManager.getStack(block.getLocation(), CompatibleMaterial.END_PORTAL_FRAME);
                            stackable.takeOne();

                            if (stackable.getSize() <= 1) {
                                stackableManager.removeStack(stackable);
                            }

                            size = stackable.getSize();
                        } else {
                            block.setType(CompatibleMaterial.AIR.getBlockMaterial());
                        }

                        player.getInventory().addItem(new ItemStack(CompatibleMaterial.END_PORTAL_FRAME.getMaterial(), 1));
                        if (hasEye && size == 1) {
                            player.getInventory().addItem(new ItemStack(CompatibleMaterial.ENDER_EYE.getMaterial(), 1));
                        }
                        player.updateInventory();

                        FileManager.Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
                        FileConfiguration configLoad = config.getFileConfiguration();

                        if (configLoad.getBoolean("Island.Block.Level.Enable")) {
                            CompatibleMaterial materials = CompatibleMaterial.END_PORTAL_FRAME;
                            IslandLevel level = island.getLevel();

                            if (level.hasMaterial(materials.name())) {
                                long materialAmount = level.getMaterialAmount(materials.name());

                                if (materialAmount - 1 <= 0) {
                                    level.removeMaterial(materials.name());
                                } else {
                                    level.setMaterialAmount(materials.name(), materialAmount - 1);
                                }
                            }
                        }

                        plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_CHICKEN_EGG.getSound(), 10.0F, 10.0F);

                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    private int getStackLimit(Player player, CompatibleMaterial materials) {
        String maxSizePermission = "fabledskyblock.stackable." + materials.name().toLowerCase() + ".maxsize.";

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            if (attachmentInfo.getPermission().startsWith(maxSizePermission)) {
                String permission = attachmentInfo.getPermission();
                int i = Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
                return i;
            }
        }
        return 5000;
    }

    @EventHandler
    public void onPlayerInteractStructure(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        MessageManager messageManager = plugin.getMessageManager();
        SoundManager soundManager = plugin.getSoundManager();

        if (event.getItem() != null) {
            try {
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    ItemStack structureTool = StructureUtil.getTool();

                    if ((event.getItem().getType() == structureTool.getType()) && (event.getItem().hasItemMeta()) && (event.getItem().getItemMeta().getDisplayName()
                            .equals(structureTool.getItemMeta().getDisplayName()))) {
                        if (player.hasPermission("fabledskyblock.admin.structure.selection") || player.hasPermission("fabledskyblock.admin.structure.*") || player
                                .hasPermission("fabledskyblock.admin.*")
                                || player.hasPermission("fabledskyblock.*")) {
                            event.setCancelled(true);

                            plugin.getPlayerDataManager().getPlayerData(player).getArea().setPosition(1, event.getClickedBlock().getLocation());

                            messageManager.sendMessage(player,
                                    plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Structure.Tool.Position.Message")
                                            .replace("%position", "1"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);
                        }
                    }
                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    ItemStack structureTool = StructureUtil.getTool();

                    if ((event.getItem().getType() == structureTool.getType()) && (event.getItem().hasItemMeta()) && (event.getItem().getItemMeta().getDisplayName()
                            .equals(structureTool.getItemMeta().getDisplayName()))) {
                        if (player.hasPermission("fabledskyblock.admin.structure.selection") || player.hasPermission("fabledskyblock.admin.structure.*") || player
                                .hasPermission("fabledskyblock.admin.*")
                                || player.hasPermission("fabledskyblock.*")) {
                            event.setCancelled(true);

                            plugin.getPlayerDataManager().getPlayerData(player).getArea().setPosition(2, event.getClickedBlock().getLocation());

                            messageManager.sendMessage(player,
                                    plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Structure.Tool.Position.Message")
                                            .replace("%position", "2"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        org.bukkit.entity.Entity entity = event.getRightClicked();

        IslandManager islandManager = plugin.getIslandManager();

        if (!plugin.getWorldManager().isIslandWorld(entity.getWorld())) return;

        Island island = islandManager.getIslandAtLocation(entity.getLocation());

        // Check permissions.
        if (!plugin.getPermissionManager().processPermission(event, player, island))
            return;
    }

    @EventHandler
    public void onPlayerDamageVehicle(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player)) {
            return;
        }

        IslandManager islandManager = plugin.getIslandManager();

        Player player = (Player) event.getAttacker();

        if (!plugin.getWorldManager().isIslandWorld(event.getVehicle().getWorld())) return;

        Island island = islandManager.getIslandAtLocation(event.getVehicle().getLocation());

        // Check permissions.
        if (!plugin.getPermissionManager().processPermission(event, player, island))
            return;
    }

    @EventHandler
    public void onPlayerDestroyVehicle(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player)) {
            return;
        }

        IslandManager islandManager = plugin.getIslandManager();

        Player player = (Player) event.getAttacker();

        if (!plugin.getWorldManager().isIslandWorld(event.getVehicle().getWorld())) return;

        Island island = islandManager.getIslandAtLocation(event.getVehicle().getLocation());

        // Check permissions.
        if (!plugin.getPermissionManager().processPermission(event, player, island))
            return;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (plugin.getStackableManager() != null && plugin.getStackableManager().isStacked(event.getRightClicked().getLocation().getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }
}
