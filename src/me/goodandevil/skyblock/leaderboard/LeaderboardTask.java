package me.goodandevil.skyblock.leaderboard;

import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.Main;

public class LeaderboardTask extends BukkitRunnable {
	
	private final Main plugin;
	
	public LeaderboardTask(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		LeaderboardManager leaderboardManager = plugin.getLeaderboardManager();
		leaderboardManager.clearLeaderboard();
		leaderboardManager.resetLeaderboard();
	}
}
