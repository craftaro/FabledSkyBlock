package com.songoda.skyblock.command.commands.island;

import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.menus.Leaderboard;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.version.Sounds;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class LeaderboardCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        MessageManager messageManager = skyblock.getMessageManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (playerDataManager.hasPlayerData(player)) {
            if (args.length == 0) {
                if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                        .getBoolean("Island.Visitor.Vote")) {
                    playerDataManager.getPlayerData(player)
                            .setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.Browse));
                } else {
                    playerDataManager.getPlayerData(player)
                            .setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.Level));
                }
            } else if (args.length == 1) {
                String type = args[0].toLowerCase();
                switch (type) {
                    case "level":
                        playerDataManager.getPlayerData(player).setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.Level));
                        break;
                    case "bank":
                        playerDataManager.getPlayerData(player).setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.Bank));
                        break;
                    case "votes":
                        if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
                            playerDataManager.getPlayerData(player).setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.Votes));
                        } else {
                            messageManager.sendMessage(player, configLoad.getString("Command.Island.Leaderboard.Disabled.Message"));
                            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                            return;
                        }
                        break;
                    default:
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Leaderboard.Invalid.Message"));
                        soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
                        return;
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Leaderboard.Invalid.Message"));
                soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

                return;
            }

            Leaderboard.getInstance().open(player);
            soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "leaderboard";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Leaderboard.Info.Message";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"lb", "top"};
    }

    @Override
    public String[] getArguments() {
        return new String[]{"level", "bank", "votes"};
    }
}
