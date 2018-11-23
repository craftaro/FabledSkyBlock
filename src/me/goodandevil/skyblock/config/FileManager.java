package me.goodandevil.skyblock.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.google.common.io.ByteStreams;

import me.goodandevil.skyblock.Main;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileManager {

	private final Main plugin;
	private Map<String, Config> loadedConfigs = new HashMap<>();
	
	public FileManager(Main plugin) {
		this.plugin = plugin;
		
		loadConfigs();
	}
	
	public void loadConfigs() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		
		if (!new File(plugin.getDataFolder().toString() + "/structures").exists()) {
			new File(plugin.getDataFolder().toString() + "/structures").mkdir();
		}
		
		Map<String, File> configFiles = new HashMap<>();
		configFiles.put("levelling.yml", new File(plugin.getDataFolder(), "levelling.yml"));
		configFiles.put("config.yml", new File(plugin.getDataFolder(), "config.yml"));
		configFiles.put("language.yml", new File(plugin.getDataFolder(), "language.yml"));
		configFiles.put("settings.yml", new File(plugin.getDataFolder(), "settings.yml"));
		configFiles.put("generators.yml", new File(plugin.getDataFolder(), "generators.yml"));
		configFiles.put("structures.yml", new File(plugin.getDataFolder(), "structures.yml"));
		configFiles.put("structures/default.structure", new File(plugin.getDataFolder().toString() + "/structures", "default.structure"));
		
		for (String configFileList : configFiles.keySet()) {
			File configFile = configFiles.get(configFileList);
	        
	        if (configFile.exists()) {
	        	if (configFileList.equals("config.yml") || configFileList.equals("language.yml") || configFileList.equals("settings.yml")) {
					FileChecker fileChecker = new FileChecker(plugin, configFileList);
					fileChecker.loadSections();
					fileChecker.compareFiles();
					fileChecker.saveChanges();
	        	}
	        } else {
	            try {
	                configFile.createNewFile();
	                try (InputStream is = plugin.getResource(configFileList);
	                OutputStream os = new FileOutputStream(configFile)) {
	                    ByteStreams.copy(is, os);
	                }
	            } catch (IOException ex) {
	            	Bukkit.getServer().getLogger().log(Level.WARNING, "SkyBlock | Error: Unable to create configuration file.");
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
		
		plugin.getConfig();
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
		
		Config config = new Config(configPath);
		loadedConfigs.put(configPath.getPath(), config);
		
		return config;
	}
	
	public Map<String, Config> getConfigs() {
		return loadedConfigs;
	}
	
	public boolean isConfigLoaded(java.io.File configPath) {
		return loadedConfigs.containsKey(configPath.getPath());
	}
	
	public static class Config {
		
		private File configFile;
		private FileConfiguration configLoad;
		
		public Config(java.io.File configPath) {
			configFile = configPath;
			configLoad = YamlConfiguration.loadConfiguration(configFile);
		}
		
		public File getFile() {
			return configFile;
		}
		
		public FileConfiguration getFileConfiguration() {
			return configLoad;
		}
		
		public void loadFile() {
			configLoad = YamlConfiguration.loadConfiguration(configFile);
		}
	}
}
