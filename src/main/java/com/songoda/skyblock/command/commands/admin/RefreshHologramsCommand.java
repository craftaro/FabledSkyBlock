package com.songoda.skyblock.command.commands.admin;

import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.leaderboard.LeaderboardManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.version.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class RefreshHologramsCommand extends SubCommand {

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
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Bukkit.getScheduler().runTaskAsynchronously(skyblock, () -> {
            LeaderboardManager leaderboardManager = skyblock.getLeaderboardManager();
            leaderboardManager.clearLeaderboard();
            leaderboardManager.resetLeaderboard();
            leaderboardManager.setupLeaderHeads();

            Bukkit.getScheduler().runTask(skyblock, () -> skyblock.getHologramManager().resetHologram());
        });

        messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.RefreshHolograms.Refreshed.Message"));
        soundManager.playSound(sender, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
    }

    @Override
    public String getName() {
        return "refreshholograms";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.RefreshHolograms.Info.Message";
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
