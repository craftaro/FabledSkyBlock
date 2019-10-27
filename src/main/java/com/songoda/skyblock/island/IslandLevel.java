package com.songoda.skyblock.island;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class IslandLevel {

    private final SkyBlock skyblock;

    private UUID ownerUUID;

    private long lastCalculatedLevel = 0;
    private long lastCalculatedPoints = 0;

    private Map<String, Long> materials;

    public IslandLevel(UUID ownerUUID, SkyBlock skyblock) {
        this.skyblock = skyblock;
        this.ownerUUID = ownerUUID;

        final Config config = skyblock.getFileManager().getConfig(new File(new File(skyblock.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml"));
        final FileConfiguration configLoad = config.getFileConfiguration();

        final ConfigurationSection section = configLoad.getConfigurationSection("Levelling.Materials");
        final Map<String, Long> materials;

        if (section != null) {
            final Set<String> keys = section.getKeys(false);
            materials = new HashMap<>(keys.size());

            for (String material : keys) {

                ConfigurationSection current = section.getConfigurationSection(material);

                if (current.isSet("Amount")) materials.put(material, current.getLong("Amount"));

            }

        } else {
            materials = new HashMap<>();
        }

        this.materials = materials;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public long getPoints() {
        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "levelling.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        ConfigurationSection materialSection = configLoad.getConfigurationSection("Materials");

        if (materialSection == null) return 0;

        long pointsEarned = 0;

        for (Entry<String, Long> entry : this.materials.entrySet()) {
            ConfigurationSection current = materialSection.getConfigurationSection(entry.getKey());

            if (current == null) continue;

            long pointsRequired = current.getLong("Points", 0);

            if (pointsRequired != 0) pointsEarned = pointsEarned + (entry.getValue() * pointsRequired);

        }

        return pointsEarned;
    }

    public long getMaterialPoints(String material) {
        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "levelling.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        ConfigurationSection materialSection = configLoad.getConfigurationSection("Materials");

        if (materialSection == null) return 0;

        ConfigurationSection current = materialSection.getConfigurationSection(material);

        if (current == null) return 0;

        Long boxedAmount = this.materials.get(material);

        if (boxedAmount == null) return 0;

        long pointsRequired = current.getLong("Points");

        return pointsRequired == 0 ? 0 : boxedAmount * pointsRequired;
    }

    public long getLevel() {
        long division = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration().getLong("Island.Levelling.Division");

        if (division == 0) {
            division = 1;
        }

        return getPoints() / division;
    }

    public void setMaterialAmount(String material, long amount) {
        skyblock.getFileManager().getConfig(new File(new File(skyblock.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml")).getFileConfiguration()
                .set("Levelling.Materials." + material + ".Amount", amount);

        this.materials.put(material, amount);
    }

    public long getMaterialAmount(String material) {
        return this.materials.getOrDefault(material, 0l);
    }

    public void removeMaterial(String material) {
        skyblock.getFileManager().getConfig(new File(new File(skyblock.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml")).getFileConfiguration()
                .set("Levelling.Materials." + material, null);

        this.materials.remove(material);
    }

    public boolean hasMaterial(String material) {
        return this.materials.containsKey(material);
    }

    public boolean hasMaterials() {
        return this.materials.size() != 0;
    }

    public Map<String, Long> getMaterials() {
        return this.materials;
    }

    public void setMaterials(Map<String, Long> materials) {
        Config config = skyblock.getFileManager().getConfig(new File(new File(skyblock.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        configLoad.set("Levelling.Materials", null);

        for (String materialList : materials.keySet()) {
            configLoad.set("Levelling.Materials." + materialList + ".Amount", materials.get(materialList));
        }

        this.materials = materials;
    }

    public long getLastCalculatedPoints() {
        return this.lastCalculatedPoints;
    }

    public void setLastCalculatedPoints(long lastCalculatedPoints) {
        this.lastCalculatedPoints = lastCalculatedPoints;
    }

    public long getLastCalculatedLevel() {
        return this.lastCalculatedLevel;
    }

    public void setLastCalculatedLevel(long lastCalculatedLevel) {
        this.lastCalculatedLevel = lastCalculatedLevel;
    }

    public void save() {
        Config config = skyblock.getFileManager().getConfig(new File(new File(skyblock.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml"));
        File configFile = config.getFile();
        FileConfiguration configLoad = config.getFileConfiguration();

        try {
            configLoad.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
