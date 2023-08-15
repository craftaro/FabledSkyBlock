package com.craftaro.skyblock.command.commands.island;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.scoreboard.ScoreboardManager;
import com.craftaro.skyblock.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class ScoreboardCommand extends SubCommand {
    public ScoreboardCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        MessageManager messageManager = this.plugin.getMessageManager();
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        ScoreboardManager scoreboardManager = this.plugin.getScoreboardManager();

        FileManager.Config config = this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        PlayerData playerData = playerDataManager.getPlayerData(player);

        if (playerData == null) {
            messageManager.sendMessage(player, configLoad.getString("Command.Scoreboard.Error.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else if (!this.plugin.getFileManager().getConfig(new File(this.plugin.getDataFolder(), "config.yml"))
                .getFileConfiguration().getBoolean("Island.Scoreboard.Enable", false)) {
            messageManager.sendMessage(player, configLoad.getString("Command.Scoreboard.GlobalDisable.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
        } else if (playerData.isScoreboard()) {
            playerData.setScoreboard(false);
            scoreboardManager.addDisabledPlayer(player);

            messageManager.sendMessage(player, configLoad.getString("Command.Scoreboard.Disabled.Message"));
            soundManager.playSound(player, XSound.BLOCK_WOODEN_DOOR_CLOSE);
        } else {
            playerData.setScoreboard(true);
            scoreboardManager.removeDisabledPlayer(player);
            Bukkit.getScheduler().runTask(this.plugin, () -> scoreboardManager.updatePlayerScoreboardType(player));

            messageManager.sendMessage(player, configLoad.getString("Command.Scoreboard.Enabled.Message"));
            soundManager.playSound(player, XSound.BLOCK_WOODEN_DOOR_OPEN);
        }
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
    }

    @Override
    public String getName() {
        return "scoreboard";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Scoreboard.Info.Message";
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
