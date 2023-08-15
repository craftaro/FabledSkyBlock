package com.craftaro.skyblock.leaderboard;

import com.craftaro.skyblock.SkyBlock;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaderboardTask extends BukkitRunnable {
    private final SkyBlock plugin;

    public LeaderboardTask(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        LeaderboardManager leaderboardManager = this.plugin.getLeaderboardManager();
        leaderboardManager.clearLeaderboard();
        leaderboardManager.resetLeaderboard();
        leaderboardManager.setupLeaderHeads();

        this.plugin.getHologramTask().updateHologram();
    }
}
