package com.craftaro.skyblock.listeners;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.MajorServerVersion;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.permission.PermissionManager;
import com.craftaro.skyblock.upgrade.Upgrade;
import com.craftaro.skyblock.utils.world.LocationUtil;
import com.craftaro.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.material.Crops;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class GrowListeners implements Listener {
    private final SkyBlock plugin;

    public GrowListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks that a structure like a tree is not growing outside or into another
     * island.
     *
     * @author LimeGlass
     */
    @EventHandler(ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        WorldManager worldManager = this.plugin.getWorldManager();
        if (!worldManager.isIslandWorld(event.getWorld())) {
            return;
        }

        IslandManager islandManager = this.plugin.getIslandManager();
        Island origin = islandManager.getIslandAtLocation(event.getLocation());
        for (Iterator<BlockState> it = event.getBlocks().iterator(); it.hasNext(); ) {
            BlockState state = it.next();
            Island growingTo = islandManager.getIslandAtLocation(state.getLocation());
            // This block is ok to continue as it's not related to Skyblock islands.
            if (origin == null && growingTo == null) {
                continue;
            }

            //Is in border of island
            if (origin != null && !origin.isInBorder(state.getLocation())) {
                it.remove();
                continue;
            }

            // A block from the structure is outside/inside that it's not suppose to.
            if (origin == null || growingTo == null) {
                it.remove();
                continue;
            }
            // The structure is growing from one island to another.
            if (!origin.getIslandUUID().equals(growingTo.getIslandUUID())) {
                it.remove();
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCropUpgrade(BlockGrowEvent event) {
        org.bukkit.block.Block block = event.getBlock();
        WorldManager worldManager = this.plugin.getWorldManager();
        if (!this.plugin.getWorldManager().isIslandWorld(block.getWorld())) {
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

        List<Upgrade> upgrades = this.plugin.getUpgradeManager().getUpgrades(Upgrade.Type.CROP);
        if (upgrades == null || upgrades.isEmpty() || !upgrades.get(0).isEnabled() || !island.isUpgrade(Upgrade.Type.CROP)) {
            return;
        }

        if (MajorServerVersion.isServerVersionAbove(MajorServerVersion.V1_12)) {
            try {
                Object blockData = block.getClass().getMethod("getBlockData").invoke(block);
                if (blockData instanceof org.bukkit.block.data.Ageable) {
                    org.bukkit.block.data.Ageable ageable = (org.bukkit.block.data.Ageable) blockData;
                    ageable.setAge(ageable.getAge() + 1);
                    block.getClass().getMethod("setBlockData", Class.forName("org.bukkit.block.data.BlockData")).invoke(block, ageable);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                     NoSuchMethodException | SecurityException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        } else {
            Optional<XMaterial> type = CompatibleMaterial.getMaterial(block.getType());
            if (block.getState().getData() instanceof Crops || type.get().name().equals("BEETROOT_BLOCK") || type.get().name().equals("CARROT") || type.get().name().equals("POTATO")
                    || type.get().name().equals("WHEAT") || type.get().name().equals("CROPS")) {
                try {
                    block.getClass().getMethod("setData", byte.class).invoke(block, (byte) (block.getData() + 1));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                         NoSuchMethodException | SecurityException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Checks that a block like a pumpkins and melons are not growing outside or
     * into another island.
     *
     * @author LimeGlass
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {
        WorldManager worldManager = this.plugin.getWorldManager();
        BlockState state = event.getNewState();
        if (!worldManager.isIslandWorld(state.getWorld())) {
            return;
        }

        IslandManager islandManager = this.plugin.getIslandManager();
        Island origin = islandManager.getIslandAtLocation(event.getBlock().getLocation());
        Island growingTo = islandManager.getIslandAtLocation(state.getLocation());
        // This block is ok to continue as it's not related to Skyblock islands.
        if (origin == null && growingTo == null) {
            return;
        }

        // The growing block is outside/inside that it's not supposed to or the block is growing from one island to another.
        if (origin == null
                || growingTo == null
                || !origin.getIslandUUID().equals(growingTo.getIslandUUID())) {
            event.setCancelled(true);
            return;
        }

        Material type = state.getType();
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (state.getBlock().getType() == type) {
                growingTo.getLevel().addMaterial(type.name(), 1);
            }
        }, 1L);
    }

    /**
     * Checks that a structure growing like a tree, does not impact spawn location
     * of the island.
     */
    @EventHandler(ignoreCancelled = true)
    public void onStructureCreate(StructureGrowEvent event) {
        if (!this.plugin.getConfiguration().getBoolean("Island.Spawn.Protection")) {
            return;
        }

        List<BlockState> blocks = event.getBlocks();
        if (blocks.isEmpty()) {
            return;
        }

        WorldManager worldManager = this.plugin.getWorldManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        Island island = islandManager.getIslandAtLocation(event.getLocation());
        if (island == null) {
            return;
        }

        // Check spawn block protection
        IslandWorld world = worldManager.getIslandWorld(blocks.get(0).getWorld());
        for (BlockState block : event.getBlocks()) {
            if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onFireSpread(BlockSpreadEvent event) {
        if (event.getSource().getType() != Material.FIRE) {
            return;
        }

        org.bukkit.block.Block block = event.getBlock();
        if (!this.plugin.getWorldManager().isIslandWorld(block.getWorld())) {
            return;
        }

        PermissionManager permissionManager = this.plugin.getPermissionManager();
        if (!permissionManager.hasPermission(block.getLocation(), "FireSpread", IslandRole.OWNER)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        org.bukkit.block.Block block = event.getBlock();
        if (!this.plugin.getWorldManager().isIslandWorld(block.getWorld())) {
            return;
        }

        PermissionManager permissionManager = this.plugin.getPermissionManager();
        if (!permissionManager.hasPermission(block.getLocation(), "LeafDecay", IslandRole.OWNER)) {
            event.setCancelled(true);
        }
    }
}
