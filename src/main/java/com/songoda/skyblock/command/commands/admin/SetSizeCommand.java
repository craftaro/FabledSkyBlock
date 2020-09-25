package com.songoda.skyblock.command.commands.admin;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.core.utils.NumberUtils;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SetSizeCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        onCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 2) {
            if (args[1].matches("[0-9]+")) {
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

                int size = Integer.valueOf(args[1]);

                if (islandOwnerUUID == null) {
                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.SetSize.Island.Owner.Message"));
                    soundManager.playSound(sender,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                } else if (size < 20) {
                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.SetSize.Size.Greater.Message"));
                    soundManager.playSound(sender,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                } else if (size > 1000) {
                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.SetSize.Size.Less.Message"));
                    soundManager.playSound(sender,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                } else {
                    if (islandManager.containsIsland(islandOwnerUUID)) {
                        Island island = islandManager
                                .getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));
                        island.setSize(size);

                        if (this.plugin.getConfiguration().getBoolean("Island.WorldBorder.Enable")
                                && island.isBorder()) {
                            islandManager.updateBorder(island);
                        }
                    } else {
                        File islandDataFile = new File(plugin.getDataFolder().toString() + "/island-data",
                                islandOwnerUUID.toString() + ".yml");

                        if (!fileManager.isFileExist(islandDataFile)) {
                            messageManager.sendMessage(sender,
                                    configLoad.getString("Command.Island.Admin.SetSize.Island.Data.Message"));
                            soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            return;
                        }

                        FileConfiguration islandDataConfigLoad = YamlConfiguration
                                .loadConfiguration(islandDataFile);
                        islandDataConfigLoad.set("Size", size);

                        try {
                            islandDataConfigLoad.save(islandDataFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.SetSize.Set.Message")
                                    .replace("%player", targetPlayerName)
                                    .replace("%size", NumberUtils.formatNumber(size)));
                    soundManager.playSound(sender, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
                }
            } else {
                messageManager.sendMessage(sender,
                        configLoad.getString("Command.Island.Admin.SetSize.Numerical.Message"));
                soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(sender,
                    configLoad.getString("Command.Island.Admin.SetSize.Invalid.Message"));
            soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public String getName() {
        return "setsize";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.SetSize.Info.Message";
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
