package me.goodandevil.skyblock.placeholder;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Level;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.leaderboard.Leaderboard;
import me.goodandevil.skyblock.leaderboard.LeaderboardManager;
import me.goodandevil.skyblock.utils.NumberUtil;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.visit.Visit;

public class EZPlaceholder extends PlaceholderExpansion {
	
	private final SkyBlock skyblock;
	
	public EZPlaceholder(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	public String getIdentifier() {
    	return "skyblock";
    }
	
    public String getPlugin() {
    	return null;
    }
        
    public String getAuthor() {
    	return skyblock.getDescription().getAuthors().get(0);
    }
    
    public String getVersion() {
    	return skyblock.getDescription().getVersion();
    }
    
    public String onPlaceholderRequest(Player player, String identifier) {
    	LeaderboardManager leaderboardManager = skyblock.getLeaderboardManager();
    	IslandManager islandManager = skyblock.getIslandManager();
    	
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
    	
		List<Leaderboard> leaderboardLevelPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.Level);
		List<Leaderboard> leaderboardVotesPlayers = leaderboardManager.getLeaderboard(Leaderboard.Type.Votes);
		
		if (identifier.equalsIgnoreCase("islands")) {
    		return "" + skyblock.getVisitManager().getIslands().size();
    	} else {
    		for (int i = 0; i < 10; i++) {
    			if (identifier.equalsIgnoreCase("skyblock_leaderboard_votes_" + (i+1))) {
					if (i < leaderboardVotesPlayers.size()) {
						Leaderboard leaderboard = leaderboardVotesPlayers.get(i);
						Visit visit = leaderboard.getVisit();
						
						Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
						String islandOwnerName;
						
						if (targetPlayer == null) {
							islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
						} else {
							islandOwnerName = targetPlayer.getName();
						}
						
						return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_leaderboard_votes.Non-empty.Message").replace("%position", "" + (i+1)).replace("%player", islandOwnerName).replace("%votes", NumberUtil.formatNumber(visit.getVoters().size())));
					}
					
					return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_leaderboard_votes.Empty.Message"));
    			} else if (identifier.equalsIgnoreCase("skyblock_leaderboard_level_" + (i+1))) {
    				if (i < leaderboardLevelPlayers.size()) {
						Leaderboard leaderboard = leaderboardLevelPlayers.get(i);
						Visit visit = leaderboard.getVisit();
						Level level = visit.getLevel();
						
						Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());
						String islandOwnerName;
						
						if (targetPlayer == null) {
							islandOwnerName = new OfflinePlayer(visit.getOwnerUUID()).getName();
						} else {
							islandOwnerName = targetPlayer.getName();
						}
						
						return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_leaderboard_level.Non-empty.Message").replace("%position", "" + (i+1)).replace("%player", islandOwnerName).replace("%level", NumberUtil.formatNumber(level.getLevel())).replace("%points", NumberUtil.formatNumber(level.getPoints())));
					}
					
					return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_leaderboard_level.Empty.Message"));
    			}
    		}
    	}
    	
    	if(player == null){
    		return "";
        }
    	
    	if (islandManager.hasIsland(player)) {
    		Island island = islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner());
    		
