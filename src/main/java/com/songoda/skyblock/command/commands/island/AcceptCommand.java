package com.songoda.skyblock.command.commands.island;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
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
    public AcceptCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        ScoreboardManager scoreboardManager = this.plugin.getScoreboardManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        PlayerData playerData = playerDataManager.getPlayerData(player);

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            InviteManager inviteManager = this.plugin.getInviteManager();

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
                            islandManager.loadIsland(Bukkit.getServer().getOfflinePlayer(invite.getOwnerUUID()));
                            island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(invite.getOwnerUUID()));
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
                                soundManager.playSound(targetPlayer, XSound.ENTITY_PLAYER_LEVELUP);
                            }

                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Accept.Accepted.Sender.Message")
                                            .replace("%player", invite.getSenderName()));
                            soundManager.playSound(player, XSound.ENTITY_PLAYER_LEVELUP);

                            playerData.setPlaytime(0);
                            playerData.setOwner(invite.getOwnerUUID());
                            playerData.setMemberSince(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                            playerData.save();

                            island.setRole(IslandRole.MEMBER, player.getUniqueId());
                            island.save();

                            if ((island.getRole(IslandRole.MEMBER).size() + island.getRole(IslandRole.OPERATOR).size() + 1) >= island.getMaxMembers(player)) {
                                Map<UUID, Invite> invites = inviteManager.getInvites();

                                for (UUID inviteList : invites.keySet()) {
                                    Invite targetInvite = invites.get(inviteList);

                                    if (targetInvite.getOwnerUUID().equals(invite.getOwnerUUID())) {
                                        inviteManager.removeInvite(inviteList);

                                        Player targetInvitePlayer = Bukkit.getServer().getPlayer(inviteList);

                                        if (targetInvitePlayer != null) {
                                            targetInvitePlayer
                                                    .sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                            configLoad.getString("Command.Island.Accept.Capacity.Broadcast.Message")
                                                                    .replace("%player", targetInvite.getSenderName())));
                                            soundManager.playSound(targetInvitePlayer, XSound.ENTITY_IRON_GOLEM_ATTACK);
                                        }
                                    }
                                }
                            }

                            this.plugin.getVisitManager().getIsland(invite.getOwnerUUID()).removeVoter(player.getUniqueId());

                            for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
                                if (!loopPlayer.getUniqueId().equals(player.getUniqueId())) {
                                    if (playerDataManager.hasPlayerData(loopPlayer)) {
                                        playerData = playerDataManager.getPlayerData(loopPlayer);

                                        if (playerData.getOwner() != null
                                                && playerData.getOwner().equals(island.getOwnerUUID())) {
                                            loopPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                    configLoad
                                                            .getString(
                                                                    "Command.Island.Accept.Accepted.Broadcast.Message")
                                                            .replace("%player", player.getName())));
                                            soundManager.playSound(loopPlayer, XSound.ENTITY_FIREWORK_ROCKET_BLAST);


                                            if (island.getRole(IslandRole.MEMBER).size() == 1 && island.getRole(IslandRole.OPERATOR).isEmpty()) {
                                                scoreboardManager.updatePlayerScoreboardType(loopPlayer);
                                            }
                                        }
                                    }
                                }
                            }

                            scoreboardManager.updatePlayerScoreboardType(player);
                        }
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Accept.Owner.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    }
                } else {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Accept.Invited.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Accept.Invite.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Accept.Invalid.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
