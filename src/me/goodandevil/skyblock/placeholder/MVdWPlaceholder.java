package me.goodandevil.skyblock.placeholder;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;

public class MVdWPlaceholder {

	private final SkyBlock skyblock;
	
	public MVdWPlaceholder(SkyBlock skyblock) {
		this.skyblock = skyblock;
	}
	
	public void register() {
		IslandManager islandManager = skyblock.getIslandManager();
		
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_islands", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				return "" + skyblock.getVisitManager().getIslands().size();
			}
		});

		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_size", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
		    		return "" + islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner()).getSize();
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_size.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_radius", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
		    		return "" + islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner()).getRadius();
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_radius.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_level", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
		    		return "" + islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner()).getLevel().getLevel();
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_level.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_points", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
	    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_points.Non-empty.Message").replace("%placeholder", "" + islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner()).getLevel().getPoints()));
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_points.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_role", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
		    		Island island = islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner());
					
	    			for (Role roleList : Role.values()) {
	    				if (island.isRole(roleList, player.getUniqueId())) {
	    	    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_role.Non-empty.Message").replace("%placeholder", roleList.name()));
	    				}
	    			}
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_role.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_owner", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
	    			UUID islandOwnerUUID = islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner()).getOwnerUUID();
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
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_owner.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_biome", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
	    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_biome.Non-empty.Message").replace("%placeholder", islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner()).getBiomeName()));
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_biome.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_time", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
	    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_time.Non-empty.Message").replace("%placeholder", "" + islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner()).getTime()));
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_time.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_weather", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
	    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_weather.Non-empty.Message").replace("%placeholder", islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner()).getWeatherName()));
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_weather.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_bans", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
	    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_bans.Non-empty.Message").replace("%placeholder", "" + islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner()).getBan().getBans().size()));
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_bans.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_members_total", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
		    		Island island = islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner());
	    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_members_total.Non-empty.Message").replace("%placeholder", "" + (island.getRole(Role.Member).size() + island.getRole(Role.Operator).size() + 1)));
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_members_total.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_members", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
	    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_members.Non-empty.Message").replace("%placeholder", "" + islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner()).getRole(Role.Member).size()));
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_members.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_operators", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
	    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_operators.Non-empty.Message").replace("%placeholder", "" + islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner()).getRole(Role.Operator).size()));
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_operators.Empty.Message"));
			}
		});
		
		PlaceholderAPI.registerPlaceholder(skyblock, "skyblock_island_visitors", new PlaceholderReplacer() {
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
				Player player = event.getPlayer();
				
				if (player == null) {
					return null;
				}
				
				if (islandManager.hasIsland(player)) {
	    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_visitors.Non-empty.Message").replace("%placeholder", "" + islandManager.getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner()).getVisitors().size()));
				}
		    	
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_visitors.Empty.Message"));
			}
		});
	}
}
