package com.craftaro.skyblock.levelling.calculator.impl;

import com.craftaro.skyblock.levelling.calculator.SpawnerCalculator;
import com.craftaro.ultimatestacker.api.UltimateStackerApi;
import com.craftaro.ultimatestacker.api.stack.spawner.SpawnerStack;
import org.bukkit.Bukkit;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.plugin.Plugin;

public class UltimateStackerCalculator implements SpawnerCalculator {
    @Override
    public long getSpawnerAmount(CreatureSpawner spawner) {
        if (!getUltimateStackerPlugin().getConfig().getBoolean("Spawners.Enabled")) {
            return 0;
        }

        final SpawnerStack stack = UltimateStackerApi.getSpawnerStackManager().getSpawner(spawner.getLocation());
        return stack == null ? 0 : stack.getAmount();
    }

    private Plugin getUltimateStackerPlugin() {
        return Bukkit.getPluginManager().getPlugin("UltimateStacker");
    }
}
