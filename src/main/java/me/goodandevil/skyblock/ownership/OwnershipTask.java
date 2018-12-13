package me.goodandevil.skyblock.ownership;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.IslandManager;

public class OwnershipTask extends BukkitRunnable {

	private final SkyBlock skyblock;
	private final OwnershipManager ownershipManager;

	protected OwnershipTask(OwnershipManager ownershipManager, SkyBlock skyblock) {
		this.ownershipManager = ownershipManager;
		this.skyblock = skyblock;
	}

	@Override
	public void run() {
		IslandManager islandManager = skyblock.getIslandManager();

		for (UUID islandList : islandManager.getIslands().keySet()) {
			if (ownershipManager.hasOwnership(islandList)) {
				Ownership ownership = ownershipManager.getOwnership(islandList);
				ownership.setTime(ownership.getTime() - 1);

				if (ownership.getTime() <= 0) {
					ownershipManager.removeOwnership(islandList);
					ownershipManager.unloadOwnership(islandList);
				}
			}
		}
	}
}
