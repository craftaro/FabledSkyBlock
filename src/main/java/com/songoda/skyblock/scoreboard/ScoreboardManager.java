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
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScoreboardManager extends Manager {

    private final boolean enabled;
    private final Scoreboard emptyScoreboard;
    private final List<Driver> drivers;
    private final Set<Player> disabledPlayers;

    public ScoreboardManager(SkyBlock plugin) {
        super(plugin);
        FileManager fileManager = plugin.getFileManager();
        
        this.enabled = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"))
                .getFileConfiguration().getBoolean("Island.Scoreboard.Enable", true);
        
        this.drivers = new ArrayList<>();
        this.disabledPlayers = new ConcurrentSet<>();
        this.emptyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        if(enabled) {
            for(ScoreboardType type : ScoreboardType.values()) {
                newDriver(type);
            }
    
            updateOnlinePlayers();
        }
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
        if(enabled) {
            for(Player player : plugin.getServer().getOnlinePlayers()) {
                updatePlayerScoreboardType(player);
            }
        }
    }

    public void updatePlayerScoreboardType(Player player) {
        if(enabled) {
            PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
            IslandManager islandManager = plugin.getIslandManager();
    
            PlayerData playerData = playerDataManager.getPlayerData(player);
            Island island = islandManager.getIslandByPlayer(player);
    
            if(playerData.isScoreboard()) {
                ScoreboardType type;
                if(island != null) {
                    Visit islandVisit = island.getVisit();
                    boolean hasVisitors = (islandVisit != null &&
                            islandVisit.getVisitors() != null &&
                            islandVisit.getVisitors().size() > 1);
                    boolean hasMembers = (islandVisit != null &&
                            islandVisit.getMembers() > 1);
            
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
                    type = ScoreboardType.NO_ISLAND;
                }
                synchronized (player) {
                    setPlayerScoreboard(player, type);
                }
            }
        }
    }
    
    
    
    public void setPlayerScoreboard(Player player, ScoreboardType type) {
        if(enabled) {
            for(Driver driver : drivers) {
                driver.unregisterHolder(player);
                if(driver.getBoardType().equals(type)) {
                    driver.registerHolder(new Holder(plugin, driver, player));
                }
            }
        }
    }
    
    public void unregisterPlayer(Player player) {
        if(enabled) {
            for(Driver driver : drivers) {
                driver.unregisterHolder(player);
            }
            player.setScoreboard(emptyScoreboard);
        }
    }

    public void addDisabledPlayer(Player player) {
        if(enabled) {
            disabledPlayers.add(player);
            Bukkit.getScheduler().runTask(plugin, () -> this.unregisterPlayer(player));
        }
    }

    public void removeDisabledPlayer(Player player) {
        if(enabled) {
            disabledPlayers.remove(player);
        }
    }

    public boolean isPlayerDisabled(Player player) {
        return disabledPlayers.contains(player);
    }
    
    private void newDriver(ScoreboardType board) {
        FileManager fileManager = plugin.getFileManager();
        FileConfiguration configload = fileManager.getConfig(
                new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration();

        Driver driver = new Driver(plugin, board);
        if(configload.getBoolean("Island.Scoreboard.Async", true)) {
            driver.runTaskTimerAsynchronously(plugin, 1L, 1L);
        } else {
            driver.runTaskTimer(plugin, 1L, 1L);
        }
        drivers.add(driver);
    }
    
    public void clearDrivers() {
        if(enabled) {
            for(Driver driver : drivers)
                driver.cancel();
            drivers.clear();
        }
    }
    
    public Scoreboard getEmptyScoreboard() {
        return emptyScoreboard;
    }
}
