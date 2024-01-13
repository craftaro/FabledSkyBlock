package com.craftaro.skyblock.command.commands.admin;

import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.command.SubCommand;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.config.FileManager.Config;
import com.craftaro.skyblock.generator.GeneratorManager;
import com.craftaro.skyblock.island.reward.RewardManager;
import com.craftaro.skyblock.leaderboard.LeaderboardManager;
import com.craftaro.skyblock.levelling.IslandLevelManager;
import com.craftaro.skyblock.limit.LimitationInstanceHandler;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.scoreboard.ScoreboardManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.item.MenuClickRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;

public class ReloadCommand extends SubCommand {
    public ReloadCommand(SkyBlock plugin) {
        super(plugin);
    }

    @Override
    public void onCommandByPlayer(Player player, String[] args) {
        onCommand(player);
    }

    @Override
    public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
        onCommand(sender);
    }

    public void onCommand(CommandSender sender) {
        LeaderboardManager leaderboardManager = this.plugin.getLeaderboardManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        LimitationInstanceHandler limitHandler = this.plugin.getLimitationHandler();
        FileManager fileManager = this.plugin.getFileManager();

        messageManager.sendMessage(sender,
                "&cPlease note that this command is not supported and may " + "cause issues that could put the plugin in an unstable state. " + "If you encounter any issues please stop your server, edit the configuration files, "
                        + "and then start your server again. This command does NOT reload all the plugin files, only " + "the config.yml, language.yml, generators.yml, levelling.yml, and limits.yml.");

        Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        Map<String, Config> configs = fileManager.getConfigs();

        for (int i = 0; i < configs.size(); ++i) {
            String configFileName = (String) configs.keySet().toArray()[i];
            Config configFileConfig = configs.get(configFileName);
            String configFilePath = configFileName.replace(configFileConfig.getFile().getName(), "");

            if (configFilePath.equals(this.plugin.getDataFolder() + "\\") || configFilePath.equals(this.plugin.getDataFolder() + "/")) {
                configFileConfig.loadFile();
            }
        }

        Config mainConfig = fileManager.getConfig(new File(this.plugin.getDataFolder(), "config.yml"));
        FileConfiguration mainConfigLoad = mainConfig.getFileConfiguration();

        if (this.plugin.getScoreboardManager() == null) {
            if (mainConfigLoad.getBoolean("Island.Scoreboard.Enable")) {
                this.plugin.setScoreboardManager(new ScoreboardManager(this.plugin));
            }
        } else {
            this.plugin.getScoreboardManager().reload();
        }

        if (this.plugin.getGeneratorManager() == null) {
            if (mainConfigLoad.getBoolean("Island.Generator.Enable")) {
                this.plugin.setGeneratorManager(new GeneratorManager(this.plugin));
            }
        } else {
            GeneratorManager generatorManager = this.plugin.getGeneratorManager();
            generatorManager.unregisterGenerators();
            generatorManager.registerGenerators();
        }

        IslandLevelManager levellingManager = this.plugin.getLevellingManager();
        levellingManager.reloadWorth();

        RewardManager rewardManager = this.plugin.getRewardManager();
        rewardManager.loadRewards();

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            leaderboardManager.clearLeaderboard();
            leaderboardManager.resetLeaderboard();

            Bukkit.getScheduler().runTask(this.plugin, () -> this.plugin.getHologramTask().updateHologram());
        });

        limitHandler.reloadAll();
        this.plugin.getLocalizationManager().reloadAll();
        MenuClickRegistry.getInstance().reloadAll();

        messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Reload.Reloaded.Message"));
        soundManager.playSound(sender, XSound.BLOCK_ANVIL_USE);
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
