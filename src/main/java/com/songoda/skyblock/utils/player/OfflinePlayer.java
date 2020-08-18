package com.songoda.skyblock.utils.player;

import com.eatthepath.uuid.FastUUID;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.usercache.UserCacheManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class OfflinePlayer {

    private final UUID uuid;
    org.bukkit.OfflinePlayer bukkitOfflinePlayer;

    private String name;
    private final String memberSince;
    private final String lastOnline;
    private UUID owner = null;
    private final String[] texture;

    private final int playtime;

    public OfflinePlayer(String name) {
        SkyBlock plugin = SkyBlock.getInstance();
        UserCacheManager userCacheManager = plugin.getUserCacheManager();
        
        bukkitOfflinePlayer = Bukkit.getServer().getOfflinePlayer(name);
        
        if (userCacheManager.hasUser(name)) {
            this.uuid = userCacheManager.getUser(name);
            bukkitOfflinePlayer = Bukkit.getServer().getOfflinePlayer(uuid);
        } else {
            this.uuid = bukkitOfflinePlayer.getUniqueId();
        }
    
        this.name = bukkitOfflinePlayer.getName();

        FileConfiguration configLoad = YamlConfiguration.loadConfiguration(
                new File(new File(plugin.getDataFolder().toString() + "/player-data"), FastUUID.toString(uuid) + ".yml"));
        texture = new String[]{configLoad.getString("Texture.Signature"), configLoad.getString("Texture.Value")};
        playtime = configLoad.getInt("Statistics.Island.Playtime");
        memberSince = configLoad.getString("Statistics.Island.Join");
        lastOnline = configLoad.getString("Statistics.Island.LastOnline");

        if (!(configLoad.getString("Island.Owner") == null || configLoad.getString("Island.Owner").isEmpty())) {
            owner = FastUUID.parseUUID(configLoad.getString("Island.Owner"));
        }
    }

    public OfflinePlayer(UUID uuid) {
        SkyBlock plugin = SkyBlock.getInstance();
        UserCacheManager userCacheManager = plugin.getUserCacheManager();
    
        bukkitOfflinePlayer = Bukkit.getServer().getOfflinePlayer(uuid);

        this.name = bukkitOfflinePlayer.getName();
        this.uuid = uuid;

        if (this.name == null) {
            if(userCacheManager.hasUser(uuid)) {
                this.name = userCacheManager.getUser(uuid);
            } else {
                this.name = "Unknown";
            }
        }

        FileConfiguration configLoad = YamlConfiguration.loadConfiguration(
                new File(new File(plugin.getDataFolder().toString() + "/player-data"), FastUUID.toString(uuid) + ".yml"));
        texture = new String[]{configLoad.getString("Texture.Signature"), configLoad.getString("Texture.Value")};
        playtime = configLoad.getInt("Statistics.Island.Playtime");
        memberSince = configLoad.getString("Statistics.Island.Join");
        lastOnline = configLoad.getString("Statistics.Island.LastOnline");

        if (!(configLoad.getString("Island.Owner") == null || configLoad.getString("Island.Owner").isEmpty())) {
            owner = FastUUID.parseUUID(configLoad.getString("Island.Owner"));
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public UUID getOwner() {
        return owner;
    }

    public String[] getTexture() {
        return texture;
    }

    public int getPlaytime() {
        return playtime;
    }
    
    public org.bukkit.OfflinePlayer getBukkitOfflinePlayer() {
        return bukkitOfflinePlayer;
    }
}
