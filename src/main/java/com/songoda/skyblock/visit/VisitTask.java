package com.songoda.skyblock.visit;

import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VisitTask extends BukkitRunnable {

    private final PlayerDataManager playerDataManager;

    public VisitTask(PlayerDataManager playerManager) {
        this.playerDataManager = playerManager;
    }

    @Override
    public void run() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (playerDataManager.hasPlayerData(all)) {
                PlayerData playerData = playerDataManager.getPlayerData(all);

                if (playerData.getIsland() != null) {
                    playerData.setVisitTime(playerData.getVisitTime() + 1);
                }
            }
        }
    }
}
