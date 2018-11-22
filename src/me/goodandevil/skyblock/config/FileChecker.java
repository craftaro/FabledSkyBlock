package me.goodandevil.skyblock.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.goodandevil.skyblock.Main;

public class FileChecker {
	
	private Map<File.Type, File> loadedFiles;
	
	public FileChecker(Main plugin, String configurationFileName) {
		loadedFiles = new EnumMap<>(File.Type.class);
		
		java.io.File configFile = new java.io.File(plugin.getDataFolder(), configurationFileName);
		loadedFiles.put(File.Type.CREATED, new File(configFile, YamlConfiguration.loadConfiguration(configFile)));
		loadedFiles.put(File.Type.RESOURCE, new File(null, YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(configurationFileName)))));
	}
	
	public void loadSections() {
		for (File.Type fileType : File.Type.values()) {
			File file = loadedFiles.get(fileType);
			FileConfiguration configLoad = file.getFileConfiguration();
			
			Set<String> configKeys = configLoad.getKeys(true);
			
			for (String configKeysList : configKeys) {
				file.addKey(configKeysList, configLoad.get(configKeysList));
			}
		}
	}
	
	public void compareFiles() {
		for (File.Type fileType : File.Type.values()) {
			File file = loadedFiles.get(fileType);
			FileConfiguration configLoad = file.getFileConfiguration();
			
			if (fileType == File.Type.CREATED) {
				File resourceFile = loadedFiles.get(File.Type.RESOURCE);
				
				for (String configKeysList : file.getKeys().keySet()) {
					if (!resourceFile.getKeys().containsKey(configKeysList)) {
						configLoad.set(configKeysList, null);
					}
				}
			} else if (fileType == File.Type.RESOURCE) {
				File createdFile = loadedFiles.get(File.Type.CREATED);
				FileConfiguration createdConfigLoad = createdFile.getFileConfiguration();
				
				for (String configKeysList : file.getKeys().keySet()) {
					if (createdConfigLoad.getString(configKeysList) == null) {
						createdConfigLoad.set(configKeysList, file.getKeys().get(configKeysList));
					}
				}
			}
		}
	}
	
	public void saveChanges() {
		File file = loadedFiles.get(File.Type.CREATED);
		
		try {
			file.getFileConfiguration().save(file.getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class File {
		
		private java.io.File configFile;
		private FileConfiguration configLoad;
		
		private HashMap<String, Object> configKeys;
		
		public File(java.io.File configFile, FileConfiguration configLoad) {
			this.configFile = configFile;
			this.configLoad = configLoad;
			configKeys = new HashMap<>();
		}
		
		public java.io.File getFile() {
			return configFile;
		}
		
		public FileConfiguration getFileConfiguration() {
			return configLoad;
		}
		
		public HashMap<String, Object> getKeys() {
			return configKeys;
		}
		
		public void addKey(String key, Object object) {
			configKeys.put(key, object);
		}
		
		public enum Type {
			CREATED,
			RESOURCE;
		}
	}
}
