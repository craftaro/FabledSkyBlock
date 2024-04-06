package com.craftaro.skyblock.command.commands.island;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class CurrentCommand extends SubCommand {
    public CurrentCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length > 0) {
            if (!args[0].equalsIgnoreCase(player.getName())) {
                if (args.length == 1) {
                    Player targetPlayer = Bukkit.getPlayerExact(args[0]);

                    if (targetPlayer == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Current.Offline.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        return;
                    }

                    if (!targetPlayer.getName().equals(player.getName())) {
                        PlayerData playerData = playerDataManager.getPlayerData(targetPlayer);

                        if (playerData.getIsland() == null) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Current.Island.None.Other.Message"));
                        } else {
                            String targetPlayerName = targetPlayer.getName(), ownerPlayerName;
                            targetPlayer = Bukkit.getServer().getPlayer(playerData.getIsland());

                            if (targetPlayer == null) {
                                ownerPlayerName = new OfflinePlayer(playerData.getIsland()).getName();
                            } else {
                                ownerPlayerName = targetPlayer.getName();
                            }

                            messageManager.sendMessage(player,
                                    configLoad.getString("Command.Island.Current.Island.Owner.Other.Message")
                                            .replace("%target", targetPlayerName)
                                            .replace("%owner", ownerPlayerName));
                        }

                        soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                        return;
                    }
                } else if (args.length > 1) {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Current.Invalid.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    return;
                }
            }
        }

        PlayerData playerData = playerDataManager.getPlayerData(player);

        if (playerData.getIsland() == null) {
            messageManager.sendMessage(player,
                    configLoad.getString("Command.Island.Current.Island.None.Yourself.Message"));
        } else {
            Player targetPlayer = Bukkit.getServer().getPlayer(playerData.getIsland());
            String targetPlayerName;

            if (targetPlayer == null) {
                targetPlayerName = new OfflinePlayer(playerData.getIsland()).getName();
            } else {
                targetPlayerName = targetPlayer.getName();
            }

            messageManager.sendMessage(player, configLoad.getString("Command.Island.Current.Island.Owner.Yourself.Message").replace("%player", targetPlayerName));
        }

        soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "current";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Current.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"cur"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
