package com.songoda.skyblock.utils.item;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.utils.StringUtil;
import com.songoda.skyblock.utils.item.nInventoryUtil.ClickEvent;
import com.songoda.skyblock.utils.version.Materials;

public final class MenuClickRegistry {

    private static MenuClickRegistry instance;

    public static MenuClickRegistry getInstance() {
        return instance == null ? instance = new MenuClickRegistry() : instance;
    }

    private Set<MenuPopulator> populators;
    private Map<RegistryKey, MenuExecutor> executors;

    private MenuClickRegistry() {
        this.executors = new HashMap<>();
        this.populators = new HashSet<>();
    }

    public void register(MenuPopulator populator) {
        populator.populate(executors);
        populators.add(populator);
    }

    public void reloadAll() {
        executors.clear();
        for (MenuPopulator populator : populators) {
            populator.populate(executors);
        }
    }

    public void dispatch(Player clicker, ClickEvent e) {

        final ItemStack item = e.getItem();

        if (item == null) return;

        final ItemMeta meta = item.getItemMeta();

        if (meta == null) return;

        @SuppressWarnings("deprecation")
        final MenuExecutor executor = executors.get(RegistryKey.fromName(meta.getDisplayName(), Materials.getMaterials(item.getType(), (byte) item.getDurability())));


        if (executor == null) return;

        executor.onClick(SkyBlock.getInstance(), clicker, e);
    }

    public static interface MenuPopulator {

        void populate(Map<RegistryKey, MenuExecutor> executors);

    }

    public static interface MenuExecutor {

        void onClick(SkyBlock skyblock, Player clicker, ClickEvent e);

    }

    public static class RegistryKey {

        private static final File path = new File(SkyBlock.getInstance().getDataFolder(), "language.yml");

        private final String name;
        private final Materials type;

        private RegistryKey(String name, Materials type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof RegistryKey)) return false;

            final RegistryKey other = (RegistryKey) obj;

            return Objects.equals(name, other.name) && type == other.type;
        }

        public static RegistryKey fromName(String name, Materials type) {
            return new RegistryKey(name, type);
        }

        public static RegistryKey fromLanguageFile(String namePath, Materials type) {
            return new RegistryKey(StringUtil.color(SkyBlock.getInstance().getFileManager().getConfig(path).getFileConfiguration().getString(namePath)), type);
        }
    }

}
