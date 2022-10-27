package com.songoda.skyblock.playerdata;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.ban.BanManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.database.DataProvider;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLocation;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandStatus;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.scoreboard.ScoreboardManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.visit.Visit;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final SkyBlock plugin;
    private final DataProvider dataProvider;
    public PlayerDataManager(SkyBlock plugin) {
        this.plugin = plugin;
        this.dataProvider = plugin.getDataManager().getDataProvider();
    }

    public void save() {

    }

    public synchronized void addData(PlayerData data) {
        playerDataStorage.put(data.getPlayerUUID(), data);
    }

    public synchronized void removeData(PlayerData data) {
        playerDataStorage.remove(data.getPlayerUUID());
    }

    public void createPlayerData(Player player) {
        if (dataProvider.hasPlayerData(player)) {
            throw new RuntimeException("Player data already exists for " + player.getName());
        }
        plugin.getDataManager().getDataProvider().createPlayerData(player);
    }

    public void loadPlayerData(Player player) {
       if (dataProvider.hasPlayerData(player)) {
           addData(dataProvider.loadPlayerData(player));
       }
    }

    public void unloadPlayerData(Player player) {
        if (isPlayerDataLoaded(player)) {
            dataProvider.unloadPlayerData(player);
        }
    }

    public void savePlayerData(Player player) {
        if (isPlayerDataLoaded(player)) {
            dataProvider.savePlayerData(player);
        }
    }

    public Map<UUID, PlayerData> getPlayerData() {
        return playerDataStorage;
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataStorage.get(uuid);
    }

    public boolean isPlayerDataLoaded(UUID uuid) {
        return playerDataStorage.containsKey(uuid);
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public boolean isPlayerDataLoaded(Player player) {
        return isPlayerDataLoaded(player.getUniqueId());
    }

    public void storeIsland(Player player) {
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        WorldManager worldManager = plugin.getWorldManager();
        BanManager banManager = plugin.getBanManager();

        FileConfiguration configLoad = plugin.getLanguage();

        if (isPlayerDataLoaded(player)) {
            if (worldManager.isIslandWorld(player.getWorld())) {
                IslandWorld world = worldManager.getIslandWorld(player.getWorld());
                Island island = islandManager.getIslandAtLocation(player.getLocation());

                if (island != null) {
                    Player targetPlayer = Bukkit.getServer().getPlayer(island.getOwnerUUID());
                    String targetPlayerName;

                    if (targetPlayer == null) {
                        targetPlayerName = new OfflinePlayer(island.getOwnerUUID()).getName();
                    } else {
                        targetPlayerName = targetPlayer.getName();
                    }

                    if (banManager.hasIsland(island.getOwnerUUID()) && this.plugin.getConfiguration().getBoolean("Island.Visitor.Banning")
                            && banManager.getIsland(island.getOwnerUUID()).isBanned(player.getUniqueId())) {
                        if (messageManager != null)
                            messageManager.sendMessage(player, configLoad.getString("Island.Visit.Banned.Island.Message").replace("%player", targetPlayerName));
                    } else {
                        if (island.hasRole(IslandRole.Member, player.getUniqueId()) || island.hasRole(IslandRole.Operator, player.getUniqueId()) || island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                            PlayerData playerData = getPlayerData(player);
                            playerData.setIsland(island.getOwnerUUID());

                            if (world == IslandWorld.Normal) {
                                if (!island.isWeatherSynchronized()) {
                                    player.setPlayerTime(island.getTime(), this.plugin.getConfiguration().getBoolean("Island.Weather.Time.Cycle"));
                                    player.setPlayerWeather(island.getWeather());
                                }
                            }

                            islandManager.updateFlight(player);

                            return;
                        } else if (!island.getStatus().equals(IslandStatus.CLOSED) || island.isCoopPlayer(player.getUniqueId())) {
                            if (island.getStatus().equals(IslandStatus.CLOSED) && island.isCoopPlayer(player.getUniqueId())) {
                                if (islandManager.removeCoopPlayers(island, null)) {
                                    return;
                                }
                            }

                            PlayerData playerData = getPlayerData(player);
                            playerData.setIsland(island.getOwnerUUID());

                            if (world == IslandWorld.Normal) {
                                if (!island.isWeatherSynchronized()) {
                                    player.setPlayerTime(island.getTime(), this.plugin.getConfiguration().getBoolean("Island.Weather.Time.Cycle"));
                                    player.setPlayerWeather(island.getWeather());
                                }
                            }

                            islandManager.updateFlight(player);

                            ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
                            if (scoreboardManager != null) {
                                Island finalIsland = island;
                                Bukkit.getScheduler().runTask(plugin, () -> {

                                    for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
                                        PlayerData targetPlayerData = getPlayerData(loopPlayer);
                                        if (targetPlayerData == null) {
                                            continue;
                                        }

                                        if (targetPlayerData.getOwner() != null &&
                                                targetPlayerData.getOwner().equals(finalIsland.getOwnerUUID())) {
                                            scoreboardManager.updatePlayerScoreboardType(loopPlayer);
                                        }
                                    }
                                });
                            }

                            return;
                        } else {
                            if (messageManager != null)
                                messageManager.sendMessage(player, configLoad.getString("Island.Visit.Closed.Island.Message").replace("%player", targetPlayerName));
                        }
                    }

                    LocationUtil.teleportPlayerToSpawn(player);

                    return;
                }

                HashMap<UUID, Visit> visitIslands = plugin.getVisitManager().getIslands();

                for (UUID visitIslandList : visitIslands.keySet()) {
                    Visit visit = visitIslands.get(visitIslandList);
                    IslandLocation location = visit.getLocation(world);

                    if (location != null && LocationUtil.isLocationInLocationRadius(player.getLocation(), location.getLocation(), visit.getRadius())) {
                        Player targetPlayer = Bukkit.getServer().getPlayer(visitIslandList);
                        String targetPlayerName;

                        if (targetPlayer == null) {
                            targetPlayerName = new OfflinePlayer(visitIslandList).getName();
                        } else {
                            targetPlayerName = targetPlayer.getName();
                        }

                        if (banManager.hasIsland(visitIslandList) && this.plugin.getConfiguration().getBoolean("Island.Visitor.Banning")
                                && banManager.getIsland(visitIslandList).isBanned(player.getUniqueId())) {
                            if (messageManager != null)
                                messageManager.sendMessage(player, configLoad.getString("Island.Visit.Banned.Island.Message").replace("%player", targetPlayerName));
                        } else {
                            org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(visitIslandList);

                            islandManager.loadIsland(offlinePlayer);
                            island = islandManager.getIsland(offlinePlayer);

                            if (island != null) {
                                if (!island.getStatus().equals(IslandStatus.CLOSED) || island.isCoopPlayer(player.getUniqueId())) {
                                    if (island.getStatus().equals(IslandStatus.CLOSED) && island.isCoopPlayer(player.getUniqueId())) {
                                        if (islandManager.removeCoopPlayers(island, null)) {
                                            islandManager.unloadIsland(island, Bukkit.getServer().getOfflinePlayer(visitIslandList));

                                            return;
                                        }
                                    }

                                    PlayerData playerData = getPlayerData(player);
                                    playerData.setIsland(visitIslandList);

                                    if (world == IslandWorld.Normal) {
                                        if (!island.isWeatherSynchronized()) {
                                            player.setPlayerTime(island.getTime(), this.plugin.getConfiguration().getBoolean("Island.Weather.Time.Cycle"));
                                            player.setPlayerWeather(island.getWeather());
                                        }
                                    }

                                    islandManager.updateFlight(player);

                                    return;
                                } else {
                                    islandManager.unloadIsland(island, Bukkit.getServer().getOfflinePlayer(visitIslandList));
                                    if (messageManager != null)
                                        messageManager.sendMessage(player, configLoad.getString("Island.Visit.Closed.Island.Message").replace("%player", targetPlayerName));
                                }
                            }
                        }

                        LocationUtil.teleportPlayerToSpawn(player);

                        return;
                    }
                }
            }
        }
    }
}
