package me.goodandevil.skyblock.command.commands.island;

import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.menus.Coop;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class CoopCommand extends SubCommand {

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		Island island = islandManager.getIsland(player);

		if (island == null) {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Owner.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		} else if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
				.getBoolean("Island.Coop.Enable")) {
			if (island.hasRole(IslandRole.Owner, player.getUniqueId())
					|| (island.hasRole(IslandRole.Operator, player.getUniqueId())
							&& island.getSetting(IslandRole.Operator, "CoopPlayers").getStatus())) {
				if (args.length == 1) {
					Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);

					UUID targetPlayerUUID = null;
					String targetPlayerName = null;

					if (targetPlayer == null) {
						OfflinePlayer offlinePlayer = new OfflinePlayer(args[0]);
						targetPlayerUUID = offlinePlayer.getUniqueId();
						targetPlayerName = offlinePlayer.getName();

						if (targetPlayerUUID != null && !Bukkit.getOfflinePlayer(targetPlayerUUID).hasPlayedBefore()) {
							targetPlayerUUID = null;
							targetPlayerName = null;
						}
					} else {
						targetPlayerUUID = targetPlayer.getUniqueId();
						targetPlayerName = targetPlayer.getName();
					}

					if (targetPlayerUUID == null) {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Found.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else if (targetPlayerUUID.equals(player.getUniqueId())) {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Coop.Yourself.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else if (island.hasRole(IslandRole.Member, targetPlayerUUID)
							|| island.hasRole(IslandRole.Operator, targetPlayerUUID)
							|| island.hasRole(IslandRole.Owner, targetPlayerUUID)) {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Member.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else if (island.getBan().isBanned(targetPlayerUUID)) {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Banned.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else if (island.isCoopPlayer(targetPlayerUUID)) {
						if (targetPlayer != null) {
							if (islandManager.getVisitorsAtIsland(island).contains(targetPlayerUUID)) {
								if (!island.isOpen()) {
									LocationUtil.teleportPlayerToSpawn(targetPlayer);

									messageManager.sendMessage(targetPlayer,
											configLoad.getString("Command.Island.Coop.Removed.Target.Message"));
									soundManager.playSound(targetPlayer, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F,
											1.0F);
								}
							}
						}

						island.removeCoopPlayer(targetPlayerUUID);

						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Coop.Removed.Sender.Message").replace("%player",
										targetPlayerName));
						soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
					} else {
						island.addCoopPlayer(targetPlayerUUID);

						messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Added.Message")
								.replace("%player", targetPlayerName));
						soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
					}

					return;
				} else if (args.length != 0) {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Invalid.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

					return;
				}

				Coop.getInstance().open(player);
				soundManager.playSound(player, Sounds.CHEST_OPEN.bukkitSound(), 1.0F, 1.0F);
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Permission.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Coop.Disabled.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "coop";
	}

	@Override
	public String getInfoMessagePath() {
		return "Command.Island.Coop.Info.Message";
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
