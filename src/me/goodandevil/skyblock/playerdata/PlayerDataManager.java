package me.goodandevil.skyblock.playerdata;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.ban.BanManager;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.Location;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.scoreboard.Scoreboard;
import me.goodandevil.skyblock.scoreboard.ScoreboardManager;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.visit.Visit;
import me.goodandevil.skyblock.world.WorldManager;

public class PlayerDataManager {

	private final Main plugin;
	private Map<UUID, PlayerData> playerDataStorage = new HashMap<>();
	
	public PlayerDataManager(Main plugin) {
		this.plugin = plugin;
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			loadPlayerData(all);
			
			if (!hasPlayerData(all)) {
				createPlayerData(all);
				loadPlayerData(all);
			}
			
			storeIsland(all);
		}
	}
	
	public void onDisable() {
		for (UUID playerDataStorageList : playerDataStorage.keySet()) {
			playerDataStorage.get(playerDataStorageList).save();
		}
	}
	
	public void createPlayerData(Player player) {
		Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId() + ".yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		String[] playerTexture;
		
		try {
			Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			Method getProfileMethod = entityPlayer.getClass().getMethod("getProfile", new Class<?>[0]);
			GameProfile gameProfile = (GameProfile) getProfileMethod.invoke(entityPlayer);
			Property property = gameProfile.getProperties().get("textures").iterator().next();
			playerTexture = new String[] { property.getSignature(), property.getValue() };
		} catch (Exception e) {
			playerTexture = new String[] { "K9P4tCIENYbNpDuEuuY0shs1x7iIvwXi4jUUVsATJfwsAIZGS+9OZ5T2HB0tWBoxRvZNi73Vr+syRdvTLUWPusVXIg+2fhXmQoaNEtnQvQVGQpjdQP0TkZtYG8PbvRxE6Z75ddq+DVx/65OSNHLWIB/D+Rg4vINh4ukXNYttn9QvauDHh1aW7/IkIb1Bc0tLcQyqxZQ3mdglxJfgIerqnlA++Lt7TxaLdag4y1NhdZyd3OhklF5B0+B9zw/qP8QCzsZU7VzJIcds1+wDWKiMUO7+60OSrIwgE9FPamxOQDFoDvz5BOULQEeNx7iFMB+eBYsapCXpZx0zf1bduppBUbbVC9wVhto/J4tc0iNyUq06/esHUUB5MHzdJ0Y6IZJAD/xIw15OLCUH2ntvs8V9/cy5/n8u3JqPUM2zhUGeQ2p9FubUGk4Q928L56l3omRpKV+5QYTrvF+AxFkuj2hcfGQG3VE2iYZO6omXe7nRPpbJlHkMKhE8Xvd1HP4PKpgivSkHBoZ92QEUAmRzZydJkp8CNomQrZJf+MtPiNsl/Q5RQM+8CQThg3+4uWptUfP5dDFWOgTnMdA0nIODyrjpp+bvIJnsohraIKJ7ZDnj4tIp4ObTNKDFC/8j8JHz4VCrtr45mbnzvB2DcK8EIB3JYT7ElJTHnc5BKMyLy5SKzuw=", "eyJ0aW1lc3RhbXAiOjE1MjkyNTg0MTE4NDksInByb2ZpbGVJZCI6Ijg2NjdiYTcxYjg1YTQwMDRhZjU0NDU3YTk3MzRlZWQ3IiwicHJvZmlsZU5hbWUiOiJTdGV2ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGMxYzc3Y2U4ZTU0OTI1YWI1ODEyNTQ0NmVjNTNiMGNkZDNkMGNhM2RiMjczZWI5MDhkNTQ4Mjc4N2VmNDAxNiJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc2N2Q0ODMyNWVhNTMyNDU2MTQwNmI4YzgyYWJiZDRlMjc1NWYxMTE1M2NkODVhYjA1NDVjYzIifX19" };
		}
		
		configLoad.set("Texture.Signature", playerTexture[0]);
		configLoad.set("Texture.Value",playerTexture[1]);
		configLoad.set("Statistics.Island.Playtime", 0);
		
		try {
			configLoad.save(config.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPlayerData(Player player) {
		if (plugin.getFileManager().isFileExist(new File(plugin.getDataFolder().toString() + "/player-data", player.getUniqueId().toString() + ".yml"))) {
			PlayerData playerData = new PlayerData(player);
			playerDataStorage.put(player.getUniqueId(), playerData);
		}
	}
	
	public void unloadPlayerData(Player player) {
		if (hasPlayerData(player)) {
			plugin.getFileManager().unloadConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId().toString() + ".yml"));
			playerDataStorage.remove(player.getUniqueId());
		}
	}
	
	public void savePlayerData(Player player) {
		if (hasPlayerData(player)) {
			Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), player.getUniqueId().toString() + ".yml"));
			
			try {
				config.getFileConfiguration().save(config.getFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Map<UUID, PlayerData> getPlayerData() {
		return playerDataStorage;
	}

	public PlayerData getPlayerData(Player player) {
		if (hasPlayerData(player)) {
			return playerDataStorage.get(player.getUniqueId());
		}
		
		return null;
	}

	public boolean hasPlayerData(Player player) {
		return playerDataStorage.containsKey(player.getUniqueId());
	}
	
	public void storeIsland(Player player) {
		IslandManager islandManager = plugin.getIslandManager();
		WorldManager worldManager = plugin.getWorldManager();
		FileManager fileManager = plugin.getFileManager();
		
		if (hasPlayerData(player) && (player.getWorld().getName().equals(worldManager.getWorld(Location.World.Normal).getName()) || player.getWorld().getName().equals(worldManager.getWorld(Location.World.Nether).getName()))) {
			for (UUID islandList : islandManager.getIslands().keySet()) {
				Island island = islandManager.getIslands().get(islandList);
				
				for (Location.World worldList : Location.World.values()) {
					if (LocationUtil.isLocationAtLocationRadius(player.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
						PlayerData playerData = getPlayerData(player);
						playerData.setIsland(island.getOwnerUUID());
						
						if (worldList == Location.World.Normal) {
							if (!island.isWeatherSynchronised()) {
			    				player.setPlayerTime(island.getTime(), fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Weather.Time.Cycle"));
			    				player.setPlayerWeather(island.getWeather());
							}
						}
						
						ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
						
						if (scoreboardManager != null) {
							if (!island.isRole(Role.Member, player.getUniqueId()) && !island.isRole(Role.Operator, player.getUniqueId()) && !island.isRole(Role.Owner, player.getUniqueId())) {
								Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
								FileConfiguration configLoad = config.getFileConfiguration();
								
								for (Player all : Bukkit.getOnlinePlayers()) {
									PlayerData targetPlayerData = getPlayerData(all);
									
									if (targetPlayerData.getOwner() != null && targetPlayerData.getOwner().equals(island.getOwnerUUID())) {
										Scoreboard scoreboard = scoreboardManager.getScoreboard(all);
										scoreboard.cancel();
										
										if ((island.getRole(Role.Member).size() + island.getRole(Role.Operator).size() + 1) == 1) {
											scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Scoreboard.Island.Solo.Displayname")));
											scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
										} else {
											scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Scoreboard.Island.Team.Displayname")));
											scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
											
											Map<String, String> displayVariables = new HashMap<>();
											displayVariables.put("%owner", configLoad.getString("Scoreboard.Island.Team.Word.Owner"));
											displayVariables.put("%operator", configLoad.getString("Scoreboard.Island.Team.Word.Operator"));
											displayVariables.put("%member", configLoad.getString("Scoreboard.Island.Team.Word.Member"));
											
											scoreboard.setDisplayVariables(displayVariables);
										}
										
										scoreboard.run();
									}
								}
							}
						}
						
						return;
					}
				}
			}
			
			HashMap<UUID, Visit> visitIslands = plugin.getVisitManager().getIslands();
			
			for (UUID visitIslandList : visitIslands.keySet()) {
				Visit visit = visitIslands.get(visitIslandList);
				
				for (Location.World worldList : Location.World.values()) {
					if (LocationUtil.isLocationAtLocationRadius(player.getLocation(), visit.getLocation(worldList), visit.getRadius())) {
						BanManager banManager = plugin.getBanManager();
						
						Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
						FileConfiguration configLoad = config.getFileConfiguration();
						
			    		Player targetPlayer = Bukkit.getServer().getPlayer(visitIslandList);
			    		String targetPlayerName;
			    		
			    		if (targetPlayer == null) {
			    			targetPlayerName = new OfflinePlayer(visitIslandList).getName();
			    		} else {
			    			targetPlayerName = targetPlayer.getName();
			    		}
						
						if (banManager.hasIsland(visitIslandList) && fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Banning") && banManager.getIsland(visitIslandList).isBanned(player.getUniqueId())) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Island.Visit.Teleport.Island.Message").replace("%player", targetPlayerName)));
						} else {
							if (visit.isOpen()) {
								PlayerData playerData = getPlayerData(player);
								playerData.setIsland(visitIslandList);
								
								islandManager.loadIsland(visitIslandList);
								
								return;
							} else {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Island.Visit.Closed.Island.Message").replace("%player", targetPlayerName)));
							}
						}
						
			    		LocationUtil.teleportPlayerToSpawn(player);
						
						return;
					}
				}
			}
		}
	}
}
