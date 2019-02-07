package me.goodandevil.skyblock.island;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.version.Sounds;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.event.island.IslandBiomeChangeEvent;
import me.goodandevil.skyblock.api.event.island.IslandLocationChangeEvent;
import me.goodandevil.skyblock.api.event.island.IslandMessageChangeEvent;
import me.goodandevil.skyblock.api.event.island.IslandOpenEvent;
import me.goodandevil.skyblock.api.event.island.IslandPasswordChangeEvent;
import me.goodandevil.skyblock.api.event.island.IslandRoleChangeEvent;
import me.goodandevil.skyblock.api.event.island.IslandUpgradeEvent;
import me.goodandevil.skyblock.api.event.island.IslandWeatherChangeEvent;
import me.goodandevil.skyblock.api.utils.APIUtil;
import me.goodandevil.skyblock.ban.Ban;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.upgrade.Upgrade;
import me.goodandevil.skyblock.utils.StringUtil;
import me.goodandevil.skyblock.utils.world.WorldBorder;
import me.goodandevil.skyblock.visit.Visit;
import org.bukkit.util.Vector;

public class Island {

	private final SkyBlock skyblock;
	private final me.goodandevil.skyblock.api.island.Island apiWrapper;

	private Map<IslandRole, List<IslandSetting>> islandSettings = new HashMap<>();
	private List<IslandLocation> islandLocations = new ArrayList<>();
	private Set<UUID> coopPlayers = new HashSet<>();

	private UUID uuid;
	private IslandLevel level;
	private int size;
	private boolean deleted = false;

