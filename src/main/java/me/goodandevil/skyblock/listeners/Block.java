package me.goodandevil.skyblock.listeners;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.generator.Generator;
import me.goodandevil.skyblock.generator.GeneratorManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandEnvironment;
import me.goodandevil.skyblock.island.IslandLevel;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.island.IslandWorld;
import me.goodandevil.skyblock.stackable.Stackable;
import me.goodandevil.skyblock.stackable.StackableManager;
import me.goodandevil.skyblock.upgrade.Upgrade;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.world.WorldManager;

public class Block implements Listener {

    private final SkyBlock skyblock;

    public Block(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlock();

        IslandManager islandManager = skyblock.getIslandManager();
        StackableManager stackableManager = skyblock.getStackableManager();
        WorldManager worldManager = skyblock.getWorldManager();

        if (!worldManager.isIslandWorld(block.getWorld())) return;

        IslandWorld world = worldManager.getIslandWorld(block.getWorld());
        Island island = islandManager.getIslandAtLocation(block.getLocation());

        if (island == null) {
            event.setCancelled(true);
            return;
        }

        if (!islandManager.hasPermission(player, block.getLocation(), "Destroy")) {
            event.setCancelled(true);
            skyblock.getMessageManager().sendMessage(player,
                    skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                            .getFileConfiguration().getString("Island.Settings.Permission.Message"));
            skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
            return;
        }

        if (stackableManager != null
                && stackableManager.isStacked(block.getLocation())) {
            Stackable stackable = stackableManager.getStack(block.getLocation(), block.getType());
            if (stackable != null) {
                stackable.takeOne();
                if (stackable.getSize() <= 1) {
                    stackableManager.removeStack(stackable);
                }

                Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
                FileConfiguration configLoad = config.getFileConfiguration();

                if (configLoad.getBoolean("Island.Block.Level.Enable")) {
                    Materials materials = Materials.getMaterials(block.getType(), block.getData());

                    if (materials != null) {

                        IslandLevel level = island.getLevel();

                        if (level.hasMaterial(materials.name())) {

                            int materialAmount = level.getMaterialAmount(materials.name());

                            if (materialAmount - 1 <= 0) {
                                level.removeMaterial(materials.name());
                            } else {
                                level.setMaterialAmount(materials.name(), materialAmount - 1);
                            }
                        }
                    }
                }

                event.setCancelled(true);
                block.getWorld().dropItemNaturally(block.getLocation().clone().add(.5, 1, .5), new ItemStack(block.getType()));
            }
        }
        
        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (LocationUtil.isLocationLocation(block.getLocation(),
                island.getLocation(world, IslandEnvironment.Main)
                        .clone()
                        .subtract(0.0D, 1.0D, 0.0D))) {
            if (configLoad.getBoolean("Island.Spawn.Protection")) {
                event.setCancelled(true);
            }
        }

        if (event.isCancelled() || !configLoad.getBoolean("Island.Block.Level.Enable")) return;

        @SuppressWarnings("deprecation")
        Materials materials = Materials.getMaterials(block.getType(), block.getData());

        if (materials == null) return;

        IslandLevel level = island.getLevel();

        if (!level.hasMaterial(materials.name())) return;

        int materialAmount = level.getMaterialAmount(materials.name());

        if (materialAmount - 1 <= 0) {
            level.removeMaterial(materials.name());
        } else {
            level.setMaterialAmount(materials.name(), materialAmount - 1);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        org.bukkit.block.Block block = event.getBlock();

        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();
        if (!worldManager.isIslandWorld(block.getWorld())) return;

        IslandWorld world = worldManager.getIslandWorld(block.getWorld());
        Island island = islandManager.getIslandAtLocation(block.getLocation());

        if (island == null) {
            event.setCancelled(true);
            return;
        }

        if (!islandManager.hasPermission(player, block.getLocation(), "Place")) {
            event.setCancelled(true);
            skyblock.getMessageManager().sendMessage(player,
                    skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                            .getFileConfiguration().getString("Island.Settings.Permission.Message"));
            skyblock.getSoundManager().playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
            return;
        }
        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getBoolean("Island.WorldBorder.Block")) {
            if (block.getType() == Material.DISPENSER) {
                if (!LocationUtil.isLocationAtLocationRadius(block.getLocation(),
                        island.getLocation(world, IslandEnvironment.Island), island.getRadius() - 2.0D)) {
                    event.setCancelled(true);
                }
            }
        }

        if (LocationUtil.isLocationLocation(block.getLocation(),
                island.getLocation(world, IslandEnvironment.Main))
                || LocationUtil.isLocationLocation(block.getLocation(),
                island.getLocation(world, IslandEnvironment.Main).clone().add(0.0D, 1.0D, 0.0D))
                || LocationUtil.isLocationLocation(block.getLocation(),
                island.getLocation(world, IslandEnvironment.Main).clone().subtract(0.0D, 1.0D, 0.0D))) {
            if (configLoad.getBoolean("Island.Spawn.Protection")) {
                event.setCancelled(true);
            }
        }

        if (event.isCancelled() || !configLoad.getBoolean("Island.Block.Level.Enable")) return;

        @SuppressWarnings("deprecation")
        Materials materials = Materials.getMaterials(block.getType(), block.getData());

        if (materials == null) return;
        int materialAmount = 0;
        IslandLevel level = island.getLevel();

        if (level.hasMaterial(materials.name())) {
            materialAmount = level.getMaterialAmount(materials.name());
        }

        level.setMaterialAmount(materials.name(), materialAmount + 1);
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
        
        if (NMSUtil.getVersionNumber() < 12) {
            if (generatorManager != null && generatorManager.getGenerators().size() > 0 && generatorManager.isGenerator(block)) {
                List<Generator> generators = new ArrayList<>(generatorManager.getGenerators());
                Collections.reverse(generators); // Use the highest generator available
                
                // Filter players on the island
                Set<Player> possiblePlayers = new HashSet<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (LocationUtil.isLocationAtLocationRadius(p.getLocation(), island.getLocation(world, IslandEnvironment.Island), island.getRadius())) {
                        possiblePlayers.add(p);
                    }
                }
                
                // Find highest generator available
                for (Generator generator : generators) {
                    for (Player p : possiblePlayers) {
                        if (generator.isPermission()) {
                            if (!p.hasPermission(generator.getPermission())
                                    && !p.hasPermission("fabledskyblock.generator.*")
                                    && !p.hasPermission("fabledskyblock.*")) {
                                continue;
                            }
                        }

                        event.setCancelled(true);
                        generatorManager.generateBlock(generator, block);
                        return;
                    }
                }
            }

            return;
        }

