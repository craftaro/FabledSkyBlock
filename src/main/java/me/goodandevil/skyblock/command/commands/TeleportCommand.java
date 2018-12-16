package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.island.IslandEnvironment;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandWorld;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.visit.VisitManager;

public class TeleportCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;

	public TeleportCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		VisitManager visitManager = skyblock.getVisitManager();

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (args.length == 1) {
			if (player.hasPermission("skyblock.teleport") || player.hasPermission("skyblock.*")) {
				Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
				UUID islandOwnerUUID = null;
				String targetPlayerName;

				if (targetPlayer == null) {
					OfflinePlayer targetOfflinePlayer = new OfflinePlayer(args[0]);
					islandOwnerUUID = targetOfflinePlayer.getOwner();
					targetPlayerName = targetOfflinePlayer.getName();
				} else {
					islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
					targetPlayerName = targetPlayer.getName();
				}

				if (islandOwnerUUID == null) {
					messageManager.sendMessage(player,
							configLoad.getString("Command.Island.Teleport.Island.None.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

					return;
				} else if (!islandOwnerUUID.equals(playerDataManager.getPlayerData(player).getOwner())) {
					if (visitManager.hasIsland(islandOwnerUUID)) {
						me.goodandevil.skyblock.visit.Visit visit = visitManager.getIsland(islandOwnerUUID);
						boolean isCoopPlayer = false;

						if (islandManager.containsIsland(islandOwnerUUID)) {
							if (islandManager.getIsland(islandOwnerUUID).isCoopPlayer(player.getUniqueId())) {
								isCoopPlayer = true;
							}
						}

						if (isCoopPlayer || player.hasPermission("skyblock.bypass")
								|| player.hasPermission("skyblock.bypass.*") || player.hasPermission("skyblock.*")
								|| visit.isOpen()) {
							if (!islandManager.containsIsland(islandOwnerUUID)) {
								islandManager.loadIsland(islandOwnerUUID);
							}

							islandManager.visitIsland(player, islandManager.getIsland(islandOwnerUUID));

							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Teleport.Teleported.Other.Message")
											.replace("%player", targetPlayerName));
							soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);

							return;
						} else {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Teleport.Island.Closed.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						}

						return;
					}

					messageManager.sendMessage(player,
							configLoad.getString("Command.Island.Teleport.Island.None.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

					return;
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Permission.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

				return;
			}
		} else if (args.length != 0) {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

			return;
		}

		if (islandManager.hasIsland(player)) {
			messageManager.sendMessage(player,
					configLoad.getString("Command.Island.Teleport.Teleported.Yourself.Message"));
			soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);

			me.goodandevil.skyblock.island.Island island = islandManager
					.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner());

			Bukkit.getServer().getScheduler().runTask(skyblock, new Runnable() {
				@Override
				public void run() {
					player.teleport(island.getLocation(IslandWorld.Normal, IslandEnvironment.Main));
					player.setFallDistance(0.0F);
				}
			});
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Teleport.Owner.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "teleport";
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
		return new String[] { "tp", "spawn", "home", "go", "warp" };
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
