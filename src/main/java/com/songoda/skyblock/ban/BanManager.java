package com.songoda.skyblock.ban;

import com.eatthepath.uuid.FastUUID;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.world.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BanManager {

    private final SkyBlock plugin;
    private final Map<UUID, Ban> banStorage = new HashMap<>();

    public BanManager(SkyBlock plugin) {
        this.plugin = plugin;

        loadIslands();
    }

    public void onDisable() {
        Map<UUID, Ban> banIslands = getIslands();

        for (Ban ban : banIslands.values()) {
            ban.save();
        }
    }

    public void loadIslands() {
        FileManager fileManager = plugin.getFileManager();

        if (!fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration()
                .getBoolean("Island.Visitor.Unload")) {
            File configFile = new File(plugin.getDataFolder().toString() + "/island-data");

            if (configFile.exists()) {
                for (File fileList : configFile.listFiles()) {
                    UUID islandOwnerUUID = FastUUID.parseUUID(fileList.getName().replaceFirst("[.][^.]+$", ""));
                    createIsland(islandOwnerUUID);
                }
            }
        }
    }

    public void transfer(UUID uuid1, UUID uuid2) {
        FileManager fileManager = plugin.getFileManager();

        Ban ban = getIsland(uuid1);
        ban.save();

        File oldBanDataFile = new File(new File(plugin.getDataFolder().toString() + "/ban-data"),
                FastUUID.toString(uuid1) + ".yml");
        File newBanDataFile = new File(new File(plugin.getDataFolder().toString() + "/ban-data"),
                FastUUID.toString(uuid2) + ".yml");

        fileManager.unloadConfig(oldBanDataFile);
        fileManager.unloadConfig(newBanDataFile);

        oldBanDataFile.renameTo(newBanDataFile);

        removeIsland(uuid1);
        addIsland(uuid2, ban);
    }

    public void removeVisitor(Island island) {
        MessageManager messageManager = plugin.getMessageManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        for (UUID visitorList : plugin.getIslandManager().getVisitorsAtIsland(island)) {
            Player targetPlayer = Bukkit.getServer().getPlayer(visitorList);

            LocationUtil.teleportPlayerToSpawn(targetPlayer);

            messageManager.sendMessage(targetPlayer, configLoad.getString("Island.Visit.Banned.Island.Message"));
            soundManager.playSound(targetPlayer, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);
        }
    }

    public boolean hasIsland(UUID islandOwnerUUID) {
        return banStorage.containsKey(islandOwnerUUID);
    }

    public Ban getIsland(UUID islandOwnerUUID) {
        return banStorage.get(islandOwnerUUID);
    }

    public Map<UUID, Ban> getIslands() {
        return banStorage;
    }

    public void createIsland(UUID islandOwnerUUID) {
        banStorage.put(islandOwnerUUID, new Ban(islandOwnerUUID));
    }

    public void addIsland(UUID islandOwnerUUID, Ban ban) {
        banStorage.put(islandOwnerUUID, ban);
    }

    public void removeIsland(UUID islandOwnerUUID) {
        banStorage.remove(islandOwnerUUID);
    }

    public void unloadIsland(UUID islandOwnerUUID) {
        if (hasIsland(islandOwnerUUID)) {
            plugin.getFileManager().unloadConfig(new File(new File(plugin.getDataFolder().toString() + "/ban-data"),
                    islandOwnerUUID.toString() + ".yml"));
            banStorage.remove(islandOwnerUUID);
        }
    }

    public void deleteIsland(UUID islandOwnerUUID) {
        if (hasIsland(islandOwnerUUID)) {
            plugin.getFileManager().deleteConfig(new File(new File(plugin.getDataFolder().toString() + "/ban-data"),
                    islandOwnerUUID.toString() + ".yml"));
            banStorage.remove(islandOwnerUUID);
        }
    }
}
