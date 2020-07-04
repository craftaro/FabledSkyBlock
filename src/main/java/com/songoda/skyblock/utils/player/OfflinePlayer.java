package com.songoda.skyblock.utils.player;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.usercache.UserCacheManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class OfflinePlayer {

    private UUID uuid;
    org.bukkit.OfflinePlayer bukkitOfflinePlayer;

    private String name;
    private String memberSince;
    private String lastOnline;
    private UUID owner = null;
    private String[] texture;

    private int playtime;

    public OfflinePlayer(String name) {
        SkyBlock skyblock = SkyBlock.getInstance();
        UserCacheManager userCacheManager = skyblock.getUserCacheManager();
        
        bukkitOfflinePlayer = Bukkit.getServer().getOfflinePlayer(name);

        this.name = bukkitOfflinePlayer.getName();
        this.uuid = bukkitOfflinePlayer.getUniqueId();

        if (this.uuid == null && userCacheManager.hasUser(name)) {
            this.uuid = userCacheManager.getUser(name);
        }

        FileConfiguration configLoad = YamlConfiguration.loadConfiguration(
                new File(new File(skyblock.getDataFolder().toString() + "/player-data"), uuid.toString() + ".yml"));
        texture = new String[]{configLoad.getString("Texture.Signature"), configLoad.getString("Texture.Value")};
        playtime = configLoad.getInt("Statistics.Island.Playtime");
        memberSince = configLoad.getString("Statistics.Island.Join");
        lastOnline = configLoad.getString("Statistics.Island.LastOnline");

        if (!(configLoad.getString("Island.Owner") == null || configLoad.getString("Island.Owner").isEmpty())) {
            owner = UUID.fromString(configLoad.getString("Island.Owner"));
        }
    }

    public OfflinePlayer(UUID uuid) {
        SkyBlock skyblock = SkyBlock.getInstance();
        UserCacheManager userCacheManager = skyblock.getUserCacheManager();
    
        bukkitOfflinePlayer = Bukkit.getServer().getOfflinePlayer(uuid);

        this.name = bukkitOfflinePlayer.getName();
        this.uuid = uuid;

        if (this.name == null && userCacheManager.hasUser(uuid)) {
            this.name = userCacheManager.getUser(uuid);
        }

        FileConfiguration configLoad = YamlConfiguration.loadConfiguration(
                new File(new File(skyblock.getDataFolder().toString() + "/player-data"), uuid.toString() + ".yml"));
        texture = new String[]{configLoad.getString("Texture.Signature"), configLoad.getString("Texture.Value")};
        playtime = configLoad.getInt("Statistics.Island.Playtime");
        memberSince = configLoad.getString("Statistics.Island.Join");
        lastOnline = configLoad.getString("Statistics.Island.LastOnline");

        if (!(configLoad.getString("Island.Owner") == null || configLoad.getString("Island.Owner").isEmpty())) {
            owner = UUID.fromString(configLoad.getString("Island.Owner"));
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
