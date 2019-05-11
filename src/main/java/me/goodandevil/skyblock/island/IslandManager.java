package me.goodandevil.skyblock.island;

import com.google.common.base.Preconditions;
import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.api.event.island.*;
import me.goodandevil.skyblock.ban.BanManager;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.cooldown.CooldownManager;
import me.goodandevil.skyblock.cooldown.CooldownType;
import me.goodandevil.skyblock.invite.Invite;
import me.goodandevil.skyblock.invite.InviteManager;
import me.goodandevil.skyblock.message.MessageManager;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.scoreboard.Scoreboard;
import me.goodandevil.skyblock.scoreboard.ScoreboardManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.structure.Structure;
import me.goodandevil.skyblock.structure.StructureManager;
import me.goodandevil.skyblock.upgrade.Upgrade;
import me.goodandevil.skyblock.upgrade.UpgradeManager;
import me.goodandevil.skyblock.utils.player.OfflinePlayer;
import me.goodandevil.skyblock.utils.structure.SchematicUtil;
import me.goodandevil.skyblock.utils.structure.StructureUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.NMSUtil;
import me.goodandevil.skyblock.utils.version.SBiome;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.utils.world.LocationUtil;
import me.goodandevil.skyblock.utils.world.WorldBorder;
import me.goodandevil.skyblock.utils.world.block.BlockDegreesType;
import me.goodandevil.skyblock.visit.VisitManager;
import me.goodandevil.skyblock.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IslandManager {

    private final SkyBlock skyblock;

    private double x = 0, offset = 1200;

    private List<IslandPosition> islandPositions = new ArrayList<>();
    private Map<UUID, Island> islandStorage = new HashMap<>();

    public IslandManager(SkyBlock skyblock) {
        this.skyblock = skyblock;

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "worlds.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        for (IslandWorld worldList : IslandWorld.values()) {
            ConfigurationSection configSection = configLoad
                    .getConfigurationSection("World." + worldList.name() + ".nextAvailableLocation");
            islandPositions
                    .add(new IslandPosition(worldList, configSection.getDouble("x"), configSection.getDouble("z")));
        }

        for (Player all : Bukkit.getOnlinePlayers()) {
            loadIsland(all);
        }
    }

    public void onDisable() {
        for (int i = 0; i < islandStorage.size(); i++) {
            UUID islandOwnerUUID = (UUID) islandStorage.keySet().toArray()[i];
            Island island = islandStorage.get(islandOwnerUUID);
            island.save();
        }
    }

    public void saveNextAvailableLocation(IslandWorld world) {
        FileManager fileManager = skyblock.getFileManager();
        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "worlds.yml"));

        File configFile = config.getFile();
        FileConfiguration configLoad = config.getFileConfiguration();

        for (IslandPosition islandPositionList : islandPositions) {
            if (islandPositionList.getWorld() == world) {
                ConfigurationSection configSection = configLoad
                        .createSection("World." + world.name() + ".nextAvailableLocation");
                configSection.set("x", islandPositionList.getX());
                configSection.set("z", islandPositionList.getZ());
            }
        }

        try {
            configLoad.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNextAvailableLocation(IslandWorld world, org.bukkit.Location location) {
        for (IslandPosition islandPositionList : islandPositions) {
            if (islandPositionList.getWorld() == world) {
                islandPositionList.setX(location.getX());
                islandPositionList.setZ(location.getZ());
            }
        }
    }

    public org.bukkit.Location prepareNextAvailableLocation(IslandWorld world) {
        for (IslandPosition islandPositionList : islandPositions) {
            if (islandPositionList.getWorld() == world) {
                double x = islandPositionList.getX() + offset, z = islandPositionList.getZ();

                if (x > Math.abs(this.x)) {
                    z += offset;
                    islandPositionList.setX(this.x);
                    x = islandPositionList.getX() + offset;
                    islandPositionList.setZ(z);
                }

                return new org.bukkit.Location(skyblock.getWorldManager().getWorld(world), x, 72, z);
            }
        }

        return null;
    }

    public boolean createIsland(Player player, Structure structure) {
        ScoreboardManager scoreboardManager = skyblock.getScoreboardManager();
        VisitManager visitManager = skyblock.getVisitManager();
        FileManager fileManager = skyblock.getFileManager();
        BanManager banManager = skyblock.getBanManager();

        if (fileManager.getConfig(new File(skyblock.getDataFolder(), "locations.yml")).getFileConfiguration()
                .getString("Location.Spawn") == null) {
            skyblock.getMessageManager().sendMessage(player,
                    fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration()
                            .getString("Island.Creator.Error.Message"));
            skyblock.getSoundManager().playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

            return false;
        }

        Island island = new Island(player);
        island.setStructure(structure.getName());
        islandStorage.put(player.getUniqueId(), island);

        for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
            prepareIsland(island, worldList);
        }

        if (!visitManager.hasIsland(island.getOwnerUUID())) {
            visitManager.createIsland(island.getOwnerUUID(),
                    new IslandLocation[]{island.getIslandLocation(IslandWorld.Normal, IslandEnvironment.Island),
                            island.getIslandLocation(IslandWorld.Nether, IslandEnvironment.Island),
                            island.getIslandLocation(IslandWorld.End, IslandEnvironment.Island)},
                    island.getSize(),
                    island.getRole(IslandRole.Member).size() + island.getRole(IslandRole.Operator).size() + 1,
                    island.getBankBalance(),
                    visitManager.getIslandSafeLevel(island.getOwnerUUID()), island.getLevel(),
                    island.getMessage(IslandMessage.Signature), island.isOpen());
        }

        if (!banManager.hasIsland(island.getOwnerUUID())) {
            banManager.createIsland(island.getOwnerUUID());
        }

        Bukkit.getServer().getScheduler().runTaskAsynchronously(skyblock, () -> {
            Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            int minimumSize = configLoad.getInt("Island.Size.Minimum");
            int maximumSize = configLoad.getInt("Island.Size.Maximum");

            if (minimumSize < 0 || minimumSize > 1000) {
                minimumSize = 50;
            }

            if (maximumSize < 0 || maximumSize > 1000) {
                maximumSize = 100;
            }

            for (int i = maximumSize; i > minimumSize; i--) {
                if (player.hasPermission("fabledskyblock.size." + i) || player.hasPermission("fabledskyblock.*")) {
                    island.setSize(i);

                    break;
                }
            }
        });

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getBoolean("Island.Creation.Cooldown.Creation.Enable")) {
            if (!player.hasPermission("fabledskyblock.bypass.cooldown") && !player.hasPermission("fabledskyblock.bypass.*")
                    && !player.hasPermission("fabledskyblock.*")) {
                skyblock.getCooldownManager().createPlayer(CooldownType.Creation, player);
            }
        }

        Bukkit.getServer().getPluginManager().callEvent(new IslandCreateEvent(island.getAPIWrapper(), player));

        skyblock.getPlayerDataManager().getPlayerData(player).setIsland(player.getUniqueId());

        config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        configLoad = config.getFileConfiguration();

        if (scoreboardManager != null) {
            Scoreboard scoreboard = scoreboardManager.getScoreboard(player);
            scoreboard.cancel();
            scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Scoreboard.Island.Solo.Displayname")));
            scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
            scoreboard.run();
        }

        Bukkit.getServer().getScheduler().runTask(skyblock, () -> {
            if (structure.getCommands() != null) {
                for (String commandList : structure.getCommands()) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
                            commandList.replace("%player", player.getName()));
                }
            }

            player.teleport(island.getLocation(IslandWorld.Normal, IslandEnvironment.Main));
            player.setFallDistance(0.0F);
        });

        String biomeName = fileManager
                .getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                .getString("Island.Biome.Default.Type").toUpperCase();
        SBiome sBiome;
        try {
            sBiome = SBiome.valueOf(biomeName);
        } catch (Exception ex) {
            sBiome = SBiome.PLAINS;
        }
        Biome biome = sBiome.getBiome();

        Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> skyblock.getBiomeManager()
                .setBiome(island, biome), 20L);

        return true;
    }

    public void giveOwnership(Island island, org.bukkit.OfflinePlayer player) {
        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        CooldownManager cooldownManager = skyblock.getCooldownManager();
        FileManager fileManager = skyblock.getFileManager();

        if (island.isDeleted()) {
            return;
        }

        if (island.hasRole(IslandRole.Member, player.getUniqueId())
                || island.hasRole(IslandRole.Operator, player.getUniqueId())) {
            UUID uuid2 = island.getOwnerUUID();

            island.save();
            island.setOwnerUUID(player.getUniqueId());
            island.getAPIWrapper().setPlayer(player);

            IslandLevel level = island.getLevel();
            level.save();
            level.setOwnerUUID(player.getUniqueId());

            Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            if (configLoad.getBoolean("Island.Ownership.Password.Reset")) {
                island.setPassword(null);
            }

            File oldCoopDataFile = new File(new File(skyblock.getDataFolder().toString() + "/coop-data"),
                    uuid2.toString() + ".yml");
            fileManager.unloadConfig(oldCoopDataFile);

            if (fileManager.isFileExist(oldCoopDataFile)) {
                File newCoopDataFile = new File(new File(skyblock.getDataFolder().toString() + "/coop-data"),
                        player.getUniqueId().toString() + ".yml");

                fileManager.unloadConfig(newCoopDataFile);
                oldCoopDataFile.renameTo(newCoopDataFile);
            }

            File oldLevelDataFile = new File(new File(skyblock.getDataFolder().toString() + "/level-data"),
                    uuid2.toString() + ".yml");
            File newLevelDataFile = new File(new File(skyblock.getDataFolder().toString() + "/level-data"),
                    player.getUniqueId().toString() + ".yml");

            fileManager.unloadConfig(oldLevelDataFile);
            fileManager.unloadConfig(newLevelDataFile);
            oldLevelDataFile.renameTo(newLevelDataFile);

            File oldSettingDataFile = new File(new File(skyblock.getDataFolder().toString() + "/setting-data"),
                    uuid2.toString() + ".yml");
            File newSettingDataFile = new File(new File(skyblock.getDataFolder().toString() + "/setting-data"),
                    player.getUniqueId().toString() + ".yml");

            fileManager.unloadConfig(oldSettingDataFile);
            fileManager.unloadConfig(newSettingDataFile);
            oldSettingDataFile.renameTo(newSettingDataFile);

            File oldIslandDataFile = new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
                    uuid2.toString() + ".yml");
            File newIslandDataFile = new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
                    player.getUniqueId().toString() + ".yml");

            fileManager.unloadConfig(oldIslandDataFile);
            fileManager.unloadConfig(newIslandDataFile);
            oldIslandDataFile.renameTo(newIslandDataFile);

            skyblock.getVisitManager().transfer(uuid2, player.getUniqueId());
            skyblock.getBanManager().transfer(uuid2, player.getUniqueId());
            skyblock.getInviteManager().tranfer(uuid2, player.getUniqueId());

            org.bukkit.OfflinePlayer player1 = Bukkit.getServer().getOfflinePlayer(uuid2);

            cooldownManager.transferPlayer(CooldownType.Levelling, player1, player);
            cooldownManager.transferPlayer(CooldownType.Ownership, player1, player);

            if (configLoad.getBoolean("Island.Ownership.Transfer.Operator")) {
                island.setRole(IslandRole.Operator, uuid2);
            } else {
                island.setRole(IslandRole.Member, uuid2);
            }

            if (island.hasRole(IslandRole.Member, player.getUniqueId())) {
                island.removeRole(IslandRole.Member, player.getUniqueId());
            } else {
                island.removeRole(IslandRole.Operator, player.getUniqueId());
            }

            removeIsland(uuid2);
            islandStorage.put(player.getUniqueId(), island);

            Bukkit.getServer().getPluginManager()
                    .callEvent(new IslandOwnershipTransferEvent(island.getAPIWrapper(), player));

            ArrayList<UUID> islandMembers = new ArrayList<>();
            islandMembers.addAll(island.getRole(IslandRole.Member));
            islandMembers.addAll(island.getRole(IslandRole.Operator));
            islandMembers.add(player.getUniqueId());

            for (UUID islandMemberList : islandMembers) {
                Player targetPlayer = Bukkit.getServer().getPlayer(islandMemberList);

                if (targetPlayer == null) {
                    File configFile = new File(new File(skyblock.getDataFolder().toString() + "/player-data"),
                            islandMemberList.toString() + ".yml");
                    configLoad = YamlConfiguration.loadConfiguration(configFile);
                    configLoad.set("Island.Owner", player.getUniqueId().toString());

                    try {
                        configLoad.save(configFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    PlayerData playerData = playerDataManager.getPlayerData(targetPlayer);
                    playerData.setOwner(player.getUniqueId());
                    playerData.setIsland(player.getUniqueId());
                    playerData.save();
                }
            }
        }
    }

    public void deleteIsland(Island island) {
        ScoreboardManager scoreboardManager = skyblock.getScoreboardManager();
        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        CooldownManager cooldownManager = skyblock.getCooldownManager();
        FileManager fileManager = skyblock.getFileManager();

        skyblock.getVisitManager().deleteIsland(island.getOwnerUUID());
        skyblock.getBanManager().deleteIsland(island.getOwnerUUID());
        skyblock.getVisitManager().removeVisitors(island, VisitManager.Removal.Deleted);

        org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());
        cooldownManager.removeCooldownPlayer(CooldownType.Levelling, offlinePlayer);
        cooldownManager.removeCooldownPlayer(CooldownType.Ownership, offlinePlayer);

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        boolean cooldownEnabled = configLoad.getBoolean("Island.Creation.Cooldown.Deletion.Enable");

        config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        configLoad = config.getFileConfiguration();

        for (Player all : Bukkit.getOnlinePlayers()) {
            if ((island.hasRole(IslandRole.Member, all.getUniqueId())
                    || island.hasRole(IslandRole.Operator, all.getUniqueId())
                    || island.hasRole(IslandRole.Owner, all.getUniqueId())) && playerDataManager.hasPlayerData(all)) {
                PlayerData playerData = playerDataManager.getPlayerData(all);
                playerData.setOwner(null);
                playerData.setMemberSince(null);
                playerData.setChat(false);
                playerData.save();

                if (scoreboardManager != null) {
                    Scoreboard scoreboard = scoreboardManager.getScoreboard(all);
                    scoreboard.cancel();
                    scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Scoreboard.Tutorial.Displayname")));
                    scoreboard.setDisplayList(configLoad.getStringList("Scoreboard.Tutorial.Displaylines"));
                    scoreboard.run();
                }

                if (isPlayerAtIsland(island, all)) {
                    LocationUtil.teleportPlayerToSpawn(all);
                }

                if (cooldownEnabled) {
                    if (!all.hasPermission("fabledskyblock.bypass.cooldown") && !all.hasPermission("fabledskyblock.bypass.*")
                            && !all.hasPermission("fabledskyblock.*")) {
                        skyblock.getCooldownManager().createPlayer(CooldownType.Creation, all);
                    }
                }
            }

            InviteManager inviteManager = skyblock.getInviteManager();

            if (inviteManager.hasInvite(all.getUniqueId())) {
                Invite invite = inviteManager.getInvite(all.getUniqueId());

                if (invite.getOwnerUUID().equals(island.getOwnerUUID())) {
                    inviteManager.removeInvite(all.getUniqueId());
                }
            }
        }

        fileManager.deleteConfig(new File(new File(skyblock.getDataFolder().toString() + "/coop-data"),
                island.getOwnerUUID().toString() + ".yml"));
        fileManager.deleteConfig(new File(new File(skyblock.getDataFolder().toString() + "/level-data"),
                island.getOwnerUUID().toString() + ".yml"));
        fileManager.deleteConfig(new File(new File(skyblock.getDataFolder().toString() + "/setting-data"),
                island.getOwnerUUID().toString() + ".yml"));
        fileManager.deleteConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
                island.getOwnerUUID().toString() + ".yml"));

        Bukkit.getServer().getPluginManager().callEvent(new IslandDeleteEvent(island.getAPIWrapper()));

        islandStorage.remove(island.getOwnerUUID());
    }

    public void deleteIslandData(UUID uuid) {
        FileManager fileManager = skyblock.getFileManager();
        fileManager
                .deleteConfig(new File(skyblock.getDataFolder().toString() + "/island-data", uuid.toString() + ".yml"));
        fileManager.deleteConfig(new File(skyblock.getDataFolder().toString() + "/ban-data", uuid.toString() + ".yml"));
        fileManager
                .deleteConfig(new File(skyblock.getDataFolder().toString() + "/coop-data", uuid.toString() + ".yml"));
        fileManager
                .deleteConfig(new File(skyblock.getDataFolder().toString() + "/level-data", uuid.toString() + ".yml"));
        fileManager.deleteConfig(
                new File(skyblock.getDataFolder().toString() + "/setting-data", uuid.toString() + ".yml"));
        fileManager
                .deleteConfig(new File(skyblock.getDataFolder().toString() + "/visit-data", uuid.toString() + ".yml"));
    }

    public Island loadIsland(org.bukkit.OfflinePlayer player) {
        VisitManager visitManager = skyblock.getVisitManager();
        FileManager fileManager = skyblock.getFileManager();
        BanManager banManager = skyblock.getBanManager();

        UUID islandOwnerUUID = null;

        Config config = fileManager.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/player-data"),
                player.getUniqueId().toString() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (isIslandExist(player.getUniqueId())) {
            if (configLoad.getString("Island.Owner") == null
                    || !configLoad.getString("Island.Owner").equals(player.getUniqueId().toString())) {
                deleteIslandData(player.getUniqueId());
                configLoad.set("Island.Owner", null);

                return null;
            }

            islandOwnerUUID = player.getUniqueId();
        } else {
            if (configLoad.getString("Island.Owner") != null) {
                islandOwnerUUID = UUID.fromString(configLoad.getString("Island.Owner"));
            }
        }

        if (islandOwnerUUID != null) {
            if (containsIsland(islandOwnerUUID)) {
                return getIsland(player);
            } else {
                config = fileManager.getConfig(new File(skyblock.getDataFolder().toString() + "/island-data",
                        islandOwnerUUID.toString() + ".yml"));

                if (config.getFileConfiguration().getString("Location") == null) {
                    deleteIslandData(islandOwnerUUID);
                    configLoad.set("Island.Owner", null);

                    return null;
                }

                Island island = new Island(Bukkit.getServer().getOfflinePlayer(islandOwnerUUID));
                islandStorage.put(islandOwnerUUID, island);

                for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
                    prepareIsland(island, worldList);
                }

                if (!visitManager.hasIsland(island.getOwnerUUID())) {
                    visitManager.createIsland(island.getOwnerUUID(),
                            new IslandLocation[]{
                                    island.getIslandLocation(IslandWorld.Normal, IslandEnvironment.Island),
                                    island.getIslandLocation(IslandWorld.Nether, IslandEnvironment.Island),
                                    island.getIslandLocation(IslandWorld.End, IslandEnvironment.Island)},
                            island.getSize(),
                            island.getRole(IslandRole.Member).size() + island.getRole(IslandRole.Operator).size() + 1,
                            island.getBankBalance(),
                            visitManager.getIslandSafeLevel(island.getOwnerUUID()), island.getLevel(),
                            island.getMessage(IslandMessage.Signature), island.isOpen());
                }

                if (!banManager.hasIsland(island.getOwnerUUID())) {
                    banManager.createIsland(island.getOwnerUUID());
                }

                Bukkit.getServer().getPluginManager().callEvent(new IslandLoadEvent(island.getAPIWrapper()));

                return island;
            }
        }

        return null;
    }

    public void loadIslandAtLocation(Location location) {
        FileManager fileManager = skyblock.getFileManager();
        File configFile = new File(skyblock.getDataFolder().toString() + "/island-data");

        if (!configFile.exists()) return;

        for (File fileList : configFile.listFiles()) {
            if (fileList != null && fileList.getName().contains(".yml") && fileList.getName().length() > 35) {
                try {
                    Config config = new FileManager.Config(fileManager, fileList);
                    FileConfiguration configLoad = config.getFileConfiguration();

                    int size = 100;
                    if (configLoad.getString("Size") != null) {
                        size = configLoad.getInt("Size");
                    }

                    Location islandLocation = fileManager.getLocation(config, "Location.Normal.Island", false);

                    if (LocationUtil.isLocationAtLocationRadius(location, islandLocation, size)) {
                        UUID islandOwnerUUID = UUID.fromString(fileList.getName().replace(".yml", ""));
                        this.loadIsland(Bukkit.getOfflinePlayer(islandOwnerUUID));
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void unloadIsland(Island island, org.bukkit.OfflinePlayer player) {
        ScoreboardManager scoreboardManager = skyblock.getScoreboardManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (island.isDeleted()) {
            return;
        }

        island.save();

        int islandMembers = island.getRole(IslandRole.Member).size() + island.getRole(IslandRole.Operator).size() + 1,
                islandVisitors = getVisitorsAtIsland(island).size();
        boolean unloadIsland = true;

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all == null || (player != null && player.getUniqueId().equals(all.getUniqueId()))) {
                continue;
            }

            if (island.hasRole(IslandRole.Member, all.getUniqueId())
                    || island.hasRole(IslandRole.Operator, all.getUniqueId())
                    || island.hasRole(IslandRole.Owner, all.getUniqueId())) {
                if (scoreboardManager != null) {
                    try {
                        if (islandMembers == 1 && islandVisitors == 0) {
                            Scoreboard scoreboard = scoreboardManager.getScoreboard(all);
                            scoreboard.cancel();
                            scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                    configLoad.getString("Scoreboard.Island.Solo.Displayname")));
                            scoreboard.setDisplayList(
                                    configLoad.getStringList("Scoreboard.Island.Solo.Empty.Displaylines"));
                            scoreboard.run();
                        } else if (islandVisitors == 0) {
                            Scoreboard scoreboard = scoreboardManager.getScoreboard(all);
                            scoreboard.cancel();
                            scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                    configLoad.getString("Scoreboard.Island.Team.Displayname")));
                            scoreboard.setDisplayList(
                                    configLoad.getStringList("Scoreboard.Island.Team.Empty.Displaylines"));

                            HashMap<String, String> displayVariables = new HashMap<>();
                            displayVariables.put("%owner", configLoad.getString("Scoreboard.Island.Team.Word.Owner"));
                            displayVariables.put("%operator",
                                    configLoad.getString("Scoreboard.Island.Team.Word.Operator"));
                            displayVariables.put("%member", configLoad.getString("Scoreboard.Island.Team.Word.Member"));

                            scoreboard.setDisplayVariables(displayVariables);
                            scoreboard.run();
                        }
                    } catch (IllegalPluginAccessException e) {
                    }
                }

                unloadIsland = false;
            }
        }

        if (!unloadIsland) {
            return;
        }

        unloadIsland = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Island.Visitor.Unload");

        if (unloadIsland) {
            VisitManager visitManager = skyblock.getVisitManager();
            visitManager.removeVisitors(island, VisitManager.Removal.Unloaded);
            visitManager.unloadIsland(island.getOwnerUUID());

            BanManager banManager = skyblock.getBanManager();
            banManager.unloadIsland(island.getOwnerUUID());
        } else {
            int nonIslandMembers = islandVisitors - getCoopPlayersAtIsland(island).size();

            if (nonIslandMembers <= 0) {
                if (island.isOpen()) {
                    return;
                } else if (player != null) {
                    removeCoopPlayers(island, player.getUniqueId());
                }
            } else {
                return;
            }
        }

        fileManager.unloadConfig(
                new File(new File(skyblock.getDataFolder().toString() + "/coop-data"), island.getOwnerUUID() + ".yml"));
        fileManager.unloadConfig(new File(new File(skyblock.getDataFolder().toString() + "/setting-data"),
                island.getOwnerUUID() + ".yml"));
        fileManager.unloadConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"),
                island.getOwnerUUID() + ".yml"));

        islandStorage.remove(island.getOwnerUUID());

        Bukkit.getServer().getPluginManager().callEvent(new IslandUnloadEvent(island.getAPIWrapper()));
    }

    public void prepareIsland(Island island, IslandWorld world) {
        WorldManager worldManager = skyblock.getWorldManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(
                new File(skyblock.getDataFolder().toString() + "/island-data", island.getOwnerUUID() + ".yml"));

        if (config.getFileConfiguration().getString("Location." + world.name()) == null) {
            pasteStructure(island, world);
            return;
        }

        for (IslandEnvironment environmentList : IslandEnvironment.values()) {
            org.bukkit.Location location;

            if (environmentList == IslandEnvironment.Island) {
                location = fileManager.getLocation(config,
                        "Location." + world.name() + "." + environmentList.name(), true);
            } else {
                location = fileManager.getLocation(config,
                        "Location." + world.name() + ".Spawn." + environmentList.name(), true);
            }

            island.addLocation(world, environmentList, worldManager.getLocation(location, world));
        }

        Bukkit.getServer().getScheduler().runTask(skyblock,
                () -> removeSpawnProtection(island.getLocation(world, IslandEnvironment.Island)));
    }

    public void resetIsland(Island island) {
        for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
            pasteStructure(island, worldList);
        }
    }

    public void pasteStructure(Island island, IslandWorld world) {
        StructureManager structureManager = skyblock.getStructureManager();
        FileManager fileManager = skyblock.getFileManager();

        Structure structure;

        if (island.getStructure() != null && !island.getStructure().isEmpty()
                && structureManager.containsStructure(island.getStructure())) {
            structure = structureManager.getStructure(island.getStructure());
        } else {
            structure = structureManager.getStructures().get(0);
        }

        org.bukkit.Location islandLocation = prepareNextAvailableLocation(world);

        Config config = fileManager.getConfig(
                new File(skyblock.getDataFolder().toString() + "/island-data", island.getOwnerUUID() + ".yml"));

        for (IslandEnvironment environmentList : IslandEnvironment.values()) {
            if (environmentList == IslandEnvironment.Island) {
                island.addLocation(world, environmentList, islandLocation);
                fileManager.setLocation(config, "Location." + world.name() + "." + environmentList.name(),
                        islandLocation, true);
            } else {
                island.addLocation(world, environmentList, islandLocation.clone().add(0.5D, 0.0D, 0.5D));
                fileManager.setLocation(config, "Location." + world.name() + ".Spawn." + environmentList.name(),
                        islandLocation.clone().add(0.5D, 0.0D, 0.5D), true);
            }
        }

        if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Island.Spawn.Protection")) {
            Bukkit.getServer().getScheduler().runTask(skyblock, () -> islandLocation.clone().subtract(0.0D, 1.0D, 0.0D).getBlock().setType(Material.STONE));
        }

        try {
            String structureFileName = null;
            switch (world) {
                case Normal:
                    structureFileName = structure.getOverworldFile();
                    break;
                case Nether:
                    structureFileName = structure.getNetherFile();
                    break;
                case End:
                    structureFileName = structure.getEndFile();
                    break;
            }

            boolean isStructureFile = structureFileName.endsWith(".structure");
            File structureFile = new File(new File(skyblock.getDataFolder().toString() + "/" + (isStructureFile ? "structures" : "schematics")), structureFileName);

            Float[] direction;
            if (isStructureFile) {
                direction = StructureUtil.pasteStructure(StructureUtil.loadStructure(structureFile),
                        island.getLocation(world, IslandEnvironment.Island), BlockDegreesType.ROTATE_360);
            } else {
                direction = SchematicUtil.pasteSchematic(structureFile, island.getLocation(world, IslandEnvironment.Island));
            }

            org.bukkit.Location spawnLocation = island.getLocation(world, IslandEnvironment.Main).clone();
            spawnLocation.setYaw(direction[0]);
            spawnLocation.setPitch(direction[1]);
            island.setLocation(world, IslandEnvironment.Main, spawnLocation);
            island.setLocation(world, IslandEnvironment.Visitor, spawnLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setNextAvailableLocation(world, islandLocation);
        saveNextAvailableLocation(world);

        // Recalculate island level after 5 seconds
        Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> skyblock.getLevellingManager().calculatePoints(null, island), 100L);
    }

    public Set<UUID> getVisitorsAtIsland(Island island) {
        Map<UUID, PlayerData> playerDataStorage = skyblock.getPlayerDataManager().getPlayerData();
        Set<UUID> islandVisitors = new HashSet<>();

        for (UUID playerDataStorageList : playerDataStorage.keySet()) {
            PlayerData playerData = playerDataStorage.get(playerDataStorageList);
            UUID islandOwnerUUID = playerData.getIsland();

            if (islandOwnerUUID != null && islandOwnerUUID.equals(island.getOwnerUUID())) {
                if (playerData.getOwner() == null || !playerData.getOwner().equals(island.getOwnerUUID())) {
                    if (Bukkit.getServer().getPlayer(playerDataStorageList) != null) {
                        islandVisitors.add(playerDataStorageList);
                    }
                }
            }
        }

        return islandVisitors;
    }

    public void visitIsland(Player player, Island island) {
        ScoreboardManager scoreboardManager = skyblock.getScoreboardManager();
        FileManager fileManager = skyblock.getFileManager();

        Config languageConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = languageConfig.getFileConfiguration();

        if (island.hasRole(IslandRole.Member, player.getUniqueId())
                || island.hasRole(IslandRole.Operator, player.getUniqueId())
                || island.hasRole(IslandRole.Owner, player.getUniqueId())) {
            player.teleport(island.getLocation(IslandWorld.Normal, IslandEnvironment.Visitor));
            player.setFallDistance(0.0F);
        } else {
            if (scoreboardManager != null) {
                int islandVisitors = getVisitorsAtIsland(island).size(),
                        islandMembers = island.getRole(IslandRole.Member).size()
                                + island.getRole(IslandRole.Operator).size() + 1;

                if (islandVisitors == 0) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        PlayerData targetPlayerData = skyblock.getPlayerDataManager().getPlayerData(all);

                        if (targetPlayerData.getOwner() != null
                                && targetPlayerData.getOwner().equals(island.getOwnerUUID())) {
                            Scoreboard scoreboard = scoreboardManager.getScoreboard(all);
                            scoreboard.cancel();

                            if (islandMembers == 1) {
                                scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Scoreboard.Island.Solo.Displayname")));
                                scoreboard.setDisplayList(
                                        configLoad.getStringList("Scoreboard.Island.Solo.Occupied.Displaylines"));
                            } else {
                                scoreboard.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Scoreboard.Island.Team.Displayname")));
                                scoreboard.setDisplayList(
                                        configLoad.getStringList("Scoreboard.Island.Team.Occupied.Displaylines"));

                                HashMap<String, String> displayVariables = new HashMap<>();
                                displayVariables.put("%owner",
                                        configLoad.getString("Scoreboard.Island.Team.Word.Owner"));
                                displayVariables.put("%operator",
                                        configLoad.getString("Scoreboard.Island.Team.Word.Operator"));
                                displayVariables.put("%member",
                                        configLoad.getString("Scoreboard.Island.Team.Word.Member"));

                                scoreboard.setDisplayVariables(displayVariables);
                            }

                            scoreboard.run();
                        }
                    }
                }
            }

            Bukkit.getServer().getScheduler().runTask(skyblock, () -> {
                player.teleport(island.getLocation(IslandWorld.Normal, IslandEnvironment.Visitor));
                player.setFallDistance(0.0F);
            });

            List<String> islandWelcomeMessage = island.getMessage(IslandMessage.Welcome);

            if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                    .getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable")
                    && islandWelcomeMessage.size() != 0) {
                for (String islandWelcomeMessageList : islandWelcomeMessage) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', islandWelcomeMessageList));
                }
            }
        }

        player.closeInventory();
    }

    public void closeIsland(Island island) {
        MessageManager messageManager = skyblock.getMessageManager();

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        island.setOpen(false);

        UUID islandOwnerUUID = island.getOwnerUUID();
        Player islandOwnerPlayer = Bukkit.getServer().getPlayer(islandOwnerUUID);
        String islandOwnerPlayerName;

        if (islandOwnerPlayer == null) {
            islandOwnerPlayerName = new OfflinePlayer(islandOwnerUUID).getName();
        } else {
            islandOwnerPlayerName = islandOwnerPlayer.getName();
        }

        for (UUID visitorList : getVisitorsAtIsland(island)) {
            if (!island.isCoopPlayer(visitorList)) {
                Player targetPlayer = Bukkit.getServer().getPlayer(visitorList);
                LocationUtil.teleportPlayerToSpawn(targetPlayer);
                messageManager.sendMessage(targetPlayer, configLoad.getString("Island.Visit.Closed.Island.Message")
                        .replace("%player", islandOwnerPlayerName));
            }
        }
    }

    public Island getIsland(org.bukkit.OfflinePlayer offlinePlayer) {
        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();

        // TODO: Find out how this can be fixed without this, for some reason IslandManager tries to load PlayerDataManager before it's even loaded
        if (playerDataManager == null) return null;

        if (islandStorage.containsKey(offlinePlayer.getUniqueId())) {
            return islandStorage.get(offlinePlayer.getUniqueId());
        }

        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();

            if (playerDataManager.hasPlayerData(player)) {
                PlayerData playerData = playerDataManager.getPlayerData(player);

                if (playerData.getOwner() != null && islandStorage.containsKey(playerData.getOwner())) {
                    return islandStorage.get(playerData.getOwner());
                }
            }
        } else {
            OfflinePlayer offlinePlayerData = new OfflinePlayer(offlinePlayer.getUniqueId());

            if (offlinePlayerData.getOwner() != null && islandStorage.containsKey(offlinePlayer.getUniqueId())) {
                return islandStorage.get(offlinePlayerData.getOwner());
            }
        }

        return null;
    }

    public Island getIslandByUUID(UUID islandUUID) {
        for (Island island : islandStorage.values()) {
            if (island.getIslandUUID().equals(islandUUID)) {
                return island;
            }
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
        return skyblock.getFileManager().isFileExist(
                new File(new File(skyblock.getDataFolder().toString() + "/island-data"), uuid.toString() + ".yml"));
    }

    /*
     * public boolean hasIsland(org.bukkit.OfflinePlayer offlinePlayer) { if
     * (offlinePlayer.isOnline()) { PlayerDataManager playerDataManager =
     * skyblock.getPlayerDataManager();
     *
     * Player player = offlinePlayer.getPlayer();
     *
     * if (playerDataManager.hasPlayerData(player)) { PlayerData playerData =
     * playerDataManager.getPlayerData(player);
     *
     * if (playerData.getOwner() != null &&
     * islandStorage.containsKey(playerData.getOwner())) { return true; } } }
     *
     * if (!isIslandExist(offlinePlayer.getUniqueId())) { return new
     * OfflinePlayer(offlinePlayer.getUniqueId()).getOwner() != null; }
     *
     * return false; }
     */

    public boolean containsIsland(UUID uuid) {
        return islandStorage.containsKey(uuid);
    }

    public boolean hasPermission(Player player, String setting) {
        return hasPermission(player, player.getLocation(), setting);
    }

    public boolean hasPermission(Player player, org.bukkit.Location location, String setting) {
        Island island = getIslandAtLocation(location);

        if (island == null)
            return true;

        if (player.hasPermission("fabledskyblock.bypass." + setting.toLowerCase()))
            return true;

        if (island.getSetting(island.getRole(player), setting).getStatus())
            return true;

        if (island.isCoopPlayer(player.getUniqueId()) && island.getSetting(IslandRole.Coop, setting).getStatus())
            return true;

        if (island.getSetting(IslandRole.Visitor, setting).getStatus())
            return true;

        return false;
    }

    public boolean hasSetting(org.bukkit.Location location, IslandRole role, String setting) {
        Island island = getIslandAtLocation(location);
        if (island == null)
            return false;

        return island.getSetting(role, setting).getStatus();
    }

    public void removeSpawnProtection(org.bukkit.Location location) {
        Block block = location.getBlock();

        if (block.getType() == Materials.MOVING_PISTON.parseMaterial()) {
            block.setType(Material.AIR);
        }

        block = location.clone().add(0.0D, 1.0D, 0.0D).getBlock();

        if (block.getType() == Materials.MOVING_PISTON.parseMaterial()) {
            block.setType(Material.AIR);
        }
    }

    public Set<UUID> getMembersOnline(Island island) {
        Set<UUID> membersOnline = new HashSet<>();

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (island.hasRole(IslandRole.Member, all.getUniqueId())
                    || island.hasRole(IslandRole.Operator, all.getUniqueId())
                    || island.hasRole(IslandRole.Owner, all.getUniqueId())) {
                membersOnline.add(all.getUniqueId());
            }
        }

        return membersOnline;
    }

    public List<Player> getPlayersAtIsland(Island island) {
        List<Player> playersAtIsland = new ArrayList<>();

        if (island != null) {
            for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
                playersAtIsland.addAll(getPlayersAtIsland(island, worldList));
            }
        }

        return playersAtIsland;
    }

    public List<Player> getPlayersAtIsland(Island island, IslandWorld world) {
        List<Player> playersAtIsland = new ArrayList<>();

        if (island != null) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (isPlayerAtIsland(island, all, world)) {
                    playersAtIsland.add(all);
                }
            }
        }

        return playersAtIsland;
    }

    public Island getIslandPlayerAt(Player player) {
        Preconditions.checkArgument(player != null, "Cannot get Island to null player");

        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);

            if (playerData.getIsland() != null) {
                org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(playerData.getIsland());
                Island island = getIsland(offlinePlayer);

                if (island != null) {
                    return island;
                }
            }
        }

        return null;
    }

    public boolean isPlayerAtAnIsland(Player player) {
        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);

            if (playerData.getIsland() != null) {
                return true;
            }
        }

        return false;
    }

    public void loadPlayer(Player player) {
        WorldManager worldManager = skyblock.getWorldManager();

        Bukkit.getServer().getScheduler().runTaskAsynchronously(skyblock, () -> {
            if (worldManager.isIslandWorld(player.getWorld())) {
                IslandWorld world = worldManager.getIslandWorld(player.getWorld());
                Island island = getIslandAtLocation(player.getLocation());

                if (island != null) {
                    Config config = skyblock.getFileManager()
                            .getConfig(new File(skyblock.getDataFolder(), "config.yml"));
                    FileConfiguration configLoad = config.getFileConfiguration();

                    if (!island.isWeatherSynchronized()) {
                        player.setPlayerTime(island.getTime(), configLoad.getBoolean("Island.Weather.Time.Cycle"));
                        player.setPlayerWeather(island.getWeather());
                    }

                    updateFlight(player);

                    if (world == IslandWorld.Nether) {
                        if (NMSUtil.getVersionNumber() < 13) {
                            return;
                        }
                    }

                    if (configLoad.getBoolean("Island.WorldBorder.Enable") && island.isBorder()) {
                        WorldBorder.send(player, island.getBorderColor(), island.getSize(),
                                island.getLocation(worldManager.getIslandWorld(player.getWorld()),
                                        IslandEnvironment.Island));
                    } else {
                        WorldBorder.send(player, null, 1.4999992E7D,
                                new org.bukkit.Location(player.getWorld(), 0, 0, 0));
                    }
                }
            }
        });
    }

    public void updateFlightAtIsland(Island island) {
        for (Player player : getPlayersAtIsland(island))
            this.updateFlight(player);
    }

    public void updateFlight(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        Island island = getIslandAtLocation(player.getLocation());

        UpgradeManager upgradeManager = skyblock.getUpgradeManager();
        List<Upgrade> flyUpgrades = upgradeManager.getUpgrades(Upgrade.Type.Fly);
        boolean isFlyUpgradeEnabled = flyUpgrades != null && flyUpgrades.size() > 0 && flyUpgrades.get(0).isEnabled();
        boolean setPlayerFlying = false;
        if (isFlyUpgradeEnabled) {
            boolean upgradeEnabled = island != null && island.isUpgrade(Upgrade.Type.Fly);
            setPlayerFlying = upgradeEnabled;
            Bukkit.getServer().getScheduler().runTask(skyblock, () -> {
                player.setAllowFlight(upgradeEnabled);
            });
        }

        boolean hasFlyPermission = player.hasPermission("fabledskyblock.fly") || player.hasPermission("fabledskyblock.*");
        if (hasFlyPermission && island != null && !setPlayerFlying) {
            WorldManager worldManager = skyblock.getWorldManager();
            boolean canFlyInWorld = worldManager.isIslandWorld(player.getWorld());
            Bukkit.getServer().getScheduler().runTask(skyblock, () -> {
                player.setAllowFlight(canFlyInWorld);
            });
        }
    }

    public Set<UUID> getCoopPlayersAtIsland(Island island) {
        Set<UUID> coopPlayersAtIsland = new HashSet<>();

        if (island != null) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (island.getCoopPlayers().contains(all.getUniqueId())) {
                    if (isPlayerAtIsland(island, all)) {
                        coopPlayersAtIsland.add(all.getUniqueId());
                    }
                }
            }
        }

        return coopPlayersAtIsland;
    }

    public boolean removeCoopPlayers(Island island, UUID uuid) {
        MessageManager messageManager = skyblock.getMessageManager();
        SoundManager soundManager = skyblock.getSoundManager();

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        boolean coopPlayers = island.getSetting(IslandRole.Operator, "CoopPlayers").getStatus();

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (uuid != null && all.getUniqueId().equals(uuid)) {
                continue;
            }

            if (island.hasRole(IslandRole.Owner, all.getUniqueId())) {
                return false;
            } else if (coopPlayers && island.hasRole(IslandRole.Operator, all.getUniqueId())) {
                return false;
            }
        }

        for (UUID coopPlayerAtIslandList : getCoopPlayersAtIsland(island)) {
            Player targetPlayer = Bukkit.getServer().getPlayer(coopPlayerAtIslandList);

            if (targetPlayer != null) {
                LocationUtil.teleportPlayerToSpawn(targetPlayer);

                if (coopPlayers) {
                    messageManager.sendMessage(targetPlayer,
                            configLoad.getString("Island.Coop.Removed.Operator.Message"));
                } else {
                    messageManager.sendMessage(targetPlayer, configLoad.getString("Island.Coop.Removed.Owner.Message"));
                }

                soundManager.playSound(targetPlayer, Sounds.IRONGOLEM_HIT.bukkitSound(), 1.0F, 1.0F);
            }
        }

        return true;
    }

    public int getIslandSafeLevel(Island island) {
        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        int safeLevel = 0;

        Map<String, Boolean> settings = new HashMap<>();
        settings.put("KeepItemsOnDeath", false);
        settings.put("PvP", true);
        settings.put("Damage", true);

        for (String settingList : settings.keySet()) {
            if (configLoad.getBoolean("Island.Settings." + settingList + ".Enable")
                    && island.getSetting(IslandRole.Owner, settingList).getStatus() == settings.get(settingList)) {
                safeLevel++;
            }
        }

        return safeLevel;
    }

    public void updateBorder(Island island) {
        WorldManager worldManager = skyblock.getWorldManager();

        if (island.isBorder()) {
            if (skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                    .getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
                for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
                    if (worldList == IslandWorld.Nether) {
                        if (NMSUtil.getVersionNumber() < 13) {
                            continue;
                        }
                    }

                    for (Player all : getPlayersAtIsland(island)) {
                        WorldBorder.send(all, island.getBorderColor(), island.getSize(), island
                                .getLocation(worldManager.getIslandWorld(all.getWorld()), IslandEnvironment.Island));
                    }
                }
            }
        } else {
            for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
                if (worldList == IslandWorld.Nether) {
                    if (NMSUtil.getVersionNumber() < 13) {
                        continue;
                    }
                }

                for (Player all : getPlayersAtIsland(island)) {
                    WorldBorder.send(all, null, 1.4999992E7D, new org.bukkit.Location(all.getWorld(), 0, 0, 0));
                }
            }
        }
    }

    public List<Island> getCoopIslands(Player player) {
        List<Island> islands = new ArrayList<>();

        for (UUID islandList : getIslands().keySet()) {
            Island island = getIslands().get(islandList);

            if (island.getCoopPlayers().contains(player.getUniqueId())) {
                islands.add(island);
            }
        }

        return islands;
    }

    public Island getIslandAtLocation(org.bukkit.Location location) {
        for (UUID islandList : getIslands().keySet()) {
            Island island = getIslands().get(islandList);

            if (isLocationAtIsland(island, location)) {
                return island;
            }
        }

        return null;
    }

    public boolean isPlayerAtIsland(Island island, Player player) {
        return isLocationAtIsland(island, player.getLocation());
    }

    public boolean isPlayerAtIsland(Island island, Player player, IslandWorld world) {
        return isLocationAtIsland(island, player.getLocation(), world);
    }

    public boolean isLocationAtIsland(Island island, org.bukkit.Location location) {
        for (IslandWorld worldList : IslandWorld.getIslandWorlds()) {
            if (isLocationAtIsland(island, location, worldList)) {
                return true;
            }
        }

        return false;
    }

    public boolean isLocationAtIsland(Island island, org.bukkit.Location location, IslandWorld world) {
        return LocationUtil.isLocationAtLocationRadius(location, island.getLocation(world, IslandEnvironment.Island), island.getRadius());
    }
}
