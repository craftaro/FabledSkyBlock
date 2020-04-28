package com.songoda.skyblock.listeners;

import com.songoda.core.compatibility.CompatibleHand;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.utils.ItemUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.levelling.rework.IslandLevelManager;
import com.songoda.skyblock.limit.impl.BlockLimitation;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.stackable.Stackable;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.structure.StructureUtil;
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.world.WorldManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Beacon;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.util.HashMap;

public class Interact implements Listener {

    private final SkyBlock skyblock;

    public Interact(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getClickedBlock();

        if (block != null && !skyblock.getWorldManager().isIslandWorld(block.getWorld())) {
            return;
        }

        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        StackableManager stackableManager = skyblock.getStackableManager();
        IslandLevelManager levellingManager = skyblock.getLevellingManager();

        Island island = islandManager.getIslandAtLocation(player.getLocation());
        if (island == null) {
            event.setCancelled(true);
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (CompatibleMaterial.getMaterial(block.getType()) == CompatibleMaterial.DRAGON_EGG) {
                if (!islandManager.hasPermission(player, block.getLocation(), "DragonEggUse")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (block.getState() instanceof Beacon) { // ChunkCollectors support
                if (!islandManager.hasPermission(player, block.getLocation(), "Beacon")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (block.getState() instanceof InventoryHolder || block.getState() instanceof CreatureSpawner) { // EpicHoppers/EpicSpawners support
                if (!islandManager.hasPermission(player, block.getLocation(), "Storage")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (CompatibleMaterial.getMaterial(block.getType()) == CompatibleMaterial.CAULDRON) { // WildStacker stackables
                if (!islandManager.hasPermission(player, block.getLocation(), "Place") || !islandManager.hasPermission(player, block.getLocation(), "Destroy")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR) {
            if (event.getItem() != null && CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.EGG) {
                if (!skyblock.getIslandManager().hasPermission(player, "Projectile")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                }
            }
        }

        CompatibleMaterial material = block == null ? null : CompatibleMaterial.getMaterial(block.getType());

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final CompatibleMaterial blockType = CompatibleMaterial.getBlockMaterial(event.getClickedBlock().getType());
            final CompatibleMaterial heldType;
            final ItemStack item = event.getItem();

            if (item != null && CompatibleMaterial.getMaterial(item.getType()) != CompatibleMaterial.AIR) {
                heldType = CompatibleMaterial.getMaterial(event.getItem());
            } else {
                heldType = CompatibleMaterial.AIR;
            }

            if (stackableManager != null && stackableManager.isStackableMaterial(heldType) && blockType == heldType && !player.isSneaking() && islandManager
                    .hasPermission(player, block.getLocation(), "Place")
                    && (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Stackable.RequirePermission")
                    || player.hasPermission("fabledskyblock.stackable"))) {

                if (levellingManager.isScanning(island)) {
                    skyblock.getMessageManager().sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Command.Island.Level.Scanning.BlockPlacing.Message"));
                    event.setCancelled(true);
                    return;
                }

                BlockLimitation limits = skyblock.getLimitationHandler().getInstance(BlockLimitation.class);

                long limit = limits.getBlockLimit(player, block);

                if (limits.isBlockLimitExceeded(block, limit)) {
                    skyblock.getMessageManager().sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Limit.Block.Exceeded.Message")
                                    .replace("%type", WordUtils.capitalizeFully(material.name().replace("_", " "))).replace("%limit", NumberUtil.formatNumber(limit)));
                    skyblock.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    event.setCancelled(true);
                    return;
                }

                Location location = event.getClickedBlock().getLocation();
                Stackable stackable = stackableManager.getStack(location, blockType);
                int itemAmount = event.getItem().getAmount();

                FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
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
            WorldManager worldManager = skyblock.getWorldManager();
            org.bukkit.block.Block clickedBlock = event.getClickedBlock();
            IslandWorld world = worldManager.getIslandWorld(clickedBlock.getWorld());
            if (!islandManager.isLocationAtIsland(island, clickedBlock.getLocation(), world)) {
                event.setCancelled(true);
                return;
            }

            if (event.getItem() != null && CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.BONE_MEAL && !islandManager.hasPermission(player, block.getLocation(), "Place")) {
                soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                event.setCancelled(true);
                return;
            }

            if (material == CompatibleMaterial.SWEET_BERRY_BUSH) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Destroy")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.ANVIL) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Anvil")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.WHITE_BED || material == CompatibleMaterial.ORANGE_BED
                    || material == CompatibleMaterial.MAGENTA_BED || material == CompatibleMaterial.LIGHT_BLUE_BED
                    || material == CompatibleMaterial.YELLOW_BED || material == CompatibleMaterial.LIME_BED
                    || material == CompatibleMaterial.PINK_BED || material == CompatibleMaterial.GRAY_BED
                    || material == CompatibleMaterial.LIGHT_GRAY_BED || material == CompatibleMaterial.CYAN_BED
                    || material == CompatibleMaterial.CYAN_BED || material == CompatibleMaterial.PURPLE_BED
                    || material == CompatibleMaterial.BLUE_BED || material == CompatibleMaterial.BROWN_BED
                    || material == CompatibleMaterial.GREEN_BED || material == CompatibleMaterial.RED_BED
                    || material == CompatibleMaterial.BLACK_BED
            ) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Bed")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.BREWING_STAND) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Brewing")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.CHEST || material == CompatibleMaterial.TRAPPED_CHEST
                    || (NMSUtil.getVersionNumber() > 9 && (material == CompatibleMaterial.SHULKER_BOX
                    || material == CompatibleMaterial.BLACK_SHULKER_BOX || material == CompatibleMaterial.BLUE_SHULKER_BOX
                    || material == CompatibleMaterial.BROWN_SHULKER_BOX || material == CompatibleMaterial.CYAN_SHULKER_BOX
                    || material == CompatibleMaterial.GRAY_SHULKER_BOX || material == CompatibleMaterial.GREEN_SHULKER_BOX
                    || material == CompatibleMaterial.LIGHT_BLUE_SHULKER_BOX || material == CompatibleMaterial.LIGHT_GRAY_SHULKER_BOX
                    || material == CompatibleMaterial.LIME_SHULKER_BOX || material == CompatibleMaterial.MAGENTA_SHULKER_BOX
                    || material == CompatibleMaterial.ORANGE_SHULKER_BOX || material == CompatibleMaterial.PINK_SHULKER_BOX
                    || material == CompatibleMaterial.PURPLE_SHULKER_BOX || material == CompatibleMaterial.RED_SHULKER_BOX
                    || material == CompatibleMaterial.WHITE_SHULKER_BOX || material == CompatibleMaterial.YELLOW_SHULKER_BOX))) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Storage")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.CRAFTING_TABLE) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Workbench")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.BIRCH_DOOR || material == CompatibleMaterial.ACACIA_DOOR
                    || material == CompatibleMaterial.DARK_OAK_DOOR || material == CompatibleMaterial.JUNGLE_DOOR
                    || material == CompatibleMaterial.SPRUCE_DOOR || material == CompatibleMaterial.OAK_DOOR) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Door")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.ENCHANTING_TABLE) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Enchant")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.FURNACE) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Furnace")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.STONE_BUTTON || material == CompatibleMaterial.OAK_BUTTON || material == CompatibleMaterial.SPRUCE_BUTTON
                    || material == CompatibleMaterial.BIRCH_BUTTON || material == CompatibleMaterial.JUNGLE_BUTTON || material == CompatibleMaterial.ACACIA_BUTTON
                    || material == CompatibleMaterial.DARK_OAK_BUTTON || material == CompatibleMaterial.LEVER) {
                if (!islandManager.hasPermission(player, block.getLocation(), "LeverButton")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.JUKEBOX) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Jukebox")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.OAK_TRAPDOOR || material == CompatibleMaterial.SPRUCE_TRAPDOOR
                    || material == CompatibleMaterial.BIRCH_TRAPDOOR || material == CompatibleMaterial.JUNGLE_TRAPDOOR
                    || material == CompatibleMaterial.ACACIA_TRAPDOOR || material == CompatibleMaterial.DARK_OAK_TRAPDOOR
                    || material == CompatibleMaterial.NOTE_BLOCK || material == CompatibleMaterial.HOPPER
                    || material == CompatibleMaterial.COMPARATOR || material == CompatibleMaterial.REPEATER) {
                if (material == CompatibleMaterial.HOPPER && !islandManager.hasPermission(player, block.getLocation(), "Hopper")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
                if (!islandManager.hasPermission(player, block.getLocation(), "Redstone")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.OAK_FENCE_GATE || material == CompatibleMaterial.ACACIA_FENCE_GATE || material == CompatibleMaterial.BIRCH_FENCE_GATE
                    || material == CompatibleMaterial.DARK_OAK_FENCE_GATE || material == CompatibleMaterial.JUNGLE_FENCE_GATE || material == CompatibleMaterial.SPRUCE_FENCE_GATE) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Gate")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if ((material == CompatibleMaterial.DROPPER || (material == CompatibleMaterial.DISPENSER))) {
                if (!islandManager.hasPermission(player, block.getLocation(), "DropperDispenser")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.TNT) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Destroy")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (material == CompatibleMaterial.CAKE) {
                if (player.getFoodLevel() < 20 && !islandManager.hasPermission(player, block.getLocation(), "Cake")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (player.getGameMode() == GameMode.SURVIVAL
                    && material == CompatibleMaterial.OBSIDIAN
                    && event.getItem() != null
                    && CompatibleMaterial.getMaterial(event.getItem()) != CompatibleMaterial.AIR
                    && CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.BUCKET) {
                if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Block.Obsidian.Enable")
                        && islandManager.hasPermission(player, block.getLocation(), "Bucket")) {

                    CompatibleSound.BLOCK_FIRE_EXTINGUISH.play(block.getWorld(), block.getLocation(), 1.0F, 1.0F);
                    block.setType(CompatibleMaterial.AIR.getBlockMaterial());

                    ItemUtils.takeActiveItem(player, CompatibleHand.getHand(event));
                    HashMap<Integer, ItemStack> overflow = player.getInventory().addItem(CompatibleMaterial.LAVA_BUCKET.getItem());
                    for (ItemStack i : overflow.values())
                        block.getWorld().dropItemNaturally(block.getLocation(), i);

                    event.setCancelled(true);
                    return;
                }
            } else if (material == CompatibleMaterial.END_PORTAL_FRAME) {
                if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Block.EndFrame.Enable")
                        && islandManager.hasPermission(player, block.getLocation(), "Destroy")) {

                    if (Bukkit.getPluginManager().isPluginEnabled("EpicAnchors")) {
                        if (com.songoda.epicanchors.EpicAnchors.getInstance().getAnchorManager().getAnchor(block.getLocation()) != null) {
                            event.setCancelled(true);
                            return;
                        }
                    }

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

                        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
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

                        CompatibleSound.ENTITY_CHICKEN_EGG.play(player, 10.0F, 10.0F);

                        event.setCancelled(true);
                        return;
                    }
                }
            }

