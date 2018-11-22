package me.goodandevil.skyblock.command.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.command.CommandManager;
import me.goodandevil.skyblock.command.SubCommand;
import me.goodandevil.skyblock.command.CommandManager.Type;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.visit.VisitManager;

public class TeleportCommand extends SubCommand {

	private final Main plugin;
	private String info;
	
	public TeleportCommand(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onCommand(Player player, String[] args) {
		PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
		IslandManager islandManager = plugin.getIslandManager();
		SoundManager soundManager = plugin.getSoundManager();
		VisitManager visitManager = plugin.getVisitManager();
		
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
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
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Teleport.Island.None.Message")));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					
					return;
				} else if (!islandOwnerUUID.equals(playerDataManager.getPlayerData(player).getOwner())) {
					if (visitManager.hasIsland(islandOwnerUUID)) {
		    			me.goodandevil.skyblock.visit.Visit visit = visitManager.getIsland(islandOwnerUUID);
		    			
		    			if (visit.isOpen()) {
		    				if (!islandManager.containsIsland(islandOwnerUUID)) {
		    					islandManager.loadIsland(islandOwnerUUID);
		    				}
		    				
		    				islandManager.visitIsland(player, islandManager.getIsland(islandOwnerUUID));
		    				
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Teleport.Teleported.Other.Message").replace("%player", targetPlayerName)));
							soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
		    			} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Teleport.Island.Closed.Message")));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		    			}
		    			
		    			return;
		    		}
					
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Teleport.Island.None.Message")));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					
					return;
				}
			} else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Teleport.Permission.Message")));
				soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
				
				return;
			}
		} else if (args.length != 0) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Teleport.Invalid.Message")));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			
			return;
		}
		
		if (islandManager.hasIsland(player)) {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Teleport.Teleported.Yourself.Message")));
			soundManager.playSound(player, Sounds.ENDERMAN_TELEPORT.bukkitSound(), 1.0F, 1.0F);
			
			me.goodandevil.skyblock.island.Island island = islandManager.getIsland(plugin.getPlayerDataManager().getPlayerData(player).getOwner());
			
			Bukkit.getServer().getScheduler().runTask(plugin, new Runnable() {
				@Override
				public void run() {
					player.teleport(island.getLocation(Location.World.Normal, Location.Environment.Main));
				}
			});
		} else {
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Command.Island.Teleport.Owner.Message")));
			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		}
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
		return new String[] { "tp", "spawn", "home", "go" };
	}

	@Override
	public Type getType() {
		return CommandManager.Type.Default;
	}
}
