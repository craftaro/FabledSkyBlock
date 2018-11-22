package me.goodandevil.skyblock.levelling;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.island.IslandManager;

public class LevellingTask extends BukkitRunnable {

	private final Main plugin;
	private final LevellingManager levellingManager;
	
 	protected LevellingTask(LevellingManager levellingManager, Main plugin) {
		this.levellingManager = levellingManager;
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		IslandManager islandManager = plugin.getIslandManager();
		
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
