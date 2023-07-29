package com.songoda.skyblock.command.commands.island;

import com.craftaro.core.compatibility.CompatibleSound;
import com.craftaro.core.hooks.economies.Economy;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.confirmation.Confirmation;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandStatus;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.scoreboard.ScoreboardManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.structure.Structure;
import com.songoda.skyblock.structure.StructureManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.utils.world.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class ConfirmCommand extends SubCommand {
    public ConfirmCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        StructureManager structureManager = this.plugin.getStructureManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();
        ScoreboardManager scoreboardManager = this.plugin.getScoreboardManager();
        Economy economy = this.plugin.getEconomyManager().getEconomy();

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);

            Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (playerData.getConfirmationTime() > 0) {
                Island island = islandManager.getIsland(player);

                if (island == null) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Command.Island.Confirmation.Owner.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                } else {
                    Confirmation confirmation = playerData.getConfirmation();

                    if (confirmation == Confirmation.OWNERSHIP || confirmation == Confirmation.RESET
                            || confirmation == Confirmation.DELETION) {
                        if (island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                            if (confirmation == Confirmation.OWNERSHIP) {
                                UUID targetPlayerUUID = playerData.getOwnership();

                                if (island.hasRole(IslandRole.MEMBER, targetPlayerUUID)
                                        || island.hasRole(IslandRole.OPERATOR, targetPlayerUUID)) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Command.Island.Confirmation.Confirmed.Message"));

                                    String targetPlayerName;
                                    Player targetPlayer = Bukkit.getServer().getPlayer(targetPlayerUUID);

                                    if (targetPlayer == null) {
                                        targetPlayerName = new OfflinePlayer(targetPlayerUUID).getName();
                                    } else {
                                        targetPlayerName = targetPlayer.getName();
                                        messageManager.sendMessage(targetPlayer, configLoad
                                                .getString("Command.Island.Confirmation.Ownership.Assigned.Message"));
                                        soundManager.playSound(targetPlayer, CompatibleSound.BLOCK_ANVIL_USE.getSound(), 1.0F,
                                                1.0F);
                                    }

                                    for (Player all : Bukkit.getOnlinePlayers()) {
                                        if ((island.hasRole(IslandRole.MEMBER, all.getUniqueId())
                                                || island.hasRole(IslandRole.OPERATOR, all.getUniqueId())
                                                || island.hasRole(IslandRole.OWNER, all.getUniqueId())
                                                || island.hasRole(IslandRole.OWNER, all.getUniqueId()))
                                                && (!all.getUniqueId().equals(targetPlayerUUID))) {
                                            all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                    configLoad.getString(
                                                                    "Command.Island.Ownership.Assigned.Broadcast.Message")
                                                            .replace("%player", targetPlayerName)));
                                            soundManager.playSound(all, CompatibleSound.BLOCK_ANVIL_USE.getSound(), 1.0F, 1.0F);
                                        }
                                    }

                                    playerData.setConfirmation(null);
                                    playerData.setConfirmationTime(0);

                                    islandManager.giveOwnership(island,
                                            Bukkit.getServer().getOfflinePlayer(targetPlayerUUID));

                                    this.plugin.getCooldownManager().createPlayer(CooldownType.OWNERSHIP,
                                            Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()));
                                } else {
                                    messageManager.sendMessage(player, configLoad
                                            .getString("Command.Island.Confirmation.Ownership.Member.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                                }
                                Bukkit.getScheduler().runTask(this.plugin, () -> {
                                    scoreboardManager.updatePlayerScoreboardType(player);
                                });
                            } else if (confirmation == Confirmation.RESET) {
                                playerData.setConfirmation(null);
                                playerData.setConfirmationTime(0);
                                Bukkit.getScheduler().runTask(this.plugin, () -> {
                                    scoreboardManager.updatePlayerScoreboardType(player);
                                });
                            } else if (confirmation == Confirmation.DELETION) {
                                if (island.getStatus() == IslandStatus.OPEN) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Command.Island.Confirmation.Deletion.Open.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                                } else {
                                    Location spawnLocation = LocationUtil.getSpawnLocation();

                                    if (spawnLocation != null
                                            && islandManager.isLocationAtIsland(island, spawnLocation)) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Command.Island.Confirmation.Deletion.Spawn.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        return;
                                    }

                                    if (economy != null && economy.isEnabled() && island.getStructure() != null
                                            && !island.getStructure().isEmpty()
                                            && structureManager.containsStructure(island.getStructure())) {
                                        Structure structure = structureManager.getStructure(island.getStructure());
                                        double deletionCost = structure.getDeletionCost();

                                        if (deletionCost != 0.0D) {
                                            if (economy.hasBalance(player, deletionCost)) {
                                                economy.withdrawBalance(player, deletionCost);
                                            } else {
                                                messageManager.sendMessage(player,
                                                        configLoad.getString(
                                                                        "Command.Island.Confirmation.Deletion.Money.Message")
                                                                .replace("%cost", "" + deletionCost));
                                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                        1.0F);

                                                return;
                                            }
                                        }
                                    }

                                    playerData.setConfirmation(null);
                                    playerData.setConfirmationTime(0);

                                    messageManager.sendMessage(player,
                                            configLoad.getString("Command.Island.Confirmation.Confirmed.Message"));

                                    for (Player all : Bukkit.getOnlinePlayers()) {
                                        if (island.hasRole(IslandRole.MEMBER, all.getUniqueId())
                                                || island.hasRole(IslandRole.OPERATOR, all.getUniqueId())) {
                                            all.sendMessage(
                                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                            "Command.Island.Confirmation.Deletion.Broadcast.Message")));
                                            soundManager.playSound(all, CompatibleSound.ENTITY_GENERIC_EXPLODE.getSound(), 10.0F, 10.0F);
                                        }
                                    }

                                    if (islandManager.deleteIsland(island, false)) {
                                        island.setDeleted(true);
                                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Confirmation.Deletion.Sender.Message"));
                                        soundManager.playSound(player, CompatibleSound.ENTITY_GENERIC_EXPLODE.getSound(), 10.0F, 10.0F);
                                    } else {
                                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Confirmation.Deletion.Sender.MaxDeletionMessage"));
                                        soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1f, 1f);
                                    }
                                }
                                Bukkit.getScheduler().runTask(this.plugin, () -> {
                                    scoreboardManager.updatePlayerScoreboardType(player);
                                });
                            }
                        } else {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Confirmation.Role.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                        }
                    } else {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Confirmation.Specified.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    }
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Confirmation.Pending.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            }
        }

    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "confirm";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Confirmation.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"confirmation"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
