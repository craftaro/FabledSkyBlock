package me.goodandevil.skyblock.command.commands.admin;

import java.io.File;
import java.util.UUID;

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
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;

public class OwnerCommand extends SubCommand {

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		onCommand(player, args);
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		onCommand(sender, args);
	}

	public void onCommand(CommandSender sender, String[] args) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		MessageManager messageManager = skyblock.getMessageManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		Player player = null;

		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (player == null || player.hasPermission("fabledskyblock.admin.owner") || player.hasPermission("fabledskyblock.admin.*")
				|| player.hasPermission("fabledskyblock.*")) {
			if (args.length == 1) {
				Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
				UUID targetPlayerUUID, islandOwnerUUID;
				String targetPlayerName, islandOwnerName;

				if (targetPlayer == null) {
					OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
					targetPlayerUUID = targetPlayerOffline.getUniqueId();
					islandOwnerUUID = targetPlayerOffline.getOwner();
					targetPlayerName = targetPlayerOffline.getName();
				} else {
					targetPlayerUUID = targetPlayer.getUniqueId();
					islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
					targetPlayerName = targetPlayer.getName();
				}

				if (islandOwnerUUID == null) {
					messageManager.sendMessage(sender,
							configLoad.getString("Command.Island.Admin.Owner.Island.None.Message"));
					soundManager.playSound(sender, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				} else if (islandOwnerUUID.equals(targetPlayerUUID)) {
					messageManager.sendMessage(sender,
							configLoad.getString("Command.Island.Admin.Owner.Island.Owner.Message").replace("%player",
									targetPlayerName));
					soundManager.playSound(sender, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
				} else {
					targetPlayer = Bukkit.getServer().getPlayer(islandOwnerUUID);

					if (targetPlayer == null) {
						islandOwnerName = new OfflinePlayer(islandOwnerUUID).getName();
					} else {
						islandOwnerName = targetPlayer.getName();
					}

					messageManager.sendMessage(sender,
							configLoad.getString("Command.Island.Admin.Owner.Island.Member.Message")
									.replace("%player", targetPlayerName).replace("%owner", islandOwnerName));
					soundManager.playSound(sender, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Owner.Invalid.Message"));
				soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Owner.Permission.Message"));
			soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "owner";
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public String[] getAliases() {
		return new String[] { "ownership", "leader" };
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
