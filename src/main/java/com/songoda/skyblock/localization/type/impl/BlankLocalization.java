package com.songoda.skyblock.localization.type.impl;

import com.songoda.skyblock.localization.type.Localization;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public final class BlankLocalization extends Localization<Object> {
    public BlankLocalization(String keysPath, Class<Object> type) {
        super(keysPath, type);
    }

    @Override
    public void reload(ConfigurationSection section) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Map<Object, String> newValueMapInstance(Class<Object> type) {
        return new HashMap<>(0);
    }

    @Override
    public String getLocale(Object obj) {
        return getDefaultLocaleFor(obj);
    }
}
