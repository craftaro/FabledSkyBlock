package me.goodandevil.skyblock.command.commands.island;

import java.io.File;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.menus.Leaderboard;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;

public class LeaderboardCommand extends SubCommand {

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (playerDataManager.hasPlayerData(player)) {
			if (args.length == 0) {
				if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
						.getBoolean("Island.Visitor.Vote")) {
					playerDataManager.getPlayerData(player)
							.setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.Browse));
				} else {
					playerDataManager.getPlayerData(player)
							.setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.Level));
				}
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("level")) {
					playerDataManager.getPlayerData(player)
							.setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.Level));
				} else if (args[0].equalsIgnoreCase("votes")) {
					if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
							.getBoolean("Island.Visitor.Vote")) {
						playerDataManager.getPlayerData(player)
								.setViewer(new Leaderboard.Viewer(Leaderboard.Viewer.Type.Votes));
					} else {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Leaderboard.Disabled.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

						return;
					}
				} else {
					messageManager.sendMessage(player,
							configLoad.getString("Command.Island.Leaderboard.Invalid.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

					return;
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Leaderboard.Invalid.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

				return;
			}

			Leaderboard.getInstance().open(player);
			soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "leaderboard";
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public String[] getAliases() {
		return new String[] { "lb", "top" };
	}

	@Override
	public String[] getArguments() {
		return new String[] { "level", "votes" };
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
