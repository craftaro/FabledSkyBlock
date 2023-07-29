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
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);
        UserCacheManager userCacheManager = plugin.getUserCacheManager();

        this.bukkitOfflinePlayer = Bukkit.getServer().getOfflinePlayer(name);

        if (userCacheManager.hasUser(name)) {
            this.uuid = userCacheManager.getUser(name);
            this.bukkitOfflinePlayer = Bukkit.getServer().getOfflinePlayer(this.uuid);
        } else {
            this.uuid = this.bukkitOfflinePlayer.getUniqueId();
        }

        this.name = this.bukkitOfflinePlayer.getName();

        FileConfiguration configLoad = YamlConfiguration.loadConfiguration(
                new File(new File(plugin.getDataFolder().toString() + "/player-data"), FastUUID.toString(this.uuid) + ".yml"));
        this.texture = new String[]{configLoad.getString("Texture.Signature"), configLoad.getString("Texture.Value")};
        this.playtime = configLoad.getInt("Statistics.Island.Playtime");
        this.memberSince = configLoad.getString("Statistics.Island.Join");
        this.lastOnline = configLoad.getString("Statistics.Island.LastOnline");

        if (!(configLoad.getString("Island.Owner") == null || configLoad.getString("Island.Owner").isEmpty())) {
            this.owner = FastUUID.parseUUID(configLoad.getString("Island.Owner"));
        }
    }

    public OfflinePlayer(UUID uuid) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);
        UserCacheManager userCacheManager = plugin.getUserCacheManager();

        this.bukkitOfflinePlayer = Bukkit.getServer().getOfflinePlayer(uuid);

        this.name = this.bukkitOfflinePlayer.getName();
        this.uuid = uuid;

        if (this.name == null) {
            if (userCacheManager.hasUser(uuid)) {
                this.name = userCacheManager.getUser(uuid);
            } else {
                this.name = "Unknown";
            }
        }

        FileConfiguration configLoad = YamlConfiguration.loadConfiguration(
                new File(new File(plugin.getDataFolder().toString() + "/player-data"), FastUUID.toString(uuid) + ".yml"));
        this.texture = new String[]{configLoad.getString("Texture.Signature"), configLoad.getString("Texture.Value")};
        this.playtime = configLoad.getInt("Statistics.Island.Playtime");
        this.memberSince = configLoad.getString("Statistics.Island.Join");
        this.lastOnline = configLoad.getString("Statistics.Island.LastOnline");

        if (!(configLoad.getString("Island.Owner") == null || configLoad.getString("Island.Owner").isEmpty())) {
            this.owner = FastUUID.parseUUID(configLoad.getString("Island.Owner"));
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getMemberSince() {
        return this.memberSince;
    }

    public String getLastOnline() {
        return this.lastOnline;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public String[] getTexture() {
        return this.texture;
    }

    public int getPlaytime() {
        return this.playtime;
    }

    public org.bukkit.OfflinePlayer getBukkitOfflinePlayer() {
        return this.bukkitOfflinePlayer;
    }
}
