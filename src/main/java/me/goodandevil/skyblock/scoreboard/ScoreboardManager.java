package me.goodandevil.skyblock.scoreboard;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.IslandRole;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;

public class ScoreboardManager {

	private final SkyBlock skyblock;
	private Map<UUID, Scoreboard> scoreboardStorage = new HashMap<>();
	
	public ScoreboardManager(SkyBlock skyblock) {
		this.skyblock = skyblock;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
				IslandManager islandManager = skyblock.getIslandManager();
				FileManager fileManager = skyblock.getFileManager();
				
				if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Scoreboard.Enable")) {
					Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
					
					for (Player all : Bukkit.getOnlinePlayers()) {
						Scoreboard scoreboard = new Scoreboard(all);
						
						if (islandManager.hasIsland(all)) {
							Island island = islandManager.getIsland(playerDataManager.getPlayerData(all).getOwner());
							
							if (island.getRole(IslandRole.Member).size() == 0 && island.getRole(IslandRole.Operator).size() == 0) {
								scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Island.Solo.Displayname")));
								
								if (islandManager.getVisitorsAtIsland(island).size() == 0) {
									scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
								} else {
									scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
								}
							} else {
								scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Island.Team.Displayname")));
								
								if (islandManager.getVisitorsAtIsland(island).size() == 0) {
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
		}.runTaskLater(skyblock, 20L);
	}
	
	public void resendScoreboard() {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		IslandManager islandManager = skyblock.getIslandManager();
		FileManager fileManager = skyblock.getFileManager();
		
		if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Scoreboard.Enable")) {
			Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
			
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (hasScoreboard(all)) {
					Scoreboard scoreboard = getScoreboard(all);
					scoreboard.cancel();
					
					if (islandManager.hasIsland(all)) {
						Island island = islandManager.getIsland(playerDataManager.getPlayerData(all).getOwner());
						
						if (island.getRole(IslandRole.Member).size() == 0 && island.getRole(IslandRole.Operator).size() == 0) {
							scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Island.Solo.Displayname")));
							
							if (islandManager.getVisitorsAtIsland(island).size() == 0) {
								scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
							} else {
								scoreboard.setDisplayList(config.getFileConfiguration().getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
							}
						} else {
							scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Scoreboard.Island.Team.Displayname")));
							
							if (islandManager.getVisitorsAtIsland(island).size() == 0) {
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
