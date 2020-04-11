package com.songoda.skyblock.leaderboard;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.scheduler.BukkitRunnable;

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

        skyblock.getHologramTask().updateHologram();
    }
}
