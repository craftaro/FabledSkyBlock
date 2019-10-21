package com.songoda.skyblock.confirmation;

import org.bukkit.scheduler.BukkitRunnable;

import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;

public class ConfirmationTask extends BukkitRunnable {

    private final PlayerDataManager playerDataManager;

    public ConfirmationTask(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @Override
    public void run() {
        for (PlayerData playerData : playerDataManager.getPlayerData().values()) {
            if (playerData.getConfirmationTime() > 0) {
                playerData.setConfirmationTime(playerData.getConfirmationTime() - 1);
            }
        }
    }
}
