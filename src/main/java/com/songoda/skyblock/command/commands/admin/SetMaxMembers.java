package com.songoda.skyblock.command.commands.admin;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.NumberUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
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

public class SetMaxMembers extends SubCommand {
    public SetMaxMembers(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        onCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        IslandManager islandManager = this.plugin.getIslandManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
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

                int maxMembers = Integer.parseInt(args[1]);

                if (islandOwnerUUID == null) {
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.SetMaxMembers.Island.Owner.Message"));
                    soundManager.playSound(sender, XSound.ENTITY_VILLAGER_NO);
                } else if (maxMembers <= 0) {
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.SetMaxMembers.Members.Greater.Message"));
                    soundManager.playSound(sender, XSound.ENTITY_VILLAGER_NO);
                } else if (maxMembers > 100000) {
                    messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.SetMaxMembers.Members.Less.Message"));
                    soundManager.playSound(sender, XSound.ENTITY_VILLAGER_NO);
                } else {
                    if (islandManager.containsIsland(islandOwnerUUID)) {
                        Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));
                        island.setMaxMembers(maxMembers);

                        if (this.plugin.getConfiguration().getBoolean("Island.WorldBorder.Enable") && island.isBorder()) {
                            islandManager.updateBorder(island);
                        }
                    } else {
                        File islandDataFile = new File(this.plugin.getDataFolder() + "/island-data", islandOwnerUUID + ".yml");

                        if (!fileManager.isFileExist(islandDataFile)) {
                            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.SetMaxMembers.Island.Data.Message"));
                            soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);

                            return;
                        }

                        FileConfiguration islandDataConfigLoad = YamlConfiguration.loadConfiguration(islandDataFile);
                        islandDataConfigLoad.set("MaxMembers", maxMembers);

                        try {
                            islandDataConfigLoad.save(islandDataFile);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.SetMaxMembers.Set.Message")
                                    .replace("%player", targetPlayerName)
                                    .replace("%maxMembers", NumberUtils.formatNumber(maxMembers)));
                    soundManager.playSound(sender, XSound.BLOCK_NOTE_BLOCK_PLING);
                }
            } else {
                messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.SetMaxMembers.Numerical.Message"));
                soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);
            }
        } else {
            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.SetMaxMembers.Invalid.Message"));
            soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);
        }
    }

    @Override
    public String getName() {
        return "setmaxmembers";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.SetMaxMembers.Info.Message";
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
