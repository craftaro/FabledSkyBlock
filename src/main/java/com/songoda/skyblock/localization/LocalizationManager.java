package com.songoda.skyblock.localization;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.Configuration;

import com.google.common.collect.Sets;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.localization.type.Localization;
import com.songoda.skyblock.localization.type.impl.BlankLocalization;
import com.songoda.skyblock.localization.type.impl.EnumLocalization;
import com.songoda.skyblock.localization.type.impl.MaterialsLocalization;
import com.songoda.skyblock.utils.version.Materials;

public final class LocalizationManager {

    private final Localization<?> def = new BlankLocalization("", Object.class);

    private Map<Class<?>, Localization<?>> map;

    public LocalizationManager() {
        this.map = new HashMap<>();
        registerLocalizationFor(Materials.class, new MaterialsLocalization("Materials"));
        registerLocalizationFor(IslandRole.class, new EnumLocalization<>("IslandRoles", IslandRole.class));
    }

    public void registerLocalizationFor(Class<?> type, Localization<?> toUse) {

        if (type == null) throw new IllegalArgumentException("type cannot be null");
        if (toUse == null) throw new IllegalArgumentException("toUse cannot be null");
        if (toUse == def) throw new IllegalArgumentException("Cannot register default localization.");

        map.put(type, toUse);

        final SkyBlock inst = SkyBlock.getInstance();

        toUse.reload(inst.getFileManager().getConfig(new File(inst.getDataFolder(), "language.yml")).getFileConfiguration().getConfigurationSection(toUse.getKeysPath()));
    }

    public void reloadAll() {

        final SkyBlock inst = SkyBlock.getInstance();
        final Configuration config = inst.getFileManager().getConfig(new File(inst.getDataFolder(), "language.yml")).getFileConfiguration();

        for (Localization<?> locale : Sets.newHashSet(map.values())) {
            locale.reload(config.getConfigurationSection(locale.getKeysPath()));
        }

    }

    @SuppressWarnings("unchecked")
    public <T> Localization<T> getLocalizationFor(Class<T> type) {

        Localization<T> locale = (Localization<T>) map.get(type);

        if (locale == null) locale = (Localization<T>) def;

        return locale;
    }

}
