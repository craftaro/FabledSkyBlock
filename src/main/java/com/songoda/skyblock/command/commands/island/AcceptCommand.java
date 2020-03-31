package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.api.event.player.PlayerIslandJoinEvent;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.invite.Invite;
import com.songoda.skyblock.invite.InviteManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.scoreboard.Scoreboard;
import com.songoda.skyblock.scoreboard.ScoreboardManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class AcceptCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        ScoreboardManager scoreboardManager = skyblock.getScoreboardManager();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        PlayerData playerData = playerDataManager.getPlayerData(player);

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            InviteManager inviteManager = skyblock.getInviteManager();

            if (inviteManager.hasInvite(player.getUniqueId())) {
                Invite invite = inviteManager.getInvite(player.getUniqueId());
                String playerName = args[0];

                if (invite.getSenderName().equalsIgnoreCase(playerName)) {
                    inviteManager.removeInvite(player.getUniqueId());

                    if (islandManager.getIsland(player) == null) {
                        boolean unloadIsland = false;
                        Island island;

                        if (islandManager.containsIsland(invite.getOwnerUUID())) {
                            island = islandManager
                                    .getIsland(Bukkit.getServer().getOfflinePlayer(invite.getOwnerUUID()));
                        } else {
                            island = islandManager
                                    .loadIsland(Bukkit.getServer().getOfflinePlayer(invite.getOwnerUUID()));
                            unloadIsland = true;
                        }

                        PlayerIslandJoinEvent islandJoinEvent = new PlayerIslandJoinEvent(player,
                                island.getAPIWrapper());
                        Bukkit.getServer().getPluginManager().callEvent(islandJoinEvent);

                        if (islandJoinEvent.isCancelled()) {
                            if (unloadIsland) {
                                islandManager.unloadIsland(island, null);
                            }
                        } else {
                            Player targetPlayer = Bukkit.getServer().getPlayer(invite.getSenderUUID());

                            if (targetPlayer != null) {
                                messageManager.sendMessage(targetPlayer,
                                        configLoad.getString("Command.Island.Accept.Accepted.Target.Message")
                                                .replace("%player", player.getName()));
                                soundManager.playSound(targetPlayer, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);
                            }

                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Accept.Accepted.Sender.Message")
                                            .replace("%player", invite.getSenderName()));
                            soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                            playerData.setPlaytime(0);
                            playerData.setOwner(invite.getOwnerUUID());
                            playerData.setMemberSince(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                            playerData.save();

                            island.setRole(IslandRole.Member, player.getUniqueId());
                            island.save();

                            if ((island.getRole(IslandRole.Member).size() + island.getRole(IslandRole.Operator).size()
                                    + 1) >= fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                                    .getFileConfiguration().getInt("Island.Member.Capacity")) {
                                Map<UUID, Invite> invites = inviteManager.getInvites();

                                for (UUID inviteList : invites.keySet()) {
                                    Invite targetInvite = invites.get(inviteList);

                                    if (targetInvite.getOwnerUUID().equals(invite.getOwnerUUID())) {
                                        inviteManager.removeInvite(inviteList);

                                        Player targetInvitePlayer = Bukkit.getServer().getPlayer(inviteList);

                                        if (targetInvitePlayer != null) {
                                            targetInvitePlayer
                                                    .sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                            configLoad.getString(
                                                                    "Command.Island.Accept.Capacity.Broadcast.Message")
                                                                    .replace("%player", targetInvite.getSenderName())));
                                            soundManager.playSound(targetInvitePlayer,
                                                    CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
                                        }
                                    }
                                }
                            }

                            skyblock.getVisitManager().getIsland(invite.getOwnerUUID())
                                    .removeVoter(player.getUniqueId());

                            for (Player all : Bukkit.getOnlinePlayers()) {
                                if (!all.getUniqueId().equals(player.getUniqueId())) {
                                    if (playerDataManager.hasPlayerData(all)) {
                                        playerData = playerDataManager.getPlayerData(all);

                                        if (playerData.getOwner() != null
                                                && playerData.getOwner().equals(island.getOwnerUUID())) {
                                            all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                    configLoad
                                                            .getString(
                                                                    "Command.Island.Accept.Accepted.Broadcast.Message")
                                                            .replace("%player", player.getName())));
                                            soundManager.playSound(all, CompatibleSound.ENTITY_FIREWORK_ROCKET_BLAST.getSound(), 1.0F,
                                                    1.0F);

                                            if (scoreboardManager != null) {
                                                if (island.getRole(IslandRole.Member).size() == 1
                                                        && island.getRole(IslandRole.Operator).size() == 0) {
                                                    Scoreboard scoreboard = scoreboardManager.getScoreboard(all);
                                                    scoreboard.setDisplayName(
                                                            ChatColor.translateAlternateColorCodes('&', configLoad
                                                                    .getString("Scoreboard.Island.Team.Displayname")));

                                                    if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                                                        scoreboard.setDisplayList(configLoad.getStringList(
                                                                "Scoreboard.Island.Team.Empty.Displaylines"));
                                                    } else {
                                                        scoreboard.setDisplayList(configLoad.getStringList(
                                                                "Scoreboard.Island.Team.Occupied.Displaylines"));
                                                    }

    
                                                    scoreboard.run();
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (scoreboardManager != null) {
                                Scoreboard scoreboard = scoreboardManager.getScoreboard(player);
                                scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Scoreboard.Island.Team.Displayname")));

                                if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                                    scoreboard.setDisplayList(
                                            configLoad.getStringList("Scoreboard.Island.Team.Empty.Displaylines"));
                                } else {
                                    scoreboard.setDisplayList(
                                            configLoad.getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
                                }


                                scoreboard.run();
                            }
                        }
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Accept.Owner.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    }
                } else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Accept.Invited.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Accept.Invite.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Accept.Invalid.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Accept.Info.Message";
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
