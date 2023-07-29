package com.songoda.skyblock.command.commands.admin;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.leaderboard.LeaderboardManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class RefreshHologramsCommand extends SubCommand {
    public RefreshHologramsCommand(SkyBlock plugin) {
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
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            LeaderboardManager leaderboardManager = this.plugin.getLeaderboardManager();
            leaderboardManager.clearLeaderboard();
            leaderboardManager.resetLeaderboard();
            leaderboardManager.setupLeaderHeads();

            Bukkit.getScheduler().runTask(this.plugin, () -> this.plugin.getHologramTask().updateHologram());
        });

        messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.RefreshHolograms.Refreshed.Message"));
        soundManager.playSound(sender, XSound.BLOCK_NOTE_BLOCK_PLING);
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
