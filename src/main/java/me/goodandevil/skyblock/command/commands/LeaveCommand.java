package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.event.player.PlayerIslandLeaveEvent;
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
import me.goodandevil.skyblock.scoreboard.Scoreboard;
import me.goodandevil.skyblock.scoreboard.ScoreboardManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class LeaveCommand extends SubCommand {

	private final SkyBlock skyblock;
	private String info;

	public LeaveCommand(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}

	@Override
	public void onCommandByPlayer(Player player, String[] args) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		ScoreboardManager scoreboardManager = skyblock.getScoreboardManager();
		MessageManager messageManager = skyblock.getMessageManager();
		IslandManager islandManager = skyblock.getIslandManager();
		SoundManager soundManager = skyblock.getSoundManager();
		FileManager fileManager = skyblock.getFileManager();

		PlayerData playerData = playerDataManager.getPlayerData(player);

		Config languageConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));

		if (islandManager.hasIsland(player)) {
			me.goodandevil.skyblock.island.Island island = islandManager.getIsland(playerData.getOwner());

			if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
				messageManager.sendMessage(player,
						languageConfig.getFileConfiguration().getString("Command.Island.Leave.Owner.Message"));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			} else {
				PlayerIslandLeaveEvent islandLeaveEvent = new PlayerIslandLeaveEvent(player, island.getAPIWrapper());
				Bukkit.getServer().getPluginManager().callEvent(islandLeaveEvent);

				if (!islandLeaveEvent.isCancelled()) {
					if (islandManager.isPlayerAtIsland(island, player)) {
						LocationUtil.teleportPlayerToSpawn(player);
					}

					if (island.hasRole(IslandRole.Member, player.getUniqueId())) {
						island.removeRole(IslandRole.Member, player.getUniqueId());
					} else if (island.hasRole(IslandRole.Operator, player.getUniqueId())) {
						island.removeRole(IslandRole.Operator, player.getUniqueId());
					}

					island.save();

					playerData.setPlaytime(0);
					playerData.setOwner(null);
					playerData.setMemberSince(null);
					playerData.setChat(false);
					playerData.save();

					Set<UUID> islandMembersOnline = islandManager.getMembersOnline(island);

					if (islandMembersOnline.size() == 1) {
						for (UUID islandMembersOnlineList : islandMembersOnline) {
							if (!islandMembersOnlineList.equals(player.getUniqueId())) {
								Player targetPlayer = Bukkit.getServer().getPlayer(islandMembersOnlineList);
								PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer);

								if (targetPlayerData.isChat()) {
									targetPlayerData.setChat(false);
									messageManager.sendMessage(targetPlayer,
											fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
													.getFileConfiguration().getString("Island.Chat.Untoggled.Message"));
								}
							}
						}
					}

					// TODO Check if player has been teleported
					islandManager.unloadIsland(island, null);

					for (Player all : Bukkit.getOnlinePlayers()) {
						if (!all.getUniqueId().equals(player.getUniqueId())) {
							if (island.hasRole(IslandRole.Member, all.getUniqueId())
									|| island.hasRole(IslandRole.Operator, all.getUniqueId())
									|| island.hasRole(IslandRole.Owner, all.getUniqueId())) {
								all.sendMessage(ChatColor.translateAlternateColorCodes('&',
										languageConfig.getFileConfiguration()
												.getString("Command.Island.Leave.Left.Broadcast.Message")
												.replace("%player", player.getName())));
								soundManager.playSound(all, Sounds.IRONGOLEM_HIT.bukkitSound(), 5.0F, 5.0F);

								if (island.getRole(IslandRole.Member).size() == 0
										&& island.getRole(IslandRole.Operator).size() == 0) {
									if (scoreboardManager != null) {
										if (islandManager.getVisitorsAtIsland(island).size() != 0) {
											Scoreboard scoreboard = scoreboardManager.getScoreboard(all);
											scoreboard.cancel();
											scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
													languageConfig.getFileConfiguration()
															.getString("Scoreboard.Island.Solo.Displayname")));
											scoreboard.setDisplayList(languageConfig.getFileConfiguration()
													.getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
											scoreboard.run();
										}
									}

									break;
								}
							}
						}
					}

					messageManager.sendMessage(player, languageConfig.getFileConfiguration()
							.getString("Command.Island.Leave.Left.Sender.Message"));
					soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 5.0F, 5.0F);

					if (scoreboardManager != null) {
						Scoreboard scoreboard = scoreboardManager.getScoreboard(player);
						scoreboard.cancel();
						scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
								languageConfig.getFileConfiguration().getString("Scoreboard.Tutorial.Displayname")));
						scoreboard.setDisplayList(languageConfig.getFileConfiguration()
								.getStringList("Scoreboard.Tutorial.Displaylines"));
						scoreboard.run();
					}
				}
			}
		} else {
			messageManager.sendMessage(player,
					languageConfig.getFileConfiguration().getString("Command.Island.Leave.Member.Message"));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public void onCommandByConsole(ConsoleCommandSender sender, String[] args) {
		sender.sendMessage("SkyBlock | Error: You must be a player to perform that command.");
	}

	@Override
	public String getName() {
		return "leave";
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
