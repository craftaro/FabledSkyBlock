package com.songoda.skyblock.listeners;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandEnvironment;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.scoreboard.ScoreboardManager;
import com.songoda.skyblock.usercache.UserCacheManager;
import com.songoda.skyblock.utils.world.LocationUtil;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.lang.reflect.Method;

public class Join implements Listener {

    private final SkyBlock plugin;

    public Join(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        UserCacheManager userCacheManager = plugin.getUserCacheManager();
        CooldownManager cooldownManager = plugin.getCooldownManager();
        IslandManager islandManager = plugin.getIslandManager();
        
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            userCacheManager.addUser(player.getUniqueId(), player.getName());
            userCacheManager.saveAsync();
        
            try {
                islandManager.loadIsland(player);
                Island island = islandManager.getIsland(player);
                boolean teleportedToIsland = false;

                FileConfiguration configLoad = plugin.getConfiguration();
            
                if (configLoad.getBoolean("Island.Join.Spawn")) {
                    LocationUtil.teleportPlayerToSpawn(player);
                } else if (configLoad.getBoolean("Island.Join.Island") && island != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        PaperLib.teleportAsync(player, island.getLocation(IslandWorld.Normal, IslandEnvironment.Main));
                        player.setFallDistance(0.0F);
                    });
                    teleportedToIsland = true;
                }
            
                if (!teleportedToIsland) {
                    islandManager.loadPlayer(player);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        
            playerDataManager.loadPlayerData(player);
        
            if (playerDataManager.hasPlayerData(player)) {
                String[] playerTexture;
            
                try {
                    Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
                    Method getProfileMethod = entityPlayer.getClass().getMethod("getProfile");
                    GameProfile gameProfile = (GameProfile) getProfileMethod.invoke(entityPlayer);
                    Property property = gameProfile.getProperties().get("textures").iterator().next();
                    playerTexture = new String[]{property.getSignature(), property.getValue()};
                } catch (Exception e) {
                    playerTexture = new String[]{
                            "K9P4tCIENYbNpDuEuuY0shs1x7iIvwXi4jUUVsATJfwsAIZGS+9OZ5T2HB0tWBoxRvZNi73Vr+syRdvTLUWPusVXIg+2fhXmQoaNEtnQvQVGQpjdQP0TkZtYG8PbvRxE6Z75ddq+DVx/65OSNHLWIB/D+Rg4vINh4ukXNYttn9QvauDHh1aW7/IkIb1Bc0tLcQyqxZQ3mdglxJfgIerqnlA++Lt7TxaLdag4y1NhdZyd3OhklF5B0+B9zw/qP8QCzsZU7VzJIcds1+wDWKiMUO7+60OSrIwgE9FPamxOQDFoDvz5BOULQEeNx7iFMB+eBYsapCXpZx0zf1bduppBUbbVC9wVhto/J4tc0iNyUq06/esHUUB5MHzdJ0Y6IZJAD/xIw15OLCUH2ntvs8V9/cy5/n8u3JqPUM2zhUGeQ2p9FubUGk4Q928L56l3omRpKV+5QYTrvF+AxFkuj2hcfGQG3VE2iYZO6omXe7nRPpbJlHkMKhE8Xvd1HP4PKpgivSkHBoZ92QEUAmRzZydJkp8CNomQrZJf+MtPiNsl/Q5RQM+8CQThg3+4uWptUfP5dDFWOgTnMdA0nIODyrjpp+bvIJnsohraIKJ7ZDnj4tIp4ObTNKDFC/8j8JHz4VCrtr45mbnzvB2DcK8EIB3JYT7ElJTHnc5BKMyLy5SKzuw=",
                            "eyJ0aW1lc3RhbXAiOjE1MjkyNTg0MTE4NDksInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGMxYzc3Y2U4ZTU0OTI1YWI1ODEyNTQ0NmVjNTNiMGNkZDNkMGNhM2RiMjczZWI5MDhkNTQ4Mjc4N2VmNDAxNiJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc2N2Q0ODMyNWVhNTMyNDU2MTQwNmI4YzgyYWJiZDRlMjc1NWYxMTE1M2NkODVhYjA1NDVjYzIifX19"};
                }
            
                PlayerData playerData = playerDataManager.getPlayerData(player);
                playerData.setTexture(playerTexture[0], playerTexture[1]);
                Bukkit.getScheduler().runTaskAsynchronously(plugin, playerData::save);
            } else {
                playerDataManager.createPlayerData(player);
                playerDataManager.loadPlayerData(player);
            }
        
            playerDataManager.storeIsland(player);
        
            cooldownManager.addCooldownPlayer(CooldownType.Biome, cooldownManager.loadCooldownPlayer(CooldownType.Biome, player));
            cooldownManager.addCooldownPlayer(CooldownType.Creation, cooldownManager.loadCooldownPlayer(CooldownType.Creation, player));
            cooldownManager.addCooldownPlayer(CooldownType.Deletion, cooldownManager.loadCooldownPlayer(CooldownType.Deletion, player));
        
        
            Island island = islandManager.getIslandPlayerAt(player);
            if (island != null) {
                islandManager.updateBorder(island);
                islandManager.updateFlight(player);
            }
        
            // Load Challenge
            SkyBlock.getInstance().getFabledChallenge().getPlayerManager().loadPlayer(player.getUniqueId());
            
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (playerDataManager.getPlayerData(player).isScoreboard()) {
                    scoreboardManager.updatePlayerScoreboardType(player);
                }
            });
        });
    }
}
