package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.island.Settings;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.ban.Ban;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;

public class BanCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public BanCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
		IslandManager islandManager = plugin.getIslandManager();
		SoundManager soundManager = plugin.getSoundManager();
		FileManager fileManager = plugin.getFileManager();
		
		PlayerData playerData = playerDataManager.getPlayerData(player);
		
		Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (args.length == 1) {
			if (islandManager.hasIsland(player)) {
				if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Banning")) {
					me.goodandevil.skyblock.island.Island island = islandManager.getIsland(playerData.getOwner());
					
					if (island.isRole(Role.Owner, player.getUniqueId()) || (island.isRole(Role.Operator, player.getUniqueId()) && island.getSetting(Settings.Role.Operator, "Ban").getStatus())) {
						UUID targetPlayerUUID = null;
						String targetPlayerName = null;
						
						Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
						
						if (targetPlayer == null) {
							OfflinePlayer targetPlayerOffline = new OfflinePlayer(args[0]);
							targetPlayerUUID = targetPlayerOffline.getUniqueId();
							targetPlayerName = targetPlayerOffline.getName();
						} else {
							targetPlayerUUID = targetPlayer.getUniqueId();
							targetPlayerName = targetPlayer.getName();
						}
						
						if (targetPlayerUUID == null) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ban.Found.Message")));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else if (targetPlayerUUID.equals(player.getUniqueId())) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ban.Yourself.Message")));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else if (island.isRole(Role.Member, targetPlayerUUID) || island.isRole(Role.Operator, targetPlayerUUID) || island.isRole(Role.Owner, targetPlayerUUID)) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ban.Member.Message")));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else if (island.getBan().isBanned(targetPlayerUUID)) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ban.Already.Message")));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else {						
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ban.Banned.Sender.Message").replace("%player", targetPlayerName)));
							soundManager.playSound(player, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
							
							Ban ban = island.getBan();
							ban.addBan(targetPlayerUUID);
							ban.save();
							
							if (targetPlayer != null) {
								targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ban.Banned.Target.Message").replace("%player", player.getName())));
								soundManager.playSound(targetPlayer, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
								
								for (Location.World worldList : Location.World.values()) {
									if (LocationUtil.isLocationAtLocationRadius(targetPlayer.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
										LocationUtil.teleportPlayerToSpawn(targetPlayer);
										
										break;
									}
								}
							}
						}
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ban.Permission.Message")));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ban.Disabled.Message")));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				}
			} else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ban.Owner.Message")));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			}	
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Ban.Invalid.Message")));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
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
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
