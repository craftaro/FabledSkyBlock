package com.songoda.skyblock.command.commands.admin;

import com.eatthepath.uuid.FastUUID;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.upgrade.Upgrade;
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

public class AddUpgradeCommand extends SubCommand {

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
    
            Upgrade.Type upgrade = null;
            for(Upgrade.Type type : Upgrade.Type.values()) {
                if(type.name().toUpperCase().equals(args[1].toUpperCase())) {
                    upgrade = type;
                    break;
                }
            }

            if (islandOwnerUUID == null) {
                messageManager.sendMessage(sender,
                        configLoad.getString("Command.Island.Admin.AddUpgrade.Island.Owner.Message"));
                soundManager.playSound(sender,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            } else if (upgrade == null) {
                messageManager.sendMessage(sender,
                        configLoad.getString("Command.Island.Admin.AddUpgrade.Upgrade.Exist.Message"));
                soundManager.playSound(sender,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
            } else {
                if (islandManager.containsIsland(islandOwnerUUID)) {
                    Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));

                    if (island.hasUpgrade(upgrade)) {
                        messageManager.sendMessage(sender,
                                configLoad.getString("Command.Island.Admin.AddUpgrade.Upgrade.Already.Message"));
                        soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        return;
                    }

                    island.setUpgrade(null, upgrade, true);
                } else {
                    File islandDataFile = new File(plugin.getDataFolder().toString() + "/island-data",
                            FastUUID.toString(islandOwnerUUID) + ".yml");

                    if (!fileManager.isFileExist(islandDataFile)) {
                        messageManager.sendMessage(sender,
                                configLoad.getString("Command.Island.Admin.AddUpgrade.Island.Data.Message"));
                        soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        return;
                    }

                    FileConfiguration islandDataConfigLoad = YamlConfiguration.loadConfiguration(islandDataFile);

                    if (islandDataConfigLoad.getString("Upgrade." + upgrade.name()) != null) {
                        messageManager.sendMessage(sender,
                                configLoad.getString("Command.Island.Admin.AddUpgrade.Upgrade.Already.Message"));
                        soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        return;
                    }

                    islandDataConfigLoad.set("Upgrade." + upgrade.name(), true);

                    try {
                        islandDataConfigLoad.save(islandDataFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                messageManager.sendMessage(sender,
                        configLoad.getString("Command.Island.Admin.AddUpgrade.Added.Message")
                                .replace("%player", targetPlayerName).replace("%upgrade", upgrade.name()));
                soundManager.playSound(sender, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
            }
        } else {
            messageManager.sendMessage(sender,
                    configLoad.getString("Command.Island.Admin.AddUpgrade.Invalid.Message"));
            soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public String getName() {
        return "addupgrade";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.AddUpgrade.Info.Message";
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
