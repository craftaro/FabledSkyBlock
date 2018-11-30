package me.goodandevil.skyblock.island;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;

public class Level {
	
	private final SkyBlock skyblock;
	
	private UUID ownerUUID;
	
	private int lastLevel = 0;
	private int lastPoints = 0;
	
	private Map<String, Integer> materials;
	
	public Level(UUID ownerUUID, SkyBlock skyblock) {
		this.skyblock = skyblock;
		this.ownerUUID = ownerUUID;
		
		Config config = skyblock.getFileManager().getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		Map<String, Integer> materials = new HashMap<>();
		
		if (configLoad.getString("Levelling.Materials") != null) {
			for (String materialList : configLoad.getConfigurationSection("Levelling.Materials").getKeys(false)) {
				if (configLoad.getString("Levelling.Materials." + materialList + ".Amount") != null) {
					materials.put(materialList, configLoad.getInt("Levelling.Materials." + materialList + ".Amount"));					
				}
			}
		}
		
		this.materials = materials;
	}
	
	public void setOwnerUUID(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
	}
	
	public int getPoints() {
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "levelling.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		int pointsEarned = 0;
		
		for (String materialList : materials.keySet()) {
			int materialAmount = materials.get(materialList);
			
			if (configLoad.getString("Materials." + materialList + ".Points") != null) {
				int pointsRequired = config.getFileConfiguration().getInt("Materials." + materialList + ".Points");
				
				if (pointsRequired != 0) {
					pointsEarned = pointsEarned + (materialAmount*pointsRequired);
				}	
			}
		}
		
		return pointsEarned;
	}
	
	public int getLevel() {
		int division = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getInt("Island.Levelling.Division");
		
		if (division == 0) {
			division = 1;
		}
		
		return getPoints() / division;
	}
	
	public void setMaterials(Map<String, Integer> materials) {
		Config config = skyblock.getFileManager().getConfig(new File(new File(skyblock.getDataFolder().toString() + "/island-data"), ownerUUID.toString() + ".yml"));
		File configFile = config.getFile();
		FileConfiguration configLoad = config.getFileConfiguration();
		
		configLoad.set("Levelling.Materials", null);
		
		for (String materialList : materials.keySet()) {
			configLoad.set("Levelling.Materials." + materialList + ".Amount", materials.get(materialList));
		}
		
		try {
			configLoad.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.materials = materials;
	}
	
	public Map<String, Integer> getMaterials() {
		return materials;
	}
	
	public void setLastPoints(int lastPoints) {
		this.lastPoints = lastPoints;
	}
	
	public int getLastPoints() {
		return lastPoints;
	}
	
	public void setLastLevel(int lastLevel) {
		this.lastLevel = lastLevel;
	}
	
	public int getLastLevel() {
		return lastLevel;
	}
}
