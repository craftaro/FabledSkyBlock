package com.craftaro.skyblock.command.commands.island;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.api.event.island.IslandKickEvent;
import com.craftaro.skyblock.api.utils.APIUtil;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.permission.PermissionManager;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.scoreboard.ScoreboardManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import com.craftaro.skyblock.utils.world.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class KickCommand extends SubCommand {
    public KickCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
            ScoreboardManager scoreboardManager = this.plugin.getScoreboardManager();
            MessageManager messageManager = this.plugin.getMessageManager();
            IslandManager islandManager = this.plugin.getIslandManager();
            SoundManager soundManager = this.plugin.getSoundManager();
            FileManager fileManager = this.plugin.getFileManager();
            PermissionManager permissionManager = this.plugin.getPermissionManager();

            PlayerData playerData = playerDataManager.getPlayerData(player);

            FileManager.Config languageConfig = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));

            if (args.length == 1) {
                Island island = islandManager.getIsland(player);

                if (island == null) {
                    messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Owner.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                } else if (island.hasRole(IslandRole.OWNER, player.getUniqueId())
                        || (island.hasRole(IslandRole.OPERATOR, player.getUniqueId()) && permissionManager.hasPermission(island, "Kick", IslandRole.OPERATOR))) {
                    UUID targetPlayerUUID = null;
                    String targetPlayerName = null;

                    Player targetPlayer = Bukkit.getPlayerExact(args[0]);

                    Set<UUID> islandMembers = island.getRole(IslandRole.MEMBER), islandOperators = island.getRole(IslandRole.OPERATOR),
                            islandVisitors = islandManager.getVisitorsAtIsland(island);

                    if (targetPlayer == null) {
                        OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
                        targetPlayerUUID = targetPlayerOffline.getUniqueId();
                        targetPlayerName = targetPlayerOffline.getName();
                    } else {
                        targetPlayerUUID = targetPlayer.getUniqueId();
                        targetPlayerName = targetPlayer.getName();
                    }

                    if (targetPlayer != null && (targetPlayer.hasPermission("fabledskyblock.bypass.kick") || targetPlayer.isOp()) && islandVisitors.contains(targetPlayer.getUniqueId())) {
                        messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Exempt"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (targetPlayerUUID.equals(player.getUniqueId())) {
                        messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Yourself.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (islandOperators.contains(player.getUniqueId()) && islandOperators.contains(targetPlayerUUID)) {
                        messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Role.Operator.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (island.getOwnerUUID().equals(targetPlayerUUID)) {
                        messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Role.Owner.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (island.getStatus() != IslandStatus.CLOSED && islandVisitors.contains(targetPlayerUUID)) {
                        if (island.isCoopPlayer(targetPlayerUUID)) {
                            messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Cooped.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else {
                            IslandKickEvent islandKickEvent = new IslandKickEvent(island.getAPIWrapper(), APIUtil.fromImplementation(IslandRole.VISITOR),
                                    Bukkit.getServer().getOfflinePlayer(targetPlayerUUID), player);

                            Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.getServer().getPluginManager().callEvent(islandKickEvent));

                            if (!islandKickEvent.isCancelled()) {
                                LocationUtil.teleportPlayerToSpawn(targetPlayer);

                                messageManager.sendMessage(player,
                                        languageConfig.getFileConfiguration().getString("Command.Island.Kick.Kicked.Sender.Message").replace("%player", targetPlayerName));
                                soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);

                                messageManager.sendMessage(targetPlayer,
                                        languageConfig.getFileConfiguration().getString("Command.Island.Kick.Kicked.Target.Message").replace("%player", player.getName()));
                                soundManager.playSound(targetPlayer, XSound.ENTITY_IRON_GOLEM_ATTACK);
                            }
                        }
                    } else if (islandMembers.contains(targetPlayerUUID) || islandOperators.contains(targetPlayerUUID)) {
                        IslandRole islandRole = IslandRole.MEMBER;

                        if (islandOperators.contains(targetPlayerUUID)) {
                            islandRole = IslandRole.OPERATOR;
                        }

                        IslandKickEvent islandKickEvent = new IslandKickEvent(island.getAPIWrapper(), APIUtil.fromImplementation(islandRole),
                                Bukkit.getServer().getOfflinePlayer(targetPlayerUUID), player);

                        Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.getServer().getPluginManager().callEvent(islandKickEvent));

                        if (!islandKickEvent.isCancelled()) {
                            messageManager.sendMessage(player,
                                    languageConfig.getFileConfiguration().getString("Command.Island.Kick.Kicked.Sender.Message").replace("%player", targetPlayerName));
                            soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);

                            if (targetPlayer == null) {
                                FileManager.Config config = fileManager.getConfig(new File(new File(this.plugin.getDataFolder().toString() + "/player-data"), targetPlayerUUID.toString() + ".yml"));
                                FileConfiguration configLoad = config.getFileConfiguration();

                                configLoad.set("Statistics.Island.Playtime", null);
                                configLoad.set("Statistics.Island.Join", null);
                                configLoad.set("Island.Owner", null);

                                try {
                                    configLoad.save(config.getFile());
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                messageManager.sendMessage(targetPlayer,
                                        languageConfig.getFileConfiguration().getString("Command.Island.Kick.Kicked.Target.Message").replace("%player", player.getName()));
                                soundManager.playSound(targetPlayer, XSound.ENTITY_IRON_GOLEM_ATTACK);

                                if (islandManager.isPlayerAtIsland(island, targetPlayer)
                                        && !targetPlayer.hasPermission("fabledskyblock.bypass.kick")
                                        && !targetPlayer.isOp()) {
                                    LocationUtil.teleportPlayerToSpawn(targetPlayer);
                                }

                                Bukkit.getScheduler().runTask(this.plugin, () -> scoreboardManager.updatePlayerScoreboardType(player));

                                playerData = playerDataManager.getPlayerData(targetPlayer);
                                playerData.setPlaytime(0);
                                playerData.setMemberSince(null);
                                playerData.setOwner(null);
                                playerData.setChat(false);
                                playerData.save();
                            }

                            if (islandMembers.contains(targetPlayerUUID)) {
                                island.removeRole(IslandRole.MEMBER, targetPlayerUUID);
                            } else if (islandOperators.contains(targetPlayerUUID)) {
                                island.removeRole(IslandRole.OPERATOR, targetPlayerUUID);
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
                                            messageManager.sendMessage(targetPlayer, fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml")).getFileConfiguration()
                                                    .getString("Island.Chat.Untoggled.Message"));
                                        }
                                    }
                                }
                            }

                            Bukkit.getScheduler().runTask(this.plugin, () -> scoreboardManager.updatePlayerScoreboardType(player));
                        }
                    } else {
                        switch (island.getStatus()) {
                            case OPEN:
                            case WHITELISTED:
                                messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Occupant.Visit.Open.Message"));
                                break;
                            case CLOSED:
                                messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Occupant.Visit.Closed.Message"));
                                break;
                        }

                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    }
                } else {
                    switch (island.getStatus()) {
                        case OPEN:
                        case WHITELISTED:
                            messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Permission.Visit.Open.Message"));
                            break;
                        case CLOSED:
                            messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Permission.Visit.Closed.Message"));
                            break;
                    }

                    soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
                }
            } else {
                messageManager.sendMessage(player, languageConfig.getFileConfiguration().getString("Command.Island.Kick.Invalid.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
