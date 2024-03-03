package com.craftaro.skyblock.playerdata;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.ban.BanManager;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.*;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.scoreboard.ScoreboardManager;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import com.craftaro.skyblock.utils.world.LocationUtil;
import com.craftaro.skyblock.visit.Visit;
import com.craftaro.skyblock.world.WorldManager;
import com.craftaro.third_party.com.cryptomorin.xseries.SkullUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final SkyBlock plugin;
    private final Map<UUID, PlayerData> playerDataStorage = new HashMap<>();

    public PlayerDataManager(SkyBlock plugin) {
        this.plugin = plugin;

        for (Player all : Bukkit.getOnlinePlayers()) {
            loadPlayerData(all);

            if (!hasPlayerData(all)) {
                createPlayerData(all);
                loadPlayerData(all);
            }

            storeIsland(all);
        }
    }

    public void onDisable() {
        for (PlayerData data : this.playerDataStorage.values()) {
            data.save();
        }
    }

    public void createPlayerData(Player player) {
        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(new File(this.plugin.getDataFolder(), "player-data"), player.getUniqueId() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        String[] playerTexture;
        String something = null;
        try {
            ItemStack skullItem = SkullUtils.getSkull(player.getUniqueId());
            ItemMeta skullMeta = skullItem.getItemMeta();
            something = SkullUtils.getSkinValue(skullMeta);
            System.out.println(something);
        } catch (Exception e) {
            e.printStackTrace();
            playerTexture = new String[]{
                    "K9P4tCIENYbNpDuEuuY0shs1x7iIvwXi4jUUVsATJfwsAIZGS+9OZ5T2HB0tWBoxRvZNi73Vr+syRdvTLUWPusVXIg+2fhXmQoaNEtnQvQVGQpjdQP0TkZtYG8PbvRxE6Z75ddq+DVx/65OSNHLWIB/D+Rg4vINh4ukXNYttn9QvauDHh1aW7/IkIb1Bc0tLcQyqxZQ3mdglxJfgIerqnlA++Lt7TxaLdag4y1NhdZyd3OhklF5B0+B9zw/qP8QCzsZU7VzJIcds1+wDWKiMUO7+60OSrIwgE9FPamxOQDFoDvz5BOULQEeNx7iFMB+eBYsapCXpZx0zf1bduppBUbbVC9wVhto/J4tc0iNyUq06/esHUUB5MHzdJ0Y6IZJAD/xIw15OLCUH2ntvs8V9/cy5/n8u3JqPUM2zhUGeQ2p9FubUGk4Q928L56l3omRpKV+5QYTrvF+AxFkuj2hcfGQG3VE2iYZO6omXe7nRPpbJlHkMKhE8Xvd1HP4PKpgivSkHBoZ92QEUAmRzZydJkp8CNomQrZJf+MtPiNsl/Q5RQM+8CQThg3+4uWptUfP5dDFWOgTnMdA0nIODyrjpp+bvIJnsohraIKJ7ZDnj4tIp4ObTNKDFC/8j8JHz4VCrtr45mbnzvB2DcK8EIB3JYT7ElJTHnc5BKMyLy5SKzuw=",
                    "eyJ0aW1lc3RhbXAiOjE1MjkyNTg0MTE4NDksInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGMxYzc3Y2U4ZTU0OTI1YWI1ODEyNTQ0NmVjNTNiMGNkZDNkMGNhM2RiMjczZWI5MDhkNTQ4Mjc4N2VmNDAxNiJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc2N2Q0ODMyNWVhNTMyNDU2MTQwNmI4YzgyYWJiZDRlMjc1NWYxMTE1M2NkODVhYjA1NDVjYzIifX19"};
        }

        configLoad.set("Texture.Signature", "");
        configLoad.set("Texture.Value", something);
        configLoad.set("Statistics.Island.Playtime", 0);

        try {
            configLoad.save(config.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void loadPlayerData(Player player) {
        if (this.plugin.getFileManager().isFileExist(new File(this.plugin.getDataFolder().toString() + "/player-data", player.getUniqueId().toString() + ".yml"))) {
            PlayerData playerData = new PlayerData(player);
            this.playerDataStorage.put(player.getUniqueId(), playerData);
        }
    }

    public void unloadPlayerData(Player player) {
        if (hasPlayerData(player)) {
            this.plugin.getFileManager().unloadConfig(new File(new File(this.plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId().toString() + ".yml"));
            this.playerDataStorage.remove(player.getUniqueId());
        }
    }

    public void savePlayerData(Player player) {
        if (hasPlayerData(player)) {
            FileManager.Config config = this.plugin.getFileManager().getConfig(new File(new File(this.plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId().toString() + ".yml"));

            try {
                config.getFileConfiguration().save(config.getFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Map<UUID, PlayerData> getPlayerData() {
        return this.playerDataStorage;
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerDataStorage.get(uuid);
    }

    public boolean hasPlayerData(UUID uuid) {
        return this.playerDataStorage.containsKey(uuid);
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public boolean hasPlayerData(Player player) {
        return hasPlayerData(player.getUniqueId());
    }

    public void storeIsland(Player player) {
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        WorldManager worldManager = this.plugin.getWorldManager();
        BanManager banManager = this.plugin.getBanManager();

        FileConfiguration configLoad = this.plugin.getLanguage();

        if (hasPlayerData(player)) {
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
                        if (messageManager != null) {
                            messageManager.sendMessage(player, configLoad.getString("Island.Visit.Banned.Island.Message").replace("%player", targetPlayerName));
                        }
                    } else {
                        if (island.hasRole(IslandRole.MEMBER, player.getUniqueId()) || island.hasRole(IslandRole.OPERATOR, player.getUniqueId()) || island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                            PlayerData playerData = getPlayerData(player);
                            playerData.setIsland(island.getOwnerUUID());

                            if (world == IslandWorld.NORMAL) {
                                if (!island.isWeatherSynchronized()) {
                                    player.setPlayerTime(island.getTime(), this.plugin.getConfiguration().getBoolean("Island.Weather.Time.Cycle"));
                                    player.setPlayerWeather(island.getWeather());
                                }
                            }

                            islandManager.updateFlight(player);

                            return;
                        } else if (island.getStatus() != IslandStatus.CLOSED || island.isCoopPlayer(player.getUniqueId())) {
                            if (island.getStatus() == IslandStatus.CLOSED && island.isCoopPlayer(player.getUniqueId())) {
                                if (islandManager.removeCoopPlayers(island, null)) {
                                    return;
                                }
                            }

                            PlayerData playerData = getPlayerData(player);
                            playerData.setIsland(island.getOwnerUUID());

                            if (world == IslandWorld.NORMAL) {
                                if (!island.isWeatherSynchronized()) {
                                    player.setPlayerTime(island.getTime(), this.plugin.getConfiguration().getBoolean("Island.Weather.Time.Cycle"));
                                    player.setPlayerWeather(island.getWeather());
                                }
                            }

                            islandManager.updateFlight(player);

                            ScoreboardManager scoreboardManager = this.plugin.getScoreboardManager();
                            if (scoreboardManager != null) {
                                Island finalIsland = island;
                                Bukkit.getScheduler().runTask(this.plugin, () -> {

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
                            if (messageManager != null) {
                                messageManager.sendMessage(player, configLoad.getString("Island.Visit.Closed.Island.Message").replace("%player", targetPlayerName));
                            }
                        }
                    }

                    LocationUtil.teleportPlayerToSpawn(player);

                    return;
                }

                HashMap<UUID, Visit> visitIslands = this.plugin.getVisitManager().getIslands();

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
                            if (messageManager != null) {
                                messageManager.sendMessage(player, configLoad.getString("Island.Visit.Banned.Island.Message").replace("%player", targetPlayerName));
                            }
                        } else {
                            org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(visitIslandList);

                            islandManager.loadIsland(offlinePlayer);
                            island = islandManager.getIsland(offlinePlayer);

                            if (island != null) {
                                if (island.getStatus() != IslandStatus.CLOSED || island.isCoopPlayer(player.getUniqueId())) {
                                    if (island.getStatus() == IslandStatus.CLOSED && island.isCoopPlayer(player.getUniqueId())) {
                                        if (islandManager.removeCoopPlayers(island, null)) {
                                            islandManager.unloadIsland(island, Bukkit.getServer().getOfflinePlayer(visitIslandList));

                                            return;
                                        }
                                    }

                                    PlayerData playerData = getPlayerData(player);
                                    playerData.setIsland(visitIslandList);

                                    if (world == IslandWorld.NORMAL) {
                                        if (!island.isWeatherSynchronized()) {
                                            player.setPlayerTime(island.getTime(), this.plugin.getConfiguration().getBoolean("Island.Weather.Time.Cycle"));
                                            player.setPlayerWeather(island.getWeather());
                                        }
                                    }

                                    islandManager.updateFlight(player);

                                    return;
                                } else {
                                    islandManager.unloadIsland(island, Bukkit.getServer().getOfflinePlayer(visitIslandList));
                                    if (messageManager != null) {
                                        messageManager.sendMessage(player, configLoad.getString("Island.Visit.Closed.Island.Message").replace("%player", targetPlayerName));
                                    }
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
