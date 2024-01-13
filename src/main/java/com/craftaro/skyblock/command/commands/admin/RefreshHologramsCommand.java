package com.craftaro.skyblock.command.commands.admin;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.config.FileManager.Config;
import com.craftaro.skyblock.leaderboard.LeaderboardManager;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
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
