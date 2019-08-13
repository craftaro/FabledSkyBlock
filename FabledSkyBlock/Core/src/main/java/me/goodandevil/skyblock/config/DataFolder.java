package me.goodandevil.skyblock.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

public enum DataFolder {
    BAN_DATA("ban-data"),
    COOP_DATA("coop-data"),
    ISLAND_DATA("island-data"),
    LEVEL_DATA("level-data"),
    PLAYER_DATA("player-data"),
    SETTING_DATA("setting-data"),
    STRUCTURES("structures"),
    VISIT_DATA("visit-data");

    private final String folderName;

    DataFolder(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return this.folderName;
    }

    public File getResourcePath(JavaPlugin plugin) {
        return new File(plugin.getDataFolder(), this.folderName);
    }

    public File getFileInFolder(JavaPlugin plugin, UUID uuid) {
        return new File(this.getResourcePath(plugin), uuid.toString() + ".yml");
    }
}
