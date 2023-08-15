package com.craftaro.skyblock.command.commands.island;

import com.craftaro.core.hooks.economies.Economy;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.confirmation.Confirmation;
import com.craftaro.skyblock.cooldown.CooldownType;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.scoreboard.ScoreboardManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.structure.Structure;
import com.craftaro.skyblock.structure.StructureManager;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import com.craftaro.skyblock.utils.world.LocationUtil;
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

            FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (playerData.getConfirmationTime() > 0) {
                Island island = islandManager.getIsland(player);

                if (island == null) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Confirmation.Owner.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
                                        messageManager.sendMessage(targetPlayer, configLoad.getString("Command.Island.Confirmation.Ownership.Assigned.Message"));
                                        soundManager.playSound(targetPlayer, XSound.BLOCK_ANVIL_USE);
                                    }

                                    for (Player all : Bukkit.getOnlinePlayers()) {
                                        if ((island.hasRole(IslandRole.MEMBER, all.getUniqueId())
                                                || island.hasRole(IslandRole.OPERATOR, all.getUniqueId())
                                                || island.hasRole(IslandRole.OWNER, all.getUniqueId())
                                                || island.hasRole(IslandRole.OWNER, all.getUniqueId()))
                                                && (!all.getUniqueId().equals(targetPlayerUUID))) {
                                            all.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                    configLoad.getString("Command.Island.Ownership.Assigned.Broadcast.Message")
                                                            .replace("%player", targetPlayerName)));
                                            soundManager.playSound(all, XSound.BLOCK_ANVIL_USE);
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
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                } else {
                                    Location spawnLocation = LocationUtil.getSpawnLocation();

                                    if (spawnLocation != null
                                            && islandManager.isLocationAtIsland(island, spawnLocation)) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Command.Island.Confirmation.Deletion.Spawn.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

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
                                                        configLoad.getString("Command.Island.Confirmation.Deletion.Money.Message")
                                                                .replace("%cost", "" + deletionCost));
                                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

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
                                            soundManager.playSound(all, XSound.ENTITY_GENERIC_EXPLODE, 10, 10);
                                        }
                                    }

                                    if (islandManager.deleteIsland(island, false)) {
                                        island.setDeleted(true);
                                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Confirmation.Deletion.Sender.Message"));
                                        soundManager.playSound(player, XSound.ENTITY_GENERIC_EXPLODE, 10, 10);
                                    } else {
                                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Confirmation.Deletion.Sender.MaxDeletionMessage"));
                                        soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO, 1, 1);
                                    }
                                }
                                Bukkit.getScheduler().runTask(this.plugin, () -> {
                                    scoreboardManager.updatePlayerScoreboardType(player);
                                });
                            }
                        } else {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Confirmation.Role.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        }
                    } else {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Confirmation.Specified.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    }
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Confirmation.Pending.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
