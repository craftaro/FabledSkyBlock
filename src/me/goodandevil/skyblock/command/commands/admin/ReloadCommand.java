package me.goodandevil.skyblock.command.commands.admin;

import java.io.File;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class ReloadCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public ReloadCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		MessageManager messageManager = plugin.getMessageManager();
		SoundManager soundManager = plugin.getSoundManager();
		FileManager fileManager = plugin.getFileManager();
		
		Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (player.hasPermission("skyblock.admin.reload") || player.hasPermission("skyblock.admin.*") || player.hasPermission("skyblock.*")) {
			Map<String, Config> configs = fileManager.getConfigs();
			
			for (int i = 0; i < configs.size(); i++) {
				String configFileName = (String) configs.keySet().toArray()[i];
				Config configFileConfig = configs.get(configFileName);
				String configFilePath = configFileName.replace(configFileConfig.getFile().getName(), "");
				
				if (configFilePath.equals(plugin.getDataFolder().toString() + "\\") || configFilePath.equals(plugin.getDataFolder().toString() + "/") ) {
					configFileConfig.loadFile();
				}
			}
			
			if (plugin.getScoreboardManager() != null) {
				plugin.getScoreboardManager().resendScoreboard();
			}
			
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Reload.Reloaded.Message"));
			soundManager.playSound(player, Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Reload.Permission.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
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
	public Type getType() {
		return CommandManager.Type.Admin;
	}
}