        if (!LocationUtil.isLocationAtLocationRadius(block.getLocation(),
                island.getLocation(world, IslandEnvironment.Island), island.getRadius() - 1.0D)) {
            event.setCancelled(true);
        } else if (LocationUtil.isLocationLocation(block.getLocation(),
                island.getLocation(world, IslandEnvironment.Main)
                        .clone()
                        .subtract(0.0D, 1.0D, 0.0D))) {
            if (configLoad.getBoolean("Island.Spawn.Protection")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (!skyblock.getWorldManager().isIslandWorld(event.getBlock().getWorld())) return;

        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();

        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        if (island == null) return;

        IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        for (org.bukkit.block.Block block : event.getBlocks()) {
            if (!LocationUtil.isLocationAtLocationRadius(block.getLocation(),
                    island.getLocation(world, IslandEnvironment.Island), island.getRadius() - 2.0D)) {
                event.setCancelled(true);
            } else if (skyblock.getStackableManager() != null
                    && skyblock.getStackableManager().isStacked(block.getLocation())) {
                event.setCancelled(true);
            } else if (LocationUtil.isLocationLocation(block.getLocation(),
                    island.getLocation(world, IslandEnvironment.Main)
                            .clone()
                            .subtract(0.0D, 1.0D, 0.0D))) {
                if (configLoad.getBoolean("Island.Spawn.Protection")) {
                    event.setCancelled(true);
                }
            }
        }

        if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                .getFileConfiguration().getBoolean("Island.Block.Piston.Connected.Extend")) {
            for (org.bukkit.block.Block blockList : event.getBlocks()) {
                if (blockList.getType() == Materials.PISTON.parseMaterial()
                        || blockList.getType() == Materials.STICKY_PISTON.parseMaterial()) {
                    event.setCancelled(true);

                    break;
                }
            }
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (!skyblock.getWorldManager().isIslandWorld(event.getBlock().getWorld())) return;

        IslandManager islandManager = skyblock.getIslandManager();
        WorldManager worldManager = skyblock.getWorldManager();

        Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        for (org.bukkit.block.Block block : event.getBlocks()) {
            if (!LocationUtil.isLocationAtLocationRadius(block.getLocation(),
                    island.getLocation(world, IslandEnvironment.Island), island.getRadius() - 2.0D)) {
                event.setCancelled(true);
            } else if (skyblock.getStackableManager() != null) {
                    if (skyblock.getStackableManager().isStacked(block.getLocation())) {
                        event.setCancelled(true);
                        return;
                    }

            } else if (LocationUtil.isLocationLocation(block.getLocation(),
                    island.getLocation(world, IslandEnvironment.Main)
                            .clone()
                            .subtract(0.0D, 1.0D, 0.0D))) {
                if (configLoad.getBoolean("Island.Spawn.Protection")) {
                    event.setCancelled(true);
                }
            }
        }

        if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                .getFileConfiguration().getBoolean("Island.Block.Piston.Connected.Retract")) {
            for (org.bukkit.block.Block blockList : event.getBlocks()) {
                if (blockList.getType() == Materials.PISTON.parseMaterial()
                        || blockList.getType() == Materials.STICKY_PISTON.parseMaterial()) {
                    event.setCancelled(true);

                    break;
                }
            }
        }
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        org.bukkit.block.Block block = event.getBlock();

        IslandManager islandManager = skyblock.getIslandManager();
        GeneratorManager generatorManager = skyblock.getGeneratorManager();
        WorldManager worldManager = skyblock.getWorldManager();

        if (!worldManager.isIslandWorld(block.getWorld())) return;

        if (block.getType() == Material.ICE || block.getType() == Material.SNOW) {
            if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                    .getFileConfiguration().getBoolean("Island.Weather.IceAndSnow")) {
                event.setCancelled(true);
            }
            return;
        }

