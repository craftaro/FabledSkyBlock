package com.craftaro.skyblock.command.commands.island;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.menus.Leaderboard;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class LeaderboardCommand extends SubCommand {
    public LeaderboardCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (playerDataManager.hasPlayerData(player)) {
            if (args.length == 0) {
                if (this.plugin.getConfiguration()
                        .getBoolean("Island.Visitor.Vote")) {
                    playerDataManager.getPlayerData(player)
                            .setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.BROWSE));
                } else {
                    playerDataManager.getPlayerData(player)
                            .setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.LEVEL));
                }
            } else if (args.length == 1) {
                String type = args[0].toLowerCase();
                switch (type) {
                    case "level":
                        playerDataManager.getPlayerData(player).setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.LEVEL));
                        break;
                    case "bank":
                        playerDataManager.getPlayerData(player).setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.BANK));
                        break;
                    case "votes":
                        if (this.plugin.getConfiguration().getBoolean("Island.Visitor.Vote")) {
                            playerDataManager.getPlayerData(player).setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.VOTES));
                        } else {
                            messageManager.sendMessage(player, configLoad.getString("Command.Island.Leaderboard.Disabled.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                            return;
                        }
                        break;
                    default:
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Leaderboard.Invalid.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        return;
                }
            } else {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Leaderboard.Invalid.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                return;
            }

            Leaderboard.getInstance().open(player);
            soundManager.playSound(player, XSound.BLOCK_CHEST_OPEN);
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
