package com.craftaro.skyblock.visit;

import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class VisitTask extends BukkitRunnable {
    private final PlayerDataManager playerDataManager;

    public VisitTask(PlayerDataManager playerManager) {
        this.playerDataManager = playerManager;
    }

    @Override
    public void run() {
        Map<UUID, PlayerData> playerDataStorage = this.playerDataManager.getPlayerData();
        synchronized (playerDataStorage) {
            for (PlayerData playerData : playerDataStorage.values()) {
                if (playerData.getIsland() != null) {
                    playerData.setVisitTime(playerData.getVisitTime() + 1);
                }
            }
        }
    }
}
