package com.craftaro.skyblock.command.commands.admin;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.config.FileManager.Config;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class OwnerCommand extends SubCommand {
    public OwnerCommand(SkyBlock plugin) {
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
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
            UUID targetPlayerUUID, islandOwnerUUID;
            String targetPlayerName, islandOwnerName;

            if (targetPlayer == null) {
                OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
                targetPlayerUUID = targetPlayerOffline.getUniqueId();
                islandOwnerUUID = targetPlayerOffline.getOwner();
                targetPlayerName = targetPlayerOffline.getName();
            } else {
                targetPlayerUUID = targetPlayer.getUniqueId();
                islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
                targetPlayerName = targetPlayer.getName();
            }

            if (islandOwnerUUID == null) {
                messageManager.sendMessage(sender,
                        configLoad.getString("Command.Island.Admin.Owner.Island.None.Message"));
                soundManager.playSound(sender, XSound.ENTITY_VILLAGER_NO);
            } else if (islandOwnerUUID.equals(targetPlayerUUID)) {
                messageManager.sendMessage(sender,
                        configLoad.getString("Command.Island.Admin.Owner.Island.Owner.Message").replace("%player",
                                targetPlayerName));
                soundManager.playSound(sender, XSound.ENTITY_VILLAGER_YES);
            } else {
                targetPlayer = Bukkit.getServer().getPlayer(islandOwnerUUID);

                if (targetPlayer == null) {
                    islandOwnerName = new OfflinePlayer(islandOwnerUUID).getName();
                } else {
                    islandOwnerName = targetPlayer.getName();
                }

                messageManager.sendMessage(sender,
                        configLoad.getString("Command.Island.Admin.Owner.Island.Member.Message")
                                .replace("%player", targetPlayerName).replace("%owner", islandOwnerName));
                soundManager.playSound(sender, XSound.ENTITY_VILLAGER_YES);
            }
        } else {
            messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Owner.Invalid.Message"));
            soundManager.playSound(sender, XSound.BLOCK_ANVIL_LAND);
        }
    }

    @Override
    public String getName() {
        return "owner";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.Owner.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ownership", "leader"};
    }

    @Override
    public String[] getArguments() {
        return new String[0];
    }
}
