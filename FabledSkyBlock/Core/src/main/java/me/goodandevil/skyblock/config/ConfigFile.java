package me.goodandevil.skyblock.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public enum ConfigFile {
    LIMITS("limits.yml"),
    WORLDS("worlds.yml"),
    LEVELLING("levelling.yml"),
    CONFIG("config.yml", true, true),
    LANGUAGE("language.yml", true, false),
    SETTINGS("settings.yml", true, false),
    UPGRADES("upgrades.yml"),
    GENERATORS("generators.yml"),
    STACKABLES("stackables.yml"),
    STRUCTURES("structures.yml");

    private final String fileName;
    private final boolean autoUpdate;
    private final boolean applyComments;

    ConfigFile(String fileName, boolean autoUpdate, boolean applyComments) {
        this.fileName = fileName;
        this.autoUpdate = autoUpdate;
        this.applyComments = applyComments;
    }

    ConfigFile(String fileName) {
        this(fileName, false, false);
    }

    public String getFileName() {
        return this.fileName;
    }

    public File getResourcePath(JavaPlugin plugin) {
        return new File(plugin.getDataFolder(), this.fileName);
    }

    public boolean shouldUpdateFile() {
        return this.autoUpdate;
    }

    public boolean shouldApplyComments() {
        return this.applyComments;
    }
}