package com.songoda.skyblock.scoreboard;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.manager.Manager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.visit.Visit;
import io.netty.util.internal.ConcurrentSet;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class ScoreboardManager extends Manager {

    private final List<Driver> drivers;
    private final Set<Player> disabledPlayers;

    public ScoreboardManager(SkyBlock plugin) {
        super(plugin);
        this.drivers = new ArrayList<>();
        this.disabledPlayers = new ConcurrentSet<>();

        for(ScoreboardType type : ScoreboardType.values()) {
            newDriver(type);
        }

        updateOnlinePlayers();
    }

    @Override
    public void disable() {
        clearDrivers();
    }

    @Override
    public void reload() {
        disable();
        updateOnlinePlayers();
    }

    public void updateOnlinePlayers() {
        for(Player player : plugin.getServer().getOnlinePlayers()) {
            updatePlayerScoreboardType(player);
        }
    }

    public void updatePlayerScoreboardType(Player player) {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        IslandManager islandManager = plugin.getIslandManager();

        PlayerData playerData = playerDataManager.getPlayerData(player);
        Island island = islandManager.getIslandByPlayer(player);

        if(playerData.isScoreboard()) {
            ScoreboardType type;
            if(island != null) {
                Visit islandVisit = island.getVisit();
                boolean hasVisitors = islandVisit.getVisitors().size() > 1;
                boolean hasMembers = islandVisit.getMembers() > 1;

                if(hasMembers) {
                    if(hasVisitors) {
                        type = ScoreboardType.ISLAND_TEAM_VISITORS;
                    } else {
                        type = ScoreboardType.ISLAND_TEAM_EMPTY;
                    }
                } else {
                    if(hasVisitors) {
                        type = ScoreboardType.ISLAND_SOLO_VISITORS;
                    } else {
                        type = ScoreboardType.ISLAND_SOLO_EMPTY;
                    }
                }
            } else {
                type = ScoreboardType.NOISLAND;
            }

            setPlayerScoreboard(player, type);
        }
    }

    public void unregisterPlayer(Player player) {
        for(Driver driver : drivers) {
            driver.unregisterHolder(player);
        }
    }

    public void addDisabledPlayer(Player player) {
        disabledPlayers.add(player);
    }

    public void removeDisabledPlayer(Player player) {
        disabledPlayers.remove(player);
    }

    public boolean isPlayerDisabled(Player player) {
        return disabledPlayers.contains(player);
    }
    
    private void newDriver(ScoreboardType board) {
        FileManager fileManager = plugin.getFileManager();
        FileConfiguration scoreboardLoad = fileManager.getConfig(
                new File(plugin.getDataFolder(), "scoreboard.yml")).getFileConfiguration();

        Driver driver = new Driver(plugin, board);
        if(scoreboardLoad.getBoolean("Settings.Async", true)) {
            driver.runTaskTimerAsynchronously(plugin, 1L, 1L);
        } else {
            driver.runTaskTimer(plugin, 1L, 1L);
        }
        drivers.add(driver);
    }
    
    public void clearDrivers() {
        for(Driver driver : drivers)
            driver.cancel();
        drivers.clear();
    }

    public void setPlayerScoreboard(Player player, ScoreboardType type) {
        for(Driver driver : drivers) {
            if(driver.getBoardType().equals(type)) {
                driver.registerHolder(new Holder(plugin, driver, player));
            } else {
                driver.unregisterHolder(player);
            }
        }
    }
}
