package com.craftaro.skyblock.listeners;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.cooldown.CooldownManager;
import com.craftaro.skyblock.cooldown.CooldownType;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandEnvironment;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.scoreboard.ScoreboardManager;
import com.craftaro.skyblock.usercache.UserCacheManager;
import com.craftaro.skyblock.utils.world.LocationUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Method;

public class JoinListeners implements Listener {
    private final SkyBlock plugin;

    public JoinListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ScoreboardManager scoreboardManager = this.plugin.getScoreboardManager();
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        UserCacheManager userCacheManager = this.plugin.getUserCacheManager();
        CooldownManager cooldownManager = this.plugin.getCooldownManager();
        IslandManager islandManager = this.plugin.getIslandManager();

        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            userCacheManager.addUser(player.getUniqueId(), player.getName());
            userCacheManager.saveAsync();

            try {
                islandManager.loadIsland(player);
                Island island = islandManager.getIsland(player);
                boolean teleportedToIsland = false;

                FileConfiguration configLoad = this.plugin.getConfiguration();

                if (configLoad.getBoolean("Island.Join.Spawn")) {
                    LocationUtil.teleportPlayerToSpawn(player);
                } else if (configLoad.getBoolean("Island.Join.Island") && island != null) {
                    Bukkit.getScheduler().runTask(this.plugin, () -> {
                        PaperLib.teleportAsync(player, island.getLocation(IslandWorld.NORMAL, IslandEnvironment.MAIN));
                        player.setFallDistance(0.0F);
                    });
                    teleportedToIsland = true;
                }

                if (!teleportedToIsland) {
                    islandManager.loadPlayer(player);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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
                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, playerData::save);
            } else {
                playerDataManager.createPlayerData(player);
                playerDataManager.loadPlayerData(player);
            }

            playerDataManager.storeIsland(player);

            cooldownManager.addCooldownPlayer(CooldownType.BIOME, cooldownManager.loadCooldownPlayer(CooldownType.BIOME, player));
            cooldownManager.addCooldownPlayer(CooldownType.CREATION, cooldownManager.loadCooldownPlayer(CooldownType.CREATION, player));
            cooldownManager.addCooldownPlayer(CooldownType.DELETION, cooldownManager.loadCooldownPlayer(CooldownType.DELETION, player));


            Island island = islandManager.getIslandPlayerAt(player);
            if (island != null) {
                islandManager.updateBorder(island);
                islandManager.updateFlight(player);
            }

            // Load Challenge
            SkyBlock.getPlugin(SkyBlock.class).getFabledChallenge().getPlayerManager().loadPlayer(player.getUniqueId());

            Bukkit.getScheduler().runTask(this.plugin, () -> {
                if (playerDataManager.getPlayerData(player).isScoreboard()) {
                    scoreboardManager.updatePlayerScoreboardType(player);
                }
            });
        });
    }
}
