package com.songoda.skyblock.command.commands.island;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.player.PlayerVoteEvent;
import com.songoda.skyblock.api.event.player.PlayerVoteRemoveEvent;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandStatus;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.visit.Visit;
import com.songoda.skyblock.visit.VisitManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class VoteCommand extends SubCommand {
    public VoteCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        VisitManager visitManager = this.plugin.getVisitManager();
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            if (!this.plugin.getConfiguration()
                    .getBoolean("Island.Visitor.Vote")) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Disabled.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                return;
            }

            Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
            UUID islandOwnerUUID;
            String targetPlayerName;

            if (targetPlayer == null) {
                OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
                islandOwnerUUID = targetPlayerOffline.getOwner();
                targetPlayerName = targetPlayerOffline.getName();
            } else {
                islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
                targetPlayerName = targetPlayer.getName();
            }

            if (islandOwnerUUID == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Island.None.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            } else if (!visitManager.hasIsland(islandOwnerUUID)) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Island.Unloaded.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            } else {
                Visit visit = visitManager.getIsland(islandOwnerUUID);
                if (!islandManager.containsIsland(islandOwnerUUID)) {
                    islandManager.loadIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));
                }

                Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));

                if (visit.getStatus() == IslandStatus.OPEN) {

                    if (island.hasRole(IslandRole.MEMBER, player.getUniqueId())
                            || island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                            || island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Vote.Island.Member.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    } else if (playerDataManager.hasPlayerData(player)) {
                        PlayerData playerData = playerDataManager.getPlayerData(player);

                        if (playerData.getIsland() != null && playerData.getIsland().equals(island.getOwnerUUID())) {
                            if (visit.getVoters().contains(player.getUniqueId())) {
                                Bukkit.getPluginManager().callEvent(new PlayerVoteRemoveEvent(player, island.getAPIWrapper()));
                                visit.removeVoter(player.getUniqueId());

                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Vote.Vote.Removed.Message")
                                                .replace("%player", targetPlayerName));
                                soundManager.playSound(player, XSound.ENTITY_GENERIC_EXPLODE);
                            } else {
                                PlayerVoteEvent playerVoteEvent = new PlayerVoteEvent(player, island.getAPIWrapper());
                                Bukkit.getServer().getPluginManager().callEvent(playerVoteEvent);
                                if (playerVoteEvent.isCancelled()) {
                                    return;
                                }

                                visit.addVoter(player.getUniqueId());

                                messageManager.sendMessage(player,
                                        configLoad.getString("Command.Island.Vote.Vote.Added.Message")
                                                .replace("%player", targetPlayerName));
                                soundManager.playSound(player, XSound.ENTITY_PLAYER_LEVELUP);
                            }
                        } else {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Vote.Island.Location.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        }

                        islandManager.unloadIsland(island, null);
                    }
                } else {
                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Vote.Island.Closed.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                }
            }
        } else {
            messageManager.sendMessage(player, configLoad.getString("Command.Island.Vote.Invalid.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "vote";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Vote.Info.Message";
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
