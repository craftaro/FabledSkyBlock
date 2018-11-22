package me.goodandevil.skyblock.island;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.ban.BanManager;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.events.IslandCreateEvent;
import me.goodandevil.skyblock.events.IslandDeleteEvent;
import me.goodandevil.skyblock.events.IslandLoadEvent;
import me.goodandevil.skyblock.events.IslandOwnershipTransferEvent;
import me.goodandevil.skyblock.events.IslandUnloadEvent;
import me.goodandevil.skyblock.invite.Invite;
import me.goodandevil.skyblock.invite.InviteManager;
import me.goodandevil.skyblock.island.Location.World;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.scoreboard.Scoreboard;
import me.goodandevil.skyblock.scoreboard.ScoreboardManager;
import me.goodandevil.skyblock.structure.Structure;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.structure.StructureUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.utils.world.WorldBorder;
import me.goodandevil.skyblock.utils.world.block.BlockDegreesType;
import me.goodandevil.skyblock.visit.VisitManager;

public class IslandManager {
	
	private final Main plugin;
	
	private double x = 0, offset = 1200;
	
	private List<IslandLocation> islandLocations = new ArrayList<>();
	private Map<UUID, Island> islandStorage = new HashMap<>();
	
	public IslandManager(Main plugin) {
		this.plugin = plugin;
		
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		for (Location.World worldList : Location.World.values()) {
			ConfigurationSection configSection = configLoad.getConfigurationSection("World." + worldList.name() + ".nextAvailableLocation");
			islandLocations.add(new IslandLocation(worldList, configSection.getDouble("x"), configSection.getDouble("z")));
		}
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			loadIsland(all.getUniqueId());
		}
	}

	public void onDisable() {
		saveNextAvailableLocation();
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			unloadIsland(all.getUniqueId());
		}
	}
	
	public void saveNextAvailableLocation() {
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
		
		File configFile = config.getFile();
		FileConfiguration configLoad = config.getFileConfiguration();
		
		for (Location.World worldList : Location.World.values()) {
			for (IslandLocation islandLocationList : islandLocations) {
				if (islandLocationList.getWorld() == worldList) {
					ConfigurationSection configSection = configLoad.createSection("World." + worldList.name() + ".nextAvailableLocation");
					configSection.set("x", islandLocationList.getX());
					configSection.set("z", islandLocationList.getZ());
				}
			}
		}
		
		try {
			configLoad.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setNextAvailableLocation(Location.World world, org.bukkit.Location location) {
		for (IslandLocation islandLocationList : islandLocations) {
			if (islandLocationList.getWorld() == world) {
				islandLocationList.setX(location.getX());
				islandLocationList.setZ(location.getZ());
			}
		}
	}
	
	public org.bukkit.Location prepareNextAvailableLocation(Location.World world) {
		for (IslandLocation islandLocationList : islandLocations) {
			if (islandLocationList.getWorld() == world) {
				double x = islandLocationList.getX() + offset, z = islandLocationList.getZ();
				
				if (x > Math.abs(this.x)) {
					z += offset;
					islandLocationList.setX(this.x);
					x = islandLocationList.getX() + offset;
					islandLocationList.setZ(z);
				}
				
				return new org.bukkit.Location(plugin.getWorldManager().getWorld(world), x, 72, z);
			}
		}
		
		return null;
	}
	
	public void createIsland(Player player, Structure structure) {
		ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
		FileManager fileManager = plugin.getFileManager();
		
		Island island = new Island(player.getUniqueId(), prepareNextAvailableLocation(Location.World.Normal), prepareNextAvailableLocation(Location.World.Nether));
		islandStorage.put(player.getUniqueId(), island);
		
		try {
			File structureFile = new File(new File(plugin.getDataFolder().toString() + "/structures"), structure.getFile());
			
			for (World worldList : World.values()) {
				Float[] direction = StructureUtil.pasteStructure(StructureUtil.loadStructure(structureFile), island.getLocation(worldList, Location.Environment.Island), BlockDegreesType.ROTATE_360);
				org.bukkit.Location spawnLocation = island.getLocation(worldList, Location.Environment.Main).clone();
				spawnLocation.setYaw(direction[0]);
				spawnLocation.setPitch(direction[1]);
				island.setLocation(worldList, Location.Environment.Main, spawnLocation);
				island.setLocation(worldList, Location.Environment.Visitor, spawnLocation);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		/*if (configLoad.getBoolean("Island.Creation.Cooldown.Creation.Enable")) {
			plugin.getCreationManager().createPlayer(player, configLoad.getInt("Island.Creation.Cooldown.Time"));
		}*/
		
		Bukkit.getServer().getPluginManager().callEvent(new IslandCreateEvent(player, island));
		
		for (Location.World worldList : Location.World.values()) {
			setNextAvailableLocation(worldList, island.getLocation(worldList, Location.Environment.Island));
		}
		
		saveNextAvailableLocation();
		
		plugin.getPlayerDataManager().getPlayerData(player).setIsland(player.getUniqueId());
		
		config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		configLoad = config.getFileConfiguration();
		
		if (scoreboardManager != null) {
			Scoreboard scoreboard = scoreboardManager.getScoreboard(player);
			scoreboard.cancel();
			scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Scoreboard.Island.Solo.Displayname")));
			scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
			scoreboard.run();
		}
		
		new BukkitRunnable() {
			@Override
			public void run() {
				player.teleport(island.getLocation(Location.World.Normal, Location.Environment.Main));
			}
		}.runTask(plugin);
	}
	
	public void giveIslandOwnership(UUID uuid) {
		FileManager fileManager = plugin.getFileManager();
		PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
		
		Player targetPlayer = Bukkit.getServer().getPlayer(uuid);
		UUID islandOwnerUUID;
		
		if (targetPlayer == null) {
			OfflinePlayer offlinePlayer = new OfflinePlayer(uuid);
			islandOwnerUUID = offlinePlayer.getOwner();
		} else {
			islandOwnerUUID = playerDataManager.getPlayerData(targetPlayer).getOwner();
		}
		
		if (containsIsland(islandOwnerUUID)) {
			Island island = getIsland(islandOwnerUUID);
			island.setOwnerUUID(uuid);
			
			Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (configLoad.getBoolean("Island.Ownership.Password.Reset")) {
				island.setPassword(null);
			}
			
			File oldIslandDataFile = new File(new File(plugin.getDataFolder().toString() + "/island-data"), islandOwnerUUID.toString() + ".yml");
			File newIslandDataFile = new File(new File(plugin.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml");
			
			fileManager.unloadConfig(oldIslandDataFile);
			fileManager.unloadConfig(newIslandDataFile);
			oldIslandDataFile.renameTo(newIslandDataFile);
			
			plugin.getVisitManager().transfer(uuid, islandOwnerUUID);
			plugin.getBanManager().transfer(uuid, islandOwnerUUID);
			plugin.getInviteManager().tranfer(uuid, islandOwnerUUID);
			
			if (configLoad.getBoolean("Island.Ownership.Transfer.Operator")) {
				island.setRole(Role.Operator, islandOwnerUUID);
			} else {
				island.setRole(Role.Member, islandOwnerUUID);
			}
			
			if (island.isRole(Role.Member, uuid)) {
				island.removeRole(Role.Member, uuid);
			} else {
				island.removeRole(Role.Operator, uuid);
			}
			
			removeIsland(islandOwnerUUID);
			islandStorage.put(uuid, island);
			
			Bukkit.getServer().getPluginManager().callEvent(new IslandOwnershipTransferEvent(island, islandOwnerUUID, uuid));
			
			ArrayList<UUID> islandMembers = new ArrayList<>();
			islandMembers.addAll(island.getRole(Role.Member));
			islandMembers.addAll(island.getRole(Role.Operator));
			islandMembers.add(uuid);
			
			for (UUID islandMemberList : islandMembers) {
				targetPlayer = Bukkit.getServer().getPlayer(islandMemberList);
				
				if (targetPlayer == null) {
					File configFile = new File(new File(plugin.getDataFolder().toString() + "/player-data"), islandMemberList.toString() + ".yml");
					configLoad = YamlConfiguration.loadConfiguration(configFile);
					configLoad.set("Island.Owner", uuid.toString());
					
					try {
						configLoad.save(configFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					PlayerData playerData = playerDataManager.getPlayerData(targetPlayer);
					playerData.setOwner(uuid);
					playerData.save();
				}
			}
		}
	}

	public void deleteIsland(Island island) {
		plugin.getVisitManager().removeVisitors(island, VisitManager.Removal.Deleted);
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (island.isRole(Role.Member, all.getUniqueId()) || island.isRole(Role.Operator, all.getUniqueId()) || island.isRole(Role.Owner, all.getUniqueId())) {
				PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(all);
				playerData.setOwner(null);
				playerData.setMemberSince(null);
				playerData.setChat(false);
				playerData.save();
			}
			
			InviteManager inviteManager = plugin.getInviteManager();
			
			if (inviteManager.hasInvite(all.getUniqueId())) {
				Invite invite = inviteManager.getInvite(all.getUniqueId());
				
				if (invite.getOwnerUUID().equals(island.getOwnerUUID())) {
					inviteManager.removeInvite(all.getUniqueId());
				}
			}
		}
		
		FileManager fileManager = plugin.getFileManager();
		fileManager.deleteConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), island.getOwnerUUID().toString() + ".yml"));
		
		/*Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (configLoad.getBoolean("Island.Creation.Cooldown.Deletion.Enable")) {
			plugin.getCreationManager().createPlayer(island.getOwnerUUID(), configLoad.getInt("Island.Creation.Cooldown.Time"));
		}*/
		
		Bukkit.getServer().getPluginManager().callEvent(new IslandDeleteEvent(island));
		
		islandStorage.remove(island.getOwnerUUID());
	}

	public void loadIsland(UUID uuid) {
		FileManager fileManager = plugin.getFileManager();
		UUID islandOwnerUUID = null;
		
		if (isIslandExist(uuid)) {
			islandOwnerUUID = uuid;
		} else {
			Config config = fileManager.getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), uuid.toString() + ".yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (configLoad.getString("Island.Owner") != null) {
				islandOwnerUUID = UUID.fromString(configLoad.getString("Island.Owner"));
			}
		}
		
		if (islandOwnerUUID != null && !hasIsland(islandOwnerUUID)) {
			File configFile = new File(plugin.getDataFolder().toString() + "/island-data");
			Config config = fileManager.getConfig(new File(configFile, islandOwnerUUID.toString() + ".yml"));
			
			org.bukkit.Location islandNormalLocation = fileManager.getLocation(config, "Location.Normal.Island", true);
			org.bukkit.Location islandNetherLocation = fileManager.getLocation(config, "Location.Nether.Island", true);
			
			Island island = new Island(islandOwnerUUID, new org.bukkit.Location(islandNormalLocation.getWorld(), islandNormalLocation.getBlockX(), 72, islandNormalLocation.getBlockZ()), new org.bukkit.Location(islandNetherLocation.getWorld(), islandNetherLocation.getBlockX(), 72, islandNetherLocation.getBlockZ()));
			islandStorage.put(islandOwnerUUID, island);
			
			Bukkit.getServer().getPluginManager().callEvent(new IslandLoadEvent(island));
		}
	}

	public void unloadIsland(UUID uuid) {
		ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
		FileManager fileManager = plugin.getFileManager();
		
		if (hasIsland(uuid)) {
			UUID islandOwnerUUID = uuid;
			
			if (!isIslandExist(uuid)) {
				Config config = fileManager.getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), uuid.toString() + ".yml"));
				FileConfiguration configLoad = config.getFileConfiguration();
				
				if (configLoad.getString("Island.Owner") != null) {
					islandOwnerUUID = UUID.fromString(configLoad.getString("Island.Owner"));
				}
			}
			
			Island island = getIsland(islandOwnerUUID);
			island.save();
			
			Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			int islandMembers = island.getRole(Role.Member).size() + island.getRole(Role.Operator).size() + 1, islandVisitors = island.getVisitors().size();
			
			for (Player all : Bukkit.getOnlinePlayers()) {
				if (!uuid.equals(islandOwnerUUID)) {
					if (all.getUniqueId().equals(uuid)) {
						continue;
					}
				}
				
				if (island.isRole(Role.Member, all.getUniqueId()) || island.isRole(Role.Operator, all.getUniqueId()) || island.isRole(Role.Owner, all.getUniqueId())) {
					if (scoreboardManager != null) {
						try {
							if (islandMembers == 1 && islandVisitors == 0) {
								Scoreboard scoreboard = scoreboardManager.getScoreboard(all);
								scoreboard.cancel();
								scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Scoreboard.Island.Solo.Displayname")));
								scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
								scoreboard.run();
							} else if (islandVisitors == 0) {
								Scoreboard scoreboard = scoreboardManager.getScoreboard(all);
								scoreboard.cancel();
								scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Scoreboard.Island.Team.Displayname")));
								scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Island.Team.Empty.Displaylines"));
								
								HashMap<String, String> displayVariables = new HashMap<>();
								displayVariables.put("%owner", configLoad.getString("Scoreboard.Island.Team.Word.Owner"));
								displayVariables.put("%operator", configLoad.getString("Scoreboard.Island.Team.Word.Operator"));
								displayVariables.put("%member", configLoad.getString("Scoreboard.Island.Team.Word.Member"));
								
								scoreboard.setDisplayVariables(displayVariables);
								scoreboard.run();
							}
						} catch (IllegalPluginAccessException e) {}
					}
					
					return;
				}
			}
			
			boolean unloadIsland = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Unload");
			
			if (unloadIsland) {
				VisitManager visitManager = plugin.getVisitManager();
				visitManager.removeVisitors(island, VisitManager.Removal.Unloaded);
				visitManager.unloadIsland(islandOwnerUUID);
				
				BanManager banManager = plugin.getBanManager();
				banManager.unloadIsland(islandOwnerUUID);
			} else {
				if (island.getVisitors().size() != 0) {
					return;
				}
			}
			
			fileManager.unloadConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), islandOwnerUUID + ".yml"));
			islandStorage.remove(islandOwnerUUID);
			
			Bukkit.getServer().getPluginManager().callEvent(new IslandUnloadEvent(island));
		}
	}
	
	public void visitIsland(Player player, Island island) {
		ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
		FileManager fileManager = plugin.getFileManager();
		
		Config languageConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = languageConfig.getFileConfiguration();
		
		if (island.isRole(Role.Member, player.getUniqueId()) || island.isRole(Role.Operator, player.getUniqueId()) || island.isRole(Role.Owner, player.getUniqueId())) {
			player.teleport(island.getLocation(Location.World.Normal, Location.Environment.Visitor));
		} else {
			if (scoreboardManager != null) {
				int islandVisitors = island.getVisitors().size(), islandMembers = island.getRole(Role.Member).size() + island.getRole(Role.Operator).size() + 1;
				
				if (islandVisitors == 0) {
					for (Player all : Bukkit.getOnlinePlayers()) {
						PlayerData targetPlayerData = plugin.getPlayerDataManager().getPlayerData(all);
						
						if (targetPlayerData.getOwner() != null && targetPlayerData.getOwner().equals(island.getOwnerUUID())) {
							Scoreboard scoreboard = scoreboardManager.getScoreboard(all);
							scoreboard.cancel();
							
							if (islandMembers == 1) {
								scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Scoreboard.Island.Solo.Displayname")));
								scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
							} else {
								scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Scoreboard.Island.Team.Displayname")));
								scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));
								
								HashMap<String, String> displayVariables = new HashMap<>();
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
			
			player.teleport(island.getLocation(Location.World.Normal, Location.Environment.Visitor));
			
			List<String> islandWelcomeMessage = island.getMessage(Message.Welcome);
			
			if (plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable") && islandWelcomeMessage.size() != 0) {
				for (String islandWelcomeMessageList : islandWelcomeMessage) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', islandWelcomeMessageList));
				}
			}
		}
		
		player.closeInventory();
	}
	
	public void closeIsland(Island island) {
		island.setOpen(false);
		
		UUID islandOwnerUUID = island.getOwnerUUID();
		Player islandOwnerPlayer = Bukkit.getServer().getPlayer(islandOwnerUUID);
		String islandOwnerPlayerName;
		
		if (islandOwnerPlayer == null) {
			islandOwnerPlayerName = new OfflinePlayer(islandOwnerUUID).getName();
		} else {
			islandOwnerPlayerName = islandOwnerPlayer.getName();
		}
		
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		for (UUID visitorList : island.getVisitors()) {
    		Player targetPlayer = Bukkit.getServer().getPlayer(visitorList);
    		
			LocationUtil.teleportPlayerToSpawn(targetPlayer);
			
			targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Island.Visit.Closed.Island.Message").replace("%player", islandOwnerPlayerName)));
		}
	}
	
	public Island getIsland(UUID islandOwnerUUID) {
		if (islandStorage.containsKey(islandOwnerUUID)) {
			return islandStorage.get(islandOwnerUUID);
		}
		
		return null;
	}
	
	public void removeIsland(UUID islandOwnerUUID) {
		islandStorage.remove(islandOwnerUUID);
	}

	public Map<UUID, Island> getIslands() {
		return islandStorage;
	}
	
	public boolean isIslandExist(UUID uuid) {
		return plugin.getFileManager().isFileExist(new File(new File(plugin.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"));
	}
	
	public boolean hasIsland(UUID uuid) {
		UUID islandOwnerUUID = uuid;
		
		if (!isIslandExist(uuid)) {
			Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/player-data"), uuid.toString() + ".yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (configLoad.getString("Island.Owner") != null) {
				islandOwnerUUID = UUID.fromString(configLoad.getString("Island.Owner"));
			}
		}
		
		return islandStorage.containsKey(islandOwnerUUID);
	}
	
	public boolean hasIsland(Player player) {
		PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
		return (playerData == null) ? hasIsland(player.getUniqueId()) : islandStorage.containsKey(playerData.getOwner());
	}
	
	public boolean containsIsland(UUID uuid) {
		return islandStorage.containsKey(uuid);
	}
	
	public boolean hasPermission(Player player, String setting) {
		if (hasIsland(player)) {
			Island island = getIsland(plugin.getPlayerDataManager().getPlayerData(player).getOwner());
			
			for (Location.World worldList : Location.World.values()) {
				if (LocationUtil.isLocationAtLocationRadius(player.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
					if (island.isRole(Role.Member, player.getUniqueId())) {
						if (!island.getSetting(Settings.Role.Member, setting).getStatus()) {
							return false;
						}
					}
					
					return true;
				}
			}
		}
		
		for (UUID islandList : getIslands().keySet()) {
			Island island = getIslands().get(islandList);
			
			for (Location.World worldList : Location.World.values()) {
				if (LocationUtil.isLocationAtLocationRadius(player.getLocation(), island.getLocation(worldList, Location.Environment.Island), island.getRadius())) {
					if (!island.getSetting(Settings.Role.Visitor, setting).getStatus()) {
						return false;
					}
					
					return true;
				}
			}
		}
		
		return true;
	}
	
	public void setSpawnProtection(org.bukkit.Location location) {
		location.getBlock().setType(Materials.LEGACY_PISTON_MOVING_PIECE.getPostMaterial());
		location.clone().add(0.0D, 1.0D, 0.0D).getBlock().setType(Materials.LEGACY_PISTON_MOVING_PIECE.getPostMaterial());
	}
	
	public void removeSpawnProtection(org.bukkit.Location location) {
		location.getBlock().setType(Material.AIR);
		location.clone().add(0.0D, 1.0D, 0.0D).getBlock().setType(Material.AIR);
	}
	
	public List<UUID> getMembersOnline(Island island) {
		List<UUID> membersOnline = new ArrayList<>();
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (island.isRole(Role.Member, all.getUniqueId()) || island.isRole(Role.Operator, all.getUniqueId()) || island.isRole(Role.Owner, all.getUniqueId())) {
				membersOnline.add(all.getUniqueId());
			}
		}
		
		return membersOnline;
	}
	
	public List<UUID> getPlayersAtIsland(Island island) {
		List<UUID> playersAtIsland = new ArrayList<>();
		Map<UUID, PlayerData> playerData = plugin.getPlayerDataManager().getPlayerData();
		
		for (UUID playerDataList : playerData.keySet()) {
			UUID islandOwnerUUID = playerData.get(playerDataList).getIsland();
			
			if (islandOwnerUUID != null && island.getOwnerUUID().equals(islandOwnerUUID)) {
				playersAtIsland.add(playerDataList);
			}
		}
		
		return playersAtIsland;
	}
	
	public List<Player> getPlayersAtIsland(Island island, Location.World world) {
		List<Player> playersAtIsland = new ArrayList<>();
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (LocationUtil.isLocationAtLocationRadius(all.getLocation(), island.getLocation(world, Location.Environment.Island), island.getRadius())) {
				playersAtIsland.add(all);
			}
		}
		
		return playersAtIsland;
	}
	
	public void loadPlayer(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (player.getWorld().getName().equals(plugin.getWorldManager().getWorld(Location.World.Normal).getName())) {
					Island island = null;
					
					if (hasIsland(player)) {
						island = getIsland(plugin.getPlayerDataManager().getPlayerData(player).getOwner());
						
						if (!LocationUtil.isLocationAtLocationRadius(player.getLocation(), island.getLocation(Location.World.Normal, Location.Environment.Island), island.getRadius())) {
							island = null;
						}
					}
					
					if (island == null) {
						for (UUID islandList : getIslands().keySet()) {
							Island targetIsland = getIslands().get(islandList);
							
							if (LocationUtil.isLocationAtLocationRadius(player.getLocation(), targetIsland.getLocation(Location.World.Normal, Location.Environment.Island), targetIsland.getRadius())) {
								island = targetIsland;
								
								break;
							}
						}
					}
					
					if (island != null) {
						Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
						FileConfiguration configLoad = config.getFileConfiguration();
						
						if (!island.isWeatherSynchronised()) {
							player.setPlayerTime(island.getTime(), configLoad.getBoolean("Island.Weather.Time.Cycle"));
							player.setPlayerWeather(island.getWeather());	
						}
						
						if (configLoad.getBoolean("Island.WorldBorder.Enable")) {
							WorldBorder.send(player, island.getSize(), island.getLocation(Location.World.Normal, Location.Environment.Island));
						}
					}
				}
			}
		}.runTaskAsynchronously(plugin);
	}
}
