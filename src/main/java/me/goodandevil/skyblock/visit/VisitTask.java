package me.goodandevil.skyblock.visit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;

public class VisitTask extends BukkitRunnable {

	private final PlayerDataManager playerDataManager;

	public VisitTask(PlayerDataManager playerManager) {
		this.playerDataManager = playerManager;
	}

	@Override
	public void run() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (playerDataManager.hasPlayerData(all)) {
				PlayerData playerData = playerDataManager.getPlayerData(all);

				if (playerData.getIsland() != null) {
					playerData.setVisitTime(playerData.getVisitTime() + 1);
				}
			}
		}
	}
}
