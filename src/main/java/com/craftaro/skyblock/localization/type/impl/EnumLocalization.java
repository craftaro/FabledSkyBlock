package com.craftaro.skyblock.localization.type.impl;

import com.craftaro.skyblock.localization.type.Localization;
import org.bukkit.configuration.ConfigurationSection;

import java.util.EnumMap;
import java.util.Map;

public class EnumLocalization<T extends Enum<T>> extends Localization<T> {
    public EnumLocalization(String keysPath, Class<T> type) {
        super(keysPath, type);
    }

    @Override
    protected final Map<T, String> newValueMapInstance(Class<T> type) {
        return new EnumMap<>(type);
    }

    @Override
    public void reload(ConfigurationSection section) {
        getValues().clear();
        if (section == null) {
            return;
        }

        for (String key : section.getKeys(false)) {
            T parse;

            try {
                parse = parseEnum(key);
            } catch (IllegalArgumentException | NullPointerException e) {
                throw new IllegalArgumentException("Unable to parse a '" + getType().getSimpleName() + "' for given string '" + key + "' in '" + section.getCurrentPath() + "'", e);
            }

            getValues().put(parse, section.getString(key));
        }
    }

    protected T parseEnum(String input) {
        return Enum.valueOf(getType(), input.toUpperCase());
    }
}
