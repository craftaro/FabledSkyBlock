package com.songoda.skyblock.levelling.rework.calculator.impl;

import org.bukkit.block.CreatureSpawner;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.objects.StackedSpawner;
import com.songoda.skyblock.levelling.rework.calculator.SpawnerCalculator;

public class WildStackerCalculator implements SpawnerCalculator {

    @Override
    public long getSpawnerAmount(CreatureSpawner spawner) {
        final StackedSpawner stacked = WildStackerAPI.getStackedSpawner(spawner);
        return stacked == null ? 0 : stacked.getStackAmount();
    }

}
