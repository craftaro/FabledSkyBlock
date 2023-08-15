package com.craftaro.skyblock.levelling.calculator.impl;

import com.craftaro.skyblock.levelling.calculator.SpawnerCalculator;
import com.songoda.epicspawners.EpicSpawners;
import com.songoda.epicspawners.spawners.spawner.PlacedSpawner;
import org.bukkit.block.CreatureSpawner;

public class EpicSpawnerCalculator implements SpawnerCalculator {
    @Override
    public long getSpawnerAmount(CreatureSpawner spawner) {
        final PlacedSpawner epic = EpicSpawners.getInstance().getSpawnerManager().getSpawnerFromWorld(spawner.getLocation());
        return epic.getStackSize();
    }
}
