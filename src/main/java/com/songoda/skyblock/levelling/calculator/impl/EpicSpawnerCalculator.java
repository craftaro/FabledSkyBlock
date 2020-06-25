package com.songoda.skyblock.levelling.calculator.impl;

import org.bukkit.block.CreatureSpawner;

import com.songoda.epicspawners.EpicSpawners;
import com.songoda.epicspawners.spawners.spawner.Spawner;
import com.songoda.skyblock.levelling.calculator.SpawnerCalculator;

public class EpicSpawnerCalculator implements SpawnerCalculator {

    @Override
    public long getSpawnerAmount(CreatureSpawner spawner) {
        final Spawner epic = EpicSpawners.getInstance().getSpawnerManager().getSpawnerFromWorld(spawner.getLocation());
        return epic == null ? 0 : epic.getFirstStack().getStackSize();
    }

}
