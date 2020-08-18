package com.songoda.skyblock.levelling.calculator.impl;

import com.songoda.epicspawners.EpicSpawners;
import com.songoda.epicspawners.spawners.spawner.Spawner;
import com.songoda.skyblock.levelling.calculator.SpawnerCalculator;
import org.bukkit.block.CreatureSpawner;

public class EpicSpawnerCalculator implements SpawnerCalculator {

    @Override
    public long getSpawnerAmount(CreatureSpawner spawner) {
        final Spawner epic = EpicSpawners.getInstance().getSpawnerManager().getSpawnerFromWorld(spawner.getLocation());
        return epic == null ? 0 : epic.getFirstStack().getStackSize();
    }

}