	public Island(OfflinePlayer player) {
		this.skyblock = SkyBlock.getInstance();

		FileManager fileManager = skyblock.getFileManager();

		this.uuid = player.getUniqueId();
		this.size = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
				.getInt("Island.Size.Minimum");

		if (this.size > 1000) {
			this.size = 50;
		}

		level = new IslandLevel(getOwnerUUID(), skyblock);

		File configFile = new File(skyblock.getDataFolder().toString() + "/island-data");

		Config config = fileManager.getConfig(new File(configFile, uuid + ".yml"));
		Config defaultSettingsConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "settings.yml"));
		Config mainConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));

		FileConfiguration mainConfigLoad = mainConfig.getFileConfiguration();

		if (fileManager.isFileExist(new File(configFile, uuid + ".yml"))) {
			FileConfiguration configLoad = config.getFileConfiguration();

			if (configLoad.getString("Size") != null) {
				size = configLoad.getInt("Size");
			}

			if (configLoad.getString("Settings") != null) {
				configLoad.set("Settings", null);
			}

			if (configLoad.getString("Levelling.Materials") != null) {
				configLoad.set("Levelling.Materials", null);
			}

			if (configLoad.getString("Border") == null) {
				configLoad.set("Border.Enable", true);
				configLoad.set("Border.Color", WorldBorder.Color.Blue.name());
			}

			if (configLoad.getString("Members") != null) {
				List<String> members = configLoad.getStringList("Members");

				for (int i = 0; i < members.size(); i++) {
					String member = members.get(i);
					Config playerDataConfig = new FileManager.Config(fileManager,
							new File(skyblock.getDataFolder().toString() + "/player-data", member + ".yml"));
					FileConfiguration playerDataConfigLoad = playerDataConfig.getFileConfiguration();

					if (playerDataConfigLoad.getString("Island.Owner") == null
							|| !playerDataConfigLoad.getString("Island.Owner").equals(uuid.toString())) {
						members.remove(member);
					}
				}

				configLoad.set("Members", members);
			}

			if (configLoad.getString("Operators") != null) {
				List<String> operators = configLoad.getStringList("Operators");

				for (int i = 0; i < operators.size(); i++) {
					String operator = operators.get(i);
					Config playerDataConfig = new FileManager.Config(fileManager,
							new File(skyblock.getDataFolder().toString() + "/player-data", operator + ".yml"));
					FileConfiguration playerDataConfigLoad = playerDataConfig.getFileConfiguration();

					if (playerDataConfigLoad.getString("Island.Owner") == null
							|| !playerDataConfigLoad.getString("Island.Owner").equals(uuid.toString())) {
						operators.remove(operator);
					}
				}

				configLoad.set("Operators", operators);
			}

			Config settingsDataConfig = null;

			if (fileManager.isFileExist(new File(skyblock.getDataFolder().toString() + "/setting-data",
					getOwnerUUID().toString() + ".yml"))) {
				settingsDataConfig = fileManager.getConfig(new File(
						skyblock.getDataFolder().toString() + "/setting-data", getOwnerUUID().toString() + ".yml"));
			}

			for (IslandRole roleList : IslandRole.values()) {
				List<IslandSetting> settings = new ArrayList<>();

				for (String settingList : defaultSettingsConfig.getFileConfiguration()
						.getConfigurationSection("Settings." + roleList.name()).getKeys(false)) {
					if (settingsDataConfig == null || settingsDataConfig.getFileConfiguration()
							.getString("Settings." + roleList.name() + "." + settingList) == null) {
						settings.add(new IslandSetting(settingList, defaultSettingsConfig.getFileConfiguration()
								.getBoolean("Settings." + roleList.name() + "." + settingList)));
					} else {
						settings.add(new IslandSetting(settingList, settingsDataConfig.getFileConfiguration()
								.getBoolean("Settings." + roleList.name() + "." + settingList)));
					}
				}

				islandSettings.put(roleList, settings);
			}
		} else {
			FileConfiguration configLoad = config.getFileConfiguration();

			configLoad.set("Visitor.Open", mainConfigLoad.getBoolean("Island.Visitor.Open"));
			configLoad.set("Border.Enable", true);
			configLoad.set("Border.Color", WorldBorder.Color.Blue.name());
			configLoad.set("Biome.Type", mainConfigLoad.getString("Island.Biome.Default.Type").toUpperCase());
			configLoad.set("Weather.Synchronised", mainConfigLoad.getBoolean("Island.Weather.Default.Synchronised"));
			configLoad.set("Weather.Time", mainConfigLoad.getInt("Island.Weather.Default.Time"));
			configLoad.set("Weather.Weather", mainConfigLoad.getString("Island.Weather.Default.Weather").toUpperCase());
			configLoad.set("Ownership.Original", uuid.toString());

			for (IslandRole roleList : IslandRole.values()) {
				List<IslandSetting> settings = new ArrayList<>();

				for (String settingList : defaultSettingsConfig.getFileConfiguration()
						.getConfigurationSection("Settings." + roleList.name()).getKeys(false)) {
					settings.add(new IslandSetting(settingList, defaultSettingsConfig.getFileConfiguration()
							.getBoolean("Settings." + roleList.name() + "." + settingList)));
				}

				islandSettings.put(roleList, settings);
			}

			save();

			PlayerData playerData = skyblock.getPlayerDataManager().getPlayerData(Bukkit.getServer().getPlayer(uuid));
			playerData.setPlaytime(0);
			playerData.setOwner(uuid);
			playerData.setMemberSince(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
			playerData.save();
		}

		if (!mainConfigLoad.getBoolean("Island.Coop.Unload")) {
			File coopDataFile = new File(skyblock.getDataFolder().toString() + "/coop-data",
					getOwnerUUID().toString() + ".yml");

			if (fileManager.isFileExist(coopDataFile)) {
				Config coopDataConfig = fileManager.getConfig(coopDataFile);
				FileConfiguration coopDataConfigLoad = coopDataConfig.getFileConfiguration();

				if (coopDataConfigLoad.getString("CoopPlayers") != null) {
					for (String coopPlayerList : coopDataConfigLoad.getStringList("CoopPlayers")) {
						coopPlayers.add(UUID.fromString(coopPlayerList));
					}
				}
			}
		}

		this.apiWrapper = new me.goodandevil.skyblock.api.island.Island(this, player);
	}

	public UUID getOwnerUUID() {
		return uuid;
	}

	public void setOwnerUUID(UUID uuid) {
		getVisit().setOwnerUUID(uuid);
		this.uuid = uuid;
	}

	public UUID getOriginalOwnerUUID() {
		return UUID
				.fromString(
						skyblock.getFileManager()
								.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
										uuid.toString() + ".yml"))
								.getFileConfiguration().getString("Ownership.Original"));
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		if (size > 1000 || size < 0) {
			size = 50;
		}

		this.size = size;
		skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().set("Size", size);
	}

	public double getRadius() {
		return (size / 2) + 0.5;
	}

	public boolean hasPassword() {
		return skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getString("Ownership.Password") != null;
	}

	public String getPassword() {
		return skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getString("Ownership.Password");
	}

	public void setPassword(String password) {
		IslandPasswordChangeEvent islandPasswordChangeEvent = new IslandPasswordChangeEvent(getAPIWrapper(), password);
		Bukkit.getServer().getPluginManager().callEvent(islandPasswordChangeEvent);

		skyblock.getFileManager()
				.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
						uuid.toString() + ".yml"))
				.getFileConfiguration().set("Ownership.Password", islandPasswordChangeEvent.getPassword());
	}

	public Location getLocation(IslandWorld world, IslandEnvironment environment) {
		for (IslandLocation islandLocationList : islandLocations) {
			if (islandLocationList.getWorld() == world && islandLocationList.getEnvironment() == environment) {
				return islandLocationList.getLocation();
			}
		}

		return null;
	}

	public IslandLocation getIslandLocation(IslandWorld world, IslandEnvironment environment) {
		for (IslandLocation islandLocationList : islandLocations) {
			if (islandLocationList.getWorld() == world && islandLocationList.getEnvironment() == environment) {
				return islandLocationList;
			}
		}

		return null;
	}

	public void addLocation(IslandWorld world, IslandEnvironment environment, Location location) {
		islandLocations.add(new IslandLocation(world, environment, location));
	}

	public void setLocation(IslandWorld world, IslandEnvironment environment, Location location) {
		for (IslandLocation islandLocationList : islandLocations) {
			if (islandLocationList.getWorld() == world && islandLocationList.getEnvironment() == environment) {
				Bukkit.getServer().getPluginManager()
						.callEvent(new IslandLocationChangeEvent(getAPIWrapper(),
								new me.goodandevil.skyblock.api.island.IslandLocation(
										APIUtil.fromImplementation(environment), APIUtil.fromImplementation(world),
										location)));

				FileManager fileManager = skyblock.getFileManager();

				if (environment == IslandEnvironment.Island) {
					fileManager.setLocation(
							fileManager
									.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
											getOwnerUUID().toString() + ".yml")),
							"Location." + world.name() + "." + environment.name(), location, true);
				} else {
					fileManager.setLocation(
							fileManager
									.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
											getOwnerUUID().toString() + ".yml")),
							"Location." + world.name() + ".Spawn." + environment.name(), location, true);
				}

				islandLocationList.setLocation(location);

				break;
			}
		}
	}

	public boolean isBorder() {
		return skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getBoolean("Border.Enable");
	}

	public void setBorder(boolean border) {
		skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().set("Border.Enable", border);
	}

	public WorldBorder.Color getBorderColor() {
		return WorldBorder.Color.valueOf(skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getString("Border.Color"));
	}

	public void setBorderColor(WorldBorder.Color color) {
		skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().set("Border.Color", color.name());
	}

	public Biome getBiome() {
		return Biome.valueOf(skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getString("Biome.Type"));
	}

	public String getBiomeName() {
		return StringUtil
				.capatilizeUppercaseLetters(WordUtils.capitalize(getBiome().name().toLowerCase()).replace("_", " "));
	}

	public void setBiome(Biome biome) {
		IslandBiomeChangeEvent islandBiomeChangeEvent = new IslandBiomeChangeEvent(getAPIWrapper(), biome);
		Bukkit.getServer().getPluginManager().callEvent(islandBiomeChangeEvent);

		skyblock.getFileManager()
				.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
						uuid.toString() + ".yml"))
				.getFileConfiguration().set("Biome.Type", islandBiomeChangeEvent.getBiome().name());
	}

	public boolean isWeatherSynchronized() {
		return skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getBoolean("Weather.Synchronised");
	}

	public void setWeatherSynchronized(boolean sync) {
		IslandWeatherChangeEvent islandWeatherChangeEvent = new IslandWeatherChangeEvent(getAPIWrapper(), getWeather(),
				getTime(), sync);
		Bukkit.getServer().getPluginManager().callEvent(islandWeatherChangeEvent);

		skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().set("Weather.Synchronised", sync);
	}

	public WeatherType getWeather() {
		String weatherTypeName = skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getString("Weather.Weather");

		WeatherType weatherType;

		if (weatherTypeName == null || weatherTypeName.isEmpty() || WeatherType.valueOf(weatherTypeName) == null) {
			weatherType = WeatherType.CLEAR;
		} else {
			weatherType = WeatherType.valueOf(weatherTypeName);
		}

		return weatherType;
	}

	public String getWeatherName() {
		return WordUtils.capitalize(getWeather().name().toLowerCase());
	}

	public void setWeather(WeatherType weatherType) {
		IslandWeatherChangeEvent islandWeatherChangeEvent = new IslandWeatherChangeEvent(getAPIWrapper(), weatherType,
				getTime(), isWeatherSynchronized());
		Bukkit.getServer().getPluginManager().callEvent(islandWeatherChangeEvent);

		skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().set("Weather.Weather", weatherType.name());
	}

	public int getTime() {
		return skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getInt("Weather.Time");
	}

	public void setTime(int time) {
		IslandWeatherChangeEvent islandWeatherChangeEvent = new IslandWeatherChangeEvent(getAPIWrapper(), getWeather(),
				time, isWeatherSynchronized());
		Bukkit.getServer().getPluginManager().callEvent(islandWeatherChangeEvent);

		skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().set("Weather.Time", time);
	}

	public Set<UUID> getCoopPlayers() {
		return coopPlayers;
	}

	public void addCoopPlayer(UUID uuid) {
		coopPlayers.add(uuid);
	}

	public void removeCoopPlayer(UUID uuid) {
		coopPlayers.remove(uuid);
	}

	public boolean isCoopPlayer(UUID uuid) {
		return coopPlayers.contains(uuid);
	}

	public Set<UUID> getRole(IslandRole role) {
		Set<UUID> islandRoles = new HashSet<>();

		if (role == IslandRole.Owner) {
			islandRoles.add(getOwnerUUID());
		} else {
			Config config = skyblock.getFileManager().getConfig(
					new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"));
			FileConfiguration configLoad = config.getFileConfiguration();

			if (configLoad.getString(role.name() + "s") != null) {
				for (String playerList : configLoad.getStringList(role.name() + "s")) {
					islandRoles.add(UUID.fromString(playerList));
				}
			}
		}

		return islandRoles;
	}

	public IslandRole getRole(OfflinePlayer player){
		for (IslandRole role : IslandRole.values())
			if(getRole(role).contains(player.getUniqueId()))
				return role;

		return IslandRole.Visitor;
	}

	public boolean setRole(IslandRole role, UUID uuid) {
		if (!(role == IslandRole.Visitor || role == IslandRole.Coop || role == IslandRole.Owner)) {
			if (!hasRole(role, uuid)) {
				if (role == IslandRole.Member) {
					if (hasRole(IslandRole.Operator, uuid)) {
						Bukkit.getServer().getPluginManager().callEvent(new IslandRoleChangeEvent(getAPIWrapper(),
								Bukkit.getServer().getOfflinePlayer(uuid), APIUtil.fromImplementation(role)));
						removeRole(IslandRole.Operator, uuid);
					}
				} else if (role == IslandRole.Operator) {
					if (hasRole(IslandRole.Member, uuid)) {
						Bukkit.getServer().getPluginManager().callEvent(new IslandRoleChangeEvent(getAPIWrapper(),
								Bukkit.getServer().getOfflinePlayer(uuid), APIUtil.fromImplementation(role)));
						removeRole(IslandRole.Member, uuid);
					}
				}

				Config config = skyblock.getFileManager()
						.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
								getOwnerUUID().toString() + ".yml"));
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

				getVisit().setMembers(getRole(IslandRole.Member).size() + getRole(IslandRole.Operator).size() + 1);

				return true;
			}
		}

		return false;
	}

	public boolean removeRole(IslandRole role, UUID uuid) {
		if (!(role == IslandRole.Visitor || role == IslandRole.Coop || role == IslandRole.Owner)) {
			if (hasRole(role, uuid)) {
				Config config = skyblock.getFileManager()
						.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
								getOwnerUUID().toString() + ".yml"));
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

				getVisit().setMembers(getRole(IslandRole.Member).size() + getRole(IslandRole.Operator).size() + 1);

				return true;
			}
		}

		return false;
	}

	public boolean hasRole(IslandRole role, UUID uuid) {
		if (role == IslandRole.Owner) {
			return getOwnerUUID().equals(uuid);
		}

		return getRole(role).contains(uuid);
	}

	public void setUpgrade(Player player, Upgrade.Type type, boolean status) {
		skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().set("Upgrade." + type.name(), status);

		Bukkit.getServer().getPluginManager()
				.callEvent(new IslandUpgradeEvent(getAPIWrapper(), player, APIUtil.fromImplementation(type)));
	}

	public void removeUpgrade(Upgrade.Type type) {
		skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().set("Upgrade." + type.name(), null);
	}

	public boolean hasUpgrade(Upgrade.Type type) {
		if (skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getString("Upgrade." + type.name()) == null) {
			return false;
		}

		return true;
	}

	public boolean isUpgrade(Upgrade.Type type) {
		return skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getBoolean("Upgrade." + type.name());
	}

	public IslandSetting getSetting(IslandRole role, String setting) {
		if (islandSettings.containsKey(role)) {
			for (IslandSetting settingList : islandSettings.get(role)) {
				if (settingList.getName().equalsIgnoreCase(setting)) {
					return settingList;
				}
			}
		}

		return new IslandSetting(setting, true); //TODO: Default setting value
	}

	public List<IslandSetting> getSettings(IslandRole role) {
		if (islandSettings.containsKey(role)) {
			return islandSettings.get(role);
		}

		return null;
	}

	public double getBankBalance() {
		return skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getDouble("Bank.Balance");
	}

	public void addToBank(double value) {
		value = value + getBankBalance();
		skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().set("Bank.Balance", value);
	}

	public void removeFromBank(double value) {
		value = getBankBalance() - value;
		skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().set("Bank.Balance", value);
	}

	public boolean isOpen() {
		return skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getBoolean("Visitor.Open");
	}

	public void setOpen(boolean open) {
		IslandOpenEvent islandOpenEvent = new IslandOpenEvent(getAPIWrapper(), open);
		Bukkit.getServer().getPluginManager().callEvent(islandOpenEvent);

		if (!islandOpenEvent.isCancelled()) {
			skyblock.getFileManager().getConfig(
					new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
					.getFileConfiguration().set("Visitor.Open", open);
			getVisit().setOpen(open);
		}
	}

	public List<String> getMessage(IslandMessage message) {
		List<String> islandMessage = new ArrayList<>();

		Config config = skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (configLoad.getString("Visitor." + message.name() + ".Message") != null) {
			islandMessage = configLoad.getStringList("Visitor." + message.name() + ".Message");
		}

		return islandMessage;
	}

	public String getMessageAuthor(IslandMessage message) {
		Config config = skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (configLoad.getString("Visitor." + message.name() + ".Author") != null) {
			return configLoad.getString("Visitor." + message.name() + ".Author");
		}

		return "";
	}

	public void setMessage(IslandMessage message, String author, List<String> lines) {
		IslandMessageChangeEvent islandMessageChangeEvent = new IslandMessageChangeEvent(getAPIWrapper(),
				APIUtil.fromImplementation(message), lines, author);
		Bukkit.getServer().getPluginManager().callEvent(islandMessageChangeEvent);

		if (!islandMessageChangeEvent.isCancelled()) {
			Config config = skyblock.getFileManager().getConfig(
					new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			configLoad.set("Visitor." + message.name() + ".Message", islandMessageChangeEvent.getLines());
			configLoad.set("Visitor." + message.name() + ".Author", islandMessageChangeEvent.getAuthor());

			if (message == IslandMessage.Signature) {
				getVisit().setSignature(lines);
			}
		}
	}

	public void setStructure(String structure) {
		skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().set("Structure", structure);
	}

	public boolean hasStructure() {
		if (getStructure() != null) {
			return true;
		}

		return false;
	}

	public String getStructure() {
		return skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"))
				.getFileConfiguration().getString("Structure");
	}

	public Visit getVisit() {
		return skyblock.getVisitManager().getIsland(getOwnerUUID());
	}

	public Ban getBan() {
		return skyblock.getBanManager().getIsland(getOwnerUUID());
	}

	public IslandLevel getLevel() {
		return level;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void save() {
		FileManager fileManager = skyblock.getFileManager();

		Config config = fileManager
				.getConfig(new File(skyblock.getDataFolder().toString() + "/island-data", uuid.toString() + ".yml"));

		try {
			config.getFileConfiguration().save(config.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}

		config = fileManager
				.getConfig(new File(skyblock.getDataFolder().toString() + "/setting-data", uuid.toString() + ".yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		for (IslandRole roleList : islandSettings.keySet()) {
			for (IslandSetting settingList : islandSettings.get(roleList)) {
				configLoad.set("Settings." + roleList + "." + settingList.getName(), settingList.getStatus());
			}
		}

		try {
			configLoad.save(config.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
				.getBoolean("Island.Coop.Unload")) {
			config = fileManager
					.getConfig(new File(skyblock.getDataFolder().toString() + "/coop-data", uuid.toString() + ".yml"));
			configLoad = config.getFileConfiguration();

			List<String> coopPlayersAsString = new ArrayList<>();

			for (UUID coopPlayerList : coopPlayers) {
				coopPlayersAsString.add(coopPlayerList.toString());
			}

			configLoad.set("CoopPlayers", coopPlayersAsString);

			try {
				configLoad.save(config.getFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		getLevel().save();
	}

	public boolean isRegionUnlocked(Player player, String type) {
		FileManager fileManager = skyblock.getFileManager();
		SoundManager soundManager = skyblock.getSoundManager();
		MessageManager messageManager = skyblock.getMessageManager();
		Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		Config islandData = fileManager
				.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
						uuid.toString() + ".yml"));
		FileConfiguration configLoadIslandData = islandData.getFileConfiguration();
		double price = configLoad.getDouble("Island.World." + type + ".UnlockPrice");

		boolean unlocked =
						configLoadIslandData.getBoolean("Unlocked." + type);
		if (price == -1) {
			configLoadIslandData.set("Unlocked." + type, true);
			unlocked = true;
		}

		if (!unlocked) {
			messageManager.sendMessage(player,
					fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration()
							.getString("Island.Unlock." + type + ".Message").replace(
							"%cost%", String.valueOf(price)));

			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			player.setVelocity(player.getLocation().getDirection().multiply(-.50));
		}
		return unlocked;
	}

	public me.goodandevil.skyblock.api.island.Island getAPIWrapper() {
		return apiWrapper;
	}
}
