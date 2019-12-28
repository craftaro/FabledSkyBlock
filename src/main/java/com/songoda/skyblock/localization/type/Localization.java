package com.songoda.skyblock.localization.type;

import java.util.Map;
import java.util.Objects;

import org.bukkit.configuration.ConfigurationSection;

public abstract class Localization<T> {

    private final Class<T> type;
    private final String keysPath;
    private final Map<T, String> values;

    public Localization(String keysPath, Class<T> type) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.keysPath = "ClassLocalization." + Objects.requireNonNull(keysPath, "keysPath canoot be null.");
        this.values = Objects.requireNonNull(newValueMapInstance(type), "type cannot be null");
    }

    public abstract void reload(ConfigurationSection section);

    protected abstract Map<T, String> newValueMapInstance(Class<T> type);

    protected final Map<T, String> getValues() {
        return values;
    }

    public final String getKeysPath() {
        return keysPath;
    }

    public final Class<T> getType() {
        return type;
    }

    public String getLocale(T object) {

        String value = getValues().get(object);

        if (value == null) value = getDefaultLocaleFor(object);

        return value;
    }

    public String getDefaultLocaleFor(T object) {

        String name = object.toString().toLowerCase();

        if (name.length() > 1) name = name.substring(0, 1).toUpperCase() + name.substring(1);

        return name;
    }

}
