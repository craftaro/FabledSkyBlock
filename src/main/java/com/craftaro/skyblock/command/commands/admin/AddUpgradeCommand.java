package com.craftaro.skyblock.command.commands.admin;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.config.FileManager.Config;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.upgrade.Upgrade;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import com.eatthepath.uuid.FastUUID;
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
    public AddUpgradeCommand(SkyBlock plugin) {
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
            for (Upgrade.Type type : Upgrade.Type.values()) {
                if (type.name().equalsIgnoreCase(args[1])) {
                    upgrade = type;
                    break;
                }
            }

            if (islandOwnerUUID == null) {
                messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.AddUpgrade.Island.Owner.Message"));
                soundManager.playSound(sender, XSound.ENTITY_VILLAGER_NO);
            } else if (upgrade == null) {
                messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.AddUpgrade.Upgrade.Exist.Message"));
                soundManager.playSound(sender, XSound.ENTITY_VILLAGER_NO);
            } else {
                if (islandManager.containsIsland(islandOwnerUUID)) {
                    Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));

                    if (island.hasUpgrade(upgrade)) {
                        messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.AddUpgrade.Upgrade.Already.Message"));
                        soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);

                        return;
                    }

                    island.setUpgrade(null, upgrade, true);
                } else {
                    File islandDataFile = new File(this.plugin.getDataFolder() + "/island-data", FastUUID.toString(islandOwnerUUID) + ".yml");

                    if (!fileManager.isFileExist(islandDataFile)) {
                        messageManager.sendMessage(sender,
                                configLoad.getString("Command.Island.Admin.AddUpgrade.Island.Data.Message"));
                        soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);

                        return;
                    }

                    FileConfiguration islandDataConfigLoad = YamlConfiguration.loadConfiguration(islandDataFile);

                    if (islandDataConfigLoad.getString("Upgrade." + upgrade.name()) != null) {
                        messageManager.sendMessage(sender,
                                configLoad.getString("Command.Island.Admin.AddUpgrade.Upgrade.Already.Message"));
                        soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);

                        return;
                    }

                    islandDataConfigLoad.set("Upgrade." + upgrade.name(), true);

                    try {
                        islandDataConfigLoad.save(islandDataFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                messageManager.sendMessage(sender,
                        configLoad.getString("Command.Island.Admin.AddUpgrade.Added.Message")
                                .replace("%player", targetPlayerName).replace("%upgrade", upgrade.name()));
                soundManager.playSound(sender, XSound.BLOCK_NOTE_BLOCK_PLING);
            }
        } else {
            messageManager.sendMessage(sender,
                    configLoad.getString("Command.Island.Admin.AddUpgrade.Invalid.Message"));
            soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);
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
