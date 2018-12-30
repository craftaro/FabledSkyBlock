package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.ban.Ban;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;

public class BanCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;

	public BanCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (args.length == 1) {
			Island island = islandManager.getIsland(player);

			if (island == null) {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Owner.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			} else if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
					.getBoolean("Island.Visitor.Banning")) {
				if (island.hasRole(IslandRole.Owner, player.getUniqueId())
						|| (island.hasRole(IslandRole.Operator, player.getUniqueId())
								&& island.getSetting(IslandRole.Operator, "Ban").getStatus())) {
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
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Found.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else if (targetPlayerUUID.equals(player.getUniqueId())) {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Yourself.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else if (island.hasRole(IslandRole.Member, targetPlayerUUID)
							|| island.hasRole(IslandRole.Operator, targetPlayerUUID)
							|| island.hasRole(IslandRole.Owner, targetPlayerUUID)) {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Member.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else if (island.getBan().isBanned(targetPlayerUUID)) {
						messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Already.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					} else {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Ban.Banned.Sender.Message").replace("%player",
										targetPlayerName));
						soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);

						if (island.isCoopPlayer(targetPlayerUUID)) {
							island.removeCoopPlayer(targetPlayerUUID);
						}

						Ban ban = island.getBan();
						ban.addBan(player.getUniqueId(), targetPlayerUUID);
						ban.save();

						if (targetPlayer != null) {
							if (islandManager.isPlayerAtIsland(island, targetPlayer)) {
								messageManager.sendMessage(targetPlayer,
										configLoad.getString("Command.Island.Ban.Banned.Target.Message")
												.replace("%player", player.getName()));
								soundManager.playSound(targetPlayer, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);

								LocationUtil.teleportPlayerToSpawn(targetPlayer);
							}
						}
					}
				} else {
					messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Permission.Message"));
					soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Disabled.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Ban.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "ban";
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
