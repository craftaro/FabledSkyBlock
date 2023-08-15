package com.craftaro.skyblock.levelling.calculator.impl;

import com.craftaro.epicspawners.api.EpicSpawnersApi;
import com.craftaro.epicspawners.api.spawners.spawner.PlacedSpawner;
import com.craftaro.skyblock.levelling.calculator.SpawnerCalculator;
import org.bukkit.block.CreatureSpawner;

public class EpicSpawnerCalculator implements SpawnerCalculator {
    @Override
    public long getSpawnerAmount(CreatureSpawner spawner) {
        final PlacedSpawner epic = EpicSpawnersApi.getSpawnerManager().getSpawnerFromWorld(spawner.getLocation());
        return epic.getStackSize();
    }
}
