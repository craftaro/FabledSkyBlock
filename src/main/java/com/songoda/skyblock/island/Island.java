package com.songoda.skyblock.island;

import com.craftaro.core.compatibility.CompatibleBiome;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.NumberUtils;
import com.craftaro.core.utils.PlayerUtils;
import com.craftaro.core.world.SWorldBorder;
import com.eatthepath.uuid.FastUUID;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.island.IslandBiomeChangeEvent;
import com.songoda.skyblock.api.event.island.IslandLocationChangeEvent;
import com.songoda.skyblock.api.event.island.IslandMessageChangeEvent;
import com.songoda.skyblock.api.event.island.IslandOpenEvent;
import com.songoda.skyblock.api.event.island.IslandPasswordChangeEvent;
import com.songoda.skyblock.api.event.island.IslandRoleChangeEvent;
import com.songoda.skyblock.api.event.island.IslandStatusChangeEvent;
import com.songoda.skyblock.api.event.island.IslandUpgradeEvent;
import com.songoda.skyblock.api.event.island.IslandWeatherChangeEvent;
import com.songoda.skyblock.api.utils.APIUtil;
import com.songoda.skyblock.ban.Ban;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.upgrade.Upgrade;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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

        FileManager fileManager = this.plugin.getFileManager();

        this.islandUUID = UUID.randomUUID();
        this.ownerUUID = player.getUniqueId();
        this.size = this.plugin.getConfiguration().getInt("Island.Size.Minimum");
        this.maxMembers = this.plugin.getConfiguration().getInt("Island.Member.Capacity", 3);

        if (this.size > 1000) {
            this.size = 50;
        }

        if (player.isOnline()) {
            int customSize = PlayerUtils.getNumberFromPermission(player.getPlayer(), "fabledskyblock.size", 0);
            if (customSize > 0 || player.getPlayer().hasPermission("fabledskyblock.*")) {
                FileConfiguration configLoad = this.plugin.getConfiguration();

                int minimumSize = configLoad.getInt("Island.Size.Minimum");
                int maximumSize = configLoad.getInt("Island.Size.Maximum");

                if (minimumSize < 0 || minimumSize > 1000) {
                    minimumSize = 50;
                }

                /*if(minimumSize % 2 != 0) {
                    minimumSize += 1;
                }*/

                if (maximumSize < 0 || maximumSize > 1000) {
                    maximumSize = 100;
                }

                /*if(maximumSize % 2 != 0) {
                    maximumSize += 1;
                }*/

                this.size = Math.max(minimumSize, Math.min(customSize, maximumSize));
            }
        }

        this.level = new IslandLevel(getOwnerUUID(), this.plugin);

        File configFile = new File(this.plugin.getDataFolder().toString() + "/island-data");

        Config config = fileManager.getConfig(new File(configFile, this.ownerUUID + ".yml"));

        FileConfiguration mainConfigLoad = this.plugin.getConfiguration();

        if (fileManager.isFileExist(new File(configFile, this.ownerUUID + ".yml"))) {
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getString("UUID") != null) {
                this.islandUUID = FastUUID.parseUUID(configLoad.getString("UUID"));
            } else {
                configLoad.set("UUID", this.islandUUID.toString());
            }

            if (configLoad.getString("MaxMembers") != null) {
                this.maxMembers = configLoad.getInt("MaxMembers");
            } else {
                configLoad.set("MaxMembers", this.maxMembers);
            }

            if (configLoad.getString("Size") != null) {
                this.size = configLoad.getInt("Size");
            } else {
                configLoad.set("Size", this.size);
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
                            new File(this.plugin.getDataFolder().toString() + "/player-data", member + ".yml"));
                    FileConfiguration playerDataConfigLoad = playerDataConfig.getFileConfiguration();

                    if (playerDataConfigLoad.getString("Island.Owner") == null
                            || !playerDataConfigLoad.getString("Island.Owner").equals(this.ownerUUID.toString())) {
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
                            new File(this.plugin.getDataFolder().toString() + "/player-data", operator + ".yml"));
                    FileConfiguration playerDataConfigLoad = playerDataConfig.getFileConfiguration();

                    if (playerDataConfigLoad.getString("Island.Owner") == null
                            || !playerDataConfigLoad.getString("Island.Owner").equals(this.ownerUUID.toString())) {
                        operators.remove(i);
                    }
                }

                configLoad.set("Operators", operators);
            }

            Config settingsDataConfig = null;

            File settingDataFile = new File(this.plugin.getDataFolder().toString() + "/setting-data", getOwnerUUID().toString() + ".yml");

            if (fileManager.isFileExist(settingDataFile)) {
                settingsDataConfig = fileManager.getConfig(settingDataFile);
            }

            for (IslandRole roleList : IslandRole.getRoles()) {
                List<BasicPermission> allPermissions = this.plugin.getPermissionManager().getPermissions();
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

                this.islandPermissions.put(roleList, permissions);
            }

            if (configLoad.getString("Whitelist") != null) {
                for (String whitelistedUUID : configLoad.getStringList("Whitelist")) {
                    this.whitelistedPlayers.add(FastUUID.parseUUID(whitelistedUUID));
                }
            }

            String open = configLoad.getString("Visitor.Open", null);
            if (open != null && (open.equalsIgnoreCase("true") ||
                    open.equalsIgnoreCase("false"))) {
                if (configLoad.getBoolean("Visitor.Open")) {
                    this.status = IslandStatus.OPEN;
                } else {
                    this.status = IslandStatus.CLOSED;
                }
                configLoad.set("Visitor.Open", null);
                configLoad.set("Visitor.Status", mainConfigLoad.getString("Island.Visitor.Status"));
            } else {
                if (configLoad.getString("Visitor.Status") != null) {
                    this.status = IslandStatus.getEnum(configLoad.getString("Visitor.Status"));
                } else {
                    this.status = IslandStatus.WHITELISTED;
                    configLoad.set("Visitor.Status", mainConfigLoad.getString("Island.Visitor.Status"));
                }
            }
        } else {
            FileConfiguration configLoad = config.getFileConfiguration();

            configLoad.set("UUID", this.islandUUID.toString());
            configLoad.set("Visitor.Status", mainConfigLoad.getString("Island.Visitor.Status").toUpperCase());
            configLoad.set("Border.Enable", mainConfigLoad.getBoolean("Island.WorldBorder.Default", false));
            configLoad.set("Border.Color", SWorldBorder.Color.Blue.name());
            configLoad.set("Biome.Type", mainConfigLoad.getString("Island.Biome.Default.Type").toUpperCase());
            configLoad.set("Weather.Synchronised", mainConfigLoad.getBoolean("Island.Weather.Default.Synchronised")); // TODO: Synchronized
            configLoad.set("Weather.Time", mainConfigLoad.getInt("Island.Weather.Default.Time"));
            configLoad.set("Weather.Weather", mainConfigLoad.getString("Island.Weather.Default.Weather").toUpperCase());
            configLoad.set("Ownership.Original", this.ownerUUID.toString());
            configLoad.set("Size", this.size);

            for (IslandRole roleList : IslandRole.getRoles()) {
                List<BasicPermission> allPermissions = this.plugin.getPermissionManager().getPermissions();
                List<IslandPermission> permissions = new ArrayList<>(allPermissions.size());

                for (BasicPermission permission : allPermissions) {
                    permissions.add(
                            new IslandPermission(permission, this.plugin.getSettings()
                                    .getBoolean("Settings." + roleList.name() + "." + permission.getName(), true)));
                }

                this.islandPermissions.put(roleList, permissions);
            }

            this.status = IslandStatus.getEnum(mainConfigLoad.getString("Island.Visitor.Status"));


            Player onlinePlayer = Bukkit.getServer().getPlayer(this.ownerUUID);

            if (!this.plugin.getPlayerDataManager().hasPlayerData(onlinePlayer)) {
                this.plugin.getPlayerDataManager().createPlayerData(onlinePlayer);
            }

            PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(onlinePlayer);
            playerData.setPlaytime(0);
            playerData.setOwner(this.ownerUUID);
            playerData.setMemberSince(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            playerData.save();
        }

        if (!mainConfigLoad.getBoolean("Island.Coop.Unload")) {
            File coopDataFile = new File(this.plugin.getDataFolder().toString() + "/coop-data",
                    getOwnerUUID().toString() + ".yml");

            if (fileManager.isFileExist(coopDataFile)) {
                Config coopDataConfig = fileManager.getConfig(coopDataFile);
                FileConfiguration coopDataConfigLoad = coopDataConfig.getFileConfiguration();

                if (coopDataConfigLoad.getString("CoopPlayers") != null) {
                    for (String coopPlayerList : coopDataConfigLoad.getStringList("CoopPlayers")) {
                        this.coopPlayers.put(FastUUID.parseUUID(coopPlayerList), IslandCoop.NORMAL);
                    }
                }
            }
        }

        save();

        this.apiWrapper = new com.songoda.skyblock.api.island.Island(this, player);
    }

    public UUID getIslandUUID() {
        return this.islandUUID;
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwnerUUID(UUID uuid) {
        getVisit().setOwnerUUID(uuid);
        this.ownerUUID = uuid;
    }

    public UUID getOriginalOwnerUUID() {
        return FastUUID.parseUUID(this.plugin.getFileManager().getConfig(new File(new File(this.plugin.getDataFolder().toString() + "/island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Ownership.Original"));
    }

    public int getMaxMembers(Player player) {
        try {
            return PlayerUtils.getNumberFromPermission(Objects.requireNonNull(player.getPlayer()), "fabledskyblock.members", this.maxMembers);
        } catch (Exception ignored) {
            return this.maxMembers;
        }
    }


    public void setMaxMembers(int maxMembers) {
        if (maxMembers > 100000 || maxMembers < 0) {
            maxMembers = 2;
        }

        this.maxMembers = maxMembers;
        this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("MaxMembers", maxMembers);
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        if (size > 1000 || size < 0) {
            size = 50;
        }

        this.size = size;
        this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Size", size);
    }

    public double getRadius() {
        return (((this.size % 2 == 0) ? this.size : (this.size - 1d)) / 2d);
    }

    public boolean hasPassword() {
        return this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Ownership.Password") != null;
    }

    public String getPassword() {
        return this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Ownership.Password");
    }

    public void setPassword(String password) {
        IslandPasswordChangeEvent islandPasswordChangeEvent = new IslandPasswordChangeEvent(getAPIWrapper(), password);
        Bukkit.getServer().getPluginManager().callEvent(islandPasswordChangeEvent);

        this.plugin.getFileManager()
                .getConfig(new File(new File(this.plugin.getDataFolder(), "island-data"),
                        this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Ownership.Password", islandPasswordChangeEvent.getPassword());
    }

    public Location getLocation(IslandWorld world, IslandEnvironment environment) {
        for (IslandLocation islandLocationList : this.islandLocations) {
            if (islandLocationList.getWorld().equals(world) && islandLocationList.getEnvironment().equals(environment)) {
                return islandLocationList.getLocation();
            }
        }

        return null;
    }

    public IslandLocation getIslandLocation(IslandWorld world, IslandEnvironment environment) {
        for (IslandLocation islandLocationList : this.islandLocations) {
            if (islandLocationList.getWorld() == world && islandLocationList.getEnvironment() == environment) {
                return islandLocationList;
            }
        }

        return null;
    }

    public void addLocation(IslandWorld world, IslandEnvironment environment, Location location) {
        this.islandLocations.add(new IslandLocation(world, environment, location));
    }

    public void setLocation(IslandWorld world, IslandEnvironment environment, Location location) {
        for (IslandLocation islandLocationList : this.islandLocations) {
            if (islandLocationList.getWorld() == world && islandLocationList.getEnvironment() == environment) {
                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () ->
                        Bukkit.getServer().getPluginManager().callEvent(new IslandLocationChangeEvent(getAPIWrapper(),
                                new com.songoda.skyblock.api.island.IslandLocation(APIUtil.fromImplementation(environment), APIUtil.fromImplementation(world), location))));

                FileManager fileManager = this.plugin.getFileManager();

                if (environment == IslandEnvironment.ISLAND) {
                    fileManager.setLocation(
                            fileManager
                                    .getConfig(new File(new File(this.plugin.getDataFolder().toString() + "/island-data"),
                                            getOwnerUUID().toString() + ".yml")),
                            "Location." + world.name() + "." + environment.name(), location, true);
                } else {
                    fileManager.setLocation(
                            fileManager
                                    .getConfig(new File(new File(this.plugin.getDataFolder().toString() + "/island-data"),
                                            getOwnerUUID().toString() + ".yml")),
                            "Location." + world.name() + ".Spawn." + environment.name(), location, true);
                }

                islandLocationList.setLocation(location);

                break;
            }
        }
    }

    public boolean isBorder() {
        return this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("Border.Enable");
    }

    public void setBorder(boolean border) {
        this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Border.Enable", border);
    }

    public SWorldBorder.Color getBorderColor() {
        return SWorldBorder.Color.valueOf(this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Border.Color"));
    }

    public void setBorderColor(SWorldBorder.Color color) {
        this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Border.Color", color.name());
    }

    public boolean isInBorder(Location blockLocation) {
        WorldManager worldManager = this.plugin.getWorldManager();
        if (!isBorder()) {
            return true;
        }

        Location islandLocation = getLocation(worldManager.getIslandWorld(blockLocation.getWorld()), IslandEnvironment.ISLAND);
        double halfSize = Math.floor(getRadius());

        return !(blockLocation.getBlockX() > (islandLocation.getBlockX() + halfSize))
                && !(blockLocation.getBlockX() < (islandLocation.getBlockX() - halfSize - 1))
                && !(blockLocation.getBlockZ() > (islandLocation.getBlockZ() + halfSize))
                && !(blockLocation.getBlockZ() < (islandLocation.getBlockZ() - halfSize - 1));
    }

    public Biome getBiome() {
        return CompatibleBiome.getBiome(this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Biome.Type")).getBiome();
    }

    public void setBiome(Biome biome) {
        IslandBiomeChangeEvent islandBiomeChangeEvent = new IslandBiomeChangeEvent(getAPIWrapper(), biome);
        Bukkit.getServer().getPluginManager().callEvent(islandBiomeChangeEvent);

        this.plugin.getFileManager()
                .getConfig(new File(new File(this.plugin.getDataFolder(), "island-data"),
                        this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Biome.Type", islandBiomeChangeEvent.getBiome().name());
    }

    public String getBiomeName() {
        return WordUtils.capitalizeFully(getBiome().name().replace("_", " "));
    }

    public boolean isWeatherSynchronized() {
        return this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("Weather.Synchronised");
    }

    public void setWeatherSynchronized(boolean sync) {
        IslandWeatherChangeEvent islandWeatherChangeEvent = new IslandWeatherChangeEvent(getAPIWrapper(), getWeather(),
                getTime(), sync);
        Bukkit.getServer().getPluginManager().callEvent(islandWeatherChangeEvent);

        this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Weather.Synchronised", sync);
    }

    public WeatherType getWeather() {
        String weatherTypeName = this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
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

        this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Weather.Weather", weatherType.name());
    }

    public String getWeatherName() {
        return WordUtils.capitalize(getWeather().name().toLowerCase());
    }

    public int getTime() {
        return this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getInt("Weather.Time");
    }

    public void setTime(int time) {
        IslandWeatherChangeEvent islandWeatherChangeEvent = new IslandWeatherChangeEvent(getAPIWrapper(), getWeather(),
                time, isWeatherSynchronized());
        Bukkit.getServer().getPluginManager().callEvent(islandWeatherChangeEvent);

        this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Weather.Time", time);
    }

    public Map<UUID, IslandCoop> getCoopPlayers() {
        return this.coopPlayers;
    }

    public void addCoopPlayer(UUID uuid, IslandCoop islandCoop) {
        this.coopPlayers.put(uuid, islandCoop);
        save();
    }

    public void removeCoopPlayer(UUID uuid) {
        this.coopPlayers.remove(uuid);
        save();
    }

    public boolean isCoopPlayer(UUID uuid) {
        return this.coopPlayers.containsKey(uuid);
    }

    public IslandCoop getCoopType(UUID uuid) {
        return this.coopPlayers.getOrDefault(uuid, null);
    }

    public void setAlwaysLoaded(boolean alwaysLoaded) {
        this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("AlwaysLoaded", alwaysLoaded);
    }

    public boolean isAlwaysLoaded() {
        return this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("AlwaysLoaded", false);
    }

    public Set<UUID> getRole(IslandRole role) {
        Set<UUID> islandRoles = new HashSet<>();

        if (role == IslandRole.OWNER) {
            islandRoles.add(getOwnerUUID());
        } else {
            Config config = this.plugin.getFileManager().getConfig(
                    new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"));
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
        if (isCoopPlayer(player.getUniqueId())) {
            return IslandRole.COOP; // TODO Rework Coop status - Fabrimat
        }
        for (IslandRole role : IslandRole.values()) {
            if (getRole(role).contains(player.getUniqueId())) {
                return role;
            }
        }

        return IslandRole.VISITOR;
    }

    public boolean setRole(IslandRole role, UUID uuid) {
        if (!(role == IslandRole.VISITOR || role == IslandRole.COOP || role == IslandRole.OWNER)) {
            if (!hasRole(role, uuid)) {
                if (role == IslandRole.MEMBER) {
                    if (hasRole(IslandRole.OPERATOR, uuid)) {
                        Bukkit.getServer().getPluginManager().callEvent(new IslandRoleChangeEvent(getAPIWrapper(),
                                Bukkit.getServer().getOfflinePlayer(uuid), APIUtil.fromImplementation(role)));
                        removeRole(IslandRole.OPERATOR, uuid);
                    }
                } else if (role == IslandRole.OPERATOR) {
                    if (hasRole(IslandRole.MEMBER, uuid)) {
                        Bukkit.getServer().getPluginManager().callEvent(new IslandRoleChangeEvent(getAPIWrapper(),
                                Bukkit.getServer().getOfflinePlayer(uuid), APIUtil.fromImplementation(role)));
                        removeRole(IslandRole.MEMBER, uuid);
                    }
                }

                Config config = this.plugin.getFileManager()
                        .getConfig(new File(new File(this.plugin.getDataFolder(), "island-data"),
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
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                getVisit().setMembers(getRole(IslandRole.MEMBER).size() + getRole(IslandRole.OPERATOR).size() + 1);

                return true;
            }
        }

        return false;
    }

    public boolean removeRole(IslandRole role, UUID uuid) {
        if (!(role == IslandRole.VISITOR || role == IslandRole.COOP || role == IslandRole.OWNER)) {
            if (hasRole(role, uuid)) {
                Config config = this.plugin.getFileManager().getConfig(new File(new File(this.plugin.getDataFolder(), "island-data"), getOwnerUUID().toString() + ".yml"));
                File configFile = config.getFile();
                FileConfiguration configLoad = config.getFileConfiguration();
                List<String> islandMembers = configLoad.getStringList(role.name() + "s");

                islandMembers.remove(FastUUID.toString(uuid));
                configLoad.set(role.name() + "s", islandMembers);

                try {
                    configLoad.save(configFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                getVisit().setMembers(getRole(IslandRole.MEMBER).size() + getRole(IslandRole.OPERATOR).size() + 1);

                return true;
            }
        }

        return false;
    }

    public boolean hasRole(IslandRole role, UUID uuid) {
        return getRole(role).contains(uuid) ||
                (this.plugin.getIslandManager().getPlayerProxyingAnotherPlayer(uuid) != null &&
                        getRole(role).contains(this.plugin.getIslandManager().getPlayerProxyingAnotherPlayer(uuid)));
    }

    public void setUpgrade(Player player, Upgrade.Type type, boolean status) {
        this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Upgrade." + type.name(), status);

        Bukkit.getServer().getPluginManager()
                .callEvent(new IslandUpgradeEvent(getAPIWrapper(), player, APIUtil.fromImplementation(type)));
    }

    public void removeUpgrade(Upgrade.Type type) {
        this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Upgrade." + type.name(), null);
    }

    public boolean hasUpgrade(Upgrade.Type type) {
        return this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Upgrade." + type.name()) != null;
    }

    public boolean isUpgrade(Upgrade.Type type) {
        return this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getBoolean("Upgrade." + type.name());
    }

    public boolean hasPermission(IslandRole role, BasicPermission permission) {
        if (this.islandPermissions.containsKey(role)) {
            for (IslandPermission islandPermission : this.islandPermissions.get(role)) {
                if (islandPermission.getPermission().equals(permission)) {
                    return islandPermission.getStatus();
                }
            }
        }

        return true; //TODO: Default setting value
    }

    public IslandPermission getPermission(IslandRole role, BasicPermission permission) {
        if (this.islandPermissions.containsKey(role)) {
            for (IslandPermission islandPermission : this.islandPermissions.get(role)) {
                if (islandPermission.getPermission() == permission) {
                    return islandPermission;
                }
            }
        }

        return null;
    }

    public List<IslandPermission> getSettings(IslandRole role) {
        if (this.islandPermissions.containsKey(role)) {
            return Collections.unmodifiableList(this.islandPermissions.get(role));
        }
        return Collections.emptyList();
    }

    public double getBankBalance() {
        return this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getDouble("Bank.Balance");
    }

    public void addToBank(double value) {
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager
                .getConfig(new File(this.plugin.getDataFolder() + "/island-data", this.ownerUUID.toString() + ".yml"));

        value = getBankBalance() + value;
        config.getFileConfiguration().set("Bank.Balance", value);

        try {
            config.getFileConfiguration().save(config.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void removeFromBank(double value) {
        addToBank(-value);
    }

    public IslandStatus getStatus() {
        return this.status;
    }

    public void setStatus(IslandStatus status) {
        IslandOpenEvent islandOpenEvent = new IslandOpenEvent(getAPIWrapper(), status == IslandStatus.OPEN);
        Bukkit.getServer().getPluginManager().callEvent(islandOpenEvent);
        if (islandOpenEvent.isCancelled()) {
            return;
        }

        IslandStatusChangeEvent islandStatusChangeEvent = new IslandStatusChangeEvent(getAPIWrapper(), APIUtil.fromImplementation(status));
        Bukkit.getServer().getPluginManager().callEvent(islandStatusChangeEvent);
        if (!islandStatusChangeEvent.isCancelled()) {
            this.status = status;
            getVisit().setStatus(status);
            save();
        }
    }

    public List<String> getMessage(IslandMessage message) {
        List<String> islandMessage = new ArrayList<>();

        Config config = this.plugin.getFileManager().getConfig(
                new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getString("Visitor." + message.name() + ".Message") != null) {
            islandMessage = configLoad.getStringList("Visitor." + message.name() + ".Message");
        }

        return islandMessage;
    }

    public String getMessageAuthor(IslandMessage message) {
        Config config = this.plugin.getFileManager().getConfig(
                new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"));
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
            Config config = this.plugin.getFileManager().getConfig(
                    new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"));
            FileConfiguration configLoad = config.getFileConfiguration();
            configLoad.set("Visitor." + message.name() + ".Message", islandMessageChangeEvent.getLines());
            configLoad.set("Visitor." + message.name() + ".Author", islandMessageChangeEvent.getAuthor());

            if (message == IslandMessage.SIGNATURE) {
                getVisit().setSignature(lines);
            }
        }
    }

    public boolean hasStructure() {
        return getStructure() != null;
    }

    public String getStructure() {
        return this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().getString("Structure");
    }

    public void setStructure(String structure) {
        this.plugin.getFileManager().getConfig(
                        new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"))
                .getFileConfiguration().set("Structure", structure);
    }

    public Visit getVisit() {
        return this.plugin.getVisitManager().getIsland(getOwnerUUID());
    }

    public Ban getBan() {
        return this.plugin.getBanManager().getIsland(getOwnerUUID());
    }

    public IslandLevel getLevel() {
        return this.level;
    }

    public boolean isDeleted() {
        return this.deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public synchronized void save() {
        FileManager fileManager = this.plugin.getFileManager();

        Config config = fileManager
                .getConfig(new File(this.plugin.getDataFolder() + "/island-data", this.ownerUUID.toString() + ".yml"));

        List<String> tempWhitelist = new ArrayList<>();
        for (UUID uuid : this.whitelistedPlayers) {
            tempWhitelist.add(FastUUID.toString(uuid));
        }
        config.getFileConfiguration().set("Whitelist", tempWhitelist);
        config.getFileConfiguration().set("Visitor.Status", this.status.toString());

        try {
            config.getFileConfiguration().save(config.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        config = fileManager
                .getConfig(new File(this.plugin.getDataFolder() + "/setting-data", this.ownerUUID.toString() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        for (Entry<IslandRole, List<IslandPermission>> entry : this.islandPermissions.entrySet()) {
            for (IslandPermission permission : entry.getValue()) {
                configLoad.set("Settings." + entry.getKey() + "." + permission.getPermission().getName(), permission.getStatus());
            }
        }

        try {
            configLoad.save(config.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (!this.plugin.getConfiguration().getBoolean("Island.Coop.Unload")) {
            config = fileManager
                    .getConfig(new File(this.plugin.getDataFolder() + "/coop-data", this.ownerUUID.toString() + ".yml"));
            configLoad = config.getFileConfiguration();

            List<String> coopPlayersAsString = new ArrayList<>(this.coopPlayers.size());

            for (Map.Entry<UUID, IslandCoop> entry : this.coopPlayers.entrySet()) {
                if (entry.getValue() == IslandCoop.TEMP) {
                    continue;
                }
                coopPlayersAsString.add(entry.getKey().toString());
            }

            configLoad.set("CoopPlayers", coopPlayersAsString);

            try {
                configLoad.save(config.getFile());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        getLevel().save();
    }

    public boolean isRegionUnlocked(Player player, IslandWorld type) {
        FileManager fileManager = this.plugin.getFileManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        MessageManager messageManager = this.plugin.getMessageManager();
        FileConfiguration configLoad = this.plugin.getConfiguration();
        Config islandData = fileManager
                .getConfig(new File(new File(this.plugin.getDataFolder(), "island-data"), this.ownerUUID.toString() + ".yml"));
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

            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
            if (type == IslandWorld.END) {
                player.setVelocity(player.getLocation().getDirection().multiply(-.50).setY(.6f));
            } else if (type == IslandWorld.NETHER) {
                player.setVelocity(player.getLocation().getDirection().multiply(-.50));
            }
        }
        return unlocked;
    }

    public com.songoda.skyblock.api.island.Island getAPIWrapper() {
        return this.apiWrapper;
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
        return new HashSet<>(this.whitelistedPlayers);
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
                "ownerUUID=" + this.ownerUUID +
                '}';
    }
}
