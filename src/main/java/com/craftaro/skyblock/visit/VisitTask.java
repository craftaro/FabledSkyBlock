package com.craftaro.skyblock.visit;

import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import org.bukkit.scheduler.BukkitRunnable;

public class VisitTask extends BukkitRunnable {
    private final PlayerDataManager playerDataManager;

    public VisitTask(PlayerDataManager playerManager) {
        this.playerDataManager = playerManager;
    }

    @Override
    public void run() {
        for (PlayerData playerData : this.playerDataManager.getPlayerData().values()) {
            if (playerData.getIsland() != null) {
                playerData.setVisitTime(playerData.getVisitTime() + 1);
            }
        }
    }
}
