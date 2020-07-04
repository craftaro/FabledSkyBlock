package com.songoda.skyblock.island;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.utils.PlayerUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.island.*;
import com.songoda.skyblock.api.utils.APIUtil;
import com.songoda.skyblock.ban.Ban;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.upgrade.Upgrade;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.world.WorldBorder;
import com.songoda.skyblock.visit.Visit;
import com.songoda.skyblock.world.WorldManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class Island {

    private final SkyBlock skyblock;
    private final com.songoda.skyblock.api.island.Island apiWrapper;

    private final Map<IslandRole, List<IslandPermission>> islandPermissions = new HashMap<>();
    private final List<IslandLocation> islandLocations = new ArrayList<>();
    private final Map<UUID, IslandCoop> coopPlayers = new HashMap<>();
    private final Set<UUID> whitelistedPlayers = new HashSet<>();

    private UUID islandUUID;
    private UUID ownerUUID;
    private IslandLevel level;
    private int size;
    private int maxMembers;
    private boolean deleted = false;

    public Island(OfflinePlayer player) {
        this.skyblock = SkyBlock.getInstance();

        FileManager fileManager = skyblock.getFileManager();

        this.islandUUID = UUID.randomUUID();
        this.ownerUUID = player.getUniqueId();
        this.size = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                .getInt("Island.Size.Minimum");
        this.maxMembers = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                .getInt("Island.Member.Capacity", 3);

        if (this.size > 1000) {
            this.size = 50;
        }

        if (player.isOnline()) {
            int customSize = PlayerUtils.getNumberFromPermission(player.getPlayer(), "fabledskyblock.size", 0);
            if (customSize > 0 || player.getPlayer().hasPermission("fabledskyblock.*")) {
                Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
                FileConfiguration configLoad = config.getFileConfiguration();

                int minimumSize = configLoad.getInt("Island.Size.Minimum");
                int maximumSize = configLoad.getInt("Island.Size.Maximum");

                if (minimumSize < 0 || minimumSize > 1000)
                    minimumSize = 50;
                if (maximumSize < 0 || maximumSize > 1000)
                    maximumSize = 100;

                size = Math.max(minimumSize, Math.min(customSize, maximumSize));
            }
        }

        level = new IslandLevel(getOwnerUUID(), skyblock);

        File configFile = new File(skyblock.getDataFolder().toString() + "/island-data");

        Config config = fileManager.getConfig(new File(configFile, ownerUUID + ".yml"));
        Config defaultSettingsConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "settings.yml"));
        Config mainConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));

        FileConfiguration mainConfigLoad = mainConfig.getFileConfiguration();

        if (fileManager.isFileExist(new File(configFile, ownerUUID + ".yml"))) {
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getString("UUID") != null) {
                islandUUID = UUID.fromString(configLoad.getString("UUID"));
            } else {
                configLoad.set("UUID", islandUUID.toString());
            }

            if (configLoad.getString("MaxMembers") != null) {
                maxMembers = configLoad.getInt("MaxMembers");
            } else {
                configLoad.set("MaxMembers", maxMembers);
            }

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
                configLoad.set("Border.Enable", mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Default", false));
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
                            || !playerDataConfigLoad.getString("Island.Owner").equals(ownerUUID.toString())) {
                        members.remove(i);
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
                            || !playerDataConfigLoad.getString("Island.Owner").equals(ownerUUID.toString())) {
                        operators.remove(i);
                    }
                }

                configLoad.set("Operators", operators);
            }

            Config settingsDataConfig = null;

            File settingDataFile = new File(skyblock.getDataFolder().toString() + "/setting-data", getOwnerUUID().toString() + ".yml");

            if (fileManager.isFileExist(settingDataFile)) {
                settingsDataConfig = fileManager.getConfig(settingDataFile);
            }

            for (IslandRole roleList : IslandRole.getRoles()) {
                List<BasicPermission> allPermissions = skyblock.getPermissionManager().getPermissions();
                List<IslandPermission> permissions = new ArrayList<>(allPermissions.size());

                for (BasicPermission permission : allPermissions) {
                    if (settingsDataConfig == null || settingsDataConfig.getFileConfiguration()
                            .getString("Settings." + roleList.name() + "." + permission.getName()) == null) {
                        permissions.add(
                                new IslandPermission(permission, defaultSettingsConfig.getFileConfiguration()
                                        .getBoolean("Settings." + roleList.name() + "." + permission.getName(), true)));
                    } else {
                        permissions.add(new IslandPermission(permission, settingsDataConfig.getFileConfiguration()
                                .getBoolean("Settings." + roleList.name() + "." + permission.getName(), true)));
                    }
                }

                islandPermissions.put(roleList, permissions);
            }
    
            if (configLoad.getString("Whitelist") != null) {
                for (String whitelistedUUID : configLoad.getStringList("Whitelist")) {
                    whitelistedPlayers.add(UUID.fromString(whitelistedUUID));
                }
            }
        } else {
            FileConfiguration configLoad = config.getFileConfiguration();

            configLoad.set("UUID", islandUUID.toString());
            configLoad.set("Visitor.Open", mainConfigLoad.getBoolean("Island.Visitor.Open"));
            configLoad.set("Border.Enable", mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Default", false));
            configLoad.set("Border.Color", WorldBorder.Color.Blue.name());
            configLoad.set("Biome.Type", mainConfigLoad.getString("Island.Biome.Default.Type").toUpperCase());
            configLoad.set("Weather.Synchronised", mainConfigLoad.getBoolean("Island.Weather.Default.Synchronised")); // TODO: Synchronized
            configLoad.set("Weather.Time", mainConfigLoad.getInt("Island.Weather.Default.Time"));
            configLoad.set("Weather.Weather", mainConfigLoad.getString("Island.Weather.Default.Weather").toUpperCase());
            configLoad.set("Ownership.Original", ownerUUID.toString());

            for (IslandRole roleList : IslandRole.getRoles()) {
                List<BasicPermission> allPermissions = skyblock.getPermissionManager().getPermissions();
                List<IslandPermission> permissions = new ArrayList<>(allPermissions.size());

                for (BasicPermission permission : allPermissions) {
                    permissions.add(
                            new IslandPermission(permission, defaultSettingsConfig.getFileConfiguration()
                                    .getBoolean("Settings." + roleList.name() + "." + permission.getName(), true)));
                }

                islandPermissions.put(roleList, permissions);
            }

            save();

            Player onlinePlayer = Bukkit.getServer().getPlayer(ownerUUID);

            if (!skyblock.getPlayerDataManager().hasPlayerData(onlinePlayer)) {
                skyblock.getPlayerDataManager().createPlayerData(onlinePlayer);
            }

            PlayerData playerData = skyblock.getPlayerDataManager().getPlayerData(onlinePlayer);
            playerData.setPlaytime(0);
            playerData.setOwner(ownerUUID);
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
                        coopPlayers.put(UUID.fromString(coopPlayerList), IslandCoop.NORMAL);
                    }
                }
            }
        }

        this.apiWrapper = new com.songoda.skyblock.api.island.Island(this, player);
    }

    public UUID getIslandUUID() {
        return islandUUID;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID uuid) {
        getVisit().setOwnerUUID(uuid);
        this.ownerUUID = uuid;
    }

    public UUID getOriginalOwnerUUID() {
        return UUID.fromString(skyblock.getFileManager().getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Ownership.Original"));
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        if (maxMembers > 100000 || maxMembers < 0) {
            maxMembers = 2;
        }

        this.maxMembers = maxMembers;
        skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("MaxMembers", maxMembers);
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
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Size", size);
    }

    public double getRadius() {
        return (size / 2) + 0.5;
    }

    public boolean hasPassword() {
        return skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Ownership.Password") != null;
    }

    public String getPassword() {
        return skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Ownership.Password");
    }

    public void setPassword(String password) {
        IslandPasswordChangeEvent islandPasswordChangeEvent = new IslandPasswordChangeEvent(getAPIWrapper(), password);
        Bukkit.getServer().getPluginManager().callEvent(islandPasswordChangeEvent);

        skyblock.getFileManager()
                .getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
                        ownerUUID.toString() + ".yml"))
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
                Bukkit.getScheduler().runTaskAsynchronously(skyblock, () ->
                        Bukkit.getServer().getPluginManager().callEvent(new IslandLocationChangeEvent(getAPIWrapper(),
                                new com.songoda.skyblock.api.island.IslandLocation(
                                        APIUtil.fromImplementation(environment), APIUtil.fromImplementation(world),
                                        location))));

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
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("Border.Enable");
    }

    public void setBorder(boolean border) {
        skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Border.Enable", border);
    }

    public WorldBorder.Color getBorderColor() {
        return WorldBorder.Color.valueOf(skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Border.Color"));
    }

    public void setBorderColor(WorldBorder.Color color) {
        skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Border.Color", color.name());
    }

    public boolean isInBorder(Location blockLocation) {
        WorldManager worldManager = skyblock.getWorldManager();
        if (!isBorder()) {
            return true;
        }

        Location islandLocation = getLocation(worldManager.getIslandWorld(blockLocation.getWorld()), IslandEnvironment.Island);
        double halfSize = Math.floor(getRadius());

        if (blockLocation.getBlockX() > (islandLocation.getBlockX() + halfSize)
                || blockLocation.getBlockX() < (islandLocation.getBlockX() - halfSize - 1)
                || blockLocation.getBlockZ() > (islandLocation.getBlockZ() + halfSize)
                || blockLocation.getBlockZ() < (islandLocation.getBlockZ() - halfSize - 1)) {
            return false;
        }

        return true;
    }

    public Biome getBiome() {
        return Biome.valueOf(skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Biome.Type"));
    }

    public void setBiome(Biome biome) {
        IslandBiomeChangeEvent islandBiomeChangeEvent = new IslandBiomeChangeEvent(getAPIWrapper(), biome);
        Bukkit.getServer().getPluginManager().callEvent(islandBiomeChangeEvent);

        skyblock.getFileManager()
                .getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
                        ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Biome.Type", islandBiomeChangeEvent.getBiome().name());
    }

    public String getBiomeName() {
        return WordUtils.capitalizeFully(getBiome().name().replace("_", " "));
    }

    public boolean isWeatherSynchronized() {
        return skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("Weather.Synchronised");
    }

    public void setWeatherSynchronized(boolean sync) {
        IslandWeatherChangeEvent islandWeatherChangeEvent = new IslandWeatherChangeEvent(getAPIWrapper(), getWeather(),
                getTime(), sync);
        Bukkit.getServer().getPluginManager().callEvent(islandWeatherChangeEvent);

        skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Weather.Synchronised", sync);
    }

    public WeatherType getWeather() {
        String weatherTypeName = skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Weather.Weather");

        WeatherType weatherType;

        if (weatherTypeName == null || weatherTypeName.isEmpty()) {
            weatherType = WeatherType.CLEAR;
        } else {
            try {
                weatherType = WeatherType.valueOf(weatherTypeName);
            } catch (IllegalArgumentException e) {
                weatherType = WeatherType.CLEAR;
            }
        }

        return weatherType;
    }

    public void setWeather(WeatherType weatherType) {
        IslandWeatherChangeEvent islandWeatherChangeEvent = new IslandWeatherChangeEvent(getAPIWrapper(), weatherType,
                getTime(), isWeatherSynchronized());
        Bukkit.getServer().getPluginManager().callEvent(islandWeatherChangeEvent);

        skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Weather.Weather", weatherType.name());
    }

    public String getWeatherName() {
        return WordUtils.capitalize(getWeather().name().toLowerCase());
    }

    public int getTime() {
        return skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getInt("Weather.Time");
    }

    public void setTime(int time) {
        IslandWeatherChangeEvent islandWeatherChangeEvent = new IslandWeatherChangeEvent(getAPIWrapper(), getWeather(),
                time, isWeatherSynchronized());
        Bukkit.getServer().getPluginManager().callEvent(islandWeatherChangeEvent);

        skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Weather.Time", time);
    }

    public Map<UUID, IslandCoop> getCoopPlayers() {
        return coopPlayers;
    }

    public void addCoopPlayer(UUID uuid, IslandCoop islandCoop) {
        coopPlayers.put(uuid, islandCoop);
        Bukkit.getScheduler().runTaskAsynchronously(skyblock, this::save);
    }

    public void removeCoopPlayer(UUID uuid) {
        coopPlayers.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(skyblock, this::save);
    }

    public boolean isCoopPlayer(UUID uuid) {
        return coopPlayers.containsKey(uuid);
    }

    public IslandCoop getCoopType(UUID uuid) {
        return coopPlayers.getOrDefault(uuid, null);
    }

    public void setAlwaysLoaded(boolean alwaysLoaded) {
        skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("AlwaysLoaded", alwaysLoaded);
    }

    public boolean isAlwaysLoaded() {
        return skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("AlwaysLoaded", false);
    }

    public Set<UUID> getRole(IslandRole role) {
        Set<UUID> islandRoles = new HashSet<>();

        if (role == IslandRole.Owner) {
            islandRoles.add(getOwnerUUID());
        } else {
            Config config = skyblock.getFileManager().getConfig(
                    new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getString(role.name() + "s") != null) {
                for (String playerList : configLoad.getStringList(role.name() + "s")) {
                    islandRoles.add(UUID.fromString(playerList));
                }
            }
        }

        return islandRoles;
    }

    public IslandRole getRole(OfflinePlayer player) {
        if(isCoopPlayer(player.getUniqueId())){
            return IslandRole.Coop; // TODO Rework Coop status - Fabrimat
        }
        for (IslandRole role : IslandRole.values())
            if (getRole(role).contains(player.getUniqueId()))
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
            return getOwnerUUID().equals(uuid) || skyblock.getIslandManager().isPlayerProxyingAnotherPlayer(uuid, getOwnerUUID());
        }

        return getRole(role).contains(uuid);
    }

    public void setUpgrade(Player player, Upgrade.Type type, boolean status) {
        skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Upgrade." + type.name(), status);

        Bukkit.getServer().getPluginManager()
                .callEvent(new IslandUpgradeEvent(getAPIWrapper(), player, APIUtil.fromImplementation(type)));
    }

    public void removeUpgrade(Upgrade.Type type) {
        skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Upgrade." + type.name(), null);
    }

    public boolean hasUpgrade(Upgrade.Type type) {
        return skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Upgrade." + type.name()) != null;
    }

    public boolean isUpgrade(Upgrade.Type type) {
        return skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("Upgrade." + type.name());
    }

    public boolean hasPermission(IslandRole role, BasicPermission permission) {
        if (islandPermissions.containsKey(role)) {
            for (IslandPermission islandPermission : islandPermissions.get(role)) {
                if (islandPermission.getPermission() == permission)
                    return islandPermission.getStatus();
            }
        }

        return true; //TODO: Default setting value
    }

    public IslandPermission getPermission(IslandRole role, BasicPermission permission) {
        if (islandPermissions.containsKey(role)) {
            for (IslandPermission islandPermission : islandPermissions.get(role)) {
                if (islandPermission.getPermission() == permission)
                    return islandPermission;
            }
        }

        return null;
    }

    public List<IslandPermission> getSettings(IslandRole role) {
        if (islandPermissions.containsKey(role))
            return Collections.unmodifiableList(islandPermissions.get(role));
        return Collections.emptyList();
    }

    public double getBankBalance() {
        return skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getDouble("Bank.Balance");
    }

    public void addToBank(double value) {
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager
                .getConfig(new File(skyblock.getDataFolder().toString() + "/island-data", ownerUUID.toString() + ".yml"));

        value = getBankBalance() + value;
        config.getFileConfiguration().set("Bank.Balance", value);

        try {
            config.getFileConfiguration().save(config.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeFromBank(double value) {
        addToBank(-value);
    }

    public boolean isOpen() {
        return skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("Visitor.Open");
    }

    public void setOpen(boolean open) {
        IslandOpenEvent islandOpenEvent = new IslandOpenEvent(getAPIWrapper(), open);
        Bukkit.getServer().getPluginManager().callEvent(islandOpenEvent);

        if (!islandOpenEvent.isCancelled()) {
            skyblock.getFileManager().getConfig(
                    new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                    .getFileConfiguration().set("Visitor.Open", open);
            getVisit().setOpen(open);
        }
    }

    public List<String> getMessage(IslandMessage message) {
        List<String> islandMessage = new ArrayList<>();

        Config config = skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getString("Visitor." + message.name() + ".Message") != null) {
            islandMessage = configLoad.getStringList("Visitor." + message.name() + ".Message");
        }

        return islandMessage;
    }

    public String getMessageAuthor(IslandMessage message) {
        Config config = skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
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
                    new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
            FileConfiguration configLoad = config.getFileConfiguration();
            configLoad.set("Visitor." + message.name() + ".Message", islandMessageChangeEvent.getLines());
            configLoad.set("Visitor." + message.name() + ".Author", islandMessageChangeEvent.getAuthor());

            if (message == IslandMessage.Signature) {
                getVisit().setSignature(lines);
            }
        }
    }

    public boolean hasStructure() {
        return getStructure() != null;
    }

    public String getStructure() {
        return skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Structure");
    }

    public void setStructure(String structure) {
        skyblock.getFileManager().getConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Structure", structure);
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public synchronized void save() {
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager
                .getConfig(new File(skyblock.getDataFolder().toString() + "/island-data", ownerUUID.toString() + ".yml"));
    
        config.getFileConfiguration().set("Whitelist", whitelistedPlayers);

        try {
            config.getFileConfiguration().save(config.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        config = fileManager
                .getConfig(new File(skyblock.getDataFolder().toString() + "/setting-data", ownerUUID.toString() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        for (Entry<IslandRole, List<IslandPermission>> entry : islandPermissions.entrySet()) {
            for (IslandPermission permission : entry.getValue()) {
                configLoad.set("Settings." + entry.getKey() + "." + permission.getPermission().getName(), permission.getStatus());
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
                    .getConfig(new File(skyblock.getDataFolder().toString() + "/coop-data", ownerUUID.toString() + ".yml"));
            configLoad = config.getFileConfiguration();

            List<String> coopPlayersAsString = new ArrayList<>(coopPlayers.size());

            for (Map.Entry<UUID, IslandCoop> entry : coopPlayers.entrySet()) {
                if (entry.getValue() == IslandCoop.TEMP) continue;
                coopPlayersAsString.add(entry.getKey().toString());
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

    public boolean isRegionUnlocked(Player player, IslandWorld type) {
        FileManager fileManager = skyblock.getFileManager();
        SoundManager soundManager = skyblock.getSoundManager();
        MessageManager messageManager = skyblock.getMessageManager();
        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        Config islandData = fileManager
                .getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
                        ownerUUID.toString() + ".yml"));
        FileConfiguration configLoadIslandData = islandData.getFileConfiguration();
        double price = configLoad.getDouble("Island.World." + type.name() + ".UnlockPrice");

        boolean unlocked = configLoadIslandData.getBoolean("Unlocked." + type.name());
        if (price == -1) {
            configLoadIslandData.set("Unlocked." + type.name(), true);
            unlocked = true;
        }

        if (!unlocked) {
            messageManager.sendMessage(player,
                    fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration()
                            .getString("Island.Unlock." + type.name() + ".Message").replace(
                            "%cost%", NumberUtil.formatNumberByDecimal(price)));

            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
            if(type.equals(IslandWorld.End)){
                player.setVelocity(player.getLocation().getDirection().multiply(-.50).setY(.6f));
            } else if(type.equals(IslandWorld.Nether)) {
                player.setVelocity(player.getLocation().getDirection().multiply(-.50));
            }
        }
        return unlocked;
    }

    public com.songoda.skyblock.api.island.Island getAPIWrapper() {
        return apiWrapper;
    }
    
    public void addWhitelistedPlayer(UUID uuid) {
        this.whitelistedPlayers.add(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(skyblock, this::save);
    }
    
    public boolean isPlayerWhitelisted(UUID uuid) {
        return this.whitelistedPlayers.contains(uuid);
    }
    
    public void removeWhitelistedPlayer(UUID uuid) {
        this.whitelistedPlayers.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(skyblock, this::save);
    }
    
    public void addWhitelistedPlayer(Player player) {
        this.addWhitelistedPlayer(player.getUniqueId());
    }
    
    public boolean isPlayerWhitelisted(Player player) {
        return this.isPlayerWhitelisted(player.getUniqueId());
    }
    
    public void removeWhitelistedPlayer(Player player) {
        this.removeWhitelistedPlayer(player.getUniqueId());
    }

}
