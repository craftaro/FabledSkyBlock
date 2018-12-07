package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.ban.Ban;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;

public class UnbanCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;

	public UnbanCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		PlayerData playerData = playerDataManager.getPlayerData(player);

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (args.length == 1) {
			if (islandManager.hasIsland(player)) {
				if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
						.getBoolean("Island.Visitor.Banning")) {
					me.goodandevil.skyblock.island.Island island = islandManager.getIsland(playerData.getOwner());

					if (island.hasRole(IslandRole.Owner, player.getUniqueId())
							|| (island.hasRole(IslandRole.Operator, player.getUniqueId())
									&& island.getSetting(IslandRole.Operator, "Unban").getStatus())) {
						Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);

						UUID targetPlayerUUID = null;
						String targetPlayerName = null;

						if (targetPlayer == null) {
							OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
							targetPlayerUUID = targetPlayerOffline.getUniqueId();
							targetPlayerName = targetPlayerOffline.getName();
						} else {
							targetPlayerUUID = targetPlayer.getUniqueId();
							targetPlayerName = targetPlayer.getName();
						}

						if (targetPlayerUUID == null) {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Unban.Found.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else if (targetPlayerUUID.equals(player.getUniqueId())) {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Unban.Yourself.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else if (island.hasRole(IslandRole.Member, targetPlayerUUID)
								|| island.hasRole(IslandRole.Operator, targetPlayerUUID)
								|| island.hasRole(IslandRole.Owner, targetPlayerUUID)) {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Unban.Member.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else if (!island.getBan().isBanned(targetPlayerUUID)) {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Unban.Banned.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Unban.Unbanned.Message").replace("%player",
											targetPlayerName));
							soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);

							Ban ban = island.getBan();
							ban.removeBan(targetPlayerUUID);
							ban.save();
						}
					} else {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Unban.Permission.Message"));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					}
				} else {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Unban.Disabled.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Unban.Owner.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Unban.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "unban";
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
		return CommandManager.Type.Default;
	}
}
