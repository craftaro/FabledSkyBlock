package com.songoda.skyblock.command.commands.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.api.event.player.PlayerIslandLeaveEvent;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.scoreboard.ScoreboardManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.world.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;
import java.util.UUID;

public class LeaveCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        PlayerData playerData = playerDataManager.getPlayerData(player);

        Config languageConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));

        Island island = islandManager.getIsland(player);

        if (island == null) {
            messageManager.sendMessage(player,
                    languageConfig.getFileConfiguration().getString("Command.Island.Leave.Member.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
            messageManager.sendMessage(player,
                    languageConfig.getFileConfiguration().getString("Command.Island.Leave.Owner.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        } else {
            PlayerIslandLeaveEvent islandLeaveEvent = new PlayerIslandLeaveEvent(player, island.getAPIWrapper());
            Bukkit.getServer().getPluginManager().callEvent(islandLeaveEvent);

            if (!islandLeaveEvent.isCancelled()) {
                if (islandManager.isPlayerAtIsland(island, player)) {
                    LocationUtil.teleportPlayerToSpawn(player);
                }

                if (island.hasRole(IslandRole.Member, player.getUniqueId())) {
                    island.removeRole(IslandRole.Member, player.getUniqueId());
                } else if (island.hasRole(IslandRole.Operator, player.getUniqueId())) {
                    island.removeRole(IslandRole.Operator, player.getUniqueId());
                }

                island.save();

                playerData.setPlaytime(0);
                playerData.setOwner(null);
                playerData.setMemberSince(null);
                playerData.setChat(false);
                playerData.save();

                Set<UUID> islandMembersOnline = islandManager.getMembersOnline(island);

                if (islandMembersOnline.size() == 1) {
                    for (UUID islandMembersOnlineList : islandMembersOnline) {
                        if (!islandMembersOnlineList.equals(player.getUniqueId())) {
                            Player targetPlayer = Bukkit.getServer().getPlayer(islandMembersOnlineList);
                            PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);

                            if (targetPlayerData.isChat()) {
                                targetPlayerData.setChat(false);
                                messageManager.sendMessage(targetPlayer,
                                        fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"))
                                                .getFileConfiguration().getString("Island.Chat.Untoggled.Message"));
                            }
                        }
                    }
                }

                // TODO Check if player has been teleported
                islandManager.unloadIsland(island, null);

                for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
                    if (!loopPlayer.getUniqueId().equals(player.getUniqueId())) {
                        if (island.hasRole(IslandRole.Member, loopPlayer.getUniqueId())
                                || island.hasRole(IslandRole.Operator, loopPlayer.getUniqueId())
                                || island.hasRole(IslandRole.Owner, loopPlayer.getUniqueId())) {
                            loopPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    languageConfig.getFileConfiguration()
                                            .getString("Command.Island.Leave.Left.Broadcast.Message")
                                            .replace("%player", player.getName())));
                            soundManager.playSound(loopPlayer, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 5.0F, 5.0F);

                            if (island.getRole(IslandRole.Member).size() == 0
                                    && island.getRole(IslandRole.Operator).size() == 0) {
                                if (scoreboardManager != null) {
                                    if (islandManager.getVisitorsAtIsland(island).size() != 0) {
                                        scoreboardManager.updatePlayerScoreboardType(loopPlayer);
                                    }
                                }

                                break;
                            }
                        }
                    }
                }

                messageManager.sendMessage(player,
                        languageConfig.getFileConfiguration().getString("Command.Island.Leave.Left.Sender.Message"));
                soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 5.0F, 5.0F);

                if (scoreboardManager != null) {
                    scoreboardManager.updatePlayerScoreboardType(player);
                }
            }
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Leave.Info.Message";
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
