package com.craftaro.skyblock.listeners;

import com.craftaro.core.compatibility.MajorServerVersion;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandEnvironment;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.PermissionManager;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.world.LocationUtil;
import com.craftaro.skyblock.world.WorldManager;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;

import java.util.Objects;

public class MoveListeners implements Listener {
    private final SkyBlock plugin;

    public MoveListeners(SkyBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        PermissionManager permissionManager = this.plugin.getPermissionManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        WorldManager worldManager = this.plugin.getWorldManager();
        if (!worldManager.isIslandWorld(player.getWorld())) {
            return;
        }

        IslandWorld world = worldManager.getIslandWorld(player.getWorld());

        if (world == IslandWorld.NETHER || world == IslandWorld.END) {
            if (!this.plugin.getConfiguration().getBoolean("Island.World." + world.getFriendlyName() + ".Enable")) {
                FileConfiguration configLoad = this.plugin.getLanguage();

                messageManager.sendMessage(player, configLoad.getString("Island.World.Message").replace(configLoad.getString("Island.World.Word." + world.getFriendlyName()), world.getFriendlyName()));

                if (playerDataManager.hasPlayerData(player)) {
                    PlayerData playerData = playerDataManager.getPlayerData(player);

                    if (playerData.getIsland() != null) {
                        Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(playerData.getIsland()));

                        if (island != null) {
                            teleportPlayerToIslandSpawn(player, soundManager, island);

                            return;
                        }
                    }
                }

                LocationUtil.teleportPlayerToSpawn(player);
                soundManager.playSound(player, XSound.ENTITY_ENDERMAN_TELEPORT);
            }
        }

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);

            if (playerData.getIsland() != null) {
                Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(playerData.getIsland()));

                if (island != null) {
                    if (islandManager.isLocationAtIsland(island, to)) {
                        FileConfiguration configLoad = this.plugin.getConfiguration();

                        boolean keepItemsOnDeath;

                        if (configLoad.getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
                            keepItemsOnDeath = permissionManager.hasPermission(island, "KeepItemsOnDeath", IslandRole.OWNER);
                        } else {
                            keepItemsOnDeath = configLoad.getBoolean("Island.KeepItemsOnDeath.Enable");
                        }

                        if (configLoad.getBoolean("Island.World." + world.getFriendlyName() + ".Liquid.Enable")) {
                            if (to.getY() <= configLoad.getInt("Island.World." + world.getFriendlyName() + ".Liquid.Height")) {
                                if (keepItemsOnDeath && configLoad.getBoolean("Island.Liquid.Teleport.Enable")) {
                                    player.setFallDistance(0.0F);
                                    teleportPlayerToIslandSpawn(player, soundManager, island);
                                }
                                return;
                            }
                        }

                        if (configLoad.getBoolean("Island.Void.Teleport.Enable")) {
                            if (to.getY() <= configLoad.getInt("Island.Void.Teleport.Offset")) {
                                if (configLoad.getBoolean("Island.Void.Teleport.ClearInventory")) {
                                    player.getInventory().clear();
                                    player.setLevel(0);
                                    player.setExp(0.0F);

                                    if (MajorServerVersion.isServerVersionAtLeast(MajorServerVersion.V1_9)) {
                                        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                                    } else {
                                        player.setHealth(player.getMaxHealth());
                                    }

                                    player.setFoodLevel(20);

                                    player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
                                }
                                player.setFallDistance(0.0F);

                                if (configLoad.getBoolean("Island.Void.Teleport.Island")) {
                                    teleportPlayerToIslandSpawn(player, island);
                                } else {
                                    LocationUtil.teleportPlayerToSpawn(player);
                                }

                                player.setFallDistance(0.0F);
                                soundManager.playSound(player, XSound.ENTITY_ENDERMAN_TELEPORT);
                            }
                        }
                    } else {
                        if (!islandManager.isLocationAtIsland(island, to)) {
                            teleportPlayerToIslandSpawn(player, world, island);
                            FileConfiguration configLoad = this.plugin.getConfiguration();


                            if (!configLoad.getBoolean("Island.Teleport.FallDamage", true)) {
                                player.setFallDistance(0.0F);
                            }

                            messageManager.sendMessage(player, this.plugin.getLanguage().getString("Island.WorldBorder.Outside.Message"));
                            soundManager.playSound(player, XSound.ENTITY_ENDERMAN_TELEPORT);
                        }
                    }

                    return;
                }
            }

            Location playerLoc = player.getLocation();
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                // Load the island they are now on if one exists
                islandManager.loadIslandAtLocation(playerLoc);
                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    Island loadedIsland = islandManager.getIslandAtLocation(playerLoc);
                    if (loadedIsland != null) {
                        if (player.hasPermission("fabledskyblock.bypass")) {
                            playerData.setIsland(loadedIsland.getOwnerUUID());
                            return;
                        }

                        if (loadedIsland.getStatus() == IslandStatus.OPEN ||
                                (loadedIsland.getStatus() == IslandStatus.WHITELISTED && loadedIsland.isPlayerWhitelisted(player))) {
                            loadedIsland.getVisit().addVisitor(player.getUniqueId());
                            return;
                        }
                    }

                    LocationUtil.teleportPlayerToSpawn(player);

                    messageManager.sendMessage(player, this.plugin.getLanguage().getString("Island.WorldBorder.Disappeared.Message"));
                    soundManager.playSound(player, XSound.ENTITY_ENDERMAN_TELEPORT);
                });
            });
        }
    }

    private void teleportPlayerToIslandSpawn(Player player, IslandWorld world, Island island) {
        Location loc = null;
        if (island.hasRole(IslandRole.MEMBER, player.getUniqueId()) || island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                || island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                if (this.plugin.getConfiguration().getBoolean("Island.Teleport.SafetyCheck", true)) {
                    Location safeLoc = LocationUtil.getSafeLocation(island.getLocation(world, IslandEnvironment.MAIN));
                    if (safeLoc != null) {
                        loc = safeLoc;
                    }
                }
            } else {
                loc = island.getLocation(world, IslandEnvironment.MAIN);

                if (this.plugin.getConfiguration().getBoolean("Island.Teleport.RemoveWater", false)) {
                    LocationUtil.removeWaterFromLoc(loc);
                }
            }
        } else {
            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                if (this.plugin.getConfiguration().getBoolean("Island.Teleport.SafetyCheck", true)) {
                    Location isLoc = island.getLocation(world, IslandEnvironment.VISITOR);

                    if (isLoc != null) {
                        Location safeLoc = LocationUtil.getSafeLocation(isLoc);
                        if (safeLoc != null) {
                            loc = safeLoc;
                        }
                    }
                }
            } else {
                loc = island.getLocation(world, IslandEnvironment.VISITOR);
            }
        }
        Location finalLoc = loc;
        if (finalLoc != null) {
            PaperLib.teleportAsync(player, finalLoc);
        } else {
            LocationUtil.teleportPlayerToSpawn(player);
            player.sendMessage(this.plugin.formatText(this.plugin.getLanguage().getString("Command.Island.Teleport.Unsafe.Message")));
        }
    }

    private void teleportPlayerToIslandSpawn(Player player, Island island) {
        this.teleportPlayerToIslandSpawn(player, IslandWorld.NORMAL, island);
    }

    private void teleportPlayerToIslandSpawn(Player player, SoundManager soundManager, Island island) {
        teleportPlayerToIslandSpawn(player, island);

        FileConfiguration configLoad = this.plugin.getConfiguration();

        if (!configLoad.getBoolean("Island.Teleport.FallDamage", true)) {
            player.setFallDistance(0.0F);
        }
        soundManager.playSound(player, XSound.ENTITY_ENDERMAN_TELEPORT);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) { // TODO We should wait for the player island to be loaded in 1.8.8 - Fabrimat
        final Player player = e.getPlayer();
        final WorldManager worldManager = this.plugin.getWorldManager();
        if (e.getTo() != null && e.getTo().getWorld() != null) {
            if (Bukkit.isPrimaryThread()) {
                e.getTo().getWorld().loadChunk(e.getTo().getChunk()); // Is that needed?
            }
            if (worldManager.isIslandWorld(e.getTo().getWorld())
                    && (!e.getTo().getWorld().equals(e.getFrom().getWorld()) || e.getTo().distance(e.getFrom()) > 1.0d)) { // We should not care of self block tp
                if (this.plugin.getIslandManager().getIslandAtLocation(e.getTo()) == null) {
                    e.setCancelled(true);
                    this.plugin.getMessageManager().sendMessage(player, this.plugin.getLanguage().getString("Island.WorldBorder.Disappeared.Message"));
                    this.plugin.getSoundManager().playSound(player, XSound.ENTITY_ENDERMAN_TELEPORT);
                }
            }
        }
    }
}
