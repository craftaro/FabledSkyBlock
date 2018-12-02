package me.goodandevil.skyblock.placeholder;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Level;
import me.goodandevil.skyblock.leaderboard.Leaderboard;
import me.goodandevil.skyblock.leaderboard.LeaderboardManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.visit.Visit;

public class MVdWPlaceholder {

	private final SkyBlock skyblock;
	
	public MVdWPlaceholder(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	public void register() {
		PlaceholderManager placeholderManager = skyblock.getPlaceholderManager();
		LeaderboardManager leaderboardManager = skyblock.getLeaderboardManager();
		
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		List<Leaderboard> leaderboardLevelPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.Level);
		List<Leaderboard> leaderboardVotesPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.Votes);
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_islands", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				return "" + skyblock.getVisitManager().getIslands().size();
			}
		});
		
		for (int i = 0; i < 10; i++) {
			PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_leaderboard_votes_" + (i+1), new PlaceholderReplacer() {
				@Override
				public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
					int index = Integer.valueOf(event.getPlaceholder().replace("skyblock_leaderboard_votes_", ""));
					
					if (index < leaderboardVotesPlayers.size()) {
						Leaderboard leaderboard = leaderboardVotesPlayers.get(index);
						Visit visit = leaderboard.getVisit();
						
						Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
						String islandOwnerName;
						
						if (targetPlayer == null) {
							islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
						} else {
							islandOwnerName = targetPlayer.getName();
						}
						
						return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_leaderboard_votes.Non-empty.Message").replace("%position", "" + (index+1)).replace("%player", islandOwnerName).replace("%votes", NumberUtil.formatNumber(visit.getVoters().size())));
					}
					
					return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_leaderboard_votes.Empty.Message"));
				}
			});
			
			PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_leaderboard_level_" + (i+1), new PlaceholderReplacer() {
				@Override
				public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
					int index = Integer.valueOf(event.getPlaceholder().replace("skyblock_leaderboard_level_", ""));
					
					if (index < leaderboardLevelPlayers.size()) {
						Leaderboard leaderboard = leaderboardLevelPlayers.get(index);
						Visit visit = leaderboard.getVisit();
						Level level = visit.getLevel();
						
						Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
						String islandOwnerName;
						
						if (targetPlayer == null) {
							islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
						} else {
							islandOwnerName = targetPlayer.getName();
						}
						
						return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_leaderboard_level.Non-empty.Message").replace("%position", "" + (index+1)).replace("%player", islandOwnerName).replace("%level", NumberUtil.formatNumber(level.getLevel())).replace("%points", NumberUtil.formatNumber(level.getPoints())));
					}
					
					return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_leaderboard_level.Empty.Message"));
				}
			});
		}

		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_size", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_radius", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_level", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_points", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_role", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_owner", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_biome", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_time", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_weather", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_bans", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_members_total", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_members", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_operators", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_visitors", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				return placeholderManager.getPlaceholder(player, event.getPlaceholder());
			}
		});
	}
}
