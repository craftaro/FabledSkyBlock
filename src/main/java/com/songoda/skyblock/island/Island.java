package com.songoda.skyblock.island;

import com.eatthepath.uuid.FastUUID;
import com.songoda.core.compatibility.CompatibleBiome;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.utils.PlayerUtils;
import com.songoda.core.world.SWorldBorder;
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
import com.songoda.core.utils.NumberUtils;
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

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class Island {

    private final SkyBlock plugin;
    private final com.songoda.skyblock.api.island.Island apiWrapper;

    private final Map<IslandRole, List<IslandPermission>> islandPermissions = new HashMap<>();
    private final List<IslandLocation> islandLocations = new ArrayList<>();
    private final Map<UUID, IslandCoop> coopPlayers = new HashMap<>();
    private final Set<UUID> whitelistedPlayers = new HashSet<>();

    private UUID islandUUID;
    private UUID ownerUUID;
    private final IslandLevel level;
    private IslandStatus status;
    private int size;
    private int maxMembers;
    private boolean deleted = false;

    public Island(@Nonnull OfflinePlayer player) {
        this.plugin = SkyBlock.getInstance();

        FileManager fileManager = plugin.getFileManager();

        this.islandUUID = UUID.randomUUID();
        this.ownerUUID = player.getUniqueId();
        this.size = this.plugin.getConfiguration().getInt("Island.Size.Minimum");
        this.maxMembers =  this.plugin.getConfiguration().getInt("Island.Member.Capacity", 3);

        if (this.size > 1000) {
            this.size = 50;
        }

        if (player.isOnline()) {
            int customSize = PlayerUtils.getNumberFromPermission(player.getPlayer(), "fabledskyblock.size", 0);
            if (customSize > 0 || player.getPlayer().hasPermission("fabledskyblock.*")) {
                FileConfiguration configLoad = this.plugin.getConfiguration();

                int minimumSize = configLoad.getInt("Island.Size.Minimum");
                int maximumSize = configLoad.getInt("Island.Size.Maximum");

                if (minimumSize < 0 || minimumSize > 1000)
                    minimumSize = 50;
    
                /*if(minimumSize % 2 != 0) {
                    minimumSize += 1;
                }*/
                
                if (maximumSize < 0 || maximumSize > 1000)
                    maximumSize = 100;
                
                /*if(maximumSize % 2 != 0) {
                    maximumSize += 1;
                }*/

                size = Math.max(minimumSize, Math.min(customSize, maximumSize));
            }
        }

        level = new IslandLevel(getOwnerUUID(), plugin);

        File configFile = new File(plugin.getDataFolder().toString() + "/island-data");

        Config config = fileManager.getConfig(new File(configFile, ownerUUID + ".yml"));

        FileConfiguration mainConfigLoad = this.plugin.getConfiguration();

        if (fileManager.isFileExist(new File(configFile, ownerUUID + ".yml"))) {
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getString("UUID") != null) {
                islandUUID = FastUUID.parseUUID(configLoad.getString("UUID"));
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
            } else {
                configLoad.set("Size", size);
            }

            if (configLoad.getString("Settings") != null) {
                configLoad.set("Settings", null);
            }

            if (configLoad.getString("Levelling.Materials") != null) {
                configLoad.set("Levelling.Materials", null);
            }

            if (configLoad.getString("Border") == null) {
                configLoad.set("Border.Enable", mainConfigLoad.getBoolean("Island.WorldBorder.Default", false));
                configLoad.set("Border.Color", SWorldBorder.Color.Blue.name());
            }

            if (configLoad.getString("Members") != null) {
                List<String> members = configLoad.getStringList("Members");

                for (int i = 0; i < members.size(); i++) {
                    String member = members.get(i);
                    Config playerDataConfig = new FileManager.Config(fileManager,
                            new File(plugin.getDataFolder().toString() + "/player-data", member + ".yml"));
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
                            new File(plugin.getDataFolder().toString() + "/player-data", operator + ".yml"));
                    FileConfiguration playerDataConfigLoad = playerDataConfig.getFileConfiguration();

                    if (playerDataConfigLoad.getString("Island.Owner") == null
                            || !playerDataConfigLoad.getString("Island.Owner").equals(ownerUUID.toString())) {
                        operators.remove(i);
                    }
                }

                configLoad.set("Operators", operators);
            }

            Config settingsDataConfig = null;

            File settingDataFile = new File(plugin.getDataFolder().toString() + "/setting-data", getOwnerUUID().toString() + ".yml");

            if (fileManager.isFileExist(settingDataFile)) {
                settingsDataConfig = fileManager.getConfig(settingDataFile);
            }

            for (IslandRole roleList : IslandRole.getRoles()) {
                List<BasicPermission> allPermissions = plugin.getPermissionManager().getPermissions();
                List<IslandPermission> permissions = new ArrayList<>(allPermissions.size());

                for (BasicPermission permission : allPermissions) {
                    if (settingsDataConfig == null || settingsDataConfig.getFileConfiguration()
                            .getString("Settings." + roleList.name() + "." + permission.getName()) == null) {
                        permissions.add(
                                new IslandPermission(permission, this.plugin.getSettings()
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
                    whitelistedPlayers.add(FastUUID.parseUUID(whitelistedUUID));
                }
            }
    
            String open = configLoad.getString("Visitor.Open", null);
            if(open != null && (open.equalsIgnoreCase("true") ||
                    open.equalsIgnoreCase("false"))) {
                if(configLoad.getBoolean("Visitor.Open")) {
                    status = IslandStatus.OPEN;
                } else {
                    status = IslandStatus.CLOSED;
                }
                configLoad.set("Visitor.Open", null);
                configLoad.set("Visitor.Status", mainConfigLoad.getString("Island.Visitor.Status"));
            } else {
                if(configLoad.getString("Visitor.Status") != null) {
                    status = IslandStatus.getEnum(configLoad.getString("Visitor.Status"));
                } else {
                    status = IslandStatus.WHITELISTED;
                    configLoad.set("Visitor.Status", mainConfigLoad.getString("Island.Visitor.Status"));
                }
            }
        } else {
            FileConfiguration configLoad = config.getFileConfiguration();

            configLoad.set("UUID", islandUUID.toString());
            configLoad.set("Visitor.Status", mainConfigLoad.getString("Island.Visitor.Status").toUpperCase());
            configLoad.set("Border.Enable", mainConfigLoad.getBoolean("Island.WorldBorder.Default", false));
            configLoad.set("Border.Color", SWorldBorder.Color.Blue.name());
            configLoad.set("Biome.Type", mainConfigLoad.getString("Island.Biome.Default.Type").toUpperCase());
            configLoad.set("Weather.Synchronised", mainConfigLoad.getBoolean("Island.Weather.Default.Synchronised")); // TODO: Synchronized
            configLoad.set("Weather.Time", mainConfigLoad.getInt("Island.Weather.Default.Time"));
            configLoad.set("Weather.Weather", mainConfigLoad.getString("Island.Weather.Default.Weather").toUpperCase());
            configLoad.set("Ownership.Original", ownerUUID.toString());
            configLoad.set("Size", size);

            for (IslandRole roleList : IslandRole.getRoles()) {
                List<BasicPermission> allPermissions = plugin.getPermissionManager().getPermissions();
                List<IslandPermission> permissions = new ArrayList<>(allPermissions.size());

                for (BasicPermission permission : allPermissions) {
                    permissions.add(
                            new IslandPermission(permission, this.plugin.getSettings()
                                    .getBoolean("Settings." + roleList.name() + "." + permission.getName(), true)));
                }

                islandPermissions.put(roleList, permissions);
            }

            status = IslandStatus.getEnum(mainConfigLoad.getString("Island.Visitor.Status"));
            

            Player onlinePlayer = Bukkit.getServer().getPlayer(ownerUUID);

            if (!plugin.getPlayerDataManager().hasPlayerData(onlinePlayer)) {
                plugin.getPlayerDataManager().createPlayerData(onlinePlayer);
            }

            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(onlinePlayer);
            playerData.setPlaytime(0);
            playerData.setOwner(ownerUUID);
            playerData.setMemberSince(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            playerData.save();
        }
        
        if (!mainConfigLoad.getBoolean("Island.Coop.Unload")) {
            File coopDataFile = new File(plugin.getDataFolder().toString() + "/coop-data",
                    getOwnerUUID().toString() + ".yml");

            if (fileManager.isFileExist(coopDataFile)) {
                Config coopDataConfig = fileManager.getConfig(coopDataFile);
                FileConfiguration coopDataConfigLoad = coopDataConfig.getFileConfiguration();

                if (coopDataConfigLoad.getString("CoopPlayers") != null) {
                    for (String coopPlayerList : coopDataConfigLoad.getStringList("CoopPlayers")) {
                        coopPlayers.put(FastUUID.parseUUID(coopPlayerList), IslandCoop.NORMAL);
                    }
                }
            }
        }

        save();

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
        return FastUUID.parseUUID(plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Ownership.Original"));
    }

    public int getMaxMembers(Player player) {
        try {
            return PlayerUtils.getNumberFromPermission(Objects.requireNonNull(player.getPlayer()), "fabledskyblock.members", maxMembers);
        } catch (Exception ignored) {
            return maxMembers;
        }
    }


    public void setMaxMembers(int maxMembers) {
        if (maxMembers > 100000 || maxMembers < 0) {
            maxMembers = 2;
        }

        this.maxMembers = maxMembers;
        plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
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
        plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Size", size);
    }

    public double getRadius() {
        return (((size%2==0) ? size : (size-1d)) / 2d);
    }

    public boolean hasPassword() {
        return plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Ownership.Password") != null;
    }

    public String getPassword() {
        return plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Ownership.Password");
    }

    public void setPassword(String password) {
        IslandPasswordChangeEvent islandPasswordChangeEvent = new IslandPasswordChangeEvent(getAPIWrapper(), password);
        Bukkit.getServer().getPluginManager().callEvent(islandPasswordChangeEvent);

        plugin.getFileManager()
                .getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"),
                        ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Ownership.Password", islandPasswordChangeEvent.getPassword());
    }

    public Location getLocation(IslandWorld world, IslandEnvironment environment) {
        for (IslandLocation islandLocationList : islandLocations) {
            if (islandLocationList.getWorld().equals(world) && islandLocationList.getEnvironment().equals(environment)) {
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
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                        Bukkit.getServer().getPluginManager().callEvent(new IslandLocationChangeEvent(getAPIWrapper(),
                                new com.songoda.skyblock.api.island.IslandLocation(
                                        APIUtil.fromImplementation(environment), APIUtil.fromImplementation(world),
                                        location))));

                FileManager fileManager = plugin.getFileManager();

                if (environment == IslandEnvironment.Island) {
                    fileManager.setLocation(
                            fileManager
                                    .getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"),
                                            getOwnerUUID().toString() + ".yml")),
                            "Location." + world.name() + "." + environment.name(), location, true);
                } else {
                    fileManager.setLocation(
                            fileManager
                                    .getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"),
                                            getOwnerUUID().toString() + ".yml")),
                            "Location." + world.name() + ".Spawn." + environment.name(), location, true);
                }

                islandLocationList.setLocation(location);

                break;
            }
        }
    }

    public boolean isBorder() {
        return plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("Border.Enable");
    }

    public void setBorder(boolean border) {
        plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Border.Enable", border);
    }

    public SWorldBorder.Color getBorderColor() {
        return SWorldBorder.Color.valueOf(plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Border.Color"));
    }

    public void setBorderColor(SWorldBorder.Color color) {
        plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Border.Color", color.name());
    }

    public boolean isInBorder(Location blockLocation) {
        WorldManager worldManager = plugin.getWorldManager();
        if (!isBorder()) {
            return true;
        }

        Location islandLocation = getLocation(worldManager.getIslandWorld(blockLocation.getWorld()), IslandEnvironment.Island);
        double halfSize = Math.floor(getRadius());
    
        return !(blockLocation.getBlockX() > (islandLocation.getBlockX() + halfSize))
                && !(blockLocation.getBlockX() < (islandLocation.getBlockX() - halfSize - 1))
                && !(blockLocation.getBlockZ() > (islandLocation.getBlockZ() + halfSize))
                && !(blockLocation.getBlockZ() < (islandLocation.getBlockZ() - halfSize - 1));
    }

    public Biome getBiome() {
        return CompatibleBiome.getBiome(plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Biome.Type")).getBiome();
    }

    public void setBiome(Biome biome) {
        IslandBiomeChangeEvent islandBiomeChangeEvent = new IslandBiomeChangeEvent(getAPIWrapper(), biome);
        Bukkit.getServer().getPluginManager().callEvent(islandBiomeChangeEvent);

        plugin.getFileManager()
                .getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"),
                        ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Biome.Type", islandBiomeChangeEvent.getBiome().name());
    }

    public String getBiomeName() {
        return WordUtils.capitalizeFully(getBiome().name().replace("_", " "));
    }

    public boolean isWeatherSynchronized() {
        return plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("Weather.Synchronised");
    }

    public void setWeatherSynchronized(boolean sync) {
        IslandWeatherChangeEvent islandWeatherChangeEvent = new IslandWeatherChangeEvent(getAPIWrapper(), getWeather(),
                getTime(), sync);
        Bukkit.getServer().getPluginManager().callEvent(islandWeatherChangeEvent);

        plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Weather.Synchronised", sync);
    }

    public WeatherType getWeather() {
        String weatherTypeName = plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
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

        plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Weather.Weather", weatherType.name());
    }

    public String getWeatherName() {
        return WordUtils.capitalize(getWeather().name().toLowerCase());
    }

    public int getTime() {
        return plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getInt("Weather.Time");
    }

    public void setTime(int time) {
        IslandWeatherChangeEvent islandWeatherChangeEvent = new IslandWeatherChangeEvent(getAPIWrapper(), getWeather(),
                time, isWeatherSynchronized());
        Bukkit.getServer().getPluginManager().callEvent(islandWeatherChangeEvent);

        plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Weather.Time", time);
    }

    public Map<UUID, IslandCoop> getCoopPlayers() {
        return coopPlayers;
    }

    public void addCoopPlayer(UUID uuid, IslandCoop islandCoop) {
        coopPlayers.put(uuid, islandCoop);
        save();
    }

    public void removeCoopPlayer(UUID uuid) {
        coopPlayers.remove(uuid);
        save();
    }

    public boolean isCoopPlayer(UUID uuid) {
        return coopPlayers.containsKey(uuid);
    }

    public IslandCoop getCoopType(UUID uuid) {
        return coopPlayers.getOrDefault(uuid, null);
    }

    public void setAlwaysLoaded(boolean alwaysLoaded) {
        plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("AlwaysLoaded", alwaysLoaded);
    }

    public boolean isAlwaysLoaded() {
        return plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("AlwaysLoaded", false);
    }

    public Set<UUID> getRole(IslandRole role) {
        Set<UUID> islandRoles = new HashSet<>();

        if (role == IslandRole.Owner) {
            islandRoles.add(getOwnerUUID());
        } else {
            Config config = plugin.getFileManager().getConfig(
                    new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getString(role.name() + "s") != null) {
                for (String playerList : configLoad.getStringList(role.name() + "s")) {
                    islandRoles.add(FastUUID.parseUUID(playerList));
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

                Config config = plugin.getFileManager()
                        .getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"),
                                getOwnerUUID().toString() + ".yml"));
                File configFile = config.getFile();
                FileConfiguration configLoad = config.getFileConfiguration();

                List<String> islandMembers;

                if (configLoad.getString(role.name() + "s") == null) {
                    islandMembers = new ArrayList<>();
                } else {
                    islandMembers = configLoad.getStringList(role.name() + "s");
                }

                islandMembers.add(FastUUID.toString(uuid));
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
                Config config = plugin.getFileManager()
                        .getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"),
                                getOwnerUUID().toString() + ".yml"));
                File configFile = config.getFile();
                FileConfiguration configLoad = config.getFileConfiguration();
                List<String> islandMembers = configLoad.getStringList(role.name() + "s");

                islandMembers.remove(FastUUID.toString(uuid));
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
        return getRole(role).contains(uuid) ||
                (plugin.getIslandManager().getPlayerProxyingAnotherPlayer(uuid) != null &&
                        getRole(role).contains(plugin.getIslandManager().getPlayerProxyingAnotherPlayer(uuid)));
    }

    public void setUpgrade(Player player, Upgrade.Type type, boolean status) {
        plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Upgrade." + type.name(), status);

        Bukkit.getServer().getPluginManager()
                .callEvent(new IslandUpgradeEvent(getAPIWrapper(), player, APIUtil.fromImplementation(type)));
    }

    public void removeUpgrade(Upgrade.Type type) {
        plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Upgrade." + type.name(), null);
    }

    public boolean hasUpgrade(Upgrade.Type type) {
        return plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Upgrade." + type.name()) != null;
    }

    public boolean isUpgrade(Upgrade.Type type) {
        return plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("Upgrade." + type.name());
    }

    public boolean hasPermission(IslandRole role, BasicPermission permission) {
        if (islandPermissions.containsKey(role)) {
            for (IslandPermission islandPermission : islandPermissions.get(role)) {
                if (islandPermission.getPermission().equals(permission))
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
        return plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getDouble("Bank.Balance");
    }

    public void addToBank(double value) {
        FileManager fileManager = plugin.getFileManager();

        Config config = fileManager
                .getConfig(new File(plugin.getDataFolder().toString() + "/island-data", ownerUUID.toString() + ".yml"));

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
    
    public IslandStatus getStatus() {
        return this.status;
    }
    
    public void setStatus(IslandStatus status) {
        IslandOpenEvent islandOpenEvent = new IslandOpenEvent(getAPIWrapper(), status.equals(IslandStatus.OPEN));
        Bukkit.getServer().getPluginManager().callEvent(islandOpenEvent);
        if(islandOpenEvent.isCancelled()) {
            return;
        }
    
        IslandStatusChangeEvent islandStatusChangeEvent = new IslandStatusChangeEvent(getAPIWrapper(), APIUtil.fromImplementation(status));
        Bukkit.getServer().getPluginManager().callEvent(islandStatusChangeEvent);
        if(!islandStatusChangeEvent.isCancelled()) {
            this.status = status;
            getVisit().setStatus(status);
            save();
        }
    }

    public List<String> getMessage(IslandMessage message) {
        List<String> islandMessage = new ArrayList<>();

        Config config = plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getString("Visitor." + message.name() + ".Message") != null) {
            islandMessage = configLoad.getStringList("Visitor." + message.name() + ".Message");
        }

        return islandMessage;
    }

    public String getMessageAuthor(IslandMessage message) {
        Config config = plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
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
            Config config = plugin.getFileManager().getConfig(
                    new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
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
        return plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Structure");
    }

    public void setStructure(String structure) {
        plugin.getFileManager().getConfig(
                new File(new File(plugin.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Structure", structure);
    }

    public Visit getVisit() {
        return plugin.getVisitManager().getIsland(getOwnerUUID());
    }

    public Ban getBan() {
        return plugin.getBanManager().getIsland(getOwnerUUID());
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
        FileManager fileManager = plugin.getFileManager();

        Config config = fileManager
                .getConfig(new File(plugin.getDataFolder().toString() + "/island-data", ownerUUID.toString() + ".yml"));
    
        List<String> tempWhitelist = new ArrayList<>();
        for(UUID uuid : whitelistedPlayers) {
            tempWhitelist.add(FastUUID.toString(uuid));
        }
        config.getFileConfiguration().set("Whitelist", tempWhitelist);
        config.getFileConfiguration().set("Visitor.Status", status.toString());

        try {
            config.getFileConfiguration().save(config.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        config = fileManager
                .getConfig(new File(plugin.getDataFolder().toString() + "/setting-data", ownerUUID.toString() + ".yml"));
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

        if (!this.plugin.getConfiguration().getBoolean("Island.Coop.Unload")) {
            config = fileManager
                    .getConfig(new File(plugin.getDataFolder().toString() + "/coop-data", ownerUUID.toString() + ".yml"));
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
        FileManager fileManager = plugin.getFileManager();
        SoundManager soundManager = plugin.getSoundManager();
        MessageManager messageManager = plugin.getMessageManager();
        FileConfiguration configLoad = this.plugin.getConfiguration();
        Config islandData = fileManager
                .getConfig(new File(new File(plugin.getDataFolder().toString() + "/island-data"),
                        ownerUUID.toString() + ".yml"));
        FileConfiguration configLoadIslandData = islandData.getFileConfiguration();
        double price = configLoad.getDouble("Island.World." + type.name() + ".UnlockPrice");

        boolean unlocked = configLoadIslandData.getBoolean("Unlocked." + type.name());
        if (price == -1) {
            configLoadIslandData.set("Unlocked." + type.name(), true);
            unlocked = true;
        }

        if (!unlocked && player != null) {
            messageManager.sendMessage(player,
                    this.plugin.getLanguage()
                            .getString("Island.Unlock." + type.name() + ".Message").replace(
                            "%cost%", NumberUtils.formatNumber(price)));

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
        save();
    }
    
    public boolean isPlayerWhitelisted(UUID uuid) {
        return this.whitelistedPlayers.contains(uuid);
    }
    
    public void removeWhitelistedPlayer(UUID uuid) {
        this.whitelistedPlayers.remove(uuid);
        save();
    }
    
    public Set<UUID> getWhitelistedPlayers() {
        return new HashSet<>(whitelistedPlayers);
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

    @Override
    public String toString() {
        return "Island{" +
                "ownerUUID=" + ownerUUID +
                '}';
    }
}
