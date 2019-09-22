package com.songoda.skyblock.confirmation;

import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ConfirmationTask extends BukkitRunnable {

    private final PlayerDataManager playerDataManager;

    public ConfirmationTask(PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    @Override
    public void run() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (playerDataManager.hasPlayerData(all)) {
                PlayerData playerData = playerDataManager.getPlayerData(all);

                if (playerData.getConfirmationTime() > 0) {
                    playerData.setConfirmationTime(playerData.getConfirmationTime() - 1);
                }
            }
        }
    }
}
