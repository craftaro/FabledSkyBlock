package com.songoda.skyblock.island;

import com.google.common.base.Strings;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.reward.LevelReward;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class IslandLevel {

    private final SkyBlock plugin;

    private UUID ownerUUID;

    private long lastCalculatedLevel = 0;
    private long lastCalculatedPoints = 0;

    private Map<String, Long> materials;

    // Highest level achieved, to prevent reward farming (since is level can decrease)
    private long highestLevel;

    public IslandLevel(UUID ownerUUID, SkyBlock plugin) {
        this.plugin = plugin;
        this.ownerUUID = ownerUUID;

        final Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml"));
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

        this.highestLevel = configLoad.contains("Levelling.Highest-Level") ? configLoad.getLong("Levelling.Highest-Level") : getLevel();
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public long getPoints() {
        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "levelling.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        ConfigurationSection materialSection = configLoad.getConfigurationSection("Materials");

        if (materialSection == null) return 0;

        long pointsEarned = 0;

        for (Entry<String, Long> entry : this.materials.entrySet()) {
            ConfigurationSection current = materialSection.getConfigurationSection(entry.getKey());

            if (current == null) continue;

            long pointsRequired = current.getLong("Points", 0);
            long blockAmount = entry.getValue();

            long materialLimit = current.getLong("Limit", -1);
            long materialAmountCounted = Math.min(materialLimit, blockAmount);

            if (materialLimit == -1)
                materialAmountCounted = blockAmount;

            if (pointsRequired != 0) pointsEarned = pointsEarned + (materialAmountCounted * pointsRequired);

        }

        return pointsEarned;
    }

    public long getMaterialPoints(String material) {
        if(ServerVersion.isServerVersion(ServerVersion.V1_8)) {
            switch (material.toUpperCase()) {
                case "DIODE_BLOCK_OFF":
                case "DIODE_BLOCK_ON":
                    material = CompatibleMaterial.REPEATER.name();
                    break;
            }
        }
        
        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "levelling.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        ConfigurationSection materialSection = configLoad.getConfigurationSection("Materials");

        if (materialSection == null) return 0;

        ConfigurationSection current = materialSection.getConfigurationSection(material);

        if (current == null) return 0;

        Long boxedAmount = this.materials.get(material);
        if (boxedAmount == null) return 0;

        long materialLimit = current.getLong("Limit", -1);
        long materialAmountCounted = Math.min(materialLimit, boxedAmount);

        if (materialLimit == -1)
            materialAmountCounted = boxedAmount;

        long pointsRequired = current.getLong("Points");

        return pointsRequired == 0 ? 0 : materialAmountCounted * pointsRequired;
    }

    public long getLevel() {
        long division = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getLong("Island.Levelling.Division");

        if (division == 0) {
            division = 1;
        }

        long points = getPoints();
        long subtract = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getLong("Island.Levelling.Subtract");
        if(points >= subtract){
            points -= subtract;
        } else {
            points = 0;
        }

        return points / division;
    }

    public void checkLevelUp() {

        long level = getLevel();

        // Level didn't reach the highest
        if (level <= highestLevel)
            return;

        final FileConfiguration language = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml")).getFileConfiguration();
        final FileConfiguration config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration();

        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUUID);

        if (owner.isOnline()) {

            Player player = owner.getPlayer();

            if (config.getBoolean("Island.LevelRewards.Rewards", false)) {
                // Reward the player for each level reached, message only for the highest, so we don't spam the chat
                for (int i = (int) highestLevel + 1; i <= level; i++) {
                    LevelReward levelReward = plugin.getRewardManager().getReward(i);

                    if (levelReward != null)
                        levelReward.give(player, plugin, i);

                    List<LevelReward> repeatRewards = plugin.getRewardManager().getRepeatRewards(i);

                    if (!repeatRewards.isEmpty()) {
                        for (LevelReward reward : repeatRewards) {
                            reward.give(player, plugin, i);
                        }
                    }
                }
            }

            if (config.getBoolean("Island.LevelRewards.Messages", false)) {
                String msg = language.getString("Command.Island.Level.LevelUp.Message");

                if (!Strings.isNullOrEmpty(msg)) {
                    msg = msg.replace("%level%", String.valueOf(level));
                    plugin.getMessageManager().sendMessage(player, msg);
                }
            }
        }

        setHighestLevel(level);
    }

    public void setMaterialAmount(String material, long amount) {
        if(ServerVersion.isServerVersion(ServerVersion.V1_8)) {
            switch (material.toUpperCase()) {
                case "DIODE_BLOCK_OFF":
                case "DIODE_BLOCK_ON":
                    material = CompatibleMaterial.REPEATER.name();
                    break;
            }
        }
        
        plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml")).getFileConfiguration()
                .set("Levelling.Materials." + material + ".Amount", amount);

        this.materials.put(material, amount);
    }

    public long getMaterialAmount(String material) {
        if(ServerVersion.isServerVersion(ServerVersion.V1_8)) {
            switch (material.toUpperCase()) {
                case "DIODE_BLOCK_OFF":
                case "DIODE_BLOCK_ON":
                    material = CompatibleMaterial.REPEATER.name();
                    break;
            }
        }
        return this.materials.getOrDefault(material, 0l);
    }

    public void removeMaterial(String material) {
        if(ServerVersion.isServerVersion(ServerVersion.V1_8)) {
            switch (material.toUpperCase()) {
                case "DIODE_BLOCK_OFF":
                case "DIODE_BLOCK_ON":
                    material = CompatibleMaterial.REPEATER.name();
                    break;
            }
        }
        
        plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml")).getFileConfiguration()
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
        Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml"));
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
        Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml"));
        File configFile = config.getFile();
        FileConfiguration configLoad = config.getFileConfiguration();

        try {
            configLoad.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setHighestLevel(long highestLevel) {
        Config config = plugin.getFileManager().getConfig(new File(new File(plugin.getDataFolder().toString() + "/level-data"), ownerUUID.toString() + ".yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        configLoad.set("Levelling.Highest-Level", highestLevel);

        this.highestLevel = highestLevel;
    }
}
