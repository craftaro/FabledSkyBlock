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
        return this.map.containsKey(type) || getDefault() >= 0;
    }

    protected Map<K, Long> getMap() {
        return this.map;
    }

    public long getDefault() {
        return this.defaultLimit;
    }

    public boolean hasTooMuch(long currentAmount, Enum<K> type) {
        final long cached = this.map.getOrDefault(type, getDefault());
        return cached > -1 && currentAmount > cached;
    }

    @Override
    public void reload(ConfigurationSection loadFrom) {
        unload();

        if (loadFrom == null) {
            return;
        }

        final Set<String> keys = loadFrom.getKeys(false);

        removeAndLoadDefaultLimit(loadFrom, keys);

        for (String key : keys) {
            final String enumName = key.toUpperCase(Locale.ENGLISH);

            try {
                this.map.put(Enum.valueOf(this.type, enumName), loadFrom.getLong(key));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Incorrect enum constant '" + enumName + "' in " + loadFrom.getCurrentPath(), ex);
            }
        }
    }

    @Override
    public void unload() {
        this.map.clear();
        this.defaultLimit = -1;
    }

    protected void removeAndLoadDefaultLimit(ConfigurationSection loadFrom, Set<String> keys) {
        keys.remove("DefaultLimit");
        this.defaultLimit = loadFrom.getInt("DefaultLimit", -1);
    }
}
