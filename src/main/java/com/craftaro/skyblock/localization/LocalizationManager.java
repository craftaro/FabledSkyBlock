package com.craftaro.skyblock.localization;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.localization.type.Localization;
import com.craftaro.skyblock.localization.type.impl.BlankLocalization;
import com.craftaro.skyblock.localization.type.impl.EnumLocalization;
import com.craftaro.skyblock.localization.type.impl.MaterialsLocalization;
import com.google.common.collect.Sets;
import org.bukkit.configuration.Configuration;

import java.util.HashMap;
import java.util.Map;

public final class LocalizationManager {
    private final Localization<?> def = new BlankLocalization("", Object.class);

    private final Map<Class<?>, Localization<?>> map;

    public LocalizationManager() {
        this.map = new HashMap<>();
        registerLocalizationFor(XMaterial.class, new MaterialsLocalization("Materials"));
        registerLocalizationFor(IslandRole.class, new EnumLocalization<>("IslandRoles", IslandRole.class));
    }

    public void registerLocalizationFor(Class<?> type, Localization<?> toUse) {
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }
        if (toUse == null) {
            throw new IllegalArgumentException("toUse cannot be null");
        }
        if (toUse == this.def) {
            throw new IllegalArgumentException("Cannot register default localization.");
        }

        this.map.put(type, toUse);

        final SkyBlock inst = SkyBlock.getPlugin(SkyBlock.class);
        toUse.reload(inst.getLanguage().getConfigurationSection(toUse.getKeysPath()));
    }

    public void reloadAll() {
        final SkyBlock inst = SkyBlock.getPlugin(SkyBlock.class);
        final Configuration config = inst.getLanguage();

        Sets.newHashSet(this.map.values()).forEach(locale -> locale.reload(config.getConfigurationSection(locale.getKeysPath())));
    }

    @SuppressWarnings("unchecked")
    public <T> Localization<T> getLocalizationFor(Class<T> type) {
        Localization<T> locale = (Localization<T>) this.map.get(type);

        if (locale == null) {
            locale = (Localization<T>) this.def;
        }

        return locale;
    }
}
