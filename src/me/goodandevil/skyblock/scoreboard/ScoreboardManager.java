package me.goodandevil.skyblock.scoreboard;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;

public class ScoreboardManager {

	private final Main plugin;
	private Map<UUID, Scoreboard> scoreboardStorage = new HashMap<>();
	
	public ScoreboardManager(Main plugin) {
		this.plugin = plugin;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
				IslandManager islandManager = plugin.getIslandManager();
				FileManager fileManager = plugin.getFileManager();
				
				if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Scoreboard.Enable")) {
					Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
					
					for (Player all : Bukkit.getOnlinePlayers()) {
						Scoreboard scoreboard = new Scoreboard(all);
						
						if (islandManager.hasIsland(all)) {
							Island island = islandManager.getIsland(playerDataManager.getPlayerData(all).getOwner());
							
							if (island.getRole(Role.Member).size() == 0 && island.getRole(Role.Operator).size() == 0) {
								scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Island.Solo.Displayname")));
								
								if (island.getVisitors().size() == 0) {
									scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
								} else {
									scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
								}
							} else {
								scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Island.Team.Displayname")));
								
								if (island.getVisitors().size() == 0) {
									scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Team.Empty.Displaylines"));
								} else {
									scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
								}
								
								Map<String, String> displayVariables = new HashMap<>();
								displayVariables.put("%owner", config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Owner"));
								displayVariables.put("%operator", config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Operator"));
								displayVariables.put("%member", config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Member"));
								
								scoreboard.setDisplayVariables(displayVariables);
							}
						} else {
							scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Tutorial.Displayname")));
							scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Tutorial.Displaylines"));
						}
						
						scoreboard.run();
						storeScoreboard(all, scoreboard);
					}
				}
			}
		}.runTaskLater(plugin, 20L);
	}
	
	public void resendScoreboard() {
		PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
		IslandManager islandManager = plugin.getIslandManager();
		FileManager fileManager = plugin.getFileManager();
		
		if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Scoreboard.Enable")) {
			Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
			
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (hasScoreboard(all)) {
					Scoreboard scoreboard = getScoreboard(all);
					scoreboard.cancel();
					
					if (islandManager.hasIsland(all)) {
						Island island = islandManager.getIsland(playerDataManager.getPlayerData(all).getOwner());
						
						if (island.getRole(Role.Member).size() == 0 && island.getRole(Role.Operator).size() == 0) {
							scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Island.Solo.Displayname")));
							
							if (island.getVisitors().size() == 0) {
								scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
							} else {
								scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
							}
						} else {
							scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Island.Team.Displayname")));
							
							if (island.getVisitors().size() == 0) {
								scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Team.Empty.Displaylines"));
							} else {
								scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
							}
							
							Map<String, String> displayVariables = new HashMap<>();
							displayVariables.put("%owner", config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Owner"));
							displayVariables.put("%operator", config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Operator"));
							displayVariables.put("%member", config.getFileConfiguration().getString("Scoreboard.Island.Team.Word.Member"));
							
							scoreboard.setDisplayVariables(displayVariables);
						}
					} else {
						scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Tutorial.Displayname")));
						scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Tutorial.Displaylines"));
					}
					
					scoreboard.run();
				}
			}
		}
	}
	
	public void storeScoreboard(Player player, Scoreboard scoreboard) {
		scoreboardStorage.put(player.getUniqueId(), scoreboard);
	}
	
	public void unloadPlayer(Player player) {
		if (hasScoreboard(player)) {
			scoreboardStorage.remove(player.getUniqueId());
		}
	}

	public Scoreboard getScoreboard(Player player) {
		if (hasScoreboard(player)) {
			return scoreboardStorage.get(player.getUniqueId());
		}
		
		return null;
	}

	public boolean hasScoreboard(Player player) {
		return scoreboardStorage.containsKey(player.getUniqueId());
	}
}
