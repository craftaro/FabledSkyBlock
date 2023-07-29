package com.songoda.skyblock.ban;

import com.eatthepath.uuid.FastUUID;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
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
        if (!this.plugin.getConfiguration().getBoolean("Island.Visitor.Unload")) {
            File configFile = new File(this.plugin.getDataFolder(), "island-data");

            if (configFile.exists()) {
                for (File fileList : configFile.listFiles()) {
                    UUID islandOwnerUUID = FastUUID.parseUUID(fileList.getName().replaceFirst("[.][^.]+$", ""));
                    createIsland(islandOwnerUUID);
                }
            }
        }
    }

    public void transfer(UUID uuid1, UUID uuid2) {
        FileManager fileManager = this.plugin.getFileManager();

        Ban ban = getIsland(uuid1);
        ban.save();

        File oldBanDataFile = new File(new File(this.plugin.getDataFolder(), "ban-data"), FastUUID.toString(uuid1) + ".yml");
        File newBanDataFile = new File(new File(this.plugin.getDataFolder(), "ban-data"), FastUUID.toString(uuid2) + ".yml");

        fileManager.unloadConfig(oldBanDataFile);
        fileManager.unloadConfig(newBanDataFile);

        oldBanDataFile.renameTo(newBanDataFile);

        removeIsland(uuid1);
        addIsland(uuid2, ban);
    }

    public void removeVisitor(Island island) {
        MessageManager messageManager = this.plugin.getMessageManager();
        SoundManager soundManager = this.plugin.getSoundManager();

        FileConfiguration configLoad = this.plugin.getLanguage();

        for (UUID visitorList : this.plugin.getIslandManager().getVisitorsAtIsland(island)) {
            Player targetPlayer = Bukkit.getServer().getPlayer(visitorList);

            LocationUtil.teleportPlayerToSpawn(targetPlayer);

            messageManager.sendMessage(targetPlayer, configLoad.getString("Island.Visit.Banned.Island.Message"));
            soundManager.playSound(targetPlayer, CompatibleSound.ENTITY_ENDERMAN_TELEPORT.getSound(), 1.0F, 1.0F);
        }
    }

    public boolean hasIsland(UUID islandOwnerUUID) {
        return this.banStorage.containsKey(islandOwnerUUID);
    }

    public Ban getIsland(UUID islandOwnerUUID) {
        return this.banStorage.get(islandOwnerUUID);
    }

    public Map<UUID, Ban> getIslands() {
        return this.banStorage;
    }

    public void createIsland(UUID islandOwnerUUID) {
        this.banStorage.put(islandOwnerUUID, new Ban(islandOwnerUUID));
    }

    public void addIsland(UUID islandOwnerUUID, Ban ban) {
        this.banStorage.put(islandOwnerUUID, ban);
    }

    public void removeIsland(UUID islandOwnerUUID) {
        this.banStorage.remove(islandOwnerUUID);
    }

    public void unloadIsland(UUID islandOwnerUUID) {
        if (hasIsland(islandOwnerUUID)) {
            this.plugin.getFileManager().unloadConfig(new File(new File(this.plugin.getDataFolder(), "ban-data"), islandOwnerUUID.toString() + ".yml"));
            this.banStorage.remove(islandOwnerUUID);
        }
    }

    public void deleteIsland(UUID islandOwnerUUID) {
        if (hasIsland(islandOwnerUUID)) {
            this.plugin.getFileManager().deleteConfig(new File(new File(this.plugin.getDataFolder(), "ban-data"), islandOwnerUUID.toString() + ".yml"));
            this.banStorage.remove(islandOwnerUUID);
        }
    }
}
