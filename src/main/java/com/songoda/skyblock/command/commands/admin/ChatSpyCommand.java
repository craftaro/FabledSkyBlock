package com.songoda.skyblock.command.commands.admin;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;
import java.util.UUID;

public class ChatSpyCommand extends SubCommand {
    public ChatSpyCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        FileManager fileManager = this.plugin.getFileManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        FileManager.Config language = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration languageLoad = language.getFileConfiguration();

        PlayerData playerData = playerDataManager.getPlayerData(player);

        if (args.length < 1) {
            toggleSpy(player, messageManager, languageLoad, playerData);
        } else {
            switch (args[0].toLowerCase()) {
                case "toggle":
                    toggleSpy(player, messageManager, languageLoad, playerData);
                    break;
                case "global":
                    if (!playerData.isGlobalChatSpy()) {
                        playerData.enableGlobalChatSpy();
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.GlobalEnabled.Message"));
                    } else {
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.GlobalAlreadyEnabled.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    }
                    break;
                case "add":
                    if (args.length == 2) {
                        OfflinePlayer offlinePlayer = new OfflinePlayer(args[1]);
                        Island island = islandManager.getIslandByOwner(offlinePlayer.getBukkitOfflinePlayer());
                        if (island != null) {
                            playerData.addChatSpyIsland(island);
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.Add.Message")
                                    .replace("%owner", new OfflinePlayer(island.getOwnerUUID()).getName()));
                        } else {
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.NullIsland.Message"));
                        }
                    } else {
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.InvalidArgNumber.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    }
                    break;
                case "remove":
                    if (args.length == 2) {
                        OfflinePlayer offlinePlayer = new OfflinePlayer(args[1]);
                        Island island = islandManager.getIslandByOwner(offlinePlayer.getBukkitOfflinePlayer());
                        if (island != null) {
                            playerData.removeChatSpyIsland(island);
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.Remove.Message")
                                    .replace("%owner", new OfflinePlayer(island.getOwnerUUID()).getName()));
                        } else {
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.NullIsland.Message"));
                        }
                    } else {
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.InvalidArgNumber.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    }
                    break;
                case "list":
                    Set<UUID> uuidSet = playerData.getChatSpyIslands();
                    if (!uuidSet.isEmpty()) {
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.List.Start.Message"));
                        for (UUID uuid : uuidSet) {
                            messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.List.List.Message")
                                    .replace("%owner", new OfflinePlayer(uuid).getName()));
                        }
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.List.End.Message"));
                    } else {
                        messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.List.Empty.Message"));
                    }
                    break;
                default:
                    messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.InvalidArgNumber.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    break;
            }
        }
    }

    private void toggleSpy(Player player, MessageManager messageManager, FileConfiguration languageLoad, PlayerData playerData) {
        if (playerData != null) {
            if (playerData.isChatSpy()) {
                playerData.setChatSpy(false);
                messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.Disabled.Message"));
            } else {
                playerData.setChatSpy(true);
                messageManager.sendMessage(player, languageLoad.getString("Command.Island.Admin.ChatSpy.Enabled.Message"));
            }
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "chatspy";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.ChatSpy.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"spychat", "spy"};
    }

    @Override
    public String[] getArguments() {
        return new String[]{"toggle", "global", "add", "remove", "list"};
    }
}
