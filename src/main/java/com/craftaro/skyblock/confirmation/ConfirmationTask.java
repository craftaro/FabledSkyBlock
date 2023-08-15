package com.craftaro.skyblock.confirmation;

import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import org.bukkit.scheduler.BukkitRunnable;

public class ConfirmationTask extends BukkitRunnable {
    private final PlayerDataManager playerDataManager;

    public ConfirmationTask(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @Override
    public void run() {
        for (PlayerData playerData : this.playerDataManager.getPlayerData().values()) {
            if (playerData.getConfirmationTime() > 0) {
                playerData.setConfirmationTime(playerData.getConfirmationTime() - 1);
            }
        }
    }
}
