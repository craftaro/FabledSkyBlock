package com.songoda.skyblock.listeners;

import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.world.WorldManager;

public class Grow implements Listener {

	private final SkyBlock skyblock;

	public Grow(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	/**
	 * Checks that a structure like a tree is not growing outside or into another island.
	 * @author LimeGlass
	 */
	@EventHandler
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

}
