package com.craftaro.skyblock.scoreboard;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.manager.Manager;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.visit.Visit;
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

        if (this.enabled) {
            for (ScoreboardType type : ScoreboardType.values()) {
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
        if (this.enabled) {
            for (Player player : this.plugin.getServer().getOnlinePlayers()) {
                updatePlayerScoreboardType(player);
            }
        }
    }

    public void updatePlayerScoreboardType(Player player) {
        if (!this.enabled)
            return;

        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        IslandManager islandManager = this.plugin.getIslandManager();

        PlayerData playerData = playerDataManager.getPlayerData(player);
        Island island = islandManager.getIsland(player);

        if (!playerData.isScoreboard())
            return;

        ScoreboardType type;
        if (island != null) {
            Visit islandVisit = island.getVisit();
            boolean hasVisitors = (islandVisit != null &&
                    islandVisit.getVisitors() != null &&
                    islandVisit.getVisitors().size() > 1);
            boolean hasMembers = (islandVisit != null &&
                    islandVisit.getMembers() > 1);

            if (hasMembers) {
                if (hasVisitors) {
                    type = ScoreboardType.ISLAND_TEAM_VISITORS;
                } else {
                    type = ScoreboardType.ISLAND_TEAM_EMPTY;
                }
            } else {
                if (hasVisitors) {
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


    public void setPlayerScoreboard(Player player, ScoreboardType type) {
        if (!enabled)
            return;

        for (Driver driver : drivers) {
            driver.unregisterHolder(player);
            if (driver.getBoardType() == type)
                driver.registerHolder(new Holder(plugin, driver, player));
        }
    }

    public void unregisterPlayer(Player player) {
        if (!enabled)
            return;

        for (Driver driver : drivers)
            driver.unregisterHolder(player);
        player.setScoreboard(emptyScoreboard);
    }

    public void addDisabledPlayer(Player player) {
        if (this.enabled) {
            this.disabledPlayers.add(player);
            Bukkit.getScheduler().runTask(plugin, () -> unregisterPlayer(player));
        }
    }

    public void removeDisabledPlayer(Player player) {
        if (this.enabled)
            this.disabledPlayers.remove(player);
    }

    public boolean isPlayerDisabled(Player player) {
        return this.disabledPlayers.contains(player);
    }

    private void newDriver(ScoreboardType board) {
        FileManager fileManager = plugin.getFileManager();
        FileConfiguration configload = fileManager.getConfig(
                new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration();

        Driver driver = new Driver(plugin, board);
        if (configload.getBoolean("Island.Scoreboard.Async", true)) {
            driver.runTaskTimerAsynchronously(plugin, 1L, 1L);
        } else {
            driver.runTaskTimer(plugin, 1L, 1L);
        }
        drivers.add(driver);
    }

    public void clearDrivers() {
        if (enabled) {
            for (Driver driver : this.drivers) {
                driver.cancel();
            }
            drivers.clear();
        }
    }

    public Scoreboard getEmptyScoreboard() {
        return this.emptyScoreboard;
    }
}
