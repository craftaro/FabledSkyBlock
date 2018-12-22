package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;

public class PromoteCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;

	public PromoteCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();

		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (args.length == 1) {
			Island island = islandManager.getIsland(player);

			if (island == null) {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Owner.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			} else if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
				Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);

				if (targetPlayer == null) {
					OfflinePlayer offlinePlayer = new OfflinePlayer(args[0]);
					Set<UUID> islandOperators = island.getRole(IslandRole.Operator);

					if (offlinePlayer.getUniqueId() != null
							&& (island.getRole(IslandRole.Member).contains(offlinePlayer.getUniqueId())
									|| islandOperators.contains(offlinePlayer.getUniqueId()))) {
						if (islandOperators.contains(offlinePlayer.getUniqueId())) {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Promote.Operator.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Promote.Promoted.Sender.Message")
											.replace("%player", offlinePlayer.getName()));
							soundManager.playSound(player, Sounds.FIREWORK_BLAST.bukkitSound(), 1.0F, 1.0F);

							for (Player all : Bukkit.getOnlinePlayers()) {
								if (!all.getUniqueId().equals(player.getUniqueId())) {
									if (island.hasRole(IslandRole.Member, player.getUniqueId())
											|| island.hasRole(IslandRole.Operator, all.getUniqueId())
											|| island.hasRole(IslandRole.Owner, all.getUniqueId())) {
										all.sendMessage(ChatColor.translateAlternateColorCodes('&',
												configLoad
														.getString("Command.Island.Promote.Promoted.Broadcast.Message")
														.replace("%player", offlinePlayer.getName())));
										soundManager.playSound(all, Sounds.FIREWORK_BLAST.bukkitSound(), 1.0F, 1.0F);
									}
								}
							}

							island.setRole(IslandRole.Operator, offlinePlayer.getUniqueId());
						}
					} else {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Promote.Member.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					}
				} else {
					if (island.hasRole(IslandRole.Member, targetPlayer.getUniqueId())
							|| island.hasRole(IslandRole.Operator, targetPlayer.getUniqueId())) {
						if (island.hasRole(IslandRole.Operator, targetPlayer.getUniqueId())) {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Promote.Operator.Message"));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else {
							messageManager.sendMessage(player,
									configLoad.getString("Command.Island.Promote.Promoted.Sender.Message")
											.replace("%player", targetPlayer.getName()));
							messageManager.sendMessage(targetPlayer,
									configLoad.getString("Command.Island.Promote.Promoted.Target.Message"));

							for (Player all : Bukkit.getOnlinePlayers()) {
								if (!all.getUniqueId().equals(player.getUniqueId())) {
									if (island.hasRole(IslandRole.Member, player.getUniqueId())
											|| island.hasRole(IslandRole.Operator, all.getUniqueId())
											|| island.hasRole(IslandRole.Owner, all.getUniqueId())) {
										all.sendMessage(ChatColor.translateAlternateColorCodes('&',
												configLoad
														.getString("Command.Island.Promote.Promoted.Broadcast.Message")
														.replace("%player", targetPlayer.getName())));
										soundManager.playSound(all, Sounds.FIREWORK_BLAST.bukkitSound(), 1.0F, 1.0F);
									}
								}
							}

							island.setRole(IslandRole.Operator, targetPlayer.getUniqueId());
						}
					} else {
						messageManager.sendMessage(player,
								configLoad.getString("Command.Island.Promote.Member.Message"));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					}
				}
			} else {
				messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Permission.Message"));
				soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			messageManager.sendMessage(player, configLoad.getString("Command.Island.Promote.Invalid.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "promote";
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
