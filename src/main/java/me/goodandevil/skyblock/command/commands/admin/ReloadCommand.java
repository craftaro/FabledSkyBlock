package me.goodandevil.skyblock.command.commands.admin;

import java.io.File;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
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

public class ReloadCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;

	public ReloadCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
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
		HologramManager hologramManager = skyblock.getHologramManager();
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		Player player = null;

		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (player == null || player.hasPermission("skyblock.admin.reload") || player.hasPermission("skyblock.admin.*")
				|| player.hasPermission("skyblock.*")) {
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
		} else {
			messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Reload.Permission.Message"));
			soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public SubCommand setInfo(String info) {
		this.info = info;

		return this;
	}

	@Override
	public String[] getAliases() {
		return new String[0];
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Admin;
	}
}
