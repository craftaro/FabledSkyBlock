package me.goodandevil.skyblock.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.goodandevil.skyblock.SkyBlock;

public class FileChecker {
	
	private final SkyBlock skyblock;
	private final FileManager fileManager;
	
	private Map<File.Type, File> loadedFiles;
	
	public FileChecker(SkyBlock skyblock, FileManager fileManager, String configurationFileName) {
		this.skyblock = skyblock;
		this.fileManager = fileManager;
		
		loadedFiles = new EnumMap<>(File.Type.class);
		
		java.io.File configFile = new java.io.File(skyblock.getDataFolder(), configurationFileName);
		loadedFiles.put(File.Type.CREATED, new File(fileManager, configFile, YamlConfiguration.loadConfiguration(configFile)));
		loadedFiles.put(File.Type.RESOURCE, new File(null, null, YamlConfiguration.loadConfiguration(new InputStreamReader(skyblock.getResource(configurationFileName)))));
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
				
				for (String configKeyList : file.getKeys().keySet()) {
					if (!configKeyList.contains(skyblock.getDescription().getName() + "_COMMENT")) {
						if (!resourceFile.getKeys().containsKey(configKeyList)) {
							configLoad.set(configKeyList, null);
						}	
					}
				}
			} else if (fileType == File.Type.RESOURCE) {
				File createdFile = loadedFiles.get(File.Type.CREATED);
				FileConfiguration createdConfigLoad = createdFile.getFileConfiguration();
				
				for (String configKeyList : file.getKeys().keySet()) {
					if (!configKeyList.contains(skyblock.getDescription().getName() + "_COMMENT")) {
						if (createdConfigLoad.getString(configKeyList) == null) {
							createdConfigLoad.set(configKeyList, file.getKeys().get(configKeyList));
						}	
					}
				}
			}
		}
	}
	
	public void saveChanges() {
		File file = loadedFiles.get(File.Type.CREATED);
		
		try {
			if (file.getFile().getName().equals("config.yml")) {
				fileManager.saveConfig(file.getFileConfiguration().saveToString(), file.getFile());
			} else {
				file.getFileConfiguration().save(file.getFile());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class File {
		
		private java.io.File configFile;
		private FileConfiguration configLoad;
		
		private HashMap<String, Object> configKeys;
		
		public File(FileManager fileManager, java.io.File configFile, FileConfiguration configLoad) {
			this.configFile = configFile;
			this.configLoad = configLoad;
			configKeys = new HashMap<>();
			
			if (configFile != null && configFile.getName().equals("config.yml")) {
				this.configLoad = YamlConfiguration.loadConfiguration(new InputStreamReader(fileManager.getConfigContent(configFile)));
			}
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
