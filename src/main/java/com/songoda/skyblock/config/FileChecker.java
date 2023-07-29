package com.songoda.skyblock.config;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileChecker {
    private final FileManager fileManager;

    private final Map<File.Type, File> loadedFiles;

    public FileChecker(SkyBlock plugin, FileManager fileManager, String configurationFileName, boolean applyComments) {
        this.fileManager = fileManager;

        this.loadedFiles = new EnumMap<>(File.Type.class);

        java.io.File configFile = new java.io.File(plugin.getDataFolder(), configurationFileName);
        this.loadedFiles.put(File.Type.CREATED, new File(fileManager, configFile, YamlConfiguration.loadConfiguration(configFile)));

        if (applyComments) {
            this.loadedFiles.put(File.Type.RESOURCE, new File(null, null, YamlConfiguration.loadConfiguration(new InputStreamReader(fileManager.getConfigContent(new InputStreamReader(plugin.getResource(configurationFileName)))))));
        } else {
            this.loadedFiles.put(File.Type.RESOURCE, new File(null, null, YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(configurationFileName)))));
        }
    }

    public void loadSections() {
        for (File.Type fileType : File.Type.values()) {
            File file = this.loadedFiles.get(fileType);
            FileConfiguration configLoad = file.getFileConfiguration();

            Set<String> configKeys = configLoad.getKeys(true);

            for (String configKeysList : configKeys) {
                file.addKey(configKeysList, configLoad.get(configKeysList));
            }
        }
    }

    public void compareFiles() {
        for (File.Type fileType : File.Type.values()) {
            File file = this.loadedFiles.get(fileType);

            if (fileType == File.Type.RESOURCE) {
                File createdFile = this.loadedFiles.get(File.Type.CREATED);
                FileConfiguration createdConfigLoad = createdFile.getFileConfiguration();

                for (String configKeyList : file.getKeys().keySet()) {
                    if (createdConfigLoad.getString(configKeyList) == null) {
                        createdConfigLoad.set(configKeyList, file.getKeys().get(configKeyList));
                    }
                }
            }
        }
    }

    public void saveChanges() {
        File file = this.loadedFiles.get(File.Type.CREATED);

        try {
            if (file.getFile().getName().equals("config.yml")) {
                this.fileManager.saveConfig(file.getFileConfiguration().saveToString(), file.getFile());
            } else {
                file.getFileConfiguration().save(file.getFile());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static class File {
        private final java.io.File configFile;
        private FileConfiguration configLoad;

        private final HashMap<String, Object> configKeys;

        public File(FileManager fileManager, java.io.File configFile, FileConfiguration configLoad) {
            this.configFile = configFile;
            this.configLoad = configLoad;
            this.configKeys = new HashMap<>();

            if (configFile != null && configFile.getName().equals("config.yml")) {
                this.configLoad = YamlConfiguration.loadConfiguration(new InputStreamReader(fileManager.getConfigContent(configFile)));
            }
        }

        public java.io.File getFile() {
            return this.configFile;
        }

        public FileConfiguration getFileConfiguration() {
            return this.configLoad;
        }

        public HashMap<String, Object> getKeys() {
            return this.configKeys;
        }

        public void addKey(String key, Object object) {
            this.configKeys.put(key, object);
        }

        public enum Type {
            CREATED,
            RESOURCE
        }
    }
}
