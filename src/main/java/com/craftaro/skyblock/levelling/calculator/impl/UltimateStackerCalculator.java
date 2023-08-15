package com.craftaro.skyblock.levelling.calculator.impl;

import com.craftaro.skyblock.levelling.calculator.SpawnerCalculator;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.stackable.spawner.SpawnerStack;
import org.bukkit.block.CreatureSpawner;

public class UltimateStackerCalculator implements SpawnerCalculator {
    @Override
    public long getSpawnerAmount(CreatureSpawner spawner) {
        if (!UltimateStacker.getInstance().getConfig().getBoolean("Spawners.Enabled")) {
            return 0;
        }

        final SpawnerStack stack = UltimateStacker.getInstance().getSpawnerStackManager().getSpawner(spawner.getLocation());
        return stack == null ? 0 : stack.getAmount();
    }
}
