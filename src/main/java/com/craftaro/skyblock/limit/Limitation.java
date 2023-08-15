package com.craftaro.skyblock.limit;

import org.bukkit.configuration.ConfigurationSection;

public interface Limitation {
    void unload();

    void reload(ConfigurationSection loadFrom);

    String getSectionName();
}
