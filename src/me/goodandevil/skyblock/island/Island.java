package me.goodandevil.skyblock.island;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.ban.Ban;
import me.goodandevil.skyblock.ban.BanManager;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.events.IslandBiomeChangeEvent;
import me.goodandevil.skyblock.events.IslandLocationChangeEvent;
import me.goodandevil.skyblock.events.IslandMessageChangeEvent;
import me.goodandevil.skyblock.events.IslandOpenEvent;
import me.goodandevil.skyblock.events.IslandPasswordChangeEvent;
import me.goodandevil.skyblock.events.IslandRoleChangeEvent;
import me.goodandevil.skyblock.events.IslandWeatherChangeEvent;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.utils.StringUtil;
import me.goodandevil.skyblock.visit.Visit;
import me.goodandevil.skyblock.visit.VisitManager;

public class Island {

	private final Main plugin;
	
	private List<Location> islandLocations = new ArrayList<>();
	private Map<Settings.Role, Map<String, Settings>> islandSettings = new EnumMap<>(Settings.Role.class);
	
	private UUID ownerUUID;
	private Level level;
	private int size;
	
	public Island(UUID ownerUUID, org.bukkit.Location islandNormalLocation, org.bukkit.Location islandNetherLocation) {
		this.plugin = Main.getInstance();
		
		FileManager fileManager = plugin.getFileManager();
		IslandManager islandManager = plugin.getIslandManager();
		
		this.ownerUUID = ownerUUID;
		this.size = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getInt("Island.Size");;
		
		if (this.size > 1000) {
			this.size = 100;
		}
		
		islandLocations.add(new Location(Location.World.Normal, Location.Environment.Island, islandNormalLocation));
		islandLocations.add(new Location(Location.World.Nether, Location.Environment.Island, islandNetherLocation));
		
		File configFile = new File(plugin.getDataFolder().toString() + "/island-data");
		Config config = fileManager.getConfig(new File(configFile, ownerUUID + ".yml"));
		
		if (fileManager.isFileExist(new File(configFile, ownerUUID + ".yml"))) {
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (configLoad.getString("Size") != null) {
				size = configLoad.getInt("Size");
			}
			
			islandLocations.add(new Location(Location.World.Normal, Location.Environment.Main, fileManager.getLocation(config, "Location.Normal.Spawn.Main", true)));
			islandLocations.add(new Location(Location.World.Nether, Location.Environment.Main, fileManager.getLocation(config, "Location.Nether.Spawn.Main", true)));
			islandLocations.add(new Location(Location.World.Normal, Location.Environment.Visitor, fileManager.getLocation(config, "Location.Normal.Spawn.Visitor", true)));
			islandLocations.add(new Location(Location.World.Nether, Location.Environment.Visitor, fileManager.getLocation(config, "Location.Nether.Spawn.Visitor", true)));
			
			Config settingsConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "settings.yml"));
			
