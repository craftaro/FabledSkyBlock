package me.goodandevil.skyblock.config;

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
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import com.google.common.io.ByteStreams;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.island.IslandWorld;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileManager {

	private final SkyBlock skyblock;
	private Map<ConfigFile, Config> loadedConfigs = new HashMap<>();
	private Map<DataFolder, Map<String, Config>> loadedDataFiles = new HashMap<>();

	public FileManager(SkyBlock skyblock) {
		this.skyblock = skyblock;

		loadConfigs();
	}

	public void loadConfigs() {
		this.loadedConfigs.clear();
		this.loadedDataFiles.clear();

		if (!skyblock.getDataFolder().exists()) {
			skyblock.getDataFolder().mkdir();
		}

		// Generate structures directory
		if (!new File(skyblock.getDataFolder(), "structures").exists()) {
			new File(skyblock.getDataFolder(), "structures").mkdir();
		}

		// Generate schematics directory if applicable
		if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit") && !new File(skyblock.getDataFolder(), "schematics").exists()) {
			new File(skyblock.getDataFolder(), "schematics").mkdir();
		}

		// Create default structure file
		if (!new File(skyblock.getDataFolder().toString() + "/structures", "default.structure").exists()) {
			File defaultStructureFile = new File(skyblock.getDataFolder().toString() + "/structures", "default.structure");
			try {
				defaultStructureFile.createNewFile();
			} catch (IOException ignored) { }

			try (InputStream is = skyblock.getResource("structures/default.structure");
				 OutputStream os = new FileOutputStream(defaultStructureFile)) {
				ByteStreams.copy(is, os);
			} catch (IOException ignored) { }
		}

		// Create/update other config files
		for (ConfigFile targetConfigFile : ConfigFile.values()) {
			File configFile = targetConfigFile.getResourcePath(this.skyblock);

			if (configFile.exists()) {
				if (targetConfigFile.shouldUpdateFile()) {
					FileChecker fileChecker = new FileChecker(this.skyblock, this, targetConfigFile);
					fileChecker.loadSections();
					fileChecker.compareFiles();
					fileChecker.saveChanges();
				}
			} else {
				try {
					configFile.createNewFile();
					try (InputStream is = skyblock.getResource(targetConfigFile.getFileName());
						 OutputStream os = new FileOutputStream(configFile)) {
						ByteStreams.copy(is, os);
					}

					if (targetConfigFile == ConfigFile.WORLDS) {
						File mainConfigFile = new File(skyblock.getDataFolder(), "config.yml");

						if (mainConfigFile.exists()) {
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
					Bukkit.getServer().getLogger().log(Level.WARNING, "SkyBlock | Error: Unable to create configuration file: " + targetConfigFile.getFileName());
				}
			}

			Config config = new Config(this, configFile);
			config.loadFile();
			this.loadedConfigs.put(targetConfigFile, config);
		}
	}

	public void setLocation(Config config, String path, Location location, boolean direction) {
		File configFile = config.getFile();
		FileConfiguration configLoad = config.getFileConfiguration();

		configLoad.set(path + ".world", location.getWorld().getName());
		configLoad.set(path + ".x", Double.valueOf(location.getX()));
		configLoad.set(path + ".y", Double.valueOf(location.getY()));
		configLoad.set(path + ".z", Double.valueOf(location.getZ()));

		if (direction) {
			configLoad.set(path + ".yaw", Float.valueOf(location.getYaw()));
			configLoad.set(path + ".pitch", Float.valueOf(location.getPitch()));
		}

		try {
			configLoad.save(configFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public Location getLocation(Config config, String path, boolean direction) {
		Location location = null;

		FileConfiguration configLoad = config.getFileConfiguration();

		if (configLoad.contains(path)) {
			String world = configLoad.getString(path + ".world");

			double x = configLoad.getDouble(path + ".x");
			double y = configLoad.getDouble(path + ".y");
			double z = configLoad.getDouble(path + ".z");
			double yaw = 0.0D;
			double pitch = 0.0D;

			if (configLoad.contains(path + ".yaw")) {
				yaw = configLoad.getDouble(path + ".yaw");
				pitch = configLoad.getDouble(path + ".pitch");
			}

			location = new org.bukkit.Location(Bukkit.getWorld(world), x, y, z);

			if (direction) {
				location.setYaw((float) yaw);
				location.setPitch((float) pitch);
			}
		}

		return location;
	}

	public boolean doesDataFileExist(DataFolder dataFolder, UUID uuid) {
		return dataFolder.getFileInFolder(this.skyblock, uuid).exists();
	}

	public void deleteDataFile(DataFolder dataFolder, UUID uuid) {
		this.unloadDataFile(dataFolder, uuid);
		dataFolder.getFileInFolder(this.skyblock, uuid).delete();
	}

	public void unloadDataFile(DataFolder dataFolder, UUID uuid) {
		Map<String, Config> dataFiles = this.loadedDataFiles.get(dataFolder);
		if (dataFiles == null)
			return;
		dataFiles.remove(uuid.toString() + ".yml");
	}

	public void renameDataFile(DataFolder dataFolder, UUID from, UUID to) {
		File file1 = dataFolder.getFileInFolder(skyblock, from);
		File file2 = dataFolder.getFileInFolder(skyblock, to);
		file1.renameTo(file2);
	}

	public Config getDataFile(DataFolder dataFolder, String fileName) {
		Map<String, Config> dataFiles = this.loadedDataFiles.get(dataFolder);
		if (dataFiles == null) {
			File folder = dataFolder.getResourcePath(this.skyblock);
			if (!folder.exists())
				folder.mkdir();
			dataFiles = new HashMap<>();
			this.loadedDataFiles.put(dataFolder, dataFiles);
		}

		Config config = dataFiles.get(fileName);
		if (config != null)
			return config;

		config = new Config(this, dataFolder.getFileInFolder(this.skyblock, fileName));
		config.loadFile();
		dataFiles.put(fileName, config);
		return config;
	}

	public FileConfiguration getDataFileConfiguration(DataFolder dataFolder, String fileName) {
		return this.getDataFile(dataFolder, fileName).getFileConfiguration();
	}

	public Config getConfig(ConfigFile configFile) {
		return loadedConfigs.get(configFile);
	}

	public FileConfiguration getFileConfiguration(ConfigFile configFile) {
		return this.getConfig(configFile).getFileConfiguration();
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

	public InputStream getConfigContent(Reader reader) {
		try {
			String addLine, currentLine, pluginName = skyblock.getDescription().getName();
			int commentNum = 0;

			StringBuilder whole = new StringBuilder("");
			BufferedReader bufferedReader = new BufferedReader(reader);

			while ((currentLine = bufferedReader.readLine()) != null) {
				if (currentLine.contains("#")) {
					addLine = currentLine.replace("[!]", "IMPORTANT").replace(":", "-").replaceFirst("#",
							pluginName + "_COMMENT_" + commentNum + ":");
					whole.append(addLine + "\n");
					commentNum++;
				} else {
					whole.append(currentLine + "\n");
				}
			}

			String config = whole.toString();
			InputStream configStream = new ByteArrayInputStream(config.getBytes(Charset.forName("UTF-8")));
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
		StringBuilder config = new StringBuilder("");

		for (String line : lines) {
			if (line.contains(skyblock.getDescription().getName() + "_COMMENT")) {
				config.append(line.replace("IMPORTANT", "[!]").replace("\n", "")
						.replace(skyblock.getDescription().getName() + "_COMMENT_", "#").replaceAll("[0-9]+:", "")
						+ "\n");
			} else if (line.contains(":")) {
				config.append(line + "\n");
			}
		}

		return config.toString();
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
			if (!configFile.getName().toLowerCase().endsWith(".yml"))
				return null;

			configLoad = YamlConfiguration.loadConfiguration(configFile);

			return configLoad;
		}

		public void save() {
			try {
				this.configLoad.save(this.configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
