package me.goodandevil.skyblock.command.commands.admin;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class DeleteCommand extends SubCommand {

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		onCommand(player, args);
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		onCommand(sender, args);
	}

	public void onCommand(CommandSender sender, String[] args) {
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		Player player = null;

		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (player == null || player.hasPermission("fabledskyblock.admin.delete") || player.hasPermission("fabledskyblock.admin.*")
				|| player.hasPermission("fabledskyblock.*")) {
			if (args.length == 1) {
				Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
				UUID targetPlayerUUID;
				String targetPlayerName;

				if (targetPlayer == null) {
					OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
					targetPlayerUUID = targetPlayerOffline.getUniqueId();
					targetPlayerName = targetPlayerOffline.getName();
				} else {
					targetPlayerUUID = targetPlayer.getUniqueId();
					targetPlayerName = targetPlayer.getName();
				}

				if (targetPlayerUUID == null || !islandManager.isIslandExist(targetPlayerUUID)) {
					messageManager.sendMessage(sender,
							configLoad.getString("Command.Island.Admin.Delete.Owner.Message"));
					soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				} else {
					Island island = islandManager.loadIsland(Bukkit.getServer().getOfflinePlayer(targetPlayerUUID));
					Location spawnLocation = LocationUtil.getSpawnLocation();

					if (spawnLocation != null && islandManager.isLocationAtIsland(island, spawnLocation)) {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Admin.Delete.Spawn.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

						islandManager.unloadIsland(island, null);

						return;
					}

					for (Player all : Bukkit.getOnlinePlayers()) {
						if (island.hasRole(IslandRole.Member, all.getUniqueId())
								|| island.hasRole(IslandRole.Operator, all.getUniqueId())) {
							all.sendMessage(ChatColor.translateAlternateColorCodes('&',
									configLoad.getString("Command.Island.Confirmation.Deletion.Broadcast.Message")));
							soundManager.playSound(all, Sounds.EXPLODE.bukkitSound(), 10.0F, 10.0F);
						}
					}

					island.setDeleted(true);
					islandManager.deleteIsland(island);

					messageManager.sendMessage(sender,
							configLoad.getString("Command.Island.Admin.Delete.Deleted.Message").replace("%player",
									targetPlayerName));
					soundManager.playSound(sender, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				messageManager.sendMessage(sender, configLoad.getString("Command.Island.Admin.Delete.Invalid.Message"));
				soundManager.playSound(sender, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Admin.Delete.Permission.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "delete";
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public String[] getAliases() {
		return new String[] { "remove", "disband" };
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
