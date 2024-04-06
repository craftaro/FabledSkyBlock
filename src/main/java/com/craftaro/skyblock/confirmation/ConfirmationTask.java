package com.craftaro.skyblock.confirmation;

import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class ConfirmationTask extends BukkitRunnable {
    private final PlayerDataManager playerDataManager;

    public ConfirmationTask(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @Override
    public void run() {
        Map<UUID, PlayerData> playerDataStorage = this.playerDataManager.getPlayerData();
        synchronized (playerDataStorage) {
            for (PlayerData playerData : playerDataStorage.values()) {
                if (playerData.getConfirmationTime() > 0) {
                    playerData.setConfirmationTime(playerData.getConfirmationTime() - 1);
                }
            }
        }
    }
}
