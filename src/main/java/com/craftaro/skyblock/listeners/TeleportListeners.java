package com.craftaro.skyblock.listeners;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.api.event.player.PlayerIslandEnterEvent;
import com.craftaro.skyblock.api.event.player.PlayerIslandExitEvent;
import com.craftaro.skyblock.api.event.player.PlayerIslandSwitchEvent;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.visit.Visit;
import com.craftaro.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

public class TeleportListeners implements Listener {
    private final SkyBlock plugin;

    public TeleportListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        WorldManager worldManager = this.plugin.getWorldManager();

        FileConfiguration configLoad = this.plugin.getLanguage();

        if (worldManager.isIslandWorld(event.getFrom().getWorld()) || worldManager.isIslandWorld(event.getTo().getWorld())) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> islandManager.updateFlight(player), 1L);
        }
        islandManager.loadPlayer(player);


        // Fix for bug that tp you in the real Nether/End when entering in a portal in an island // TODO Simplify
        if (event.getTo() != null && (worldManager.isIslandWorld(event.getFrom().getWorld())
                && !worldManager.isIslandWorld(event.getTo().getWorld())
                && (event.getFrom().getBlock().getType() == XMaterial.END_PORTAL.parseMaterial()
                || event.getFrom().getBlock().getType() == XMaterial.NETHER_PORTAL.parseMaterial())
                && (event.getTo().getWorld() != null
                && event.getTo().getWorld().getEnvironment() == World.Environment.NETHER
                || event.getTo().getWorld().getEnvironment() == World.Environment.THE_END))
                || event.getTo() != null
                && (worldManager.isIslandWorld(event.getFrom().getWorld())
                && !worldManager.isIslandWorld(event.getTo().getWorld())
                && (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL
                || event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL
                || event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)
                && (event.getTo().getWorld() != null
                && event.getTo().getWorld().getEnvironment() == World.Environment.NETHER
                || event.getTo().getWorld().getEnvironment() == World.Environment.THE_END))) {
            event.setCancelled(true);
        }

        if (worldManager.isIslandWorld(player.getWorld())) {
            Island island = islandManager.getIslandAtLocation(event.getTo());

            // Check permissions.
            if (!this.plugin.getPermissionManager().processPermission(event, player, island)) {
                return;
            }
        }

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);

            Island island = islandManager.getIslandAtLocation(event.getTo());

            if (island != null) {
                if (!island.getOwnerUUID().equals(playerData.getOwner())) {
                    if (!player.hasPermission("fabledskyblock.bypass") && !player.hasPermission("fabledskyblock.bypass.*") && !player.hasPermission("fabledskyblock.*")) {
                        if (island.getStatus() != IslandStatus.OPEN &&
                                !island.isCoopPlayer(player.getUniqueId()) &&
                                !(island.getStatus() == IslandStatus.WHITELISTED && island.isPlayerWhitelisted(player))) {
                            event.setCancelled(true);

                            messageManager.sendMessage(player, configLoad.getString("Island.Visit.Closed.Plugin.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            return;
                        } else if (this.plugin.getConfiguration().getBoolean("Island.Visitor.Banning") && island.getBan().isBanned(player.getUniqueId())) {
                            event.setCancelled(true);

                            messageManager.sendMessage(player, configLoad.getString("Island.Visit.Banned.Teleport.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            return;
                        }
                    }
                }

                if (playerData.getIsland() != null && !playerData.getIsland().equals(island.getOwnerUUID())) {
                    com.craftaro.skyblock.api.island.Island exitIsland = null;

                    OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(playerData.getIsland());

                    if (islandManager.containsIsland(playerData.getIsland())) {
                        exitIsland = islandManager.getIsland(offlinePlayer).getAPIWrapper();
                    }

                    Bukkit.getServer().getPluginManager().callEvent(new PlayerIslandExitEvent(player, exitIsland));
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerIslandSwitchEvent(player, exitIsland, island.getAPIWrapper()));

                    playerData.setVisitTime(0);
                }

                if (worldManager.getIslandWorld(event.getTo().getWorld()) == IslandWorld.NORMAL) {
                    if (!island.isWeatherSynchronized()) {
                        player.setPlayerTime(island.getTime(), this.plugin.getConfiguration().getBoolean("Island.Weather.Time.Cycle"));
                        player.setPlayerWeather(island.getWeather());
                    }
                }

                UUID islandOwnerUUID = playerData.getIsland();
                playerData.setIsland(island.getOwnerUUID());

                if (islandOwnerUUID != null && islandManager.containsIsland(islandOwnerUUID) && (playerData.getOwner() == null || !playerData.getOwner().equals(islandOwnerUUID))) {
                    islandManager.unloadIsland(islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID)), null);
                }

                Visit visit = island.getVisit();

                if (visit != null && !visit.isVisitor(player.getUniqueId())) {
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerIslandEnterEvent(player, island.getAPIWrapper()));

                    visit.addVisitor(player.getUniqueId());
                    visit.save();
                }

                return;
            }

            player.resetPlayerTime();
            player.resetPlayerWeather();

            if (playerData.getIsland() != null) {
                com.craftaro.skyblock.api.island.Island islandWrapper = null;
                island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(playerData.getIsland()));

                if (island != null) {
                    islandWrapper = island.getAPIWrapper();
                }

                Bukkit.getServer().getPluginManager().callEvent(new PlayerIslandExitEvent(player, islandWrapper));

                playerData.setVisitTime(0);
            }

            UUID islandOwnerUUID = playerData.getIsland();
            playerData.setIsland(null);

            if (islandOwnerUUID != null && islandManager.containsIsland(islandOwnerUUID) && (playerData.getOwner() == null || !playerData.getOwner().equals(islandOwnerUUID))) {
                islandManager.unloadIsland(islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID)), null);
            }
        }
    }

    @EventHandler
    public void onEntityTeleport(EntityPortalEvent e) {
        WorldManager worldManager = this.plugin.getWorldManager();

        // Do not handle player
        if (e.getEntityType() == EntityType.PLAYER) {
            return;
        }

        Location from = e.getFrom();
        Location to = e.getTo();

        if (to == null || from.getWorld() == to.getWorld()) {
            return;
        }

        if (worldManager.getIslandWorld(e.getFrom().getWorld()) != null) {
            e.setCancelled(true);
        }
    }
}
