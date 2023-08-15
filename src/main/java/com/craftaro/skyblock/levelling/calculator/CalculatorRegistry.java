package com.craftaro.skyblock.levelling.calculator;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CalculatorRegistry {
    private static final Map<XMaterial, List<Calculator>> CALCULATORS = new HashMap<>();

    public static void registerCalculator(Calculator calculator, XMaterial to) {
        List<Calculator> list = CALCULATORS.computeIfAbsent(to, key -> new ArrayList<>());
        list.add(calculator);
    }

    public static List<Calculator> getCalculators(XMaterial type) {
        return CALCULATORS.get(type);
    }
}
