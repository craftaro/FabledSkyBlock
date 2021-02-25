package com.songoda.skyblock.levelling.calculator;

import com.songoda.core.compatibility.CompatibleMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CalculatorRegistry {

    private static final Map<CompatibleMaterial, List<Calculator>> calculators = new HashMap<>();

    public static void registerCalculator(Calculator calculator, CompatibleMaterial to) {

        List<Calculator> list = calculators.computeIfAbsent(to, k -> new ArrayList<>());
        list.add(calculator);
    }

    public static List<Calculator> getCalculators(CompatibleMaterial type) {
        return calculators.get(type);
    }

}
