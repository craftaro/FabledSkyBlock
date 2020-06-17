package com.songoda.skyblock.listeners;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.commands.island.TeleportCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.*;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.version.NMSUtil;
import com.songoda.skyblock.utils.world.LocationUtil;
import com.songoda.skyblock.world.WorldManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.Objects;

public class Move implements Listener {

    private final SkyBlock skyblock;

    public Move(SkyBlock skyblock) {
        this.skyblock = skyblock;
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null || (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())) {
            return;
        }

        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        PermissionManager permissionManager = skyblock.getPermissionManager();
        SoundManager soundManager = skyblock.getSoundManager();
        WorldManager worldManager = skyblock.getWorldManager();
        FileManager fileManager = skyblock.getFileManager();

        if (!worldManager.isIslandWorld(player.getWorld())) return;

        IslandWorld world = worldManager.getIslandWorld(player.getWorld());

        if (world == IslandWorld.Nether || world == IslandWorld.End) {
            if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.World." + world.name() + ".Enable")) {
                Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
                FileConfiguration configLoad = config.getFileConfiguration();

                messageManager.sendMessage(player, configLoad.getString("Island.World.Message").replace(configLoad.getString("Island.World.Word." + world.name()), world.name()));

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
                soundManager.playSound(player, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);
            }
        }

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);

            if (playerData.getIsland() != null) {
                Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(playerData.getIsland()));

                if (island != null) {
                    if (islandManager.isLocationAtIsland(island, to)) {
                        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
                        FileConfiguration configLoad = config.getFileConfiguration();

                        boolean keepItemsOnDeath;

                        if (configLoad.getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
                            keepItemsOnDeath = permissionManager.hasPermission(island,"KeepItemsOnDeath", IslandRole.Owner);
                        } else {
                            keepItemsOnDeath = configLoad.getBoolean("Island.KeepItemsOnDeath.Enable");
                        }

                        if (configLoad.getBoolean("Island.World." + world.name() + ".Liquid.Enable")) {
                            if (to.getY() <= configLoad.getInt("Island.World." + world.name() + ".Liquid.Height")) {
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

                                    if (NMSUtil.getVersionNumber() > 8) {
                                        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                                    } else {
                                        player.setHealth(player.getMaxHealth());
                                    }

                                    player.setFoodLevel(20);

                                    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                                        player.removePotionEffect(potionEffect.getType());
                                    }
                                }
                                player.setFallDistance(0.0F);

                                if (configLoad.getBoolean("Island.Void.Teleport.Island")) {
                                    teleportPlayerToIslandSpawn(player, island);
                                } else {
                                    LocationUtil.teleportPlayerToSpawn(player);
                                }

                                player.setFallDistance(0.0F);
                                soundManager.playSound(player, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);
                            }
                        }
                    } else {
                        if (!LocationUtil.isLocationAtLocationRadius(island.getLocation(world, IslandEnvironment.Island), to, island.getRadius())) {
                            teleportPlayerToIslandSpawn(player, world, island);
                            Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
                            FileConfiguration configLoad = config.getFileConfiguration();


                            if(!configLoad.getBoolean("Island.Teleport.FallDamage", true)){
                                player.setFallDistance(0.0F);
                            }

                            messageManager.sendMessage(player, skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration()
                                    .getString("Island.WorldBorder.Outside.Message"));
                            soundManager.playSound(player, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);
                        }
                    }

                    return;
                }
            }

            // Load the island they are now on if one exists
            if (player.hasPermission("fabledskyblock.bypass")) {
                Island loadedIsland = islandManager.loadIslandAtLocation(player.getLocation());
                if (loadedIsland != null) {
                    playerData.setIsland(loadedIsland.getOwnerUUID());
                    return;
                }
            }

            LocationUtil.teleportPlayerToSpawn(player);

            messageManager.sendMessage(player,
                    skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration().getString("Island.WorldBorder.Disappeared.Message"));
            soundManager.playSound(player, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);
        }
    }

    private void teleportPlayerToIslandSpawn(Player player, IslandWorld world, Island island) {
        Location loc;
        if (island.hasRole(IslandRole.Member, player.getUniqueId()) || island.hasRole(IslandRole.Operator, player.getUniqueId())
                || island.hasRole(IslandRole.Owner, player.getUniqueId())) {
            if (!player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                loc = LocationUtil.getSafeLocation(island.getLocation(world, IslandEnvironment.Main));
            } else {
                loc = island.getLocation(world, IslandEnvironment.Main);
                LocationUtil.removeWaterFromLoc(skyblock, loc);
            }
        } else {
            if (!player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                loc = LocationUtil.getSafeLocation(island.getLocation(world, IslandEnvironment.Visitor));
            } else {
                loc = island.getLocation(world, IslandEnvironment.Visitor);
            }
        }
        if(loc != null){
            player.teleport(loc);
        } else {
            LocationUtil.teleportPlayerToSpawn(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    Objects.requireNonNull(skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                            .getFileConfiguration().getString("Command.Island.Teleport.Unsafe.Message"))));
        }
    }

    private void teleportPlayerToIslandSpawn(Player player, Island island) {
        this.teleportPlayerToIslandSpawn(player, IslandWorld.Normal, island);
    }

    private void teleportPlayerToIslandSpawn(Player player, SoundManager soundManager, Island island) {
        teleportPlayerToIslandSpawn(player, island);

        FileManager fileManager = skyblock.getFileManager();
        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if(!configLoad.getBoolean("Island.Teleport.FallDamage", true)){
            player.setFallDistance(0.0F);
        }
        soundManager.playSound(player, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) { // TODO We should wait for the player island to be loaded in 1.8.8 - Fabrimat
        final Player player = e.getPlayer();
        final WorldManager worldManager = skyblock.getWorldManager();
        if(e.getTo() != null && e.getTo().getWorld() != null){
            e.getTo().getWorld().loadChunk(e.getTo().getChunk());
            if(worldManager.isIslandWorld(e.getTo().getWorld())
                    && (!e.getTo().getWorld().equals(e.getFrom().getWorld()) || e.getTo().distance(e.getFrom()) > 1.0d)){ // We should not care of self block tp
                if(skyblock.getIslandManager().getIslandAtLocation(e.getTo()) == null){
                    e.setCancelled(true);
                    skyblock.getMessageManager().sendMessage(player,
                            skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                    .getFileConfiguration().getString("Island.WorldBorder.Disappeared.Message"));
                    skyblock.getSoundManager().playSound(player, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);
                }
            }
        }
    }
}
