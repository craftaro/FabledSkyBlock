package me.goodandevil.skyblock.structure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.utils.version.Materials;

public class StructureManager {

	private List<Structure> structureStorage = new ArrayList<>();

	public StructureManager(SkyBlock skyblock) {
		Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "structures.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();

		if (configLoad.getString("Structures") != null) {
			for (String structureList : configLoad.getConfigurationSection("Structures").getKeys(false)) {
				Materials materials = null;

				if (configLoad.getString("Structures." + structureList + ".Item.Material") == null) {
					materials = Materials.GRASS_BLOCK;
				} else {
					materials = Materials
							.fromString(configLoad.getString("Structures." + structureList + ".Item.Material"));

					if (materials == null) {
						materials = Materials.GRASS_BLOCK;
					}
				}

				structureStorage.add(new Structure(configLoad.getString("Structures." + structureList + ".Name"),
						materials, configLoad.getString("Structures." + structureList + ".File"),
						configLoad.getString("Structures." + structureList + ".Displayname"),
						configLoad.getBoolean("Structures." + structureList + ".Permission"),
						configLoad.getStringList("Structures." + structureList + ".Description")));
			}
		}
	}

	public void addStructure(String name, Materials materials, String fileName, String displayName, boolean permission,
			List<String> description) {
		structureStorage.add(new Structure(name, materials, fileName, displayName, permission, description));
	}

	public void removeStructure(Structure structure) {
		structureStorage.remove(structure);
	}

	public Structure getStructure(String name) {
		for (Structure structureList : structureStorage) {
			if (structureList.getName().equalsIgnoreCase(name)) {
				return structureList;
			}
		}

		return null;
	}

	public boolean containsStructure(String name) {
		for (Structure structureList : structureStorage) {
			if (structureList.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}

		return false;
	}

	public List<Structure> getStructures() {
		return structureStorage;
	}
}
