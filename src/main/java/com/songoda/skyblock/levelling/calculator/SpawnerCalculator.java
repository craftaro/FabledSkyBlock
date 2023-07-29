package com.songoda.skyblock.levelling.calculator;

import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;

public interface SpawnerCalculator extends Calculator {
    @Override
    default long getAmount(Block block) {
        return getSpawnerAmount((CreatureSpawner) block.getState());
    }

    long getSpawnerAmount(CreatureSpawner spawner);
}
