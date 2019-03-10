package me.goodandevil.skyblock.command.commands.admin;

import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.hologram.Hologram;
import me.goodandevil.skyblock.hologram.HologramManager;
import me.goodandevil.skyblock.hologram.HologramType;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class SetHologramCommand extends SubCommand {

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		HologramManager hologramManager = skyblock.getHologramManager();
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (args.length == 1) {
			HologramType hologramType = null;

			if (args[0].equalsIgnoreCase("Level")) {
				hologramType = HologramType.Level;
			} else if (args[0].equalsIgnoreCase("Votes")) {
				hologramType = HologramType.Votes;
			}

			if (hologramType != null) {
				fileManager.setLocation(
						fileManager.getConfig(new File(skyblock.getDataFolder(), "locations.yml")),
						"Location.Hologram.Leaderboard." + hologramType.name(), player.getLocation(), true);

				Bukkit.getServer().getScheduler().runTask(skyblock, () -> {
					HologramType hologramType1 = HologramType
							.valueOf(WordUtils.capitalize(args[0].toLowerCase()));
					Hologram hologram = hologramManager.getHologram(hologramType1);

					if (hologram != null) {
						hologramManager.removeHologram(hologram);
					}

					hologramManager.spawnHologram(hologramType1);
				});

				messageManager.sendMessage(player,
						configLoad.getString("Command.Island.Admin.SetHologram.Set.Message").replace("%type",
								hologramType.name()));
				soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

				return;
			}
		}

		messageManager.sendMessage(player,
				configLoad.getString("Command.Island.Admin.SetHologram.Invalid.Message"));
		soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "sethologram";
	}

	@Override
	public String getInfoMessagePath() {
		return "Command.Island.Admin.SetHologram.Info.Message";
	}

	@Override
	public String[] getAliases() {
		return new String[0];
	}

	@Override
	public String[] getArguments() {
		return new String[] { "level", "votes" };
	}
}
