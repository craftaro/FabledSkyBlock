package com.songoda.skyblock.limit;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.limit.impl.BlockLimitation;
import com.songoda.skyblock.limit.impl.EntityLimitaton;
import org.bukkit.configuration.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class LimitationInstanceHandler {

    private final Map<Class<? extends Limitation>, Limitation> instances;

    public LimitationInstanceHandler() {
        this.instances = new HashMap<>();
        registerInstance(new EntityLimitaton());
        registerInstance(new BlockLimitation());
        reloadAll();
    }

    public <T extends Limitation> T getInstance(Class<T> type) {
        return type.cast(instances.get(type));
    }

    public void registerInstance(Limitation instance) {
        instances.put(instance.getClass(), instance);
    }

    public void reloadAll() {
        final SkyBlock instance = SkyBlock.getInstance();
        final Configuration config = instance.getFileManager().getConfig(new File(instance.getDataFolder(), "limits.yml")).getFileConfiguration();

        for (Limitation limit : instances.values()) {
            limit.reload(config.getConfigurationSection(limit.getSectionName()));
        }

    }

}
