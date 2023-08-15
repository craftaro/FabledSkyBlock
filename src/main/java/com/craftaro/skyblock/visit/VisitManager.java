package com.craftaro.skyblock.visit;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandLevel;
import com.craftaro.skyblock.island.IslandLocation;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.world.LocationUtil;
import com.craftaro.skyblock.world.WorldManager;
import com.eatthepath.uuid.FastUUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VisitManager {
    private final SkyBlock plugin;
    private final HashMap<UUID, Visit> visitStorage = new HashMap<>();

    public VisitManager(SkyBlock plugin) {
        this.plugin = plugin;

        loadIslands();
    }

    public void onDisable() {
        HashMap<UUID, Visit> visitIslands = getIslands();

        for (Visit visit : visitIslands.values()) {
            visit.save();
        }
    }

    public void loadIslands() {
        WorldManager worldManager = this.plugin.getWorldManager();
        FileManager fileManager = this.plugin.getFileManager();

        if (!this.plugin.getConfiguration().getBoolean("Island.Visitor.Unload")) {
            File configFile = new File(this.plugin.getDataFolder(), "island-data");

            if (!configFile.exists()) {
                return;
            }

            for (File fileList : configFile.listFiles()) {
                if (fileList != null && fileList.getName().contains(".yml") && fileList.getName().length() > 35) {
                    try {
                        FileManager.Config config = new FileManager.Config(fileManager, fileList);
                        FileConfiguration configLoad = config.getFileConfiguration();

                        UUID islandOwnerUUID = FastUUID.parseUUID(fileList.getName().replace(".yml", ""));

                        List<String> islandSignature = new ArrayList<>();

                        if (configLoad.getString("Visitor.Signature.Message") != null) {
                            islandSignature = configLoad.getStringList("Visitor.Signature.Message");
                        }

                        int size = 100;

                        if (configLoad.getString("Size") != null) {
                            size = configLoad.getInt("Size");
                        }

                        IslandStatus status;
                        String open = configLoad.getString("Visitor.Open", null);
                        if (open != null && (open.equalsIgnoreCase("true") ||
                                open.equalsIgnoreCase("false"))) {
                            if (configLoad.getBoolean("Visitor.Open")) {
                                status = IslandStatus.OPEN;
                            } else {
                                status = IslandStatus.CLOSED;
                            }
                            configLoad.set("Visitor.Open", null);
                        } else {
                            if (configLoad.getString("Visitor.Status") != null) {
                                status = IslandStatus.getEnum(configLoad.getString("Visitor.Status"));
                            } else {
                                status = IslandStatus.WHITELISTED;
                            }
                        }

                        createIsland(islandOwnerUUID,
                                new IslandLocation[]{
                                        new IslandLocation(IslandWorld.NORMAL, null,
                                                worldManager.getLocation(fileManager.getLocation(config,
                                                        "Location.Normal.Island", true), IslandWorld.NORMAL)),
                                        new IslandLocation(IslandWorld.NETHER, null,
                                                worldManager.getLocation(fileManager.getLocation(config,
                                                        "Location.Nether.Island", true), IslandWorld.NETHER)),
                                        new IslandLocation(IslandWorld.END, null,
                                                worldManager.getLocation(fileManager.getLocation(config,
                                                        "Location.Nether.Island", true), IslandWorld.END))},
                                size,
                                configLoad.getStringList("Members").size()
                                        + configLoad.getStringList("Operators").size() + 1,
                                configLoad.getDouble("Bank.Balance", 0),
                                getIslandSafeLevel(islandOwnerUUID), new IslandLevel(islandOwnerUUID, this.plugin),
                                islandSignature,
                                status);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void transfer(UUID uuid1, UUID uuid2) {
        Visit visit = getIsland(uuid1);
        visit.setOwnerUUID(uuid2);
        visit.getLevel().setOwnerUUID(uuid2);
        visit.save();

        File oldVisitDataFile = new File(new File(this.plugin.getDataFolder().toString() + "/visit-data"),
                uuid1.toString() + ".yml");
        File newVisitDataFile = new File(new File(this.plugin.getDataFolder().toString() + "/visit-data"),
                uuid2.toString() + ".yml");

        this.plugin.getFileManager().unloadConfig(oldVisitDataFile);
        this.plugin.getFileManager().unloadConfig(newVisitDataFile);

        oldVisitDataFile.renameTo(newVisitDataFile);

        removeIsland(uuid1);
        addIsland(uuid2, visit);
    }

    public void removeVisitors(Island island, VisitManager.Removal removal) {
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();
        FileManager fileManager = this.plugin.getFileManager();

        FileManager.Config config = fileManager.getConfig(new File(this.plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        for (UUID visitorList : this.plugin.getIslandManager().getVisitorsAtIsland(island)) {
            Player targetPlayer = Bukkit.getServer().getPlayer(visitorList);

            LocationUtil.teleportPlayerToSpawn(targetPlayer);

            messageManager.sendMessage(targetPlayer, configLoad.getString("Island.Visit." + removal.name() + ".Message"));
            soundManager.playSound(targetPlayer, XSound.ENTITY_ENDERMAN_TELEPORT);
        }
    }

    public int getIslandSafeLevel(UUID islandOwnerUUID) {
        FileManager fileManager = this.plugin.getFileManager();

        FileManager.Config settingDataConfig = new FileManager.Config(fileManager,
                new File(this.plugin.getDataFolder() + "/setting-data", islandOwnerUUID.toString() + ".yml"));
        FileConfiguration settingDataConfigLoad = settingDataConfig.getFileConfiguration();

        FileManager.Config mainConfig = fileManager.getConfig(new File(this.plugin.getDataFolder(), "config.yml"));
        FileConfiguration mainConfigLoad = mainConfig.getFileConfiguration();

        int safeLevel = 0;

        Map<String, Boolean> settings = new HashMap<>();
        settings.put("KeepItemsOnDeath", false);
        settings.put("PvP", true);
        settings.put("Damage", true);

        for (String settingList : settings.keySet()) {
            if (mainConfigLoad.getBoolean("Island.Settings." + settingList + ".Enable")
                    && settingDataConfigLoad.getString("Settings.Owner." + settingList) != null
                    && settingDataConfigLoad.getBoolean("Settings.Owner." + settingList) == settings.get(settingList)) {
                safeLevel++;
            }
        }

        return safeLevel;
    }

    public boolean hasIsland(UUID islandOwnerUUID) {
        return this.visitStorage.containsKey(islandOwnerUUID);
    }

    public Visit getIsland(UUID islandOwnerUUID) {
        return this.visitStorage.get(islandOwnerUUID);
    }

    public HashMap<UUID, Visit> getIslands() {
        return this.visitStorage;
    }

    public Map<UUID, Visit> getOpenIslands() {
        Map<UUID, Visit> visitIslands = new ConcurrentHashMap<>(this.visitStorage);

        Iterator<UUID> it = visitIslands.keySet().iterator();

        while (it.hasNext()) {
            UUID islandOwnerUUID = it.next();
            Visit visit = visitIslands.get(islandOwnerUUID);

            if (visit.getStatus() != IslandStatus.OPEN) {
                visitIslands.remove(islandOwnerUUID);
            }
        }

        return visitIslands;
    }

    public void createIsland(UUID islandOwnerUUID, IslandLocation[] islandLocations, int islandSize, int islandMembers,
                             double islandBankBalance, int safeLevel, IslandLevel islandLevel, List<String> islandSignature, IslandStatus status) {
        this.visitStorage.put(islandOwnerUUID, new Visit(this.plugin, islandOwnerUUID, islandLocations, islandSize,
                islandMembers, islandBankBalance, safeLevel, islandLevel, islandSignature, status));
    }

    public void addIsland(UUID islandOwnerUUID, Visit visit) {
        this.visitStorage.put(islandOwnerUUID, visit);
    }

    public void removeIsland(UUID islandOwnerUUID) {
        if (hasIsland(islandOwnerUUID)) {
            this.visitStorage.remove(islandOwnerUUID);
        }
    }

    public void unloadIsland(UUID islandOwnerUUID) {
        if (hasIsland(islandOwnerUUID)) {
            this.plugin.getFileManager()
                    .unloadConfig(new File(new File(this.plugin.getDataFolder(), "visit-data"),
                            islandOwnerUUID.toString() + ".yml"));
            this.visitStorage.remove(islandOwnerUUID);
        }
    }

    public void deleteIsland(UUID islandOwnerUUID) {
        if (hasIsland(islandOwnerUUID)) {
            this.plugin.getFileManager()
                    .deleteConfig(new File(new File(this.plugin.getDataFolder(), "visit-data"),
                            islandOwnerUUID.toString() + ".yml"));
            this.visitStorage.remove(islandOwnerUUID);
        }
    }

    public enum Removal {
        UNLOADED, KICKED, DELETED
    }
}
