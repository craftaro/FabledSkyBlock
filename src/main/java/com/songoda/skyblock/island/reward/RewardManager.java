package com.songoda.skyblock.island.reward;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardManager {
    private final SkyBlock skyBlock;

    private final Map<Long, LevelReward> registeredRewards = new HashMap<>();

    private final Map<Long, LevelReward> repeatRewards = new HashMap<>();

    public RewardManager(SkyBlock skyBlock) {
        this.skyBlock = skyBlock;
    }

    public void loadRewards() {
        final FileConfiguration configLoad = this.skyBlock.getRewards();

        this.registeredRewards.clear();
        this.repeatRewards.clear();

        ConfigurationSection onceSection = configLoad.getConfigurationSection("Once");

        if (onceSection != null) {
            for (String key : onceSection.getKeys(false)) {
                long level;
                try {
                    level = Long.parseLong(key);
                } catch (NumberFormatException e) {
                    continue;
                }

                this.registeredRewards.put(level, loadReward("Once." + key));
            }
        }

        ConfigurationSection repeatSection = configLoad.getConfigurationSection("Repeat");

        if (repeatSection != null) {
            for (String key : repeatSection.getKeys(false)) {
                long level;
                try {
                    level = Long.parseLong(key);
                } catch (NumberFormatException e) {
                    continue;
                }

                this.repeatRewards.put(level, loadReward("Repeat." + key));
            }
        }
    }

    private LevelReward loadReward(String path) {
        final FileConfiguration config = this.skyBlock.getRewards();

        ConfigurationSection section = config.getConfigurationSection(path);

        LevelReward levelReward = new LevelReward();

        if (section == null) {
            return levelReward;
        }

        double money = section.getDouble("money", 0);
        levelReward.setMoney(money);

        double islandVault = section.getDouble("island-balance", 0);
        levelReward.setIslandBalance(islandVault);

        List<String> commands = section.contains("commands") ? section.getStringList("commands") : new ArrayList<>();
        levelReward.setCommands(commands);

        return levelReward;
    }

    public LevelReward getReward(long level) {
        return this.registeredRewards.getOrDefault(level, null);
    }

    public List<LevelReward> getRepeatRewards(long level) {
        List<LevelReward> levelRewards = new ArrayList<>();

        for (long loopLevel : this.repeatRewards.keySet()) {
            if (level % loopLevel == 0) {
                levelRewards.add(this.repeatRewards.get(loopLevel));
            }
        }

        return levelRewards;
    }

    public Map<Long, LevelReward> getRegisteredRewards() {
        return Collections.unmodifiableMap(this.registeredRewards);
    }
}
