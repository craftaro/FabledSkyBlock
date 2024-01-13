package com.craftaro.skyblock.utils.item;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.utils.StringUtil;
import com.craftaro.skyblock.utils.item.nInventoryUtil.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public final class MenuClickRegistry {
    private static MenuClickRegistry instance;

    public static MenuClickRegistry getInstance() {
        return instance == null ? instance = new MenuClickRegistry() : instance;
    }

    private final Set<MenuPopulator> populators;
    private final Map<RegistryKey, MenuExecutor> executors;

    private MenuClickRegistry() {
        this.executors = new HashMap<>();
        this.populators = new HashSet<>();
    }

    public void register(MenuPopulator populator) {
        populator.populate(this.executors);
        this.populators.add(populator);
    }

    public void reloadAll() {
        this.executors.clear();
        for (MenuPopulator populator : this.populators) {
            populator.populate(this.executors);
        }
    }

    public void dispatch(Player clicker, ClickEvent e) {
        final ItemStack item = e.getItem();
        if (item == null) {
            return;
        }

        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        @SuppressWarnings("deprecation") final MenuExecutor executor = this.executors.get(RegistryKey.fromName(meta.getDisplayName(), CompatibleMaterial.getMaterial(item.getType()).get()));


        if (executor == null) {
            return;
        }
        executor.onClick(SkyBlock.getPlugin(SkyBlock.class), clicker, e);
    }

    public interface MenuPopulator {
        void populate(Map<RegistryKey, MenuExecutor> executors);
    }

    public interface MenuExecutor {
        void onClick(SkyBlock plugin, Player clicker, ClickEvent e);
    }

    public static class RegistryKey {
        private static final File path = new File(SkyBlock.getPlugin(SkyBlock.class).getDataFolder(), "language.yml");

        private final String name;
        private final XMaterial type;

        private RegistryKey(String name, XMaterial type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.type);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof RegistryKey)) {
                return false;
            }

            final RegistryKey other = (RegistryKey) obj;

            return Objects.equals(this.name, other.name) && this.type == other.type;
        }

        public static RegistryKey fromName(String name, XMaterial type) {
            return new RegistryKey(name, type);
        }

        public static RegistryKey fromLanguageFile(String namePath, XMaterial type) {
            return new RegistryKey(StringUtil.color(SkyBlock.getPlugin(SkyBlock.class).getFileManager().getConfig(path).getFileConfiguration().getString(namePath)), type);
        }
    }
}
