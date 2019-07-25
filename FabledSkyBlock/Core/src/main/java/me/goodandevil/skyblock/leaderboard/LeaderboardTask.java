package me.goodandevil.skyblock.leaderboard;

import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.SkyBlock;

public class LeaderboardTask extends BukkitRunnable {

	private final SkyBlock skyblock;

	public LeaderboardTask(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@Override
	public void run() {
		LeaderboardManager leaderboardManager = skyblock.getLeaderboardManager();
		leaderboardManager.clearLeaderboard();
		leaderboardManager.resetLeaderboard();
		leaderboardManager.setupLeaderHeads();

		skyblock.getHologramManager().resetHologram();
	}
}
