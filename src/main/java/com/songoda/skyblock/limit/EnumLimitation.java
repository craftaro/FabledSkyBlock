package com.songoda.skyblock.limit;

import org.bukkit.configuration.ConfigurationSection;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public abstract class EnumLimitation<K extends Enum<K>> implements Limitation {

    private long defaultLimit;
    private final Class<K> type;
    private final Map<K, Long> map;

    public EnumLimitation(Class<K> type) {
        this.defaultLimit = -1;
        this.map = new EnumMap<>(type);
        this.type = type;
    }

    public boolean isBeingTracked(Enum<K> type) {
        return map.containsKey(type) || getDefault() >= 0;
    }

    protected Map<K, Long> getMap() {
        return map;
    }

    public long getDefault() {
        return defaultLimit;
    }

    public boolean hasTooMuch(long currentAmount, Enum<K> type) {
        final long cached = map.getOrDefault(type, getDefault());

        if (cached <= -1) return false;

        return currentAmount > cached;
    }

    @Override
    public void reload(ConfigurationSection loadFrom) {
        unload();

        if (loadFrom == null) return;

        final Set<String> keys = loadFrom.getKeys(false);

        removeAndLoadDefaultLimit(loadFrom, keys);

        for (String key : keys) {

            final String enumName = key.toUpperCase(Locale.ENGLISH);

            try {
                map.put(Enum.valueOf(type, enumName), loadFrom.getLong(key));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Incorrect enum constant '" + enumName + "' in " + loadFrom.getCurrentPath(), e);
            }

        }

    }

    @Override
    public void unload() {
        map.clear();
        defaultLimit = -1;
    }

    protected void removeAndLoadDefaultLimit(ConfigurationSection loadFrom, Set<String> keys) {
        keys.remove("DefaultLimit");
        defaultLimit = loadFrom.getInt("DefaultLimit", -1);
    }

}
