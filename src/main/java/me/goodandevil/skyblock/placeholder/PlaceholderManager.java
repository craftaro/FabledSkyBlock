package me.goodandevil.skyblock.placeholder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;

public class PlaceholderManager {

	private final SkyBlock skyblock;
	
	private boolean PlaceholderAPI = false;
	private boolean MVdWPlaceholderAPI = false;
	
	public PlaceholderManager(SkyBlock skyblock) {
		this.skyblock = skyblock;
		
		PluginManager pluginManager = skyblock.getServer().getPluginManager();
		
		if (pluginManager.getPlugin("PlaceholderAPI") != null) {
			PlaceholderAPI = true;
		}
		
		if (pluginManager.getPlugin("MVdWPlaceholderAPI") != null) {
			MVdWPlaceholderAPI = true;
		}
	}
	
	public void registerPlaceholders() {
		if (PlaceholderAPI) {
			new EZPlaceholder(skyblock).register();
		}
		
		if (MVdWPlaceholderAPI) {
			new MVdWPlaceholder(skyblock).register();
		}
	}
	
	public boolean isPlaceholderAPIEnabled() {
		return PlaceholderAPI;
	}
	
	public boolean isMVdWPlaceholderAPIEnabled() {
		return MVdWPlaceholderAPI;
	}
	
	public String getPlaceholder(Player player, String placeholder) {
		PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
		IslandManager islandManager = skyblock.getIslandManager();
		
		Island island = null;
		
		if (playerDataManager.hasPlayerData(player)) {
			PlayerData playerData = playerDataManager.getPlayerData(player);
			
			if (playerData.getOwner() != null && islandManager.containsIsland(playerData.getOwner())) {
				island = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
			}
		}
		
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (placeholder.equalsIgnoreCase("skyblock_island_size")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_size.Empty.Message"));
			} else {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_size.Non-empty.Message").replace("%placeholder", "" + island.getSize()));				
			}
		} else if (placeholder.equalsIgnoreCase("skyblock_island_radius")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_radius.Empty.Message"));
			} else {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_radius.Non-empty.Message").replace("%placeholder", "" + island.getRadius()));
			}
		} else if (placeholder.equalsIgnoreCase("skyblock_island_level")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_level.Empty.Message"));
			} else {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_level.Non-empty.Message").replace("%placeholder", "" + island.getLevel().getLevel()));
			}			
		} else if (placeholder.equalsIgnoreCase("skyblock_island_points")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_points.Empty.Message"));
			} else {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_points.Non-empty.Message").replace("%placeholder", "" + island.getLevel().getPoints()));
			}
		} else if (placeholder.equalsIgnoreCase("skyblock_island_role")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_role.Empty.Message"));
			} else {
				for (Role roleList : Role.values()) {
					if (island.isRole(roleList, player.getUniqueId())) {
		    			return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_role.Non-empty.Message").replace("%placeholder", roleList.name()));
					}
				}
			}
		} else if (placeholder.equalsIgnoreCase("skyblock_island_owner")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_owner.Empty.Message"));
			} else {
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
			}
		} else if (placeholder.equalsIgnoreCase("skyblock_island_biome")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_biome.Empty.Message"));
			} else {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_biome.Non-empty.Message").replace("%placeholder", island.getBiomeName()));
			}
		} else if (placeholder.equalsIgnoreCase("skyblock_island_time")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_time.Empty.Message"));
			} else {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_time.Non-empty.Message").replace("%placeholder", "" + island.getTime()));
			}
		} else if (placeholder.equalsIgnoreCase("skyblock_island_weather")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_weather.Empty.Message"));
			} else {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_weather.Non-empty.Message").replace("%placeholder", "" + island.getWeatherName()));
			}
		} else if (placeholder.equalsIgnoreCase("skyblock_island_bans")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_bans.Empty.Message"));
			} else {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_bans.Non-empty.Message").replace("%placeholder", "" + island.getBan().getBans().size()));
			}
		} else if (placeholder.equalsIgnoreCase("skyblock_island_members_total")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_members_total.Empty.Message"));
			} else {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_members_total.Non-empty.Message").replace("%placeholder", "" + (island.getRole(Role.Member).size() + island.getRole(Role.Operator).size() + 1)));
			}
		} else if (placeholder.equalsIgnoreCase("skyblock_island_members")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_members.Empty.Message"));
			} else {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_members.Non-empty.Message").replace("%placeholder", "" + island.getRole(Role.Member).size()));
			}
		} else if (placeholder.equalsIgnoreCase("skyblock_island_operators")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_operators.Empty.Message"));
			} else {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_operators.Non-empty.Message").replace("%placeholder", "" + island.getRole(Role.Operator).size()));
			}
		} else if (placeholder.equalsIgnoreCase("skyblock_island_visitors")) {
			if (island == null) {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_visitors.Empty.Message"));
			} else {
				return ChatColor.translateAlternateColorCodes('&', configLoad.getString("Placeholder.skyblock_island_visitors.Non-empty.Message").replace("%placeholder", "" + island.getVisitors().size()));
			}
		}
		
		return "";
	}
	
	public List<String> getPlaceholders() {
		List<String> placeholders = new ArrayList<>();
		placeholders.add("skyblock_island_size");
		placeholders.add("skyblock_island_radius");
		placeholders.add("skyblock_island_level");
		placeholders.add("skyblock_island_points");
		placeholders.add("skyblock_island_role");
		placeholders.add("skyblock_island_owner");
		placeholders.add("skyblock_island_biome");
		placeholders.add("skyblock_island_time");
		placeholders.add("skyblock_island_weather");
		placeholders.add("skyblock_island_bans");
		placeholders.add("skyblock_island_members_total");
		placeholders.add("skyblock_island_members");
		placeholders.add("skyblock_island_operators");
		placeholders.add("skyblock_island_visitors");
		
		return placeholders;
	}
}
