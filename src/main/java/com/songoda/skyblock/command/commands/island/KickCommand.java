package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.api.event.island.IslandKickEvent;
import com.songoda.skyblock.api.utils.APIUtil;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandStatus;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.scoreboard.ScoreboardManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.utils.world.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class KickCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
            ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
            MessageManager messageManager = plugin.getMessageManager();
            IslandManager islandManager = plugin.getIslandManager();
            SoundManager soundManager = plugin.getSoundManager();
            FileManager fileManager = plugin.getFileManager();
            PermissionManager permissionManager = plugin.getPermissionManager();

            PlayerData playerData = playerDataManager.getPlayerData(player);

            Config languageConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));

            if (args.length == 1) {
                Island island = islandManager.getIsland(player);

                if (island == null) {
                    messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Owner.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                } else if (island.hasRole(IslandRole.Owner, player.getUniqueId())
                        || (island.hasRole(IslandRole.Operator, player.getUniqueId()) && permissionManager.hasPermission(island, "Kick", IslandRole.Operator))) {
                            UUID targetPlayerUUID = null;
                            String targetPlayerName = null;

                            Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);

                            Set<UUID> islandMembers = island.getRole(IslandRole.Member), islandOperators = island.getRole(IslandRole.Operator),
                                    islandVisitors = islandManager.getVisitorsAtIsland(island);

                            if (targetPlayer == null) {
                                OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
                                targetPlayerUUID = targetPlayerOffline.getUniqueId();
                                targetPlayerName = targetPlayerOffline.getName();
                            } else {
                                targetPlayerUUID = targetPlayer.getUniqueId();
                                targetPlayerName = targetPlayer.getName();
                            }

                            if(targetPlayer != null && (targetPlayer.hasPermission("fabledskyblock.bypass.kick") || targetPlayer.isOp()) && islandVisitors.contains(targetPlayer.getUniqueId())){
                                messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Exempt"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            } else if (targetPlayerUUID.equals(player.getUniqueId())) {
                                messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Yourself.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            } else if (islandOperators.contains(player.getUniqueId()) && islandOperators.contains(targetPlayerUUID)) {
                                messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Role.Operator.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            } else if (island.getOwnerUUID().equals(targetPlayerUUID)) {
                                messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Role.Owner.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            } else if (!island.getStatus().equals(IslandStatus.CLOSED) && islandVisitors.contains(targetPlayerUUID)) {
                                if (island.isCoopPlayer(targetPlayerUUID)) {
                                    messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Cooped.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                                } else {
                                    IslandKickEvent islandKickEvent = new IslandKickEvent(island.getAPIWrapper(), APIUtil.fromImplementation(IslandRole.Visitor),
                                            Bukkit.getServer().getOfflinePlayer(targetPlayerUUID), player);

                                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(islandKickEvent));

                                    if (!islandKickEvent.isCancelled()) {
                                        LocationUtil.teleportPlayerToSpawn(targetPlayer);

                                        messageManager.sendMessage(player,
                                                languageConfig.getFileConfiguration().getString("Command.Island.Kick.Kicked.Sender.Message").replace("%player", targetPlayerName));
                                        soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);

                                        messageManager.sendMessage(targetPlayer,
                                                languageConfig.getFileConfiguration().getString("Command.Island.Kick.Kicked.Target.Message").replace("%player", player.getName()));
                                        soundManager.playSound(targetPlayer, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
                                    }
                                }
                            } else if (islandMembers.contains(targetPlayerUUID) || islandOperators.contains(targetPlayerUUID)) {
                                IslandRole islandRole = IslandRole.Member;

                                if (islandOperators.contains(targetPlayerUUID)) {
                                    islandRole = IslandRole.Operator;
                                }

                                IslandKickEvent islandKickEvent = new IslandKickEvent(island.getAPIWrapper(), APIUtil.fromImplementation(islandRole),
                                        Bukkit.getServer().getOfflinePlayer(targetPlayerUUID), player);

                                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(islandKickEvent));

                                if (!islandKickEvent.isCancelled()) {
                                    messageManager.sendMessage(player,
                                            languageConfig.getFileConfiguration().getString("Command.Island.Kick.Kicked.Sender.Message").replace("%player", targetPlayerName));
                                    soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);

                                    if (targetPlayer == null) {
                                        Config config = fileManager.getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), targetPlayerUUID.toString() + ".yml"));
                                        FileConfiguration configLoad = config.getFileConfiguration();

                                        configLoad.set("Statistics.Island.Playtime", null);
                                        configLoad.set("Statistics.Island.Join", null);
                                        configLoad.set("Island.Owner", null);

                                        try {
                                            configLoad.save(config.getFile());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        messageManager.sendMessage(targetPlayer,
                                                languageConfig.getFileConfiguration().getString("Command.Island.Kick.Kicked.Target.Message").replace("%player", player.getName()));
                                        soundManager.playSound(targetPlayer, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);

                                        if (islandManager.isPlayerAtIsland(island, targetPlayer)
                                                && !targetPlayer.hasPermission("fabledskyblock.bypass.kick")
                                                && !targetPlayer.isOp()) {
                                            LocationUtil.teleportPlayerToSpawn(targetPlayer);
                                        }
    
                                        Player finalTargetPlayer = targetPlayer;
                                        Bukkit.getScheduler().runTask(plugin, () -> {
                                            scoreboardManager.updatePlayerScoreboardType(player);
                                        });

                                        playerData = playerDataManager.getPlayerData(targetPlayer);
                                        playerData.setPlaytime(0);
                                        //playerData.setMemberSince(null);
                                        playerData.clearMemberSince();
                                        playerData.setOwner(null);
                                        playerData.setChat(false);
                                        playerData.save();
                                    }

                                    if (islandMembers.contains(targetPlayerUUID)) {
                                        island.removeRole(IslandRole.Member, targetPlayerUUID);
                                    } else if (islandOperators.contains(targetPlayerUUID)) {
                                        island.removeRole(IslandRole.Operator, targetPlayerUUID);
                                    }

                                    island.save();

                                    Set<UUID> islandMembersOnline = islandManager.getMembersOnline(island);

                                    if (islandMembersOnline.size() == 1) {
                                        for (UUID islandMembersOnlineList : islandMembersOnline) {
                                            if (!islandMembersOnlineList.equals(player.getUniqueId())) {
                                                targetPlayer = Bukkit.getServer().getPlayer(islandMembersOnlineList);
                                                PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);

                                                if (targetPlayerData.isChat()) {
                                                    targetPlayerData.setChat(false);
                                                    messageManager.sendMessage(targetPlayer, fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration()
                                                            .getString("Island.Chat.Untoggled.Message"));
                                                }
                                            }
                                        }
                                    }

                                    Bukkit.getScheduler().runTask(plugin, () -> {
                                        scoreboardManager.updatePlayerScoreboardType(player);
                                    });
                                }
                            } else {
                                switch (island.getStatus()){
                                    case OPEN:
                                    case WHITELISTED:
                                        messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Occupant.Visit.Open.Message"));
                                        break;
                                    case CLOSED:
                                        messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Occupant.Visit.Closed.Message"));
                                        break;
                                }

                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            }
                        } else {
                            switch (island.getStatus()){
                                case OPEN:
                                case WHITELISTED:
                                    messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Permission.Visit.Open.Message"));
                                    break;
                                case CLOSED:
                                    messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Permission.Visit.Closed.Message"));
                                    break;
                            }

                            soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                        }
            } else {
                messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Invalid.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            }
        });
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Kick.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
