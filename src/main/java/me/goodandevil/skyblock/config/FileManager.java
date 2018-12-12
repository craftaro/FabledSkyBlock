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
import java.util.logging.Level;

import com.google.common.io.ByteStreams;

import me.goodandevil.skyblock.SkyBlock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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

		if (!new File(skyblock.getDataFolder().toString() + "/structures").exists()) {
			new File(skyblock.getDataFolder().toString() + "/structures").mkdir();
		}

		Map<String, File> configFiles = new HashMap<>();
		configFiles.put("levelling.yml", new File(skyblock.getDataFolder(), "levelling.yml"));
		configFiles.put("config.yml", new File(skyblock.getDataFolder(), "config.yml"));
		configFiles.put("language.yml", new File(skyblock.getDataFolder(), "language.yml"));
		configFiles.put("settings.yml", new File(skyblock.getDataFolder(), "settings.yml"));
		configFiles.put("upgrades.yml", new File(skyblock.getDataFolder(), "upgrades.yml"));
		configFiles.put("generators.yml", new File(skyblock.getDataFolder(), "generators.yml"));
		configFiles.put("structures.yml", new File(skyblock.getDataFolder(), "structures.yml"));
		configFiles.put("structures/default.structure",
				new File(skyblock.getDataFolder().toString() + "/structures", "default.structure"));

		for (String configFileList : configFiles.keySet()) {
			File configFile = configFiles.get(configFileList);

			if (configFile.exists()) {
				if (configFileList.equals("config.yml") || configFileList.equals("language.yml")
						|| configFileList.equals("settings.yml")) {
					FileChecker fileChecker;

					if (configFileList.equals("config.yml")) {
						fileChecker = new FileChecker(skyblock, this, configFileList, true);
					} else {
						fileChecker = new FileChecker(skyblock, this, configFileList, false);
					}

					fileChecker.loadSections();
					fileChecker.compareFiles();
					fileChecker.saveChanges();
				}
			} else {
				try {
					configFile.createNewFile();
					try (InputStream is = skyblock.getResource(configFileList);
							OutputStream os = new FileOutputStream(configFile)) {
						ByteStreams.copy(is, os);
					}
				} catch (IOException ex) {
					Bukkit.getServer().getLogger().log(Level.WARNING,
							"SkyBlock | Error: Unable to create configuration file.");
				}
			}
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
		if (loadedConfigs.containsKey(configPath.getPath())) {
			return loadedConfigs.get(configPath.getPath());
		}

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
				configLoad = YamlConfiguration
						.loadConfiguration(new InputStreamReader(fileManager.getConfigContent(configFile)));
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
