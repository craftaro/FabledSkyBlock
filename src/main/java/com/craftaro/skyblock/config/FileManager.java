package com.craftaro.skyblock.config;

import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.IslandWorld;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public class FileManager {
    private final SkyBlock plugin;
    private final Map<String, Config> loadedConfigs = new HashMap<>();

    public FileManager(SkyBlock plugin) {
        this.plugin = plugin;

        backupIfNeeded();

        loadConfigs();
    }

    public void loadConfigs() {
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdir();
        }

        File structureDirectory = new File(this.plugin.getDataFolder(), "structures");

        if (!structureDirectory.exists()) {
            structureDirectory.mkdir();
        }

        // Will remain null unless WorldEdit is present.
        File schematicsDirectory;

        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit") && !(schematicsDirectory = new File(this.plugin.getDataFolder(), "schematics")).exists()) {
            schematicsDirectory.mkdir();
        }

        Map<String, File> configFiles = new LinkedHashMap<>();
        configFiles.put("limits.yml", new File(this.plugin.getDataFolder(), "limits.yml"));
        configFiles.put("worlds.yml", new File(this.plugin.getDataFolder(), "worlds.yml"));
        configFiles.put("levelling.yml", new File(this.plugin.getDataFolder(), "levelling.yml"));
        configFiles.put("config.yml", new File(this.plugin.getDataFolder(), "config.yml"));
        configFiles.put("language.yml", new File(this.plugin.getDataFolder(), "language.yml"));
        configFiles.put("settings.yml", new File(this.plugin.getDataFolder(), "settings.yml"));
        configFiles.put("upgrades.yml", new File(this.plugin.getDataFolder(), "upgrades.yml"));
        configFiles.put("biomes.yml", new File(this.plugin.getDataFolder(), "biomes.yml"));
        // configFiles.put("menus.yml", new File(plugin.getDataFolder(), "menus.yml"));
        configFiles.put("scoreboard.yml", new File(this.plugin.getDataFolder(), "scoreboard.yml"));
        configFiles.put("placeholders.yml", new File(this.plugin.getDataFolder(), "placeholders.yml"));
        configFiles.put("generators.yml", new File(this.plugin.getDataFolder(), "generators.yml"));
        configFiles.put("stackables.yml", new File(this.plugin.getDataFolder(), "stackables.yml"));
        configFiles.put("structures.yml", new File(this.plugin.getDataFolder(), "structures.yml"));
        configFiles.put("rewards.yml", new File(this.plugin.getDataFolder(), "rewards.yml"));
        configFiles.put("structures/default.structure", new File(this.plugin.getDataFolder() + "/structures", "default.structure"));
        configFiles.put("challenges.yml", new File(this.plugin.getDataFolder(), "challenges.yml"));

        File oldStructureFile = new File(this.plugin.getDataFolder().toString() + "/structures", "default.structure");
        oldStructureFile.delete();

        for (Entry<String, File> configEntry : configFiles.entrySet()) {

            String fileName = configEntry.getKey();
            File configFile = configEntry.getValue();

            if (fileName.equals("structures/default.structure")) {
                configFile.delete();
                try {
                    configFile.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try (InputStream is = this.plugin.getResource(fileName); OutputStream os = Files.newOutputStream(configFile.toPath())) {
                    if (is != null) {
                        ByteStreams.copy(is, os);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                continue;
            }

            if (configFile.exists()) {
                if (fileName.equals("config.yml") ||
                        fileName.equals("language.yml") ||
                        fileName.equals("worlds.yml") ||
                        fileName.equals("biomes.yml") ||
                        fileName.equals("scoreboard.yml") ||
                        fileName.equals("placeholders.yml")) {
                    FileChecker fileChecker;

                    if (fileName.equals("config.yml") || fileName.equals("biomes.yml")) {
                        fileChecker = new FileChecker(this.plugin, this, fileName, true);
                    } else {
                        fileChecker = new FileChecker(this.plugin, this, fileName, false);
                    }

                    fileChecker.loadSections();
                    fileChecker.compareFiles();
                    fileChecker.saveChanges();
                }
            } else {
                try {
                    configFile.createNewFile();
                    try (InputStream is = this.plugin.getResource(fileName); OutputStream os = Files.newOutputStream(configFile.toPath())) {
                        if (is != null) {
                            ByteStreams.copy(is, os);
                        }
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
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());

        if (direction) {
            section.set("yaw", location.getYaw());
            section.set("pitch", location.getPitch());
        }

        try {
            config.getFileConfiguration().save(config.getFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void backupIfNeeded() {
        File languageFile = new File(this.plugin.getDataFolder(), "language.yml");
        File scoreboardFile = new File(this.plugin.getDataFolder(), "scoreboard.yml");
        File placeholderFile = new File(this.plugin.getDataFolder(), "placeholders.yml");
        if (languageFile.exists() && (!scoreboardFile.exists() || !placeholderFile.exists())) {
            File backupDir = new File(this.plugin.getDataFolder().toString() + "/backup");
            if (!backupDir.exists()) {
                backupDir.mkdir();
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
            LocalDateTime now = LocalDateTime.now();


            Path oldLanguagePath = languageFile.toPath();
            Path newLanguagePath = new File(this.plugin.getDataFolder() + "/backup", "language" + dtf.format(now) + ".yml").toPath();

            CopyOption[] options = new CopyOption[]{
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES
            };

            try {
                Files.copy(oldLanguagePath, newLanguagePath, options);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Location getLocation(Config config, String path, boolean direction) {
        ConfigurationSection section = config.getFileConfiguration().getConfigurationSection(path);
        if (section == null) {
            return null;
        }

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
        this.loadedConfigs.remove(configPath.getPath());
    }

    public void deleteConfig(File configPath) {
        Config config = getConfig(configPath);
        config.getFile().delete();
        this.loadedConfigs.remove(configPath.getPath());
    }

    public Config getConfig(File configPath) {
        Config cached = this.loadedConfigs.get(configPath.getPath());

        if (cached != null) {
            return cached;
        }

        Config config = new Config(this, configPath);
        this.loadedConfigs.put(configPath.getPath(), config);

        return config;
    }

    public Map<String, Config> getConfigs() {
        return this.loadedConfigs;
    }

    public boolean isConfigLoaded(java.io.File configPath) {
        return this.loadedConfigs.containsKey(configPath.getPath());
    }

    public InputStream getConfigContent(Reader reader) {
        try {
            String addLine, currentLine, pluginName = this.plugin.getDescription().getName();
            int commentNum = 0;

            StringBuilder whole = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(reader);

            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.contains("#")) {
                    addLine = currentLine.replace("[!]", "IMPORTANT").replace(":", "-").replaceFirst("#", pluginName + "_COMMENT_" + commentNum + ":");
                    whole.append(addLine).append("\n");
                    commentNum++;
                } else {
                    whole.append(currentLine).append("\n");
                }
            }

            String config = whole.toString();
            InputStream configStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
            bufferedReader.close();

            return configStream;
        } catch (IOException ex) {
            ex.printStackTrace();

            return null;
        }
    }

    public InputStream getConfigContent(File configFile) {
        if (!configFile.exists()) {
            return null;
        }

        try {
            return getConfigContent(new FileReader(configFile));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private String prepareConfigString(String configString) {
        String[] lines = configString.split("\n");
        StringBuilder config = new StringBuilder();

        for (String line : lines) {
            if (line.contains(this.plugin.getDescription().getName() + "_COMMENT")) {
                config.append(line.replace("IMPORTANT", "[!]")
                                .replace("\n", "")
                                .replace(this.plugin.getDescription().getName() + "_COMMENT_", "#")
                                .replaceAll("[0-9]+:", ""))
                        .append("\n");
            } else if (line.contains(":")) {
                config.append(line).append("\n");
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static class Config {
        private final File configFile;
        private FileConfiguration configLoad;

        public Config(FileManager fileManager, java.io.File configPath) {
            this.configFile = configPath;

            if (configPath.getName().equals("config.yml")) {
                this.configLoad = YamlConfiguration.loadConfiguration(new InputStreamReader(fileManager.getConfigContent(this.configFile)));
            } else {
                this.configLoad = YamlConfiguration.loadConfiguration(configPath);
            }
        }

        public File getFile() {
            return this.configFile;
        }

        public FileConfiguration getFileConfiguration() {
            return this.configLoad;
        }

        public FileConfiguration loadFile() {
            this.configLoad = YamlConfiguration.loadConfiguration(this.configFile);

            return this.configLoad;
        }
    }
}