    		if (identifier.equalsIgnoreCase("island_size")) {
    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_size.Non-empty.Message").replace("%placeholder", "" + island.getSize()));
    		} else if (identifier.equalsIgnoreCase("island_radius")) {
    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_radius.Non-empty.Message").replace("%placeholder", "" + island.getRadius()));
    		} else if (identifier.equalsIgnoreCase("island_level")) {
    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_level.Non-empty.Message").replace("%placeholder", "" + island.getLevel().getLevel()));
    		} else if (identifier.equalsIgnoreCase("island_points")) {
    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_points.Non-empty.Message").replace("%placeholder", "" + island.getLevel().getPoints()));
    		} else if (identifier.equalsIgnoreCase("island_role")) {
    			for (Role roleList : Role.values()) {
    				if (island.isRole(roleList, player.getUniqueId())) {
    	    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_role.Non-empty.Message").replace("%placeholder", roleList.name()));
    				}
    			}
    		} else if (identifier.equalsIgnoreCase("island_owner")) {
    			UUID islandOwnerUUID = island.getOwnerUUID();
    			Player targetPlayer = Bukkit.getServer().getPlayer(islandOwnerUUID);
    			
    			if (targetPlayer == null) {
        			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_owner.Non-empty.Other.Message").replace("%placeholder", Bukkit.getServer().getOfflinePlayer(islandOwnerUUID).getName()));
    			} else {
    				if (targetPlayer.getName().equals(player.getName())) {
            			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_owner.Non-empty.Yourself.Message").replace("%placeholder", targetPlayer.getName()));    					
    				} else {
            			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_owner.Non-empty.Other.Message").replace("%placeholder", targetPlayer.getName()));
    				}
    			}
    		} else if (identifier.equalsIgnoreCase("island_biome")) {
    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_biome.Non-empty.Message").replace("%placeholder", island.getBiomeName()));
    		} else if (identifier.equalsIgnoreCase("island_time")) {
    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_time.Non-empty.Message").replace("%placeholder", "" + island.getTime()));
    		} else if (identifier.equalsIgnoreCase("island_weather")) {
    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_weather.Non-empty.Message").replace("%placeholder", "" + island.getWeatherName()));
    		} else if (identifier.equalsIgnoreCase("island_bans")) {
    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_bans.Non-empty.Message").replace("%placeholder", "" + island.getBan().getBans().size()));
    		} else if (identifier.equalsIgnoreCase("island_members_total")) {
    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_members_total.Non-empty.Message").replace("%placeholder", "" + (island.getRole(Role.Member).size() + island.getRole(Role.Operator).size() + 1)));
    		} else if (identifier.equalsIgnoreCase("island_members")) {
    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_members.Non-empty.Message").replace("%placeholder", "" + island.getRole(Role.Member).size()));
    		} else if (identifier.equalsIgnoreCase("island_operators")) {
    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_operators.Non-empty.Message").replace("%placeholder", "" + island.getRole(Role.Operator).size()));
    		} else if (identifier.equalsIgnoreCase("island_visitors")) {
    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_visitors.Non-empty.Message").replace("%placeholder", "" + island.getVisitors().size()));
    		}
    		
    		return null;
    	}
    	
    	if (identifier.equalsIgnoreCase("island_size")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_size.Empty.Message"));
    	} else if (identifier.equalsIgnoreCase("island_radius")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_radius.Empty.Message"));
    	} else if (identifier.equalsIgnoreCase("island_level")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_level.Empty.Message"));
		} else if (identifier.equalsIgnoreCase("island_points")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_points.Empty.Message"));
		} else if (identifier.equalsIgnoreCase("island_role")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_role.Empty.Message"));
		} else if (identifier.equalsIgnoreCase("island_owner")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_owner.Empty.Message"));
		} else if (identifier.equalsIgnoreCase("island_biome")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_biome.Empty.Message"));
		} else if (identifier.equalsIgnoreCase("island_time")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_time.Empty.Message"));
		} else if (identifier.equalsIgnoreCase("island_weather")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_weather.Empty.Message"));
		} else if (identifier.equalsIgnoreCase("island_bans")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_bans.Empty.Message"));
		} else if (identifier.equalsIgnoreCase("island_members_total")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_members_total.Empty.Message"));
		} else if (identifier.equalsIgnoreCase("island_members")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_members.Empty.Message"));
		} else if (identifier.equalsIgnoreCase("island_operators")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_operators.Empty.Message"));
		} else if (identifier.equalsIgnoreCase("island_visitors")) {
			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_visitors.Empty.Message"));
		}
        
        return null;
    }
}