        Material material = event.getBlock().getType();
        if (material != Materials.WATER.parseMaterial() && 
            material != Materials.LEGACY_STATIONARY_WATER.parseMaterial() && 
            material != Materials.LAVA.parseMaterial() &&
            material != Materials.LEGACY_STATIONARY_LAVA.parseMaterial())
        	return;

        Material type = event.getNewState().getType();
        if (type != Material.COBBLESTONE && type != Material.STONE)
        	return;

        if (generatorManager != null && generatorManager.getGenerators().size() > 0) {
            Island island = islandManager.getIslandAtLocation(event.getBlock().getLocation());
            IslandWorld world = worldManager.getIslandWorld(event.getBlock().getWorld());
            
            List<Generator> generators = new ArrayList<>(generatorManager.getGenerators());
            Collections.reverse(generators); // Use the highest generator available
            
            // Filter players on the island
            Set<Player> possiblePlayers = new HashSet<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (LocationUtil.isLocationAtLocationRadius(player.getLocation(), island.getLocation(world, IslandEnvironment.Island), island.getRadius())) {
                    possiblePlayers.add(player);
                }
            }     
            
            // Find highest generator available
            for (Generator generator : generators) {
                for (Player player : possiblePlayers) {
                    if (generator.isPermission()) {
                        if (!player.hasPermission(generator.getPermission())
                                && !player.hasPermission("fabledskyblock.generator.*")
                                && !player.hasPermission("fabledskyblock.*")) {
                            continue;
                        }
                    }

                    event.setCancelled(true);
                    generatorManager.generateBlock(generator, block);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        org.bukkit.block.Block block = event.getBlock();

        WorldManager worldManager = skyblock.getWorldManager();

        if (worldManager.isIslandWorld(block.getWorld())) {
            if (!skyblock.getIslandManager().hasSetting(block.getLocation(), IslandRole.Owner, "FireSpread")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        org.bukkit.block.Block block = event.getBlock();

        WorldManager worldManager = skyblock.getWorldManager();

        if (event.getSource().getType() != Material.FIRE) {
            return;
        }

        if (worldManager.isIslandWorld(block.getWorld())) {
            if (!skyblock.getIslandManager().hasSetting(block.getLocation(), IslandRole.Owner, "FireSpread")) {
                event.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        org.bukkit.block.Block block = event.getBlock();

        IslandManager islandManager = skyblock.getIslandManager();

        if (!skyblock.getWorldManager().isIslandWorld(block.getWorld())) return;

        Island island = islandManager.getIslandAtLocation(block.getLocation());

        if (island == null) return;

        List<Upgrade> upgrades = skyblock.getUpgradeManager().getUpgrades(Upgrade.Type.Crop);

        if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()
                && island.isUpgrade(Upgrade.Type.Crop)) {
            if (NMSUtil.getVersionNumber() > 12) {
                try {
                    Object blockData = block.getClass().getMethod("getBlockData").invoke(block);

                    if (blockData instanceof org.bukkit.block.data.Ageable) {
                        org.bukkit.block.data.Ageable ageable = (org.bukkit.block.data.Ageable) blockData;
                        ageable.setAge(ageable.getAge() + 1);
                        block.getClass()
                                .getMethod("setBlockData", Class.forName("org.bukkit.block.data.BlockData"))
                                .invoke(block, ageable);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                if (block.getState().getData() instanceof Crops) {
                    try {
                        block.getClass().getMethod("setData", byte.class).invoke(block,
                                (byte) (block.getData() + 1));
                        block.getState().update();
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                            | NoSuchMethodException | SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        org.bukkit.block.Block block = event.getBlock();

        if (skyblock.getWorldManager().isIslandWorld(block.getWorld())) {
            if (!skyblock.getIslandManager().hasSetting(block.getLocation(), IslandRole.Owner, "LeafDecay")) {
                event.setCancelled(false);
            }
        }
    }
}
