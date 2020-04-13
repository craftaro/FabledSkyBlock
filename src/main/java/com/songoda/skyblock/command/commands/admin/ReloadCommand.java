package com.songoda.skyblock.command.commands.admin;

import java.io.File;
import java.util.Map;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.island.reward.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.generator.GeneratorManager;
import com.songoda.skyblock.leaderboard.LeaderboardManager;
import com.songoda.skyblock.levelling.rework.IslandLevelManager;
import com.songoda.skyblock.limit.LimitationInstanceHandler;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.scoreboard.ScoreboardManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.item.MenuClickRegistry;

public class ReloadCommand extends SubCommand {

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        onCommand(player, args);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        onCommand(sender, args);
    }

    public void onCommand(CommandSender sender, String[] args) {
        LeaderboardManager leaderboardManager = skyblock.getLeaderboardManager();
        MessageManager messageManager = skyblock.getMessageManager();
        SoundManager soundManager = skyblock.getSoundManager();
        LimitationInstanceHandler limitHandler = skyblock.getLimitationHandler();
        FileManager fileManager = skyblock.getFileManager();

        messageManager.sendMessage(sender,
                "&cPlease note that this command is not supported and may " + "cause issues that could put the plugin in an unstable state. " + "If you encounter any issues please stop your server, edit the configuration files, "
                        + "and then start your server again. This command does NOT reload all the plugin files, only " + "the config.yml, language.yml, generators.yml, levelling.yml, and limits.yml.");

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Map<String, Config> configs = fileManager.getConfigs();

        for (int i = 0; i < configs.size(); i++) {
            String configFileName = (String) configs.keySet().toArray()[i];
            Config configFileConfig = configs.get(configFileName);
            String configFilePath = configFileName.replace(configFileConfig.getFile().getName(), "");

            if (configFilePath.equals(skyblock.getDataFolder().toString() + "\\") || configFilePath.equals(skyblock.getDataFolder().toString() + "/")) {
                configFileConfig.loadFile();
            }
        }

        Config mainConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration mainConfigLoad = mainConfig.getFileConfiguration();

        if (skyblock.getScoreboardManager() == null) {
            if (mainConfigLoad.getBoolean("Island.Scoreboard.Enable")) {
                skyblock.setScoreboardManager(new ScoreboardManager(skyblock));
            }
        } else {
            skyblock.getScoreboardManager().resendScoreboard();
        }

        if (skyblock.getGeneratorManager() == null) {
            if (mainConfigLoad.getBoolean("Island.Generator.Enable")) {
                skyblock.setGeneratorManager(new GeneratorManager(skyblock));
            }
        } else {
            GeneratorManager generatorManager = skyblock.getGeneratorManager();
            generatorManager.unregisterGenerators();
            generatorManager.registerGenerators();
        }

        IslandLevelManager levellingManager = skyblock.getLevellingManager();
        levellingManager.reloadWorth();

        RewardManager rewardManager = skyblock.getRewardManager();
        rewardManager.loadRewards();

        Bukkit.getScheduler().runTaskAsynchronously(skyblock, () -> {
            leaderboardManager.clearLeaderboard();
            leaderboardManager.resetLeaderboard();
            leaderboardManager.setupLeaderHeads();

            Bukkit.getScheduler().runTask(skyblock, () -> skyblock.getHologramTask().updateHologram());
        });

        limitHandler.reloadAll();
        skyblock.getLocalizationManager().reloadAll();
        MenuClickRegistry.getInstance().reloadAll();

        messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Reload.Reloaded.Message"));
        soundManager.playSound(sender, CompatibleSound.BLOCK_ANVIL_USE.getSound(), 1.0F, 1.0F);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getInfoMessagePath() {
        return "Command.Island.Admin.Reload.Info.Message";
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
