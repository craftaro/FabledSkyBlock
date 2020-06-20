package com.songoda.skyblock.command.commands.admin;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class ProxyCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        onCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);

            UUID islandOwnerUUID = targetPlayerOffline.getOwner();

            if (islandManager.getIsland(Bukkit.getOfflinePlayer(islandOwnerUUID)) != null) {
                if (islandManager.isPlayerProxyingAnotherPlayer(((Player)sender).getUniqueId())) {
                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.Proxy.IsOffPlayer.Message")
                                    .replace("%player", targetPlayerOffline.getName()));
                    soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    islandManager.removeProxyingPlayer(((Player)sender).getUniqueId());
                } else {
                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.Proxy.IsOn.Message")
                                    .replace("%player", targetPlayerOffline.getName()));
                    soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    islandManager.addProxiedPlayer(((Player)sender).getUniqueId(), targetPlayerOffline.getUniqueId());
                }
            }
        } else if (args.length == 0){
            messageManager.sendMessage(sender,
                    configLoad.getString("Command.Island.Admin.Proxy.IsOff.Message")
                            .replace("%player", ""));
            soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

            islandManager.removeProxyingPlayer(((Player)sender).getUniqueId());
        }
    }

    @Override
    public String getName() {
        return "proxy";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.Proxy.Info.Message";
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
