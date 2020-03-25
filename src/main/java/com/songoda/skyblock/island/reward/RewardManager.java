package com.songoda.skyblock.island.reward;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class RewardManager {

    private final SkyBlock skyBlock;

    private final Map<Long, LevelReward> registeredRewards = new HashMap<>();

    private final Map<Long, LevelReward> repeatRewards = new HashMap<>();

    public RewardManager(SkyBlock skyBlock) {
        this.skyBlock = skyBlock;
    }

    public void loadRewards() {
        final Config config = skyBlock.getFileManager().getConfig(new File(skyBlock.getDataFolder(), "rewards.yml"));
        final FileConfiguration configLoad = config.getFileConfiguration();

        this.registeredRewards.clear();
        this.repeatRewards.clear();

        ConfigurationSection onceSection = configLoad.getConfigurationSection("Once");
        for (String key : onceSection.getKeys(false)) {
            long level;
            try {
                level = Long.parseLong(key);
            } catch (NumberFormatException e) {
                continue;
            }

            ConfigurationSection section = onceSection.getConfigurationSection(key);

            double money = section.getDouble("money", 0);

            List<String> commands = section.contains("commands") ? section.getStringList("commands") : new ArrayList<>();

            LevelReward levelReward = new LevelReward(commands, money);

            this.registeredRewards.put(level, levelReward);
        }

        ConfigurationSection repeatSection = configLoad.getConfigurationSection("Repeat");
        for (String key : repeatSection.getKeys(false)) {
            long level;
            try {
                level = Long.parseLong(key);
            } catch (NumberFormatException e) {
                continue;
            }

            ConfigurationSection section = repeatSection.getConfigurationSection(key);

            double money = section.getDouble("money", 0);

            List<String> commands = section.contains("commands") ? section.getStringList("commands") : new ArrayList<>();

            LevelReward levelReward = new LevelReward(commands, money);

            this.repeatRewards.put(level, levelReward);
        }
    }

    public LevelReward getReward(long level) {
        return this.registeredRewards.getOrDefault(level, null);
    }

    public LevelReward getRepeatReward(long level) {
        for (long loopLevel : this.repeatRewards.keySet()) {
            if (level % loopLevel == 0) {
                return this.repeatRewards.get(loopLevel);
            }
        }
        return null;
    }

    public Map<Long, LevelReward> getRegisteredRewards() {
        return Collections.unmodifiableMap(this.registeredRewards);
    }
}