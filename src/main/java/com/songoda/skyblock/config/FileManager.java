package com.songoda.skyblock.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.ByteStreams;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.IslandWorld;

public class FileManager {

    private final SkyBlock skyblock;
    private Map<String, Config> loadedConfigs = new HashMap<>();

    public FileManager(SkyBlock skyblock) {
        this.skyblock = skyblock;

        loadConfigs();
    }

    public void loadConfigs() {
        if (!skyblock.getDataFolder().exists()) {
            skyblock.getDataFolder().mkdir();
        }

        File structureDirectory = new File(skyblock.getDataFolder().toString() + "/structures");

        if (!structureDirectory.exists()) {
            structureDirectory.mkdir();
        }

        // Will remain null unless WorldEdit is present.
        File schematicsDirectory = null;

        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit") && !(schematicsDirectory = new File(skyblock.getDataFolder().toString() + "/schematics")).exists()) {
            schematicsDirectory.mkdir();
        }

        Map<String, File> configFiles = new LinkedHashMap<>();
        configFiles.put("limits.yml", new File(skyblock.getDataFolder(), "limits.yml"));
        configFiles.put("worlds.yml", new File(skyblock.getDataFolder(), "worlds.yml"));
        configFiles.put("levelling.yml", new File(skyblock.getDataFolder(), "levelling.yml"));
        configFiles.put("config.yml", new File(skyblock.getDataFolder(), "config.yml"));
        configFiles.put("language.yml", new File(skyblock.getDataFolder(), "language.yml"));
        configFiles.put("settings.yml", new File(skyblock.getDataFolder(), "settings.yml"));
        configFiles.put("upgrades.yml", new File(skyblock.getDataFolder(), "upgrades.yml"));
        configFiles.put("generators.yml", new File(skyblock.getDataFolder(), "generators.yml"));
        configFiles.put("stackables.yml", new File(skyblock.getDataFolder(), "stackables.yml"));
        configFiles.put("structures.yml", new File(skyblock.getDataFolder(), "structures.yml"));
        configFiles.put("structures/default.structure", new File(skyblock.getDataFolder().toString() + "/structures", "default.structure"));
        configFiles.put("challenges.yml", new File(skyblock.getDataFolder(), "challenges.yml"));

        File oldStructureFile = new File(skyblock.getDataFolder().toString() + "/structures", "default.structure");
        oldStructureFile.delete();

