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

    public RewardManager(SkyBlock skyBlock) {
        this.skyBlock = skyBlock;
    }

    public void loadRewards() {
        final Config config = skyBlock.getFileManager().getConfig(new File(skyBlock.getDataFolder(), "rewards.yml"));
        final FileConfiguration configLoad = config.getFileConfiguration();

        this.registeredRewards.clear();

        for (String key : configLoad.getKeys(false)) {
            long level;
            try {
                level = Long.parseLong(key);
            } catch (NumberFormatException e) {
                continue;
            }

            ConfigurationSection section = configLoad.getConfigurationSection(key);

            double money = section.getDouble("money", 0);

            List<String> commands = section.contains("commands") ? section.getStringList("commands") : new ArrayList<>();

            LevelReward levelReward = new LevelReward(commands, money);

            this.registeredRewards.put(level, levelReward);
        }
    }

    public LevelReward getReward(long level) {
        return this.registeredRewards.getOrDefault(level, null);
    }

    public Map<Long, LevelReward> getRegisteredRewards() {
        return Collections.unmodifiableMap(this.registeredRewards);
    }
}
