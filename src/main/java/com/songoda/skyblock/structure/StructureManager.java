package com.songoda.skyblock.structure;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StructureManager {

    private final List<Structure> structureStorage = new ArrayList<>();

    public StructureManager(SkyBlock plugin) {
        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "structures.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        if (configLoad.getString("Structures") != null) {
            for (String structureList : configLoad.getConfigurationSection("Structures").getKeys(false)) {
                CompatibleMaterial materials;

                if (configLoad.getString("Structures." + structureList + ".Item.Material") == null) {
                    materials = CompatibleMaterial.GRASS_BLOCK;
                } else {
                    materials = CompatibleMaterial.getMaterial(configLoad.getString("Structures." + structureList + ".Item.Material"));
                    if (materials == null) {
                        materials = CompatibleMaterial.GRASS_BLOCK;
                    }
                }

                String structureFile, overworldFile = null, netherFile = null, endFile = null;

                if (configLoad.getString("Structures." + structureList + ".File.Overworld") == null
                        && configLoad.getString("Structures." + structureList + ".File.Nether") == null
                        && configLoad.getString("Structures." + structureList + ".File.End") == null) {
                    if (configLoad.getString("Structures." + structureList + ".File") != null) {
                        structureFile = configLoad.getString("Structures." + structureList + ".File");
                        overworldFile = structureFile;
                        netherFile = structureFile;
                        endFile = structureFile;
                    }
                } else {
                    if (configLoad.getString("Structures." + structureList + ".File.Overworld") != null) {
                        overworldFile = configLoad.getString("Structures." + structureList + ".File.Overworld");
                    }

                    if (configLoad.getString("Structures." + structureList + ".File.Nether") == null
                            && overworldFile != null) {
                        netherFile = overworldFile;
                    } else {
                        netherFile = configLoad.getString("Structures." + structureList + ".File.Nether");
                    }

                    if (configLoad.getString("Structures." + structureList + ".File.End") == null
                            && overworldFile != null) {
                        endFile = overworldFile;
                    } else {
                        endFile = configLoad.getString("Structures." + structureList + ".File.End");
                    }

                    if (overworldFile == null && netherFile != null) {
                        overworldFile = netherFile;
                    } else if (overworldFile == null && endFile != null) {
                        overworldFile = endFile;
                    }

                    if (netherFile == null && endFile != null) {
                        netherFile = endFile;
                    }

                    if (endFile == null && overworldFile != null) {
                        endFile = overworldFile;
                    }
                }

                structureStorage.add(new Structure(configLoad.getString("Structures." + structureList + ".Name"),
                        materials, overworldFile, netherFile, endFile,
                        configLoad.getString("Structures." + structureList + ".Displayname"),
                        configLoad.getBoolean("Structures." + structureList + ".Permission"),
                        configLoad.getStringList("Structures." + structureList + ".Description"),
                        configLoad.getStringList("Structures." + structureList + ".Commands"),
                        configLoad.getDouble("Structures." + structureList + ".Deletion.Cost")));
            }
        }
    }

    public void loadStructures() {

    }

    public void addStructure(String name, CompatibleMaterial materials, String overworldFile, String netherFile, String endFile,
                             String displayName, boolean permission, List<String> description, List<String> commands,
                             double deletionCost) {
        structureStorage.add(new Structure(name, materials, overworldFile, netherFile, endFile, displayName, permission,
                description, commands, deletionCost));
    }

    public void removeStructure(Structure structure) {
        structureStorage.remove(structure);
    }

    public Structure getStructure(String name) {
        return structureStorage.stream().filter(structureList -> structureList.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean containsStructure(String name) {
        return structureStorage.stream().anyMatch(structureList -> structureList.getName().equalsIgnoreCase(name));
    }

    public List<Structure> getStructures() {
        return structureStorage;
    }
}
