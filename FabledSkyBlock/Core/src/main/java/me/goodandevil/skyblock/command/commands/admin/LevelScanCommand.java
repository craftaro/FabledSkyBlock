package me.goodandevil.skyblock.command.commands.admin;

import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.levelling.LevellingManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class LevelScanCommand extends SubCommand {

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		this.onCommand(player, args);
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		this.onCommand(sender, args);
	}

	private void onCommand(CommandSender sender, String[] args) {
		LevellingManager levellingManager = skyblock.getLevellingManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		FileManager.Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (args.length == 0) {
			messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.LevelScan.Invalid.Message"));
			soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			return;
		}

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
		Island island = islandManager.getIsland(offlinePlayer);

		if (island == null) {
			messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.LevelScan.NoIsland.Message"));
			soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			return;
		}

		levellingManager.calculatePoints(sender instanceof Player ? (Player) sender : null, island);

		messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.LevelScan.Started.Message"));
		soundManager.playSound(sender, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
	}

	@Override
	public String getName() {
		return "levelscan";
	}

	@Override
	public String getInfoMessagePath() {
		return "Command.Island.Admin.LevelScan.Info.Message";
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
