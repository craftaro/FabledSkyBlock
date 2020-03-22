package com.songoda.skyblock.levelling.rework.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

public final class CalculatorRegistry {

    private CalculatorRegistry() {

    }

    private static final Map<Material, List<Calculator>> calculators = new HashMap<>();

    public static void registerCalculator(Calculator calculator, Material to) {

        List<Calculator> list = calculators.get(to);

        if (list == null) {
            list = new ArrayList<>();
            calculators.put(to, list);
        }

        list.add(calculator);
    }

    public static List<Calculator> getCalculators(Material type) {
        return calculators.get(type);
    }

}
