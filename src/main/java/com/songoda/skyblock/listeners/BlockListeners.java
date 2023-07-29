package com.songoda.skyblock.listeners;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.CompatibleSound;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.hooks.LogManager;
import com.craftaro.core.utils.NumberUtils;
import com.google.common.collect.Lists;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.generator.Generator;
import com.songoda.skyblock.generator.GeneratorManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.limit.impl.BlockLimitation;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.stackable.Stackable;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.utils.version.CompatibleSpawners;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.world.WorldManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Strider;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zoglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class BlockListeners implements Listener {
    private final SkyBlock plugin;
    private final Set<Location> generatorWaitingLocs;

    public BlockListeners(SkyBlock plugin) {
        this.plugin = plugin;
        this.generatorWaitingLocs = new HashSet<>();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlock();

        IslandManager islandManager = this.plugin.getIslandManager();
        StackableManager stackableManager = this.plugin.getStackableManager();
        WorldManager worldManager = this.plugin.getWorldManager();
        if (!worldManager.isIslandWorld(block.getWorld())) {
            return;
        }

        Location blockLocation = block.getLocation();

        Island island = islandManager.getIslandAtLocation(blockLocation);
        if (island == null) {
            event.setCancelled(true);
            return;
        }

        // Check permissions.
        if (!this.plugin.getPermissionManager().processPermission(event, player, island) || event.isCancelled()) {
            return;
        }

        if (stackableManager != null && stackableManager.isStacked(blockLocation)) {
            Stackable stackable = stackableManager.getStack(block.getLocation(), CompatibleMaterial.getMaterial(block));
            if (stackable != null) {
                CompatibleMaterial material = null;
                if (ServerVersion.isServerVersion(ServerVersion.V1_8)) {
                    switch (block.getType().toString().toUpperCase()) {
                        case "DIODE_BLOCK_OFF":
                        case "DIODE_BLOCK_ON":
                            material = CompatibleMaterial.REPEATER;
                            break;
                    }
                }
                if (material == null) {
                    material = CompatibleMaterial.getMaterial(block);
                }
                byte data = block.getData();

                int droppedAmount;
                if (event.getPlayer().isSneaking()) {
                    Location dropLoc = blockLocation.clone().add(0.5, 0.5, 0.5);
                    int count = stackable.getSize();
                    droppedAmount = count;
                    while (count > 64) {
                        dropLoc.getWorld().dropItemNaturally(dropLoc, new ItemStack(material.getMaterial(), 64, data));
                        count -= 64;
                    }
                    dropLoc.getWorld().dropItemNaturally(dropLoc, new ItemStack(material.getMaterial(), count, block.getData()));
                    block.setType(Material.AIR);
                    stackable.setSize(0);
                } else {
                    block.getWorld().dropItemNaturally(blockLocation.clone().add(.5, 1, .5), new ItemStack(material.getMaterial(), 1, data));
                    stackable.takeOne();
                    droppedAmount = 1;
                }

                if (LogManager.isEnabled()) {
                    LogManager.logRemoval(player, block);
                }

                if (stackable.getSize() <= 1) {
                    stackableManager.removeStack(stackable);
                }

                FileConfiguration configLoad = this.plugin.getConfiguration();

                if (configLoad.getBoolean("Island.Block.Level.Enable")) {
                    if (material != null) {
                        IslandLevel level = island.getLevel();

                        if (level.hasMaterial(material.name())) {
                            long materialAmount = level.getMaterialAmount(material.name());

                            if (materialAmount - droppedAmount <= 0) {
                                level.removeMaterial(material.name());
                            } else {
                                level.setMaterialAmount(material.name(), materialAmount - droppedAmount);
                            }
                        }
                    }
                }

                event.setCancelled(true);
            }
        }

        FileConfiguration configLoad = this.plugin.getConfiguration();

        IslandWorld world = worldManager.getIslandWorld(block.getWorld());

        if (LocationUtil.isLocationLocation(block.getLocation(), island.getLocation(world, IslandEnvironment.MAIN).clone().subtract(0.0D, 1.0D, 0.0D)) || LocationUtil.isLocationLocation(block.getLocation(), island.getLocation(world, IslandEnvironment.VISITOR).clone().subtract(0.0D, 1.0D, 0.0D))
                || LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
            if (configLoad.getBoolean("Island.Spawn.Protection")) {
                event.setCancelled(true);
                this.plugin.getMessageManager().sendMessage(player, this.plugin.getLanguage().getString("Island.SpawnProtection.Break.Message"));
                this.plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            }
        }

        if (event.isCancelled() || !configLoad.getBoolean("Island.Block.Level.Enable")) {
            return;
        }

        CompatibleMaterial material = null;
        if (ServerVersion.isServerVersion(ServerVersion.V1_8)) {
            switch (block.getType().toString().toUpperCase()) {
                case "DIODE_BLOCK_OFF":
                case "DIODE_BLOCK_ON":
                    material = CompatibleMaterial.REPEATER;
                    break;
            }
        }
        if (material == null) {
            material = CompatibleMaterial.getMaterial(block);
        }

        if (material == null) {
            return;
        }

        if (material.isTall()) {

            final org.bukkit.block.Block belowBlock = block.getRelative(BlockFace.DOWN);

            if (CompatibleMaterial.getMaterial(belowBlock).isTall()) {
                block = belowBlock;
            }
        }

        if (block.getType() == CompatibleMaterial.SPAWNER.getBlockMaterial()) {
            CompatibleSpawners spawner = CompatibleSpawners.getSpawner(((CreatureSpawner) block.getState()).getSpawnedType());

            if (spawner != null) {
                material = CompatibleMaterial.getBlockMaterial(spawner.getMaterial());
            }
        }

        if (material == null) {
            return;
        }

        IslandLevel level = island.getLevel();

        if (!level.hasMaterial(material.name())) {
            return;
        }

        long materialAmount = level.getMaterialAmount(material.name());

        if (materialAmount - 1 <= 0) {
            level.removeMaterial(material.name());
        } else {
            level.setMaterialAmount(material.name(), materialAmount - 1);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlock();

        IslandManager islandManager = this.plugin.getIslandManager();
        WorldManager worldManager = this.plugin.getWorldManager();
        IslandLevelManager islandLevelManager = this.plugin.getLevellingManager();
        if (!worldManager.isIslandWorld(block.getWorld())) {
            return;
        }

        Location blockLoc = block.getLocation();

        Island island = islandManager.getIslandAtLocation(blockLoc);

        // Check permissions.
        if (!this.plugin.getPermissionManager().processPermission(event, player, island)) {
            return;
        }

        if (island == null) {
            event.setCancelled(true);
            return;
        }

        if (ServerVersion.isServerVersionAbove(ServerVersion.V1_8)) {
            if (event instanceof BlockMultiPlaceEvent) {
                for (BlockState blockState : ((BlockMultiPlaceEvent) event).getReplacedBlockStates()) {
                    if (!island.equals(islandManager.getIslandAtLocation(blockState.getLocation()))) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        if (islandLevelManager.isScanning(island)) {
            this.plugin.getMessageManager().sendMessage(player,
                    this.plugin.getLanguage().getString("Command.Island.Level.Scanning.BlockPlacing.Message"));
            event.setCancelled(true);
            return;
        }

        FileConfiguration configLoad = this.plugin.getConfiguration();
        IslandWorld world = worldManager.getIslandWorld(block.getWorld());

        if (!player.hasPermission("fabledskyblock.bypass.netherplace") && !islandManager.isIslandWorldUnlocked(island, IslandWorld.NETHER)) {
            if (configLoad.getConfigurationSection("Island.Restrict.NetherBlocks") != null) {
                for (String s : configLoad.getConfigurationSection("Island.Restrict.NetherBlocks").getKeys(false)) {
                    if (s.equalsIgnoreCase(block.getType().toString())) {
                        if (configLoad.getBoolean("Island.Restrict.NetherBlocks." + s, false)) {
                            this.plugin.getMessageManager().sendMessage(player, Objects.requireNonNull(this.plugin.getLanguage().getString("Island.Unlock.NetherBlocksPlace.Message")));
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }

        if (!player.hasPermission("fabledskyblock.bypass.endplace") && !islandManager.isIslandWorldUnlocked(island, IslandWorld.END)) {
            if (configLoad.getConfigurationSection("Island.Restrict.EndBlocks") != null) {
                for (String s : configLoad.getConfigurationSection("Island.Restrict.EndBlocks").getKeys(false)) {
                    if (s.equalsIgnoreCase(block.getType().toString())) {
                        if (configLoad.getBoolean("Island.Restrict.EndBlocks." + s)) {
                            this.plugin.getMessageManager().sendMessage(player, Objects.requireNonNull(this.plugin.getLanguage().getString("Island.Unlock.EndBlocksPlace.Message")));
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }

        if (configLoad.getBoolean("Island.WorldBorder.Block") && block.getType() == Material.DISPENSER) {
            if (!islandManager.isLocationAtIsland(island, blockLoc, world)) {
                event.setCancelled(true);
            }
        }

        // Check spawn protection
        if (configLoad.getBoolean("Island.Spawn.Protection")) {
            boolean isObstructing = false;
            // Directly on the block
            if (LocationUtil.isLocationAffectingIslandSpawn(blockLoc, island, world)) {
                isObstructing = true;
            }

            // Specific check for beds
            if (!isObstructing && event.getBlock().getState().getData() instanceof org.bukkit.material.Bed) {
                BlockFace bedDirection = ((org.bukkit.material.Bed) event.getBlock().getState().getData()).getFacing();
                org.bukkit.block.Block bedBlock = block.getRelative(bedDirection);
                if (LocationUtil.isLocationAffectingIslandSpawn(bedBlock.getLocation(), island, world)) {
                    isObstructing = true;
                }
            }

            if (isObstructing) {
                this.plugin.getMessageManager().sendMessage(player, this.plugin.getLanguage().getString("Island.SpawnProtection.Place.Message"));
                this.plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                event.setCancelled(true);
                return;
            }
        }

        BlockLimitation limits = this.plugin.getLimitationHandler().getInstance(BlockLimitation.class);

        long limit = limits.getBlockLimit(player, block.getType());

        ItemStack item = event.getItemInHand();

        if (limits.isBlockLimitExceeded(block, limit) && CompatibleMaterial.getMaterial(item) != CompatibleMaterial.ENDER_EYE) {
            CompatibleMaterial material = null;
            if (ServerVersion.isServerVersion(ServerVersion.V1_8)) {
                switch (block.getType().toString().toUpperCase()) {
                    case "DIODE_BLOCK_OFF":
                    case "DIODE_BLOCK_ON":
                        material = CompatibleMaterial.REPEATER;
                        break;
                }
            }
            if (material == null) {
                material = CompatibleMaterial.getMaterial(block);
            }

            this.plugin.getMessageManager().sendMessage(player, this.plugin.getLanguage().getString("Island.Limit.Block.Exceeded.Message")
                    .replace("%type", WordUtils.capitalizeFully(material.name().replace("_", " "))).replace("%limit", NumberUtils.formatNumber(limit)));
            this.plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

            event.setCancelled(true);
            return;
        }

        if (!configLoad.getBoolean("Island.Block.Level.Enable")) {
            return;
        }

        if (event.getBlock().getType() == CompatibleMaterial.END_PORTAL_FRAME.getMaterial()
                && event.getPlayer().getItemInHand().getType() == CompatibleMaterial.ENDER_EYE.getMaterial()) {
            return;
        }

        // Not util used 2 islandLevelManager if condition is true
        // Sponge level dupe fix
        if (ServerVersion.isServerVersionBelow(ServerVersion.V1_13) &&
                block.getType() == CompatibleMaterial.SPONGE.getBlockMaterial()) {
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                if (blockLoc.getBlock().getType() == CompatibleMaterial.WET_SPONGE.getBlockMaterial()) {
                    IslandLevel level = island.getLevel();
                    CompatibleMaterial material = CompatibleMaterial.SPONGE;
                    if (level.hasMaterial(material.name())) {
                        long materialAmount = level.getMaterialAmount(material.name());

                        if (materialAmount - 1 <= 0) {
                            level.removeMaterial(material.name());
                        } else {
                            level.setMaterialAmount(material.name(), materialAmount - 1);
                        }

                        islandLevelManager.updateLevel(island, blockLoc);
                    }
                }
            });
        } else {
            islandLevelManager.updateLevel(island, blockLoc);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!this.plugin.getWorldManager().isIslandWorld(event.getBlock().getWorld())) {
            return;
        }

        IslandManager islandManager = this.plugin.getIslandManager();
        WorldManager worldManager = this.plugin.getWorldManager();

        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());

        FileConfiguration configLoad = this.plugin.getConfiguration();

        if (island == null) {
            return;
        }

        org.bukkit.block.Block block = event.getToBlock();

        // Protect outside of border
        if (!islandManager.isLocationAtIsland(island, block.getLocation(), world)) {
            event.setCancelled(true);
            return;
        }

        // Protect spawn
        if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world) && configLoad.getBoolean("Island.Spawn.Protection")) {
            event.setCancelled(true);
            return;
        }

        // Nether mobs
        if (configLoad.getBoolean("Island.Nether.WaterDoNotFlowNearNetherMobs", false) && worldManager.getIslandWorld(block.getWorld()).equals(IslandWorld.NETHER)) {
            Collection<Entity> entities = block.getWorld().getNearbyEntities(block.getLocation(), 1d, 1d, 1d);
            if (entities.size() > 0) {
                for (Entity ent : entities) {

                    boolean witherSkeleton;
                    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                        witherSkeleton = ent.getType().equals(EntityType.WITHER_SKELETON);
                    } else {
                        witherSkeleton = ent instanceof Skeleton && ((Skeleton) ent).getSkeletonType().equals(Skeleton.SkeletonType.WITHER);
                    }
                    if ((((ent instanceof Blaze || ent instanceof MagmaCube) || ent instanceof Wither) || ent instanceof Ghast) || witherSkeleton) {
                        event.setCancelled(true);
                        event.getToBlock().getWorld().playSound(block.getLocation(), CompatibleSound.BLOCK_FIRE_EXTINGUISH.getSound(), 1f, 1f);
                        event.getToBlock().getWorld().playEffect(block.getLocation(), Effect.SMOKE, 1);
                    } else {
                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)) {
                            if (((ent instanceof Piglin || ent instanceof Hoglin) || ent instanceof Strider) || ent instanceof Zoglin) {
                                event.setCancelled(true);
                                event.getToBlock().getWorld().playSound(block.getLocation(), CompatibleSound.BLOCK_FIRE_EXTINGUISH.getSound(), 1f, 1f);
                                event.getToBlock().getWorld().playEffect(block.getLocation(), Effect.SMOKE, 1);
                            }
                        } else {
                            if (ent instanceof PigZombie) {
                                event.setCancelled(true);
                                event.getToBlock().getWorld().playSound(block.getLocation(), CompatibleSound.BLOCK_FIRE_EXTINGUISH.getSound(), 1f, 1f);
                                event.getToBlock().getWorld().playEffect(block.getLocation(), Effect.SMOKE, 1);
                            }
                        }
                    }
                }
            }
        }

        // Generators
        if (this.generatorWaitingLocs.contains(LocationUtil.toBlockLocation(block.getLocation().clone()))) {
            event.setCancelled(true);
            return;
        }

        if (ServerVersion.isServerVersionBelow(ServerVersion.V1_12)) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                handleGeneration(block, island, event.getToBlock().getState());
            }, 1L);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        WorldManager worldManager = this.plugin.getWorldManager();
        if (!worldManager.isIslandWorld(event.getBlock().getWorld())) {
            return;
        }

        IslandManager islandManager = this.plugin.getIslandManager();
        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        if (island == null) {
            return;
        }

        FileConfiguration configLoad = this.plugin.getConfiguration();

        if (performStackCheck(event.getBlock(), event.getBlocks(), event.getDirection())) {
            event.setCancelled(true);
            return;
        }

        IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());
        for (org.bukkit.block.Block block : event.getBlocks()) {
            if (!islandManager.isLocationAtIsland(island, block.getLocation(), world) || !islandManager.isLocationAtIsland(island, block.getRelative(event.getDirection()).getLocation(), world)) {
                event.setCancelled(true);
                return;
            }

            if (!island.isInBorder(block.getRelative(event.getDirection()).getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (this.plugin.getStackableManager() != null && this.plugin.getStackableManager().isStacked(block.getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (configLoad.getBoolean("Island.Spawn.Protection")) {
                // Check exact block
                if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
                    event.setCancelled(true);
                    return;
                }

                // Check block in direction
                if (LocationUtil.isLocationAffectingIslandSpawn(block.getRelative(event.getDirection()).getLocation(), island, world)) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (!this.plugin.getConfiguration().getBoolean("Island.Block.Piston.Connected.Extend")) {
                if (block.getType() == CompatibleMaterial.PISTON.getMaterial() || block.getType() == CompatibleMaterial.STICKY_PISTON.getMaterial()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // Check piston head
        if (configLoad.getBoolean("Island.Spawn.Protection")) {
            if (LocationUtil.isLocationAffectingIslandSpawn(event.getBlock().getRelative(event.getDirection()).getLocation(), island, world)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean performStackCheck(org.bukkit.block.Block block, List<org.bukkit.block.Block> list, BlockFace blockFace) {
        return !getArmorStands(list.isEmpty() ? block : list.get(list.size() - 1), blockFace).isEmpty();
    }

    private List<ArmorStand> getArmorStands(org.bukkit.block.Block block, BlockFace blockFace) {

        final List<ArmorStand> list = new ArrayList<>();

        block = block.getRelative(blockFace);

        final Location loc = block.getLocation();

        int locX = loc.getBlockX();
        int locZ = loc.getBlockZ();
        int locY = loc.getBlockY();

        for (org.bukkit.entity.Entity entity : block.getChunk().getEntities()) {

            if (!(entity instanceof ArmorStand) || !entity.hasMetadata("StackableArmorStand")) {
                continue;
            }

            final Location entityLoc = entity.getLocation();

            if (entityLoc.getBlockX() != locX) {
                continue;
            }
            if (entityLoc.getBlockZ() != locZ) {
                continue;
            }

            final int dist = locY - entityLoc.getBlockY();

            if (dist >= 0 && dist < 2) {
                list.add((ArmorStand) entity);
            }
        }

        return list;
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        WorldManager worldManager = this.plugin.getWorldManager();
        if (!this.plugin.getWorldManager().isIslandWorld(event.getBlock().getWorld())) {
            return;
        }

        IslandManager islandManager = this.plugin.getIslandManager();
        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        if (island == null) {
            return;
        }

        FileConfiguration configLoad = this.plugin.getConfiguration();

        if (performStackCheck(event.getBlock(), event.getBlocks(), event.getDirection())) {
            event.setCancelled(true);
            return;
        }

        IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());
        for (org.bukkit.block.Block block : event.getBlocks()) {
            if (!islandManager.isLocationAtIsland(island, block.getLocation(), world)) {
                event.setCancelled(true);
                return;
            }

            if (!island.isInBorder(block.getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (this.plugin.getStackableManager() != null && this.plugin.getStackableManager().isStacked(block.getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world) && configLoad.getBoolean("Island.Spawn.Protection")) {
                event.setCancelled(true);
                return;
            }

            if (!this.plugin.getConfiguration().getBoolean("Island.Block.Piston.Connected.Retract")) {
                if (block.getType() == CompatibleMaterial.PISTON.getMaterial() || block.getType() == CompatibleMaterial.STICKY_PISTON.getMaterial()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        org.bukkit.block.Block block = event.getBlock();
        WorldManager worldManager = this.plugin.getWorldManager();

        FileConfiguration config = this.plugin.getConfiguration();

        if (!worldManager.isIslandWorld(block.getWorld())) {
            return;
        }

        // Check ice/snow forming
        if (block.getType() == Material.ICE || block.getType() == Material.SNOW) {
            if (!config.getBoolean("Island.Weather.IceAndSnow")) {
                event.setCancelled(true);
            }
            return;
        }

        IslandManager islandManager = this.plugin.getIslandManager();
        Island island = islandManager.getIslandAtLocation(block.getLocation());

        if (island == null) {
            return;
        }

        // Check spawn block protection
        IslandWorld world = worldManager.getIslandWorld(block.getWorld());
        if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
            if (this.plugin.getConfiguration().getBoolean("Island.Spawn.Protection")) {
                event.setCancelled(true);
                return;
            }
        }
        if (handleGeneration(block, island, event.getNewState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        org.bukkit.block.Block block = event.getBlock();
        WorldManager worldManager = this.plugin.getWorldManager();
        if (!worldManager.isIslandWorld(block.getWorld())) {
            return;
        }

        IslandManager islandManager = this.plugin.getIslandManager();
        PermissionManager permissionManager = this.plugin.getPermissionManager();
        if (!permissionManager.hasPermission(
                islandManager.getIslandAtLocation(block.getLocation()), "FireSpread", IslandRole.OWNER)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        if (!this.plugin.getConfiguration().getBoolean("Island.Spawn.Protection")) {
            return;
        }

        WorldManager worldManager = this.plugin.getWorldManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        // PortalCreateEvent.getBlocks() changed from ArrayList<Block> to
        // ArrayList<BlockState> in 1.14.1
        if (ServerVersion.isServerVersionAbove(ServerVersion.V1_13)) {
            List<BlockState> blocks = event.getBlocks(); // TODO 1.8
            if (event.getBlocks().isEmpty()) {
                return;
            }

            Island island = islandManager.getIslandAtLocation(event.getBlocks().get(0).getLocation());
            if (island == null) {
                return;
            }

            // Check spawn block protection
            IslandWorld world = worldManager.getIslandWorld(event.getBlocks().get(0).getWorld());

            for (BlockState block : blocks) {
                if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            try {
                @SuppressWarnings("unchecked")
                List<org.bukkit.block.Block> blocks = (List<org.bukkit.block.Block>) event.getClass().getMethod("getBlocks").invoke(event);
                if (blocks.isEmpty()) {
                    return;
                }

                Island island = islandManager.getIslandAtLocation(blocks.get(0).getLocation());
                if (island == null) {
                    return;
                }

                // Check spawn block protection
                IslandWorld world = worldManager.getIslandWorld(blocks.get(0).getWorld());
                for (org.bukkit.block.Block block : blocks) {
                    if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            } catch (ReflectiveOperationException ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        if (this.plugin.getWorldManager().isIslandWorld(player.getWorld())) {
            IslandManager islandManager = this.plugin.getIslandManager();
            Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
            // Check permissions.
            this.plugin.getPermissionManager().processPermission(event, player, island);
        }
    }

    @EventHandler
    public void onDispenserDispenseBlock(BlockDispenseEvent event) {
        if (!this.plugin.getConfiguration().getBoolean("Island.Spawn.Protection")) {
            return;
        }

        WorldManager worldManager = this.plugin.getWorldManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        @SuppressWarnings("deprecation")
        BlockFace dispenserDirection = ((org.bukkit.material.Dispenser) event.getBlock().getState().getData()).getFacing();
        org.bukkit.block.Block placeLocation = event.getBlock().getRelative(dispenserDirection);

        if (CompatibleMaterial.getMaterial(event.getItem()) == CompatibleMaterial.WATER_BUCKET
                && this.plugin.getConfiguration().getBoolean("Island.Nether.AllowNetherWater", false)) {
            placeLocation.setType(Material.WATER);
        }

        Island island = islandManager.getIslandAtLocation(placeLocation.getLocation());
        if (island == null) {
            return;
        }

        // Check spawn block protection
        IslandWorld world = worldManager.getIslandWorld(placeLocation.getWorld());

        if (LocationUtil.isLocationAffectingIslandSpawn(placeLocation.getLocation(), island, world)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLiquidDestroyBlock(BlockFromToEvent event) {
        if (!this.plugin.getWorldManager().isIslandWorld(event.getBlock().getWorld())) {
            return;
        }

        IslandManager islandManager = this.plugin.getIslandManager();
        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        if (island == null) {
            return;
        }

        CompatibleMaterial destmaterial = CompatibleMaterial.getMaterial(event.getToBlock());
        if (destmaterial == CompatibleMaterial.AIR) {
            return;
        }
        if (ServerVersion.isServerVersion(ServerVersion.V1_8)) {
            switch (event.getToBlock().getType().toString().toUpperCase()) {
                case "DIODE_BLOCK_OFF":
                case "DIODE_BLOCK_ON":
                    destmaterial = CompatibleMaterial.REPEATER;
                    break;
            }
        }

        CompatibleMaterial srcmaterial = CompatibleMaterial.getMaterial(event.getBlock());
        if (srcmaterial != CompatibleMaterial.WATER
                && srcmaterial != CompatibleMaterial.LAVA) {
            return;
        }

        FileConfiguration configLoad = this.plugin.getConfiguration();
        if (!configLoad.getBoolean("Island.Block.Level.Enable")) {
            return;
        }

        IslandLevel level = island.getLevel();
        if (destmaterial != null && level.hasMaterial(destmaterial.name())) {
            long materialAmount = level.getMaterialAmount(destmaterial.name());

            if (materialAmount - 1 <= 0) {
                level.removeMaterial(destmaterial.name());
            } else {
                level.setMaterialAmount(destmaterial.name(), materialAmount - 1);
            }
        }
    }

    public boolean handleGeneration(Block block, Island island, BlockState state) {
        WorldManager worldManager = this.plugin.getWorldManager();
        IslandLevelManager islandLevelManager = this.plugin.getLevellingManager();
        FileConfiguration config = this.plugin.getConfiguration();
        IslandManager islandManager = this.plugin.getIslandManager();
        IslandWorld world = worldManager.getIslandWorld(block.getWorld());

        CompatibleMaterial material = CompatibleMaterial.getMaterial(block);

        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_12)
                && material != CompatibleMaterial.WATER
                && material != CompatibleMaterial.LAVA) {
            return false;
        }

        Material type = state.getType();

        if (type != Material.COBBLESTONE && type != Material.STONE) {
            return false;
        }

        GeneratorManager generatorManager = this.plugin.getGeneratorManager();
        if (generatorManager == null) {
            return false;
        }

        List<Generator> generators = Lists.newArrayList(generatorManager.getGenerators());

        if (generators.isEmpty()) {
            return false;
        }

        Collections.reverse(generators); // Use the highest generator available

        boolean ignoreVisitors = config.getBoolean("Island.Generator.IgnoreVisitors", false);

        // Filter valid players on the island.
        List<Player> possiblePlayers = new ArrayList<>();
        Set<UUID> visitors = island.getVisit().getVisitors();
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean isMember = island.hasRole(IslandRole.OWNER, player.getUniqueId()) ||
                    island.hasRole(IslandRole.MEMBER, player.getUniqueId()) ||
                    island.hasRole(IslandRole.COOP, player.getUniqueId()) ||
                    island.hasRole(IslandRole.OPERATOR, player.getUniqueId()) ||
                    (!ignoreVisitors &&
                            visitors.contains(player.getUniqueId()) &&
                            player.hasPermission("fabledskyblock.generator.anywhere"));

            if (isMember && islandManager.isLocationAtIsland(island, player.getLocation(), world)) {
                possiblePlayers.add(player);
            }
        }

        if (!possiblePlayers.isEmpty()) {
            boolean nearestPlayer = config.getBoolean("Island.Generator.CheckOnlyNearestPlayer", false);

            if (nearestPlayer) {
                possiblePlayers.sort(Comparator.comparingDouble(a -> a.getLocation().distance(block.getLocation())));
            }

            boolean onlyOwner = config.getBoolean("Island.Generator.CheckOnlyOwnerPermissions", false);

            double distance = possiblePlayers.get(0).getLocation().distance(block.getLocation());
            // Find highest generator available
            for (Generator generator : generators) {
                if (island.getLevel().getLevel() < generator.getLevel()) {
                    continue;
                }
                if (onlyOwner && this.plugin.getVaultPermission() != null) {
                    OfflinePlayer owner = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());
                    if (owner.isOnline()) {
                        Player onlineOwner = (Player) owner;
                        if (onlineOwner.hasPermission(generator.getPermission())) {
                            applyGenerator(block, worldManager, islandLevelManager, island, state, generatorManager, generator);
                        }
                        continue;
                    }
                    org.bukkit.World finalWorld = block.getWorld();
                    this.generatorWaitingLocs.add(LocationUtil.toBlockLocation(block.getLocation().clone()));
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        if (this.plugin.getVaultPermission().playerHas(block.getWorld().getName(), owner, generator.getPermission()) ||
                                this.plugin.getVaultPermission().playerHas(block.getWorld().getName(), owner, "fabledskyblock.generator.*") ||
                                this.plugin.getVaultPermission().playerHas(block.getWorld().getName(), owner, "fabledskyblock.*")) {
                            Bukkit.getScheduler().runTask(this.plugin, () -> {
                                this.generatorWaitingLocs.remove(LocationUtil.toBlockLocation(block.getLocation().clone()));

                                if (worldManager.getIslandWorld(finalWorld) != generator.getIsWorld()) {
                                    return;
                                }

                                BlockState genState = generatorManager.generateBlock(generator, block);
                                block.setType(genState.getType());

                                if (ServerVersion.isServerVersionBelow(ServerVersion.V1_13)) {
                                    BlockState tempState = block.getState();
                                    tempState.setData(genState.getData());
                                    tempState.update(true, true);
                                }
                                islandLevelManager.updateLevel(island, genState.getLocation());
                            });
                        } else {
                            Bukkit.getScheduler().runTask(this.plugin, () ->
                                    block.setType(CompatibleMaterial.COBBLESTONE.getMaterial()));
                        }
                    });
                    return true;
                }
                for (Player player : possiblePlayers) {
                    if (nearestPlayer && player.getLocation().distance(block.getLocation()) > distance) {
                        break;
                    }
                    if (generator.isPermission()) {
                        if (!player.hasPermission(generator.getPermission()) && !player.hasPermission("fabledskyblock.generator.*") && !player.hasPermission("fabledskyblock.*")) {
                            continue;
                        }
                    }

                    if (applyGenerator(block, worldManager, islandLevelManager, island, state, generatorManager, generator)) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private boolean applyGenerator(org.bukkit.block.Block block, WorldManager worldManager, IslandLevelManager islandLevelManager, Island island, BlockState state, GeneratorManager generatorManager, Generator generator) {
        if (worldManager.getIslandWorld(block.getWorld()) == generator.getIsWorld()) {
            BlockState genState = generatorManager.generateBlock(generator, block);
            state.setType(genState.getType());

            if (ServerVersion.isServerVersionBelow(ServerVersion.V1_13)) {
                state.setData(genState.getData());
            }
            islandLevelManager.updateLevel(island, genState.getLocation());
            return true;
        }
        return false;
    }
}
