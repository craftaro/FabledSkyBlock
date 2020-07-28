package com.songoda.skyblock.listeners;

import com.google.common.collect.Lists;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.generator.Generator;
import com.songoda.skyblock.generator.GeneratorManager;
import com.songoda.skyblock.island.*;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.limit.impl.BlockLimitation;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.stackable.Stackable;
import com.songoda.skyblock.stackable.StackableManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.version.CompatibleSpawners;
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.world.WorldManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class Block implements Listener {

    private final SkyBlock plugin;

    public Block(SkyBlock plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlock();

        IslandManager islandManager = plugin.getIslandManager();
        StackableManager stackableManager = plugin.getStackableManager();
        WorldManager worldManager = plugin.getWorldManager();

        if (!worldManager.isIslandWorld(block.getWorld())) return;

        Location blockLocation = block.getLocation();

        Island island = islandManager.getIslandAtLocation(blockLocation);

        if (island == null) {
            event.setCancelled(true);
            return;
        }

        // Check permissions.
        if (!plugin.getPermissionManager().processPermission(event, player, island) || event.isCancelled()) {
            return;
        }

        if (stackableManager != null && stackableManager.isStacked(blockLocation)) {
            Stackable stackable = stackableManager.getStack(block.getLocation(), CompatibleMaterial.getMaterial(block));
            if (stackable != null) {
                CompatibleMaterial material = null;
                if(ServerVersion.isServerVersion(ServerVersion.V1_8)) {
                    switch (block.getType().toString().toUpperCase()) {
                        case "DIODE_BLOCK_OFF":
                        case "DIODE_BLOCK_ON":
                            material = CompatibleMaterial.REPEATER;
                            break;
                    }
                }
                if(material == null) {
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

                if(plugin.getCoreProtectAPI() != null) {
                    plugin.getCoreProtectAPI().logRemoval(player.getName(), block.getLocation(), material.getMaterial(), null);
                }

                if (stackable.getSize() <= 1) {
                    stackableManager.removeStack(stackable);
                }

                Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
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

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        IslandWorld world = worldManager.getIslandWorld(block.getWorld());

        if (LocationUtil.isLocationLocation(block.getLocation(), island.getLocation(world, IslandEnvironment.Main).clone().subtract(0.0D, 1.0D, 0.0D))  || LocationUtil.isLocationLocation(block.getLocation(), island.getLocation(world, IslandEnvironment.Visitor).clone().subtract(0.0D, 1.0D, 0.0D))
                || LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
            if (configLoad.getBoolean("Island.Spawn.Protection")) {
                event.setCancelled(true);
                plugin.getMessageManager().sendMessage(player, plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.SpawnProtection.Break.Message"));
                plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            }
        }

        if (event.isCancelled() || !configLoad.getBoolean("Island.Block.Level.Enable")) return;
        
        CompatibleMaterial material = null;
        if(ServerVersion.isServerVersion(ServerVersion.V1_8)) {
            switch (block.getType().toString().toUpperCase()) {
                case "DIODE_BLOCK_OFF":
                case "DIODE_BLOCK_ON":
                    material = CompatibleMaterial.REPEATER;
                    break;
            }
        }
        if(material == null) {
            material = CompatibleMaterial.getMaterial(block);
        }

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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlock();

        IslandManager islandManager = plugin.getIslandManager();
        WorldManager worldManager = plugin.getWorldManager();
        IslandLevelManager islandLevelManager = plugin.getLevellingManager();
        if (!worldManager.isIslandWorld(block.getWorld())) return;

        Location blockLoc = block.getLocation();

        Island island = islandManager.getIslandAtLocation(blockLoc);

        // Check permissions.
        if (!plugin.getPermissionManager().processPermission(event, player, island)){
            return;
        }

        if (island == null) {
            event.setCancelled(true);
            return;
        }
    
        if(ServerVersion.isServerVersionAbove(ServerVersion.V1_8)) {
            if(event instanceof BlockMultiPlaceEvent) {
                for(BlockState blockState : ((BlockMultiPlaceEvent) event).getReplacedBlockStates()) {
                    if(!island.equals(islandManager.getIslandAtLocation(blockState.getLocation()))) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        if (islandLevelManager.isScanning(island)) {
            plugin.getMessageManager().sendMessage(player,
                    plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Command.Island.Level.Scanning.BlockPlacing.Message"));
            event.setCancelled(true);
            return;
        }

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        IslandWorld world = worldManager.getIslandWorld(block.getWorld());

        if(!player.hasPermission("fabledskyblock.bypass.netherplace") && !islandManager.isIslandWorldUnlocked(island, IslandWorld.Nether)){
            if(configLoad.getConfigurationSection("Island.Restrict.NetherBlocks") != null) {
                for(String s : configLoad.getConfigurationSection("Island.Restrict.NetherBlocks").getKeys(false)){
                    if(s.equalsIgnoreCase(block.getType().toString())){
                        if(configLoad.getBoolean("Island.Restrict.NetherBlocks." + s, false)){
                            plugin.getMessageManager().sendMessage(player, Objects.requireNonNull(plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"))
                                    .getFileConfiguration().getString("Island.Unlock.NetherBlocksPlace.Message")));
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }

        if(!player.hasPermission("fabledskyblock.bypass.endplace") && !islandManager.isIslandWorldUnlocked(island, IslandWorld.End)){
            if(configLoad.getConfigurationSection("Island.Restrict.EndBlocks") != null) {
                for(String s : configLoad.getConfigurationSection("Island.Restrict.EndBlocks").getKeys(false)){
                    if(s.equalsIgnoreCase(block.getType().toString())){
                        if(configLoad.getBoolean("Island.Restrict.EndBlocks." + s)){
                            plugin.getMessageManager().sendMessage(player, Objects.requireNonNull(plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"))
                                    .getFileConfiguration().getString("Island.Unlock.EndBlocksPlace.Message")));
                            event.setCancelled(true);
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
                if (LocationUtil.isLocationAffectingIslandSpawn(bedBlock.getLocation(), island, world))
                    isObstructing = true;
            }

            if (isObstructing) {
                plugin.getMessageManager().sendMessage(player, plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.SpawnProtection.Place.Message"));
                plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                event.setCancelled(true);
                return;
            }
        }

        BlockLimitation limits = plugin.getLimitationHandler().getInstance(BlockLimitation.class);

        long limit = limits.getBlockLimit(player, block.getType());

        if (limits.isBlockLimitExceeded(block, limit)) {
            CompatibleMaterial material = null;
            if(ServerVersion.isServerVersion(ServerVersion.V1_8)) {
                switch (block.getType().toString().toUpperCase()) {
                    case "DIODE_BLOCK_OFF":
                    case "DIODE_BLOCK_ON":
                        material = CompatibleMaterial.REPEATER;
                        break;
                }
            }
            if(material == null) {
                material = CompatibleMaterial.getMaterial(block);
            }

            plugin.getMessageManager().sendMessage(player, plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.Limit.Block.Exceeded.Message")
                    .replace("%type", WordUtils.capitalizeFully(material.name().replace("_", " "))).replace("%limit", NumberUtil.formatNumber(limit)));
            plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

            event.setCancelled(true);
            return;
        }

        if (!configLoad.getBoolean("Island.Block.Level.Enable")) return;

        if (event.getBlock().getType() == CompatibleMaterial.END_PORTAL_FRAME.getMaterial()
                && event.getPlayer().getItemInHand().getType() == CompatibleMaterial.ENDER_EYE.getMaterial()) return;
        
        islandLevelManager.updateLevel(island, blockLoc);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!plugin.getWorldManager().isIslandWorld(event.getBlock().getWorld())) return;

        GeneratorManager generatorManager = plugin.getGeneratorManager();
        IslandManager islandManager = plugin.getIslandManager();
        WorldManager worldManager = plugin.getWorldManager();
        IslandLevelManager islandLevelManager = plugin.getLevellingManager();

        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
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

        // Nether mobs
        if(configLoad.getBoolean("Island.Nether.WaterDoNotFlowNearNetherMobs", false) && worldManager.getIslandWorld(block.getWorld()).equals(IslandWorld.Nether)){
            Collection<Entity> entities = block.getWorld().getNearbyEntities(block.getLocation(), 1d, 1d, 1d);
            if(entities.size() > 0){
                for(Entity ent : entities){
    
                    boolean witherSkeleton;
                    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                        witherSkeleton = ent.getType().equals(EntityType.WITHER_SKELETON);
                    } else {
                        witherSkeleton = ent instanceof Skeleton && ((Skeleton) ent).getSkeletonType().equals(Skeleton.SkeletonType.WITHER);
                    }
                    if((((ent instanceof Blaze || ent instanceof MagmaCube) || ent instanceof Wither) || ent instanceof Ghast) || witherSkeleton){
                        event.setCancelled(true);
                        event.getToBlock().getWorld().playSound(block.getLocation(), CompatibleSound.BLOCK_FIRE_EXTINGUISH.getSound(), 1f, 1f);
                        event.getToBlock().getWorld().playEffect(block.getLocation(), Effect.SMOKE, 1);
                    } else {
                        if(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)){
                            if(((ent instanceof Piglin || ent instanceof Hoglin) || ent instanceof Strider) || ent instanceof Zoglin) {
                                event.setCancelled(true);
                                event.getToBlock().getWorld().playSound(block.getLocation(), CompatibleSound.BLOCK_FIRE_EXTINGUISH.getSound(), 1f, 1f);
                                event.getToBlock().getWorld().playEffect(block.getLocation(), Effect.SMOKE, 1);
                            }
                        } else {
                            if(ent instanceof PigZombie) {
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
                    if(generator.getIsWorld().equals(world)){
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
                            islandLevelManager.updateLevel(island, genState.getLocation());
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        WorldManager worldManager = plugin.getWorldManager();
        if (!worldManager.isIslandWorld(event.getBlock().getWorld())) return;

        IslandManager islandManager = plugin.getIslandManager();
        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        if (island == null) return;

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
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

            if (plugin.getStackableManager() != null && plugin.getStackableManager().isStacked(block.getLocation())) {
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

            if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Block.Piston.Connected.Extend")) {
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
        WorldManager worldManager = plugin.getWorldManager();
        if (!plugin.getWorldManager().isIslandWorld(event.getBlock().getWorld())) return;

        IslandManager islandManager = plugin.getIslandManager();
        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        if (island == null) return;

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
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

            if (plugin.getStackableManager() != null && plugin.getStackableManager().isStacked(block.getLocation())) {
                event.setCancelled(true);
                return;
            }

            if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world) && configLoad.getBoolean("Island.Spawn.Protection")) {
                event.setCancelled(true);
                return;
            }

            if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Block.Piston.Connected.Retract")) {
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
        WorldManager worldManager = plugin.getWorldManager();
        IslandLevelManager islandLevelManager = plugin.getLevellingManager();
        
        FileConfiguration config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration();
        
        if (!worldManager.isIslandWorld(block.getWorld())) return;

        // Check ice/snow forming
        if (block.getType() == Material.ICE || block.getType() == Material.SNOW) {
            if (!config.getBoolean("Island.Weather.IceAndSnow"))
                event.setCancelled(true);
            return;
        }

        IslandManager islandManager = plugin.getIslandManager();
        Island island = islandManager.getIslandAtLocation(block.getLocation());

        if (island == null) return;

        // Check spawn block protection
        IslandWorld world = worldManager.getIslandWorld(block.getWorld());
        if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
            if (plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection")) {
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

        GeneratorManager generatorManager = plugin.getGeneratorManager();
        if (generatorManager == null) return;

        List<Generator> generators = Lists.newArrayList(generatorManager.getGenerators());

        if (generators.isEmpty()) return;

        Collections.reverse(generators); // Use the highest generator available

        boolean ignoreVisitors = config.getBoolean("Island.Generator.IgnoreVisitors", false);
        
        // Filter valid players on the island.
        List<Player> possiblePlayers = new ArrayList<>();
        Set<UUID> visitors = island.getVisit().getVisitors();
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean isMember = island.hasRole(IslandRole.Owner, player.getUniqueId()) ||
                    island.hasRole(IslandRole.Member, player.getUniqueId()) ||
                    island.hasRole(IslandRole.Coop, player.getUniqueId()) ||
                    island.hasRole(IslandRole.Operator, player.getUniqueId()) ||
                    (!ignoreVisitors &&
                            visitors.contains(player.getUniqueId()) &&
                            player.hasPermission("fabledskyblock.generator.anywhere"));

            if (isMember && islandManager.isLocationAtIsland(island, player.getLocation(), world)) {
                possiblePlayers.add(player);
            }
        }
    
        if(!possiblePlayers.isEmpty()){
            boolean nearestPlayer = config.getBoolean("Island.Generator.CheckOnlyNearestPlayer", false);
    
            if(nearestPlayer){
                possiblePlayers.sort(Comparator.comparingDouble(a -> a.getLocation().distance(block.getLocation())));
            }
    
            boolean onlyOwner = config.getBoolean("Island.Generator.CheckOnlyOwnerPermissions", false);
            
            double distance = possiblePlayers.get(0).getLocation().distance(block.getLocation());
            // Find highest generator available
            for (Generator generator : generators) {
                if(onlyOwner && plugin.getVaultPermission() != null) {
                    OfflinePlayer owner = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());
                    event.setCancelled(true);
                    org.bukkit.World finalWorld = event.getBlock().getWorld();
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        if(plugin.getVaultPermission().playerHas(block.getWorld().getName(), owner, generator.getPermission()) ||
                                plugin.getVaultPermission().playerHas(block.getWorld().getName(), owner, "fabledskyblock.generator.*") ||
                                plugin.getVaultPermission().playerHas(block.getWorld().getName(), owner, "fabledskyblock.*")) {
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                if(worldManager.getIslandWorld(finalWorld).equals(generator.getIsWorld())){
                                    BlockState genState = generatorManager.generateBlock(generator, block);
                                    block.setType(genState.getType());
                                    
                                    if (ServerVersion.isServerVersionBelow(ServerVersion.V1_13)) {
                                        BlockState tempState = block.getState();
                                        tempState.setData(genState.getData());
                                        tempState.update(true, true);
                                    }
                                    islandLevelManager.updateLevel(island, genState.getLocation());
                                }
                            });
                        } else {
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                block.setType(CompatibleMaterial.COBBLESTONE.getMaterial());
                            });
                        }
                    });
                } else {
                    for (Player player : possiblePlayers) {
                        if(nearestPlayer && player.getLocation().distance(block.getLocation()) > distance){
                            break;
                        }
                        if (generator.isPermission()) {
                            if (!player.hasPermission(generator.getPermission()) && !player.hasPermission("fabledskyblock.generator.*") && !player.hasPermission("fabledskyblock.*")) {
                                continue;
                            }
                        }
        
                        if (applyGenerator(event.getBlock().getWorld(), block, worldManager, islandLevelManager, island, state, generatorManager, generator))
                            return;
                    }
                }
            }
        }
    }
    
    private boolean applyGenerator(org.bukkit.World world, org.bukkit.block.Block block, WorldManager worldManager, IslandLevelManager islandLevelManager, Island island, BlockState state, GeneratorManager generatorManager, Generator generator) {
        if(worldManager.getIslandWorld(world).equals(generator.getIsWorld())){
            BlockState genState = generatorManager.generateBlock(generator, block);
            state.setType(genState.getType());

            if (ServerVersion.isServerVersionBelow(ServerVersion.V1_13)) state.setData(genState.getData());
            islandLevelManager.updateLevel(island, genState.getLocation());
            return true;
        }
        return false;
    }
    
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        org.bukkit.block.Block block = event.getBlock();
        WorldManager worldManager = plugin.getWorldManager();
        if (!worldManager.isIslandWorld(block.getWorld())) return;

        IslandManager islandManager = plugin.getIslandManager();
        PermissionManager permissionManager = plugin.getPermissionManager();
        if (!permissionManager.hasPermission(
                islandManager.getIslandAtLocation(block.getLocation()),"FireSpread", IslandRole.Owner))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection"))
            return;

        WorldManager worldManager = plugin.getWorldManager();
        IslandManager islandManager = plugin.getIslandManager();
        // PortalCreateEvent.getBlocks() changed from ArrayList<Block> to
        // ArrayList<BlockState> in 1.14.1
        if (NMSUtil.getVersionNumber() > 13) {
            List<BlockState> blocks = event.getBlocks(); // TODO 1.8
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
    public void onBlockIgnite(BlockIgniteEvent event) {
        Player player = event.getPlayer();

        if (player == null) return;

        if (plugin.getWorldManager().isIslandWorld(player.getWorld())) {
            IslandManager islandManager = plugin.getIslandManager();
            Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
            // Check permissions.
            plugin.getPermissionManager().processPermission(event, player, island);
        }
    }

    @EventHandler
    public void onDispenserDispenseBlock(BlockDispenseEvent event) {
        if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection"))
            return;

        WorldManager worldManager = plugin.getWorldManager();
        IslandManager islandManager = plugin.getIslandManager();
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

}
