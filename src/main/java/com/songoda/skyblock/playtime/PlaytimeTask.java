package com.songoda.skyblock.playtime;

import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaytimeTask extends BukkitRunnable {
    private final PlayerDataManager playerDataManager;
    private final IslandManager islandManager;

    public PlaytimeTask(PlayerDataManager playerDataManager, IslandManager islandManager) {
        this.playerDataManager = playerDataManager;
        this.islandManager = islandManager;
    }

    @Override
    public void run() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (this.playerDataManager.hasPlayerData(all) && this.islandManager.getIsland(all) != null) {
                PlayerData playerData = this.playerDataManager.getPlayerData(all);
                playerData.setPlaytime(playerData.getPlaytime() + 1);
            }
        }
    }
}
