package me.goodandevil.skyblock.playtime;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;

public class PlaytimeTask extends BukkitRunnable {

	private final PlayerDataManager playerDataManager;
	private final IslandManager islandManager;

	public PlaytimeTask(PlayerDataManager playerDataManager, IslandManager islandManager) {
		this.playerDataManager = playerDataManager;
		this.islandManager = islandManager;
	}

	@Override
	public void run() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (playerDataManager.hasPlayerData(all) && islandManager.hasIsland(all)) {
				PlayerData playerData = playerDataManager.getPlayerData(all);
				playerData.setPlaytime(playerData.getPlaytime() + 1);
			}
		}
	}
}