            if ((event.getItem() != null) && (CompatibleMaterial.getMaterial(event.getItem()) != CompatibleMaterial.AIR) && !event.isCancelled()) {
                if (CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.BUCKET
                        || CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.WATER_BUCKET
                        || CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.LAVA_BUCKET) {
                    if (!islandManager.hasPermission(player, block.getLocation(), "Bucket")) {
                        event.setCancelled(true);

                        messageManager.sendMessage(player,
                                skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                        soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                        player.updateInventory();
                    }
                } else if (CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.GLASS_BOTTLE) {
                    if (material == CompatibleMaterial.WATER || material == CompatibleMaterial.CAULDRON) {
                        if (!islandManager.hasPermission(player, block.getLocation(), "WaterCollection")) {
                            event.setCancelled(true);

                            messageManager.sendMessage(player,
                                    skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                            soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                            player.updateInventory();
                        }
                    }
                } else if (event.getItem().getType().name().contains("SPAWN_EGG") || event.getItem().getType().name().equals("MONSTER_EGG")) {
                    if (!islandManager.hasPermission(player, block.getLocation(), "SpawnEgg")) {
                        event.setCancelled(true);

                        messageManager.sendMessage(player,
                                skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                        soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                        player.updateInventory();
                    }
                } else if (CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.ARMOR_STAND || event.getItem().getType().name().contains("BOAT") || event.getItem().getType().name().contains("MINECART")) {
                    if (!islandManager.hasPermission(player, block.getLocation(), "EntityPlacement")) {
                        event.setCancelled(true);

                        messageManager.sendMessage(player,
                                skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                        soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                        player.updateInventory();
                    }
                }
            }
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Note: Cast is necessary as it is ambiguous without it in 1.8
            if (CompatibleMaterial.getMaterial(player.getTargetBlock(null, 5).getType()) == CompatibleMaterial.FIRE) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Fire")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                }
            }
        } else if (event.getAction() == Action.PHYSICAL) {
            if (material == CompatibleMaterial.TURTLE_EGG) {
                event.setCancelled(true);
            } else if (material == CompatibleMaterial.FARMLAND) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Crop")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                }
            } else if (material == CompatibleMaterial.STONE_PRESSURE_PLATE || material == CompatibleMaterial.OAK_PRESSURE_PLATE
                    || material == CompatibleMaterial.SPRUCE_PRESSURE_PLATE || material == CompatibleMaterial.BIRCH_PRESSURE_PLATE
                    || material == CompatibleMaterial.JUNGLE_PRESSURE_PLATE || material == CompatibleMaterial.ACACIA_PRESSURE_PLATE
                    || material == CompatibleMaterial.DARK_OAK_PRESSURE_PLATE
                    || material == CompatibleMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE
                    || material == CompatibleMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                if (!islandManager.hasPermission(player, block.getLocation(), "PressurePlate")) {
                    event.setCancelled(true);
                }
            } else if (material == CompatibleMaterial.TRIPWIRE) {
                if (!islandManager.hasPermission(player, block.getLocation(), "Redstone")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
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

        MessageManager messageManager = skyblock.getMessageManager();
        SoundManager soundManager = skyblock.getSoundManager();

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

                            skyblock.getPlayerDataManager().getPlayerData(player).getArea().setPosition(1, event.getClickedBlock().getLocation());

                            messageManager.sendMessage(player,
                                    skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Structure.Tool.Position.Message")
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

                            skyblock.getPlayerDataManager().getPlayerData(player).getArea().setPosition(2, event.getClickedBlock().getLocation());

                            messageManager.sendMessage(player,
                                    skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Structure.Tool.Position.Message")
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

        ItemStack is = player.getItemInHand();

        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();

        if (skyblock.getWorldManager().isIslandWorld(entity.getWorld())) {
            if ((is != null) && (CompatibleMaterial.getMaterial(is) != CompatibleMaterial.AIR)) {
                if (CompatibleMaterial.getMaterial(is) == CompatibleMaterial.LEAD) {
                    if (!islandManager.hasPermission(player, entity.getLocation(), "Leash")) {
                        event.setCancelled(true);

                        messageManager.sendMessage(player,
                                skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                        soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                        return;
                    }
                }
            }

            if (entity.getType() == EntityType.HORSE || entity.getType() == EntityType.PIG) {
                if (entity.getType() == EntityType.HORSE) {
                    Horse horse = (Horse) event.getRightClicked();

                    if (horse.getInventory().getSaddle() != null && player.isSneaking()) {
                        if (!islandManager.hasPermission(player, horse.getLocation(), "HorseInventory")) {
                            event.setCancelled(true);

                            messageManager.sendMessage(player,
                                    skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                            soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                            return;
                        }
                    } else {
                        if (!islandManager.hasPermission(player, horse.getLocation(), "MobRiding")) {
                            event.setCancelled(true);

                            messageManager.sendMessage(player,
                                    skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                            soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                            return;
                        }
                    }
                } else if (entity.getType() == EntityType.PIG) {
                    if (!islandManager.hasPermission(player, entity.getLocation(), "MobRiding")) {
                        event.setCancelled(true);

                        messageManager.sendMessage(player,
                                skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                        soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                        return;
                    }
                }
            } else if (entity.getType().equals(EntityType.SHEEP)) {
                if (!islandManager.hasPermission(player, entity.getLocation(), "EntityPlacement")) {
                    event.setCancelled(true);
                    skyblock.getMessageManager().sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    skyblock.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                }
            } else if (entity.getType().equals(EntityType.ITEM_FRAME)) {
                if (!skyblock.getIslandManager().hasPermission(player, entity.getLocation(), "Storage")) {
                    event.setCancelled(true);
                    skyblock.getMessageManager().sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    skyblock.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                }
            } else if (entity.getType() == EntityType.COW || entity.getType() == EntityType.MUSHROOM_COW) {
                if (CompatibleMaterial.getMaterial(is) == CompatibleMaterial.BUCKET) {
                    if (!islandManager.hasPermission(player, entity.getLocation(), "Milking")) {
                        event.setCancelled(true);

                        messageManager.sendMessage(player,
                                skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                        soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                        return;
                    }
                }
            } else if (entity.getType() == EntityType.VILLAGER) {
                if (!islandManager.hasPermission(player, entity.getLocation(), "Trading")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (entity instanceof StorageMinecart) {
                if (!islandManager.hasPermission(player, entity.getLocation(), "Storage")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (entity.getType() == EntityType.MINECART || entity.getType() == EntityType.BOAT) {
                if (!islandManager.hasPermission(player, entity.getLocation(), "MinecartBoat")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            } else if (entity.getType() == EntityType.MINECART_HOPPER) {
                if (!islandManager.hasPermission(player, entity.getLocation(), "Hopper")) {
                    event.setCancelled(true);

                    messageManager.sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    return;
                }
            }

            if (entity.getType() == EntityType.HORSE) {
                if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.GOLDEN_APPLE
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.GOLDEN_CARROT
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.SUGAR
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.WHEAT
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.APPLE
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.HAY_BLOCK)) {
                    return;
                }
            } else if (entity.getType() == EntityType.SHEEP || entity.getType() == EntityType.COW || entity.getType() == EntityType.MUSHROOM_COW) {
                if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.WHEAT)) {
                    return;
                }
            } else if (entity.getType() == EntityType.PIG) {
                if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.CARROT || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.POTATO)) {
                    return;
                }
            } else if (entity.getType() == EntityType.CHICKEN) {
                if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.WHEAT_SEEDS
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.PUMPKIN_SEEDS || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.MELON_SEEDS)) {
                    if (NMSUtil.getVersionNumber() > 8) {
                        if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.BEETROOT_SEEDS)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            } else if (entity.getType() == EntityType.WOLF) {
                if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.BONE
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.PORKCHOP
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.BEEF
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.CHICKEN
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.RABBIT
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.MUTTON
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.ROTTEN_FLESH
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.COOKED_PORKCHOP
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.COOKED_BEEF
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.COOKED_CHICKEN
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.COOKED_RABBIT
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.COOKED_MUTTON)) {
                    return;
                }
            } else if (entity.getType() == EntityType.OCELOT) {
                if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.COD
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.SALMON
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.TROPICAL_FISH
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.PUFFERFISH)) {
                    return;
                }
            } else if (entity.getType() == EntityType.RABBIT) {
                if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.DANDELION
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.CARROTS
                        || CompatibleMaterial.getMaterial(is) == CompatibleMaterial.GOLDEN_CARROT)) {
                    return;
                }
            } else {
                int NMSVersion = NMSUtil.getVersionNumber();

                if (NMSVersion > 10) {
                    if (entity.getType() == EntityType.LLAMA) {
                        if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.HAY_BLOCK)) {
                            return;
                        }
                    } else if (NMSVersion > 12) {
                        if (entity.getType() == EntityType.TURTLE) {
                            if (!(CompatibleMaterial.getMaterial(is) == CompatibleMaterial.SEAGRASS)) {
                                return;
                            }
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }

            if (!islandManager.hasPermission(player, entity.getLocation(), "AnimalBreeding")) {
                event.setCancelled(true);

                messageManager.sendMessage(player,
                        skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            }
        }
    }

    @EventHandler
    public void onPlayerDamageVehicle(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getAttacker();

        if (!skyblock.getIslandManager().hasPermission(player, event.getVehicle().getLocation(), "MobHurting")) {
            event.setCancelled(true);

            skyblock.getMessageManager()
                    .sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
            skyblock.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
        }
    }

    @EventHandler
    public void onPlayerDestroyVehicle(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getAttacker();

        if (!skyblock.getIslandManager().hasPermission(player, event.getVehicle().getLocation(), "MobHurting")) {
            event.setCancelled(true);

            skyblock.getMessageManager()
                    .sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
            skyblock.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (skyblock.getStackableManager() != null && skyblock.getStackableManager().isStacked(event.getRightClicked().getLocation().getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        org.bukkit.entity.Entity entity = event.getRightClicked();

        if (!skyblock.getWorldManager().isIslandWorld(entity.getWorld())) {
            return;
        }

        if (entity instanceof ArmorStand) {
            if (!skyblock.getIslandManager().hasPermission(player, entity.getLocation(), "ArmorStandUse")) {
                event.setCancelled(true);

                skyblock.getMessageManager().sendMessage(player,
                        skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Settings.Permission.Message"));
                skyblock.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            }
        }

    }
}
