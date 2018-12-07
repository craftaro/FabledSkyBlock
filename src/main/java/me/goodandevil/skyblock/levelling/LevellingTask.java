package me.goodandevil.skyblock.levelling;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.IslandManager;

public class LevellingTask extends BukkitRunnable {

	private final SkyBlock skyblock;
	private final LevellingManager levellingManager;

	protected LevellingTask(LevellingManager levellingManager, SkyBlock skyblock) {
		this.levellingManager = levellingManager;
		this.skyblock = skyblock;
	}

	@Override
	public void run() {
		IslandManager islandManager = skyblock.getIslandManager();

		for (UUID islandList : islandManager.getIslands().keySet()) {
			if (levellingManager.hasLevelling(islandList)) {
				Levelling levelling = levellingManager.getLevelling(islandList);
				levelling.setTime(levelling.getTime() - 1);

				if (levelling.getTime() <= 0) {
					levellingManager.removeLevelling(islandList);
					levellingManager.unloadLevelling(islandList);
				}
			}
		}
	}
}
