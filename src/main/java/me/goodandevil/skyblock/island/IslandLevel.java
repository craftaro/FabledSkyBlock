package me.goodandevil.skyblock.island;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;

public class IslandLevel {

	private final SkyBlock skyblock;

	private UUID ownerUUID;

	private int lastCalculatedLevel = 0;
	private int lastCalculatedPoints = 0;

	private Map<String, Integer> materials;

	public IslandLevel(UUID ownerUUID, SkyBlock skyblock) {
		this.skyblock = skyblock;
		this.ownerUUID = ownerUUID;

		Config config = skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml"));
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

		for (String materialList : this.materials.keySet()) {
			int materialAmount = this.materials.get(materialList);

			if (configLoad.getString("Materials." + materialList + ".Points") != null) {
				int pointsRequired = config.getFileConfiguration().getInt("Materials." + materialList + ".Points");

				if (pointsRequired != 0) {
					pointsEarned = pointsEarned + (materialAmount * pointsRequired);
				}
			}
		}

		return pointsEarned;
	}

	public int getMaterialPoints(String material) {
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "levelling.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		int pointsEarned = 0;

		if (this.materials.containsKey(material)) {
			int materialAmount = this.materials.get(material);

			if (configLoad.getString("Materials." + materials + ".Points") != null) {
				int pointsRequired = config.getFileConfiguration().getInt("Materials." + materials + ".Points");

				if (pointsRequired != 0) {
					pointsEarned = materialAmount * pointsRequired;
				}
			}
		}

		return pointsEarned;
	}

	public int getLevel() {
		int division = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
				.getFileConfiguration().getInt("Island.Levelling.Division");

		if (division == 0) {
			division = 1;
		}

		return getPoints() / division;
	}

	public void setMaterialAmount(String material, int amount) {
		skyblock.getFileManager()
				.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/level-data"),
						ownerUUID.toString() + ".yml"))
				.getFileConfiguration().set("Levelling.Materials." + material + ".Amount", amount);

		this.materials.put(material, amount);
	}

	public int getMaterialAmount(String material) {
		if (this.materials.containsKey(material)) {
			return this.materials.get(material);
		}

		return 0;
	}

	public void removeMaterial(String material) {
		skyblock.getFileManager()
				.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/level-data"),
						ownerUUID.toString() + ".yml"))
				.getFileConfiguration().set("Levelling.Materials." + material, null);

		this.materials.remove(material);
	}

	public void setMaterials(Map<String, Integer> materials) {
		Config config = skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		configLoad.set("Levelling.Materials", null);

		for (String materialList : materials.keySet()) {
			configLoad.set("Levelling.Materials." + materialList + ".Amount", materials.get(materialList));
		}

		this.materials = materials;
	}

	public boolean hasMaterial(String material) {
		return this.materials.containsKey(material);
	}

	public boolean hasMaterials() {
		if (this.materials.size() == 0) {
			return false;
		}

		return true;
	}

	public Map<String, Integer> getMaterials() {
		return this.materials;
	}

	public void setLastCalculatedPoints(int lastCalculatedPoints) {
		this.lastCalculatedPoints = lastCalculatedPoints;
	}

	public int getLastCalculatedPoints() {
		return this.lastCalculatedPoints;
	}

	public void setLastCalculatedLevel(int lastCalculatedLevel) {
		this.lastCalculatedLevel = lastCalculatedLevel;
	}

	public int getLastCalculatedLevel() {
		return this.lastCalculatedLevel;
	}

	public void save() {
		Config config = skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml"));
		File configFile = config.getFile();
		FileConfiguration configLoad = config.getFileConfiguration();

		try {
			configLoad.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
