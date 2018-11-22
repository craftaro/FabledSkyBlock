package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.confirmation.Confirmation;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.scoreboard.Scoreboard;
import me.goodandevil.skyblock.scoreboard.ScoreboardManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class ConfirmCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public ConfirmCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
		IslandManager islandManager = plugin.getIslandManager();
		SoundManager soundManager = plugin.getSoundManager();
		FileManager fileManager = plugin.getFileManager();
		
		PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
		
		Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (playerData.getConfirmationTime() > 0) {
			if (islandManager.hasIsland(player)) {
				me.goodandevil.skyblock.island.Island island = islandManager.getIsland(playerData.getOwner());
				Confirmation confirmation = playerData.getConfirmation();
				
				if (confirmation == Confirmation.Ownership || confirmation == Confirmation.Deletion) {
					if (island.isRole(Role.Owner, player.getUniqueId())) {
						if (confirmation == Confirmation.Ownership) {
							UUID targetPlayerUUID = playerData.getOwnership();
							
							if (island.isRole(Role.Member, targetPlayerUUID) || island.isRole(Role.Operator, targetPlayerUUID)) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Confirmed.Message")));
								
								String targetPlayerName;
								Player targetPlayer = Bukkit.getServer().getPlayer(targetPlayerUUID);
								
								if (targetPlayer == null) {
									targetPlayerName = new OfflinePlayer(targetPlayerUUID).getName();
								} else {
									targetPlayerName = targetPlayer.getName();
									targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Ownership.Assigned.Message")));
									soundManager.playSound(targetPlayer, Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
								}
								
								for (Player all : Bukkit.getOnlinePlayers()) {
									if ((island.isRole(Role.Member, all.getUniqueId()) || island.isRole(Role.Operator, all.getUniqueId()) || island.isRole(Role.Owner, all.getUniqueId()) || island.isRole(Role.Owner, all.getUniqueId())) && (!all.getUniqueId().equals(targetPlayerUUID))) {
										all.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ownership.Assigned.Broadcast.Message").replace("%player", targetPlayerName)));
										soundManager.playSound(all, Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
									}
								}
								
								playerData.setConfirmation(null);
								playerData.setConfirmationTime(0);
								
								islandManager.giveIslandOwnership(targetPlayerUUID);
							} else {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Ownership.Member.Message")));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							}
						} else if (confirmation == Confirmation.Deletion) {
							playerData.setConfirmation(null);
							playerData.setConfirmationTime(0);
							
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Confirmed.Message")));
							
							boolean hasSpawnPoint = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "locations.yml")).getFileConfiguration().getString("Location.Spawn") != null;
							
							for (Player all : Bukkit.getOnlinePlayers()) {
								if (island.isRole(Role.Member, all.getUniqueId()) || island.isRole(Role.Operator, all.getUniqueId()) || island.isRole(Role.Owner, all.getUniqueId())) {
									if (scoreboardManager != null) {
										Scoreboard scoreboard = scoreboardManager.getScoreboard(all);
										scoreboard.cancel();
										scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Scoreboard.Tutorial.Displayname")));
										scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Tutorial.Displaylines"));
										scoreboard.run();	
									}
									
									for (Location.World worldList : Location.World.values()) {
										if (LocationUtil.isLocationAtLocationRadius(all.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
											if (hasSpawnPoint) {
												LocationUtil.teleportPlayerToSpawn(all);
											} else {
												Bukkit.getServer().getLogger().log(Level.WARNING, "SkyBlock | Error: A spawn point hasn't been set.");
											}
											
											break;
										}
									}
									
									if (!island.isRole(Role.Owner, all.getUniqueId())) {
										all.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Deletion.Broadcast.Message")));
										soundManager.playSound(all, Sounds.EXPLODE.bukkitSound(), 10.0F, 10.0F);
									}
								}
							}
							
							islandManager.deleteIsland(island);
							plugin.getVisitManager().deleteIsland(player.getUniqueId());
							plugin.getBanManager().deleteIsland(player.getUniqueId());
							
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Deletion.Sender.Message")));
							soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 10.0F, 10.0F);
						}
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Role.Message")));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Specified.Message")));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Owner.Message")));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Confirmation.Pending.Message")));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
	}

	@Override
	public String getName() {
		return "confirm";
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
		return new String[] { "confirmation" };
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
