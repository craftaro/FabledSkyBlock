package com.songoda.skyblock.listeners;

import com.google.common.collect.Lists;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.generator.Generator;
import com.songoda.skyblock.generator.GeneratorManager;
import com.songoda.skyblock.island.*;
import com.songoda.skyblock.levelling.rework.IslandLevelManager;
import com.songoda.skyblock.limit.impl.BlockLimitation;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.permission.event.events.PlayerEnterPortalEvent;
import com.songoda.skyblock.stackable.Stackable;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.version.CompatibleSpawners;
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.world.WorldManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Block implements Listener {

    private final SkyBlock skyblock;

    public Block(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlock();

        IslandManager islandManager = skyblock.getIslandManager();
        StackableManager stackableManager = skyblock.getStackableManager();
        WorldManager worldManager = skyblock.getWorldManager();

        if (!worldManager.isIslandWorld(block.getWorld())) return;

        Location blockLocation = block.getLocation();

        Island island = islandManager.getIslandAtLocation(blockLocation);

        if (island == null) {
            event.setCancelled(true);
            return;
        }

        // Check permissions.
        if (!skyblock.getPermissionManager().processPermission(event, player, island)) {
            return;
        }

        if (stackableManager != null && stackableManager.isStacked(blockLocation)) {
            Stackable stackable = stackableManager.getStack(block.getLocation(), CompatibleMaterial.getBlockMaterial(block.getType()));
            if (stackable != null) {
                CompatibleMaterial material = CompatibleMaterial.getBlockMaterial(block.getType());
                byte data = block.getData();

                int droppedAmount = 0;
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

                if (stackable.getSize() <= 1) {
                    stackableManager.removeStack(stackable);
                }

                Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
                FileConfiguration configLoad = config.getFileConfiguration();

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

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        IslandWorld world = worldManager.getIslandWorld(block.getWorld());

        if (LocationUtil.isLocationLocation(block.getLocation(), island.getLocation(world, IslandEnvironment.Main).clone().subtract(0.0D, 1.0D, 0.0D))
                || LocationUtil.isLocationLocation(block.getLocation(), island.getLocation(world, IslandEnvironment.Main).clone())) {
            if (configLoad.getBoolean("Island.Spawn.Protection")) {
                event.setCancelled(true);
                skyblock.getMessageManager().sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.SpawnProtection.Break.Message"));
                skyblock.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            }
        }

        if (event.isCancelled() || !configLoad.getBoolean("Island.Block.Level.Enable")) return;

        CompatibleMaterial material = CompatibleMaterial.getMaterial(block);

        if (material == null) return;

        if (material.isTall()) {

            final org.bukkit.block.Block belowBlock = block.getRelative(BlockFace.DOWN);

            if (CompatibleMaterial.getMaterial(belowBlock).isTall()) {
                block = belowBlock;
            }

        }

        if (block.getType() == CompatibleMaterial.SPAWNER.getBlockMaterial()) {
            CompatibleSpawners spawner = CompatibleSpawners.getSpawner(((CreatureSpawner) block.getState()).getSpawnedType());

            if (spawner != null)
                material = CompatibleMaterial.getBlockMaterial(spawner.getMaterial());
        }

        if (material == null) return;

        IslandLevel level = island.getLevel();

        if (!level.hasMaterial(material.name())) return;

        long materialAmount = level.getMaterialAmount(material.name());

        if (materialAmount - 1 <= 0) {
            level.removeMaterial(material.name());
        } else {
            level.setMaterialAmount(material.name(), materialAmount - 1);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlock();

        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();
        IslandLevelManager levellingManager = skyblock.getLevellingManager();
        if (!worldManager.isIslandWorld(block.getWorld())) return;

        Location blockLoc = block.getLocation();

        Island island = islandManager.getIslandAtLocation(blockLoc);

        if (island == null) {
            event.setCancelled(true);
            return;
        }

        if (levellingManager.isScanning(island)) {
            skyblock.getMessageManager().sendMessage(player,
                    skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Command.Island.Level.Scanning.BlockPlacing.Message"));
            event.setCancelled(true);
            return;
        }

        // Check permissions.
        if (!skyblock.getPermissionManager().processPermission(new PlayerEnterPortalEvent(player, player.getLocation()),
                player, island))
            return;

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        IslandWorld world = worldManager.getIslandWorld(block.getWorld());

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
                if (LocationUtil.isLocationAffectingIslandSpawn(bedBlock.getLocation(), island, world))
                    isObstructing = true;
            }

            if (isObstructing) {
                skyblock.getMessageManager().sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.SpawnProtection.Place.Message"));
                skyblock.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                event.setCancelled(true);
                return;
            }
        }

        BlockLimitation limits = skyblock.getLimitationHandler().getInstance(BlockLimitation.class);

        long limit = limits.getBlockLimit(player, block);

        if (limits.isBlockLimitExceeded(block, limit)) {
            CompatibleMaterial material = CompatibleMaterial.getMaterial(block.getType());

            skyblock.getMessageManager().sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Limit.Block.Exceeded.Message")
                    .replace("%type", WordUtils.capitalizeFully(material.name().replace("_", " "))).replace("%limit", NumberUtil.formatNumber(limit)));
            skyblock.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

            event.setCancelled(true);
            return;
        }

        if (!configLoad.getBoolean("Island.Block.Level.Enable")) return;

        if (event.getBlock().getType() == CompatibleMaterial.END_PORTAL_FRAME.getMaterial()
                && event.getPlayer().getItemInHand().getType() == CompatibleMaterial.ENDER_EYE.getMaterial()) return;

        updateLevel(island, blockLoc);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!skyblock.getWorldManager().isIslandWorld(event.getBlock().getWorld())) return;

        GeneratorManager generatorManager = skyblock.getGeneratorManager();
        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();

        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (island == null) return;

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

        if (NMSUtil.getVersionNumber() < 12) {
            if (generatorManager != null && generatorManager.getGenerators().size() > 0 && generatorManager.isGenerator(block)) {
                List<Generator> generators = new ArrayList<>(generatorManager.getGenerators());
                Collections.reverse(generators); // Use the highest generator available

                // Filter valid players on the island
                Set<Player> possiblePlayers = new HashSet<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    boolean isMember = island.hasRole(IslandRole.Owner, p.getUniqueId()) || island.hasRole(IslandRole.Member, p.getUniqueId()) || island.hasRole(IslandRole.Coop, p.getUniqueId())
                            || island.hasRole(IslandRole.Operator, p.getUniqueId());
                    if (isMember && islandManager.isLocationAtIsland(island, p.getLocation(), world)) {
                        possiblePlayers.add(p);
                    }
                }

                // Find highest generator available
                for (Generator generator : generators) {
                    for (Player p : possiblePlayers) {

                        if (generator.isPermission() &&
                                !p.hasPermission(generator.getPermission()) &&
                                !p.hasPermission("fabledskyblock.generator.*") &&
                                !p.hasPermission("fabledskyblock.*")) {
                            continue;
                        }

                        org.bukkit.block.BlockState genState = generatorManager.generateBlock(generator, block);
                        org.bukkit.block.BlockState toBlockState = event.getToBlock().getState();

                        toBlockState.setData(genState.getData());
                        toBlockState.setType(genState.getType());
                        toBlockState.update();
                        updateLevel(island, genState.getLocation());
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        WorldManager worldManager = skyblock.getWorldManager();
        if (!worldManager.isIslandWorld(event.getBlock().getWorld())) return;

        IslandManager islandManager = skyblock.getIslandManager();
        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        if (island == null) return;

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

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

            if (skyblock.getStackableManager() != null && skyblock.getStackableManager().isStacked(block.getLocation())) {
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

            if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Block.Piston.Connected.Extend")) {
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

            if (!(entity instanceof ArmorStand) || !entity.hasMetadata("StackableArmorStand")) continue;

            final Location entityLoc = entity.getLocation();

            if (entityLoc.getBlockX() != locX) continue;
            if (entityLoc.getBlockZ() != locZ) continue;

            final int dist = locY - entityLoc.getBlockY();

            if (dist >= 0 && dist < 2) list.add((ArmorStand) entity);
        }

        return list;
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        WorldManager worldManager = skyblock.getWorldManager();
        if (!skyblock.getWorldManager().isIslandWorld(event.getBlock().getWorld())) return;

        IslandManager islandManager = skyblock.getIslandManager();
        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        if (island == null) return;

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

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

            if (skyblock.getStackableManager() != null && skyblock.getStackableManager().isStacked(block.getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world) && configLoad.getBoolean("Island.Spawn.Protection")) {
                event.setCancelled(true);
                return;
            }

            if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Block.Piston.Connected.Retract")) {
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
        WorldManager worldManager = skyblock.getWorldManager();
        if (!worldManager.isIslandWorld(block.getWorld())) return;

        // Check ice/snow forming
        if (block.getType() == Material.ICE || block.getType() == Material.SNOW) {
            if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Weather.IceAndSnow"))
                event.setCancelled(true);
            return;
        }

        IslandManager islandManager = skyblock.getIslandManager();
        Island island = islandManager.getIslandAtLocation(block.getLocation());

        if (island == null) return;

        // Check spawn block protection
        IslandWorld world = worldManager.getIslandWorld(block.getWorld());
        if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
            if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection")) {
                event.setCancelled(true);
                return;
            }
        }

        CompatibleMaterial material = CompatibleMaterial.getMaterial(block);
        if (material != CompatibleMaterial.WATER
                && material != CompatibleMaterial.LAVA)
            return;

        BlockState state = event.getNewState();
        Material type = state.getType();

        if (type != Material.COBBLESTONE && type != Material.STONE) return;

        GeneratorManager generatorManager = skyblock.getGeneratorManager();
        if (generatorManager == null) return;

        List<Generator> generators = Lists.newArrayList(generatorManager.getGenerators());

        if (generators.isEmpty()) return;

        Collections.reverse(generators); // Use the highest generator available

        // Filter valid players on the island.
        Set<Player> possiblePlayers = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean isMember = island.hasRole(IslandRole.Owner, player.getUniqueId()) ||
                    island.hasRole(IslandRole.Member, player.getUniqueId()) ||
                    island.hasRole(IslandRole.Coop, player.getUniqueId()) ||
                    island.hasRole(IslandRole.Operator, player.getUniqueId());

            if (isMember && islandManager.isLocationAtIsland(island, player.getLocation(), world)) {
                possiblePlayers.add(player);
            }
        }

        // Find highest generator available
        for (Generator generator : generators) {
            for (Player player : possiblePlayers) {
                if (generator.isPermission()) {
                    if (!player.hasPermission(generator.getPermission()) && !player.hasPermission("fabledskyblock.generator.*") && !player.hasPermission("fabledskyblock.*")) {
                        continue;
                    }
                }

                org.bukkit.block.BlockState genState = generatorManager.generateBlock(generator, block);
                state.setType(genState.getType());

                if (NMSUtil.getVersionNumber() < 13) state.setData(genState.getData());
                updateLevel(island, genState.getLocation());
                return;
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        org.bukkit.block.Block block = event.getBlock();
        WorldManager worldManager = skyblock.getWorldManager();
        if (!worldManager.isIslandWorld(block.getWorld())) return;

        IslandManager islandManager = skyblock.getIslandManager();
        PermissionManager permissionManager = skyblock.getPermissionManager();
        if (!permissionManager.hasPermission(
                islandManager.getIslandAtLocation(block.getLocation()),"FireSpread", IslandRole.Owner))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection"))
            return;

        WorldManager worldManager = skyblock.getWorldManager();
        IslandManager islandManager = skyblock.getIslandManager();
        // PortalCreateEvent.getBlocks() changed from ArrayList<Block> to
        // ArrayList<BlockState> in 1.14.1
        if (NMSUtil.getVersionNumber() > 13) {
            List<BlockState> blocks = event.getBlocks();
            if (event.getBlocks().isEmpty()) return;

            Island island = islandManager.getIslandAtLocation(event.getBlocks().get(0).getLocation());
            if (island == null) return;

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
                if (blocks.isEmpty()) return;

                Island island = islandManager.getIslandAtLocation(blocks.get(0).getLocation());
                if (island == null) return;

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
    public void onDispenserDispenseBlock(BlockDispenseEvent event) {
        if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection"))
            return;

        WorldManager worldManager = skyblock.getWorldManager();
        IslandManager islandManager = skyblock.getIslandManager();
        @SuppressWarnings("deprecation")
        BlockFace dispenserDirection = ((org.bukkit.material.Dispenser) event.getBlock().getState().getData()).getFacing();
        org.bukkit.block.Block placeLocation = event.getBlock().getRelative(dispenserDirection);

        Island island = islandManager.getIslandAtLocation(placeLocation.getLocation());
        if (island == null) return;

        // Check spawn block protection
        IslandWorld world = worldManager.getIslandWorld(placeLocation.getWorld());

        if (LocationUtil.isLocationAffectingIslandSpawn(placeLocation.getLocation(), island, world))
            event.setCancelled(true);
    }

    private void updateLevel(Island island, Location location) {
        // Fix a bug in Paper 1.8.8 when using ViaVersion on a 1.12.2 client.
        // BUG: Player can infinitely increase their level by placing a block at their
        // feet.
        // It doesn't take the block away but still increments the level.
        // This doesn't happen in Spigot, but does happen in PaperSpigot due to a
        // BlockPlaceEvent being incorrectly fired.
        // The solution is to wait a tick to make sure that the block was actually
        // placed.
        // This shouldn't cause any issues besides the task number being increased
        // insanely fast.
        Bukkit.getScheduler().runTask(skyblock, () -> {
            org.bukkit.block.Block block = location.getBlock();
            CompatibleMaterial material = CompatibleMaterial.getMaterial(block);

            if (material == null || material == CompatibleMaterial.AIR) return;

            if (material == CompatibleMaterial.SPAWNER) {
                if (Bukkit.getPluginManager().isPluginEnabled("EpicSpawners") || Bukkit.getPluginManager().isPluginEnabled("WildStacker"))
                    return;

                CompatibleSpawners spawner = CompatibleSpawners.getSpawner(((CreatureSpawner) block.getState()).getSpawnedType());

                if (spawner != null)
                    material = CompatibleMaterial.getBlockMaterial(spawner.getMaterial());
            }

            long materialAmount = 0;
            IslandLevel level = island.getLevel();

            if (level.hasMaterial(material.name()))
                materialAmount = level.getMaterialAmount(material.name());

            level.setMaterialAmount(material.name(), materialAmount + 1);
        });
    }

}
