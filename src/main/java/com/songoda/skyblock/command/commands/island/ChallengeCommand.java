package com.songoda.skyblock.command.commands.island;

import java.io.File;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.songoda.skyblock.challenge.FabledChallenge;
import com.songoda.skyblock.challenge.challenge.Challenge;
import com.songoda.skyblock.challenge.challenge.ChallengeCategory;
import com.songoda.skyblock.command.SubCommand;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.version.Sounds;

public class ChallengeCommand extends SubCommand {

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();
		FabledChallenge fabledChallenge = skyblock.getFabledChallenge();

		Config langConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration langConfigLoad = langConfig.getFileConfiguration();

		// Not loaded
		if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
				.getBoolean("Island.Challenge.Enable")) {
			messageManager.sendMessage(player, langConfigLoad.getString("Command.Island.Challenge.Disabled.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			return;
		}
		if (args.length == 0) {
			// Open challenge inventory
			ChallengeCategory cc = fabledChallenge.getChallengeManager().getChallenge(1);
			if (cc == null) {
				messageManager.sendMessage(player,
						langConfigLoad.getString("Command.Island.Challenge.NotFound.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				return;
			}
			fabledChallenge.openChallengeInventory(player, fabledChallenge.getChallengeManager().getChallenge(1));
			return;
		}
		if (args.length == 2) {
			// Complete a challenge
			int ccId = 0;
			int cId = 0;
			try {
				ccId = Integer.parseInt(args[0]);
				cId = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				messageManager.sendMessage(player,
						langConfigLoad.getString("Command.Island.Challenge.Invalid.Message"));
				soundManager.playSound(player, Sounds.VILLAGER_HIT.bukkitSound(), 1.0F, 1.0F);
				return;
			}
			ChallengeCategory cc = fabledChallenge.getChallengeManager().getChallenge(ccId);
			if (cc == null) {
				messageManager.sendMessage(player,
						langConfigLoad.getString("Command.Island.Challenge.CategoryNotFound.Message"));
				soundManager.playSound(player, Sounds.VILLAGER_HIT.bukkitSound(), 1.0F, 1.0F);
				return;
			}
			Challenge c = cc.getChallenge(cId);
			if (c == null) {
				messageManager.sendMessage(player,
						langConfigLoad.getString("Command.Island.Challenge.ChallengeNotFound.Message"));
				soundManager.playSound(player, Sounds.VILLAGER_HIT.bukkitSound(), 1.0F, 1.0F);
				return;
			}
			if (fabledChallenge.getPlayerManager().doChallenge(player, c))
				// Ok
				soundManager.playSound(player, Sounds.LEVEL_UP.bukkitSound(), 1.0F, 1.0F);
			else
				soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "challenge";
	}

	@Override
	public String getInfoMessagePath() {
		return "Command.Island.Challenge.Info.Message";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "c" };
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}
}
