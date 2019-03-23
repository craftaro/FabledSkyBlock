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

	private long lastCalculatedLevel = 0;
	private long lastCalculatedPoints = 0;

	private Map<String, Long> materials;

	public IslandLevel(UUID ownerUUID, SkyBlock skyblock) {
		this.skyblock = skyblock;
		this.ownerUUID = ownerUUID;

		Config config = skyblock.getFileManager().getConfig(
				new File(new File(skyblock.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		Map<String, Long> materials = new HashMap<>();

		if (configLoad.getString("Levelling.Materials") != null) {
			for (String materialList : configLoad.getConfigurationSection("Levelling.Materials").getKeys(false)) {
				if (configLoad.getString("Levelling.Materials." + materialList + ".Amount") != null) {
					materials.put(materialList, configLoad.getLong("Levelling.Materials." + materialList + ".Amount"));
				}
			}
		}

		this.materials = materials;
	}

	public void setOwnerUUID(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
	}

	public long getPoints() {
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "levelling.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		long pointsEarned = 0;

		for (String materialList : this.materials.keySet()) {
			long materialAmount = this.materials.get(materialList);

			if (configLoad.getString("Materials." + materialList + ".Points") != null) {
				long pointsRequired = config.getFileConfiguration().getLong("Materials." + materialList + ".Points");

				if (pointsRequired != 0) {
					pointsEarned = pointsEarned + (materialAmount * pointsRequired);
				}
			}
		}

		return pointsEarned;
	}

	public long getMaterialPoints(String material) {
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "levelling.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		long pointsEarned = 0;

		if (this.materials.containsKey(material)) {
			long materialAmount = this.materials.get(material);

			if (configLoad.getString("Materials." + materials + ".Points") != null) {
				long pointsRequired = config.getFileConfiguration().getLong("Materials." + materials + ".Points");

				if (pointsRequired != 0) {
					pointsEarned = materialAmount * pointsRequired;
				}
			}
		}

		return pointsEarned;
	}

	public long getLevel() {
		long division = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"))
				.getFileConfiguration().getLong("Island.Levelling.Division");

		if (division == 0) {
			division = 1;
		}

		return getPoints() / division;
	}

	public void setMaterialAmount(String material, long amount) {
		skyblock.getFileManager()
				.getConfig(new File(new File(skyblock.getDataFolder().toString() + "/level-data"),
						ownerUUID.toString() + ".yml"))
				.getFileConfiguration().set("Levelling.Materials." + material + ".Amount", amount);

		this.materials.put(material, amount);
	}

	public long getMaterialAmount(String material) {
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

	public void setMaterials(Map<String, Long> materials) {
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

	public Map<String, Long> getMaterials() {
		return this.materials;
	}

	public void setLastCalculatedPoints(long lastCalculatedPoints) {
		this.lastCalculatedPoints = lastCalculatedPoints;
	}

	public long getLastCalculatedPoints() {
		return this.lastCalculatedPoints;
	}

	public void setLastCalculatedLevel(long lastCalculatedLevel) {
		this.lastCalculatedLevel = lastCalculatedLevel;
	}

	public long getLastCalculatedLevel() {
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
