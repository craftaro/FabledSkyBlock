package me.goodandevil.skyblock.command.commands.admin.disabled;

import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.generator.GeneratorManager;
import me.goodandevil.skyblock.hologram.Hologram;
import me.goodandevil.skyblock.hologram.HologramManager;
import me.goodandevil.skyblock.hologram.HologramType;
import me.goodandevil.skyblock.levelling.LevellingManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.scoreboard.ScoreboardManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;

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
		HologramManager hologramManager = skyblock.getHologramManager();
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		Map<String, Config> configs = fileManager.getConfigs();

		for (int i = 0; i < configs.size(); i++) {
			String configFileName = (String) configs.keySet().toArray()[i];
			Config configFileConfig = configs.get(configFileName);
			String configFilePath = configFileName.replace(configFileConfig.getFile().getName(), "");

			if (configFilePath.equals(skyblock.getDataFolder().toString() + "\\")
					|| configFilePath.equals(skyblock.getDataFolder().toString() + "/")) {
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

		LevellingManager levellingManager = skyblock.getLevellingManager();
		levellingManager.unregisterMaterials();
		levellingManager.registerMaterials();

		Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
			@Override
			public void run() {
				for (HologramType hologramTypeList : HologramType.values()) {
					Hologram hologram = hologramManager.getHologram(hologramTypeList);

					if (hologram != null) {
						hologramManager.removeHologram(hologram);
					}

					hologramManager.spawnHologram(hologramTypeList);
				}
			}
		});

		messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Reload.Reloaded.Message"));
		soundManager.playSound(sender, Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
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