			for (Settings.Role roleList : Settings.Role.values()) {
				HashMap<String, Settings> roleSettings = new HashMap<>();
				
				for (String settingList : settingsConfig.getFileConfiguration().getConfigurationSection(WordUtils.capitalize(roleList.name().toLowerCase())).getKeys(false)) {
					roleSettings.put(settingList, new Settings(configLoad.getBoolean("Settings." + roleList.name() + "." + settingList)));
				}
				
				islandSettings.put(roleList, roleSettings);
			}
		} else {
			islandLocations.add(new Location(Location.World.Normal, Location.Environment.Main, islandNormalLocation.clone().add(0.5D, 0.0D, 0.5D)));
			islandLocations.add(new Location(Location.World.Nether, Location.Environment.Main, islandNetherLocation.clone().add(0.5D, 0.0D, 0.5D)));
			islandLocations.add(new Location(Location.World.Normal, Location.Environment.Visitor, islandNormalLocation.clone().add(0.5D, 0.0D, 0.5D)));
			islandLocations.add(new Location(Location.World.Nether, Location.Environment.Visitor, islandNetherLocation.clone().add(0.5D, 0.0D, 0.5D)));
			
			Config mainConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"));
			FileConfiguration mainConfigLoad = mainConfig.getFileConfiguration();
			
			fileManager.setLocation(config, "Location.Normal.Island", islandNormalLocation, true);
			fileManager.setLocation(config, "Location.Nether.Island", islandNetherLocation, true);
			fileManager.setLocation(config, "Location.Normal.Spawn.Main", islandNormalLocation.clone().add(0.5D, 0.0D, 0.5D), true);
			fileManager.setLocation(config, "Location.Nether.Spawn.Main", islandNetherLocation.clone().add(0.5D, 0.0D, 0.5D), true);
			fileManager.setLocation(config, "Location.Normal.Spawn.Visitor", islandNormalLocation.clone().add(0.5D, 0.0D, 0.5D), true);
			fileManager.setLocation(config, "Location.Nether.Spawn.Visitor", islandNetherLocation.clone().add(0.5D, 0.0D, 0.5D), true);
			
			configFile = config.getFile();
			FileConfiguration configLoad = config.getFileConfiguration();
			
			configLoad.set("Size", size);
			configLoad.set("Visitor.Open", mainConfigLoad.getBoolean("Island.Visitor.Open"));
			configLoad.set("Biome.Type", mainConfigLoad.getString("Island.Biome.Default.Type").toUpperCase());
			configLoad.set("Weather.Synchronised", mainConfigLoad.getBoolean("Island.Weather.Default.Synchronised"));
			configLoad.set("Weather.Time", mainConfigLoad.getInt("Island.Weather.Default.Time"));
			configLoad.set("Weather.Weather", mainConfigLoad.getString("Island.Weather.Default.Weather").toUpperCase());
			configLoad.set("Ownership.Original", ownerUUID.toString());
			
			Config settingsConfig = fileManager.getConfig(new File(plugin.getDataFolder(), "settings.yml"));
			
			for (Settings.Role roleList : Settings.Role.values()) {
				HashMap<String, Settings> roleSettings = new HashMap<>();
				
				for (String settingList : settingsConfig.getFileConfiguration().getConfigurationSection(WordUtils.capitalize(roleList.name().toLowerCase())).getKeys(false)) {
					roleSettings.put(settingList, new Settings(settingsConfig.getFileConfiguration().getBoolean(WordUtils.capitalize(roleList.name().toLowerCase()) + "." + settingList)));
				}
				
				islandSettings.put(roleList, roleSettings);
			}
			
			save();
			
			PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(Bukkit.getServer().getPlayer(ownerUUID));
			playerData.setPlaytime(0);
			playerData.setOwner(ownerUUID);
			playerData.setMemberSince(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
			playerData.save();
			
			if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Spawn.Protection")) {
				Bukkit.getServer().getScheduler().runTask(plugin, new Runnable() {
					@Override
					public void run() {
						islandNormalLocation.clone().subtract(0.0D, 1.0D, 0.0D).getBlock().setType(Material.STONE);
						islandManager.setSpawnProtection(islandNormalLocation);
						islandManager.setSpawnProtection(islandNetherLocation);
					}
				});
			}
		}
		
		level = new Level(getOwnerUUID(), plugin);
		
		VisitManager visitManager = plugin.getVisitManager();
		
		if (!visitManager.hasIsland(getOwnerUUID())) {
			visitManager.createIsland(getOwnerUUID(), new org.bukkit.Location[] { getLocation(Location.World.Normal, Location.Environment.Island), getLocation(Location.World.Nether, Location.Environment.Island) }, size, getRole(Role.Member).size() + getRole(Role.Operator).size() + 1, level, getMessage(Message.Signature), isOpen());
		}
		
		BanManager banManager = plugin.getBanManager();
		
		if (!banManager.hasIsland(getOwnerUUID())) {
			banManager.createIsland(getOwnerUUID());
		}
	}
	
	public UUID getOwnerUUID() {
		return ownerUUID;
	}
	
	public void setOwnerUUID(UUID uuid) {
		getVisit().setOwnerUUID(uuid);
		ownerUUID = uuid;
	}
	
	public UUID getOriginalOwnerUUID() {
		return UUID.fromString(plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().getString("Ownership.Original"));
	}
	
	public int getSize() {
		return size;
	}
	
	public double getRadius() {
		return (size / 2) + 0.5;
	}
	
	public boolean hasPassword() {
		return plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().getString("Ownership.Password") != null;
	}
	
	public String getPassword() {
		return plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().getString("Ownership.Password");
	}
	
	public void setPassword(String password) {
		IslandPasswordChangeEvent islandPasswordChangeEvent = new IslandPasswordChangeEvent(this, getPassword(), password);
		Bukkit.getServer().getPluginManager().callEvent(islandPasswordChangeEvent);
		
		if (!islandPasswordChangeEvent.isCancelled()) {
			plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().set("Ownership.Password", password);
		}
	}
	
	public org.bukkit.Location getLocation(Location.World world, Location.Environment environment) {
		for (Location islandLocationList : islandLocations) {
			if (islandLocationList.getWorld() == world && islandLocationList.getEnvironment() == environment) {
				return islandLocationList.getLocation();
			}
		}
		
		return null;
	}
	
	public void setLocation(Location.World world, Location.Environment environment, org.bukkit.Location location) {
		for (Location islandLocationList : islandLocations) {
			if (islandLocationList.getWorld() == world && islandLocationList.getEnvironment() == environment) {
				Bukkit.getServer().getPluginManager().callEvent(new IslandLocationChangeEvent(this, islandLocationList, new Location(world, environment, location)));
				
				FileManager fileManager = plugin.getFileManager();
				fileManager.setLocation(fileManager.getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), getOwnerUUID().toString() + ".yml")), "Location." + world.name() + ".Spawn." + environment.name(), location, true);
				
				islandLocationList.setLocation(location);
				
				break;
			}
		}
	}
	
	public Biome getBiome() {
		return Biome.valueOf(plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().getString("Biome.Type"));
	}
	
	public String getBiomeName() {
		return StringUtil.capatilizeUppercaseLetters(WordUtils.capitalize(getBiome().name().toLowerCase()).replace("_", " "));
	}
	
	public void setBiome(Biome biome) {
		Bukkit.getServer().getPluginManager().callEvent(new IslandBiomeChangeEvent(this, getBiome(), biome));
		plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().set("Biome.Type", biome.name());
	}
	
	public boolean isWeatherSynchronised() {
		return plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().getBoolean("Weather.Synchronised");
	}
	
	public void setWeatherSynchronised(boolean synchronised) {
		Bukkit.getServer().getPluginManager().callEvent(new IslandWeatherChangeEvent(this, getWeather(), getTime(), synchronised));
		plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().set("Weather.Synchronised", synchronised);
	}
	
	public WeatherType getWeather() {
		return WeatherType.valueOf(plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().getString("Weather.Weather"));
	}
	
	public String getWeatherName() {
		return WordUtils.capitalize(getWeather().name().toLowerCase());
	}
	
	public void setWeather(WeatherType weatherType) {
		Bukkit.getServer().getPluginManager().callEvent(new IslandWeatherChangeEvent(this, weatherType, getTime(), isWeatherSynchronised()));
		plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().set("Weather.Weather", weatherType.name());
	}
	
	public int getTime() {
		return plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().getInt("Weather.Time");
	}
	
	public void setTime(int time) {
		Bukkit.getServer().getPluginManager().callEvent(new IslandWeatherChangeEvent(this, getWeather(), time, isWeatherSynchronised()));
		plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().set("Weather.Time", time);
	}
	
	public List<UUID> getRole(Role role) {
		Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		List<UUID> islandRoles = new ArrayList<>();
		
		if (configLoad.getString(role.name() + "s") != null) {
			for (String operatorList : configLoad.getStringList(role.name() + "s")) {
				islandRoles.add(UUID.fromString(operatorList));
			}
		}
		
		return islandRoles;
	}
	
	public void setRole(Role role, UUID uuid) {
		if (!(role == Role.Visitor || role == Role.Owner)) {
			if (!isRole(role, uuid)) {
				if (role == Role.Member) {
					if (isRole(Role.Operator, uuid)) {
						Bukkit.getServer().getPluginManager().callEvent(new IslandRoleChangeEvent(uuid, this, Role.Operator, role));
						removeRole(Role.Operator, uuid);
					}
				} else if (role == Role.Operator) {
					if (isRole(Role.Member, uuid)) {
						Bukkit.getServer().getPluginManager().callEvent(new IslandRoleChangeEvent(uuid, this, Role.Member, role));
						removeRole(Role.Member, uuid);
					}
				}
				
				Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), getOwnerUUID().toString() + ".yml"));
				File configFile = config.getFile();
				FileConfiguration configLoad = config.getFileConfiguration();
				
				List<String> islandMembers;
				
				if (configLoad.getString(role.name() + "s") == null) {
					islandMembers = new ArrayList<>();
				} else {
					islandMembers = configLoad.getStringList(role.name() + "s");
				}
				
				islandMembers.add(uuid.toString());
				configLoad.set(role.name() + "s", islandMembers);
				
				try {
					configLoad.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				getVisit().setMembers(getRole(Role.Member).size() + getRole(Role.Operator).size() + 1);
			}
		}
	}
	
	public void removeRole(Role role, UUID uuid) {
		if (!(role == Role.Visitor || role == Role.Owner)) {
			if (isRole(role, uuid)) {
				Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
				File configFile = config.getFile();
				FileConfiguration configLoad = config.getFileConfiguration();
				List<String> islandMembers = configLoad.getStringList(role.name() + "s");
				
				islandMembers.remove(uuid.toString());
				configLoad.set(role.name() + "s", islandMembers);
				
				try {
					configLoad.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				getVisit().setMembers(getRole(Role.Member).size() + getRole(Role.Operator).size() + 1);
			}
		}
	}
	
	public boolean isRole(Role role, UUID uuid) {
		if (role == Role.Owner) {
			return getOwnerUUID().equals(uuid);
		}
		
		return getRole(role).contains(uuid);
	}
	
	public Settings getSetting(Settings.Role role, String setting) {
		if (islandSettings.containsKey(role)) {
			Map<String, Settings> roleSettings = islandSettings.get(role);
			
			if (roleSettings.containsKey(setting)) {
				return roleSettings.get(setting);
			}
		}
		
		return null;
	}
	
	public Map<String, Settings> getSettings(Settings.Role role) {
		if (islandSettings.containsKey(role)) {
			return islandSettings.get(role);
		}
		
		return null;
	}
	
	public void setOpen(boolean open) {
		IslandOpenEvent islandOpenEvent = new IslandOpenEvent(this, open);
		Bukkit.getServer().getPluginManager().callEvent(islandOpenEvent);
		
		if (!islandOpenEvent.isCancelled()) {
			plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().set("Visitor.Open", open);
			getVisit().setOpen(open);
		}
	}
	
	public boolean isOpen() {
		return plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml")).getFileConfiguration().getBoolean("Visitor.Open");
	}
	
	public List<UUID> getVisitors() {
		Map<UUID, PlayerData> playerDataStorage = plugin.getPlayerDataManager().getPlayerData();
		List<UUID> islandVisitors = new ArrayList<>();
		
		for (UUID playerDataStorageList : playerDataStorage.keySet()) {
			PlayerData playerData = playerDataStorage.get(playerDataStorageList);
			UUID islandOwnerUUID = playerData.getIsland();
			
			if (islandOwnerUUID != null && islandOwnerUUID.equals(getOwnerUUID())) {
				if (playerData.getOwner() == null || !playerData.getOwner().equals(getOwnerUUID())) {
					if (Bukkit.getServer().getPlayer(playerDataStorageList) != null) {
						islandVisitors.add(playerDataStorageList);
					}
				}
			}
		}
		
		return islandVisitors;
	}
	
	public List<String> getMessage(Message message) {
		List<String> islandMessage = new ArrayList<>();
		
		Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (configLoad.getString("Visitor." + message.name() + ".Message") != null) {
			islandMessage = configLoad.getStringList("Visitor." + message.name() + ".Message");
		}
		
		return islandMessage;
	}
	
	public String getMessageAuthor(Message message) {
		Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (configLoad.getString("Visitor." + message.name() + ".Author") != null) {
			return configLoad.getString("Visitor." + message.name() + ".Author");
		}
		
		return "";
	}
	
	public void setMessage(Message message, String author, List<String> islandMessage) {
		IslandMessageChangeEvent islandMessageChangeEvent = new IslandMessageChangeEvent(this, message, islandMessage, author);
		Bukkit.getServer().getPluginManager().callEvent(islandMessageChangeEvent);
		
		if (!islandMessageChangeEvent.isCancelled()) {
			Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			configLoad.set("Visitor." + message.name() + ".Message", islandMessage);
			configLoad.set("Visitor." + message.name() + ".Author", author);
			
			if (message == Message.Signature) {
				getVisit().setSignature(islandMessage);
			}
		}
	}
	
	public Visit getVisit() {
		return plugin.getVisitManager().getIsland(getOwnerUUID());
	}
	
	public Ban getBan() {
		return plugin.getBanManager().getIsland(getOwnerUUID());
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void save() {
		Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
		File configFile = config.getFile();
		FileConfiguration configLoad = config.getFileConfiguration();
		
		for (Settings.Role roleList : Settings.Role.values()) {
			if (islandSettings.containsKey(roleList)) {
				Map<String, Settings> roleSettings = islandSettings.get(roleList);
				
				for (String roleSettingList : roleSettings.keySet()) {
					configLoad.set("Settings." + roleList.name() + "." + roleSettingList, roleSettings.get(roleSettingList).getStatus());
				}
			}
		}
		
		try {
			configLoad.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