        for (Entry<String, File> configEntry : configFiles.entrySet()) {

            String fileName = configEntry.getKey();
            File configFile = configEntry.getValue();

            if (fileName.equals("structures/default.structure")) {
                configFile.delete();
                try {
                    configFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try (InputStream is = skyblock.getResource(fileName); OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }

            if (configFile.exists()) {
                if (fileName.equals("config.yml") || fileName.equals("language.yml") || fileName.equals("settings.yml") || fileName.equals("worlds.yml")) {
                    FileChecker fileChecker;

                    if (fileName.equals("config.yml")) {
                        fileChecker = new FileChecker(skyblock, this, fileName, true);
                    } else {
                        fileChecker = new FileChecker(skyblock, this, fileName, false);
                    }

                    fileChecker.loadSections();
                    fileChecker.compareFiles();
                    fileChecker.saveChanges();
                }
            } else {
                try {
                    configFile.createNewFile();
                    try (InputStream is = skyblock.getResource(fileName); OutputStream os = new FileOutputStream(configFile)) {
                        ByteStreams.copy(is, os);
                    }

                    if (fileName.equals("worlds.yml")) {
                        File mainConfigFile = configFiles.get("config.yml");

                        if (isFileExist(mainConfigFile)) {
                            Config config = new Config(this, configFile);
                            Config mainConfig = new Config(this, mainConfigFile);

                            FileConfiguration configLoad = config.getFileConfiguration();
                            FileConfiguration mainConfigLoad = mainConfig.getFileConfiguration();

                            for (IslandWorld worldList : IslandWorld.values()) {
                                if (mainConfigLoad.getString("World." + worldList.name()) != null) {
                                    configLoad.set("World." + worldList.name() + ".nextAvailableLocation.x", mainConfigLoad.getDouble("World." + worldList.name() + ".nextAvailableLocation.x"));
                                    configLoad.set("World." + worldList.name() + ".nextAvailableLocation.z", mainConfigLoad.getDouble("World." + worldList.name() + ".nextAvailableLocation.z"));
                                }
                            }

                            mainConfigLoad.set("World", null);

                            configLoad.save(config.getFile());
                            saveConfig(mainConfigLoad.saveToString(), mainConfig.getFile());
                        }
                    }
                } catch (IOException ex) {
                    Bukkit.getServer().getLogger().log(Level.WARNING, "SkyBlock | Error: Unable to create configuration file.");
                }
            }
        }
    }

    public void setLocation(Config config, String path, Location location, boolean direction) {

        final ConfigurationSection section = config.getFileConfiguration().createSection(path);

        section.set("world", location.getWorld().getName());
        section.set("x", Double.valueOf(location.getX()));
        section.set("y", Double.valueOf(location.getY()));
        section.set("z", Double.valueOf(location.getZ()));

        if (direction) {
            section.set("yaw", Float.valueOf(location.getYaw()));
            section.set("pitch", Float.valueOf(location.getPitch()));
        }

        try {
            config.getFileConfiguration().save(config.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Location getLocation(Config config, String path, boolean direction) {

        ConfigurationSection section = config.getFileConfiguration().getConfigurationSection(path);

        if (section == null) return null;

        String world = section.getString("world");
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");

        double yaw = 0.0D;
        double pitch = 0.0D;

        if (direction) {
            yaw = section.getDouble("yaw");
            pitch = section.getDouble("pitch");
        }

        return new org.bukkit.Location(Bukkit.getWorld(world), x, y, z, (short) yaw, (short) pitch);
    }

    public boolean isFileExist(File configPath) {
        return configPath.exists();
    }

    public void unloadConfig(File configPath) {
        loadedConfigs.remove(configPath.getPath());
    }

    public void deleteConfig(File configPath) {
        Config config = getConfig(configPath);
        config.getFile().delete();
        loadedConfigs.remove(configPath.getPath());
    }

    public Config getConfig(File configPath) {

        Config cached = loadedConfigs.get(configPath.getPath());

        if (cached != null) return cached;

        Config config = new Config(this, configPath);
        loadedConfigs.put(configPath.getPath(), config);

        return config;
    }

    public Map<String, Config> getConfigs() {
        return loadedConfigs;
    }

    public boolean isConfigLoaded(java.io.File configPath) {
        return loadedConfigs.containsKey(configPath.getPath());
    }

    public InputStream getConfigContent(Reader reader) {
        try {
            String addLine, currentLine, pluginName = skyblock.getDescription().getName();
            int commentNum = 0;

            StringBuilder whole = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(reader);

            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.contains("#")) {
                    addLine = currentLine.replace("[!]", "IMPORTANT").replace(":", "-").replaceFirst("#", pluginName + "_COMMENT_" + commentNum + ":");
                    whole.append(addLine + "\n");
                    commentNum++;
                } else {
                    whole.append(currentLine + "\n");
                }
            }

            String config = whole.toString();
            InputStream configStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
            bufferedReader.close();

            return configStream;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public InputStream getConfigContent(File configFile) {
        if (!configFile.exists()) {
            return null;
        }

        try {
            return getConfigContent(new FileReader(configFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String prepareConfigString(String configString) {
        String[] lines = configString.split("\n");
        StringBuilder config = new StringBuilder();

        for (String line : lines) {
            if (line.contains(skyblock.getDescription().getName() + "_COMMENT")) {
                config.append(line.replace("IMPORTANT", "[!]").replace("\n", "").replace(skyblock.getDescription().getName() + "_COMMENT_", "#").replaceAll("[0-9]+:", "") + "\n");
            } else if (line.contains(":")) {
                config.append(line + "\n");
            }
        }

        return config.toString();
    }

    public void saveConfig(String configString, File configFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            writer.write(prepareConfigString(configString));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Config {

        private File configFile;
        private FileConfiguration configLoad;

        public Config(FileManager fileManager, java.io.File configPath) {
            configFile = configPath;

            if (configPath.getName().equals("config.yml")) {
                configLoad = YamlConfiguration.loadConfiguration(new InputStreamReader(fileManager.getConfigContent(configFile)));
            } else {
                configLoad = YamlConfiguration.loadConfiguration(configPath);
            }
        }

        public File getFile() {
            return configFile;
        }

        public FileConfiguration getFileConfiguration() {
            return configLoad;
        }

        public FileConfiguration loadFile() {
            configLoad = YamlConfiguration.loadConfiguration(configFile);

            return configLoad;
        }
    }
}
