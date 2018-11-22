package me.goodandevil.skyblock.command.commands.admin;

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
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.scoreboard.Scoreboard;
import me.goodandevil.skyblock.scoreboard.ScoreboardManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;

public class DeleteCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public DeleteCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
		IslandManager islandManager = plugin.getIslandManager();
		SoundManager soundManager = plugin.getSoundManager();
		FileManager fileManager = plugin.getFileManager();
		
		Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (player.hasPermission("skyblock.admin.delete") || player.hasPermission("skyblock.admin.*") || player.hasPermission("skyblock.*")) {
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
				
				if (islandManager.isIslandExist(targetPlayerUUID)) {
					islandManager.loadIsland(targetPlayerUUID);
					
					boolean hasSpawnPoint = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "locations.yml")).getFileConfiguration().getString("Location.Spawn") != null;
					Island island = islandManager.getIsland(targetPlayerUUID);
					
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
				
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Delete.Deleted.Message").replace("%player", targetPlayerName)));
					soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Delete.Owner.Message")));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Delete.Invalid.Message")));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Admin.Delete.Permission.Message")));
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
	public SubCommand setInfo(String info) {
		this.info = info;
		
		return this;
	}
	
	@Override
	public String[] getAliases() {
		return new String[] { "remove", "disband" };
	}
	
	@Override
	public Type getType() {
		return CommandManager.Type.Admin;
	}
}
