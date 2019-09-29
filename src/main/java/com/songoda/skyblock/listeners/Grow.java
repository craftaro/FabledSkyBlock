package com.songoda.skyblock.listeners;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.material.Crops;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.upgrade.Upgrade;
import com.songoda.skyblock.utils.version.Materials;
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.world.WorldManager;

@SuppressWarnings("deprecation")
public class Grow implements Listener {

	private final SkyBlock skyblock;

	public Grow(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	/**
	 * Checks that a structure like a tree is not growing outside or into another island.
	 * @author LimeGlass
	 */
	@EventHandler(ignoreCancelled = true)
	public void onStructureGrow(StructureGrowEvent event) {
		WorldManager worldManager = skyblock.getWorldManager();
		if (!worldManager.isIslandWorld(event.getWorld()))
			return;

		IslandManager islandManager = skyblock.getIslandManager();
		Island origin = islandManager.getIslandAtLocation(event.getLocation());
		for (BlockState state : event.getBlocks()) {
			Island growingTo = islandManager.getIslandAtLocation(state.getLocation());
			// This block is ok to continue as it's not related to Skyblock islands.
			if (origin == null && growingTo == null)
				continue;
			// A block from the structure is outside/inside that it's not suppose to.
			if (origin == null || growingTo == null) {
				event.getBlocks().remove(state);
				continue;
			}
			// The structure is growing from one island to another.
			if (!origin.getIslandUUID().equals(growingTo.getIslandUUID())) {
				event.getBlocks().remove(state);
				continue;
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onCropUpgrade(BlockGrowEvent event) {
		org.bukkit.block.Block block = event.getBlock();
		WorldManager worldManager = skyblock.getWorldManager();
		if (!skyblock.getWorldManager().isIslandWorld(block.getWorld()))
			return;

		IslandManager islandManager = skyblock.getIslandManager();
		Island island = islandManager.getIslandAtLocation(block.getLocation());
		if (island == null)
			return;

		// Check spawn block protection
		IslandWorld world = worldManager.getIslandWorld(block.getWorld());
		if (LocationUtil.isLocationAffectingIslandSpawn(block.getLocation(), island, world)) {
			if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection")) {
				event.setCancelled(true);
				return;
			}
		}

		List<Upgrade> upgrades = skyblock.getUpgradeManager().getUpgrades(Upgrade.Type.Crop);
		if (upgrades == null || upgrades.size() == 0 || !upgrades.get(0).isEnabled() || !island.isUpgrade(Upgrade.Type.Crop))
			return;

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
			Material type = block.getType();
			if (block.getState().getData() instanceof Crops
					|| type.name().equals("BEETROOT_BLOCK")
					|| type.name().equals("CARROT")
					|| type.name().equals("POTATO")
					|| type.name().equals("WHEAT")
					|| type.name().equals("CROPS")) {
				try {
					block.getClass().getMethod("setData", byte.class).invoke(block, (byte) (block.getData() + 1));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Checks that a block like a pumpkins and melons are not growing outside or into another island.
	 * @author LimeGlass
	 */
	@EventHandler(ignoreCancelled = true)
	public void onBlockGrow(BlockGrowEvent event) {
		WorldManager worldManager = skyblock.getWorldManager();
		BlockState state = event.getNewState();
		if (!worldManager.isIslandWorld(state.getWorld()))
			return;
		if (state.getType() != Materials.PUMPKIN.parseMaterial() && state.getType() != Materials.MELON.parseMaterial())
			return;

		IslandManager islandManager = skyblock.getIslandManager();
		Island origin = islandManager.getIslandAtLocation(event.getBlock().getLocation());
		Island growingTo = islandManager.getIslandAtLocation(state.getLocation());
		// This block is ok to continue as it's not related to Skyblock islands.
		if (origin == null && growingTo == null)
			return;
		// The growing block is outside/inside that it's not suppose to.
		if (origin == null || growingTo == null) {
			event.setCancelled(true);
			return;
		}
		// The block is growing from one island to another.
		if (!origin.getIslandUUID().equals(growingTo.getIslandUUID())) {
			event.setCancelled(true);
			return;
		}
	}

	/**
	 * Checks that a structure growing like a tree, does not impact spawn location of the island.
	 */
	@EventHandler(ignoreCancelled = true)
	public void onStructureCreate(StructureGrowEvent event) {	
		if (!skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection"))
			return;

		List<BlockState> blocks = event.getBlocks();
		if (blocks.isEmpty())
			return;

		WorldManager worldManager = skyblock.getWorldManager();
		IslandManager islandManager = skyblock.getIslandManager();
		Island island = islandManager.getIslandAtLocation(event.getLocation());
		if (island == null)
			return;

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
		if (event.getSource().getType() != Material.FIRE)
			return;

		org.bukkit.block.Block block = event.getBlock();
		if (!skyblock.getWorldManager().isIslandWorld(block.getWorld()))
			return;

		IslandManager islandManager = skyblock.getIslandManager();
		if (!islandManager.hasSetting(block.getLocation(), IslandRole.Owner, "FireSpread"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		org.bukkit.block.Block block = event.getBlock();
		if (!skyblock.getWorldManager().isIslandWorld(block.getWorld()))
			return;

		IslandManager islandManager = skyblock.getIslandManager();
		if (!islandManager.hasSetting(block.getLocation(), IslandRole.Owner, "LeafDecay"))
			event.setCancelled(true);
	}

}
