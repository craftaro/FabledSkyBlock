package com.songoda.skyblock.command.commands.admin;

import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.utils.version.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SetAlwaysLoadedCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        onCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (args.length == 1) {
            Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
            UUID islandOwnerUUID;

            if (targetPlayer == null) {
                OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
                islandOwnerUUID = targetPlayerOffline.getOwner();
            } else {
                islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
            }

            if (islandManager.containsIsland(islandOwnerUUID)) {
                Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));

                if (island.isAlwaysLoaded()) {
                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.SetAlwaysLoaded.IsOff.Message"));
                    soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

                    island.setAlwaysLoaded(false);
                } else {
                    messageManager.sendMessage(sender,
                            configLoad.getString("Command.Island.Admin.SetAlwaysLoaded.IsOn.Message"));
                    soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

                    island.setAlwaysLoaded(true);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "setalwaysloaded";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.SetAlwaysLoaded.Info.Message";
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
