package me.goodandevil.skyblock.listeners;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.*;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;

import java.io.File;

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

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        WorldManager worldManager = skyblock.getWorldManager();
        FileManager fileManager = skyblock.getFileManager();

        if (!worldManager.isIslandWorld(player.getWorld())) return;

        IslandWorld world = worldManager.getIslandWorld(player.getWorld());

        if (world == IslandWorld.Nether || world == IslandWorld.End) {
            if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                    .getBoolean("Island.World." + world.name() + ".Enable")) {
                Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
                FileConfiguration configLoad = config.getFileConfiguration();

                messageManager.sendMessage(player, configLoad.getString("Island.World.Message")
                        .replace(configLoad.getString("Island.World.Word." + world.name()), world.name()));

                if (playerDataManager.hasPlayerData(player)) {
                    PlayerData playerData = playerDataManager.getPlayerData(player);

                    if (playerData.getIsland() != null) {
                        Island island = islandManager
                                .getIsland(Bukkit.getServer().getOfflinePlayer(playerData.getIsland()));

                        if (island != null) {
                            if (island.hasRole(IslandRole.Member, player.getUniqueId())
                                    || island.hasRole(IslandRole.Operator, player.getUniqueId())
                                    || island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                                player.teleport(island.getLocation(IslandWorld.Normal, IslandEnvironment.Main));
                            } else {
                                player.teleport(island.getLocation(IslandWorld.Normal, IslandEnvironment.Visitor));
                            }

                            player.setFallDistance(0.0F);
                            soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);

                            return;
                        }
                    }
                }

                LocationUtil.teleportPlayerToSpawn(player);
                soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
            }
        }

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);

            if (playerData.getIsland() != null) {
                Island island = islandManager
                        .getIsland(Bukkit.getServer().getOfflinePlayer(playerData.getIsland()));


                if (island != null) {
                    islandManager.removeUpgrades(player, false);
                    if (islandManager.isLocationAtIsland(island, to)) {
                        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
                        FileConfiguration configLoad = config.getFileConfiguration();

                        boolean keepItemsOnDeath;

                        if (configLoad.getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
                            if (island.getSetting(IslandRole.Owner, "KeepItemsOnDeath").getStatus()) {
                                keepItemsOnDeath = true;
                            } else {
                                keepItemsOnDeath = false;
                            }
                        } else if (configLoad.getBoolean("Island.KeepItemsOnDeath.Enable")) {
                            keepItemsOnDeath = true;
                        } else {
                            keepItemsOnDeath = false;
                        }

                        if (configLoad.getBoolean("Island.World." + world.name() + ".Liquid.Enable")) {
                            if (to.getY() <= configLoad.getInt("Island.World." + world.name() + ".Liquid.Height")) {
                                if (!configLoad.getBoolean("Island.Liquid.Teleport.Enable")) return;
                                if (keepItemsOnDeath) {
                                    player.setFallDistance(0.0F);

                                    if (island.hasRole(IslandRole.Member, player.getUniqueId())
                                            || island.hasRole(IslandRole.Operator, player.getUniqueId())
                                            || island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                                        player.teleport(
                                                island.getLocation(IslandWorld.Normal, IslandEnvironment.Main));
                                    } else {
                                        player.teleport(
                                                island.getLocation(IslandWorld.Normal, IslandEnvironment.Visitor));
                                    }

                                    player.setFallDistance(0.0F);
                                    soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F,
                                            1.0F);
                                }

                                return;
                            }
                        }

                        if (configLoad.getBoolean("Island.Void.Teleport.Enable")) {
                            if (to.getY() <= configLoad.getInt("Island.Void.Teleport.Offset")) {
                                if (!keepItemsOnDeath) {
                                    player.getInventory().clear();
                                    player.setLevel(0);
                                    player.setExp(0.0F);

                                    if (NMSUtil.getVersionNumber() > 8) {
                                        player.setHealth(
                                                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
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
                                    if (island.hasRole(IslandRole.Member, player.getUniqueId())
                                            || island.hasRole(IslandRole.Operator, player.getUniqueId())
                                            || island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                                        player.teleport(
                                                island.getLocation(IslandWorld.Normal, IslandEnvironment.Main));
                                    } else {
                                        player.teleport(
                                                island.getLocation(IslandWorld.Normal, IslandEnvironment.Visitor));
                                    }
                                } else {
                                    LocationUtil.teleportPlayerToSpawn(player);
                                }

                                player.setFallDistance(0.0F);
                                soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
                            }
                        }
                    } else {
                        Config config = skyblock.getFileManager()
                                .getConfig(new File(skyblock.getDataFolder(), "config.yml"));
                        FileConfiguration configLoad = config.getFileConfiguration();

                        if (LocationUtil.isLocationAtLocationRadius(to,
                                island.getLocation(world, IslandEnvironment.Island), island.getRadius() + 2)) {
                            if (!configLoad.getBoolean("Island.WorldBorder.Enable")) {
                                player.teleport(player.getLocation()
                                        .add(from.toVector().subtract(to.toVector()).normalize().multiply(2.0D)));
                                player.setFallDistance(0.0F);
                                soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
                            }
                        } else {
                            if (island.getVisit().isVisitor(player.getUniqueId())) {
                                player.teleport(island.getLocation(world, IslandEnvironment.Visitor));
                            } else {
                                player.teleport(island.getLocation(world, IslandEnvironment.Main));
                            }

                            player.setFallDistance(0.0F);
                            messageManager.sendMessage(player, skyblock.getFileManager()
                                    .getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                                    .getFileConfiguration().getString("Island.WorldBorder.Outside.Message"));
                            soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
                        }
                    }

                    return;
                }
            }

            LocationUtil.teleportPlayerToSpawn(player);

            messageManager.sendMessage(player,
                    skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                            .getFileConfiguration().getString("Island.WorldBorder.Disappeared.Message"));
            soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
        }
    }
}
