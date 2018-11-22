package me.goodandevil.skyblock.command.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;

public class CurrentCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public CurrentCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
		SoundManager soundManager = plugin.getSoundManager();
		
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (args.length > 0) {
			if (!args[0].equalsIgnoreCase(player.getName())) {
				if (player.hasPermission("skyblock.current") || player.hasPermission("skyblock.*")) {
					if (args.length == 1) {
						Player targetPlayer = Bukkit.getServer().getPlayer(args[0]);
						
						if (targetPlayer == null) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Current.Offline.Message")));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							
							return;
						}
						
						if (!targetPlayer.getName().equals(player.getName())) {
							PlayerData playerData = playerDataManager.getPlayerData(targetPlayer);
							
							if (playerData.getIsland() == null) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Current.Island.None.Other.Message")));
							} else {
								String targetPlayerName = targetPlayer.getName(), ownerPlayerName;
								targetPlayer = Bukkit.getServer().getPlayer(playerData.getIsland());
								
								if (targetPlayer == null) {
									ownerPlayerName = new OfflinePlayer(playerData.getIsland()).getName();
								} else {
									ownerPlayerName = targetPlayer.getName();
								}
								
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Current.Island.Owner.Other.Message").replace("%target", targetPlayerName).replace("%owner", ownerPlayerName)));
							}
							
							soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
							
							return;
						}
					} else if (args.length > 1) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Current.Invalid.Message")));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						
						return;
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Current.Permission.Message")));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					
					return;
				}
			}
		}
		
		PlayerData playerData = playerDataManager.getPlayerData(player);
		
		if (playerData.getIsland() == null) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Current.Island.None.Yourself.Message")));
		} else {
			Player targetPlayer = Bukkit.getServer().getPlayer(playerData.getIsland());
			String targetPlayerName;
			
			if (targetPlayer == null) {
				targetPlayerName = new OfflinePlayer(playerData.getIsland()).getName();
			} else {
				targetPlayerName = targetPlayer.getName();
			}
			
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Current.Island.Owner.Yourself.Message").replace("%player", targetPlayerName)));
		}
		
		soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
	}

	@Override
	public String getName() {
		return "current";
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
		return new String[] { "cur" };
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
