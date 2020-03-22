package com.songoda.skyblock.stackable;

import java.io.File;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.utils.version.Materials;

public class StackableManager {

    // ToDO: Should pobably be a GUI for this

    private final SkyBlock skyblock;
    private Set<Materials> stackableMaterials = EnumSet.noneOf(Materials.class);
    private Map<Location, Stackable> stacks = new HashMap<>();

    public StackableManager(SkyBlock skyblock) {
        this.skyblock = skyblock;
        registerStackables();
    }

    public void registerStackables() {
        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "stackables.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        List<String> stackableList = configLoad.getStringList("Stackables");
        if (stackableList == null) return;

        for (String stackableStr : stackableList) {
            try {
                this.stackableMaterials.add(Materials.fromString(stackableStr));
            } catch (Exception ignored) {
            }
        }
    }

    /*
     * public void loadSavedStackables() { FileManager fileManager =
     * SkyBlock.getInstance().getFileManager(); String path =
     * SkyBlock.getInstance().getDataFolder().toString() + "/island-data"; File[]
     * files = new File(path).listFiles(); if (files == null) return; for (File file
     * : files) { File configFile = new File(path); FileManager.Config config =
     * fileManager.getConfig(new File(configFile, file.getName()));
     * FileConfiguration configLoad = config.getFileConfiguration();
     * ConfigurationSection cs = configLoad.getConfigurationSection("Stackables");
     * if (cs == null) continue; Set<String> keys = cs.getKeys(false); if (keys ==
     * null) continue; for (String uuid : keys) { ConfigurationSection section =
     * cs.getConfigurationSection(uuid); Location location = (Location)
     * section.get("Location"); org.bukkit.Material material =
     * org.bukkit.Material.valueOf(section.getString("Material")); int size =
     * section.getInt("Size"); if (size == 0) continue; this.addStack(new
     * Stackable(UUID.fromString(uuid), location, material, size)); } } }
     */

    @SuppressWarnings("deprecation")
    public void loadSavedStackables() {
        final File path = new File(skyblock.getDataFolder(), "island-data");
        final File[] files = path.listFiles();

        if (files == null) return;

        for (File file : files) {
            final FileConfiguration config = skyblock.getFileManager().getConfig(file).getFileConfiguration();

            ConfigurationSection stackableSection = config.getConfigurationSection("Stackables");

            if (stackableSection == null) continue;

            for (String key : stackableSection.getKeys(false)) {

                final ConfigurationSection currentSection = stackableSection.getConfigurationSection(key);
                final Location loc = (Location) currentSection.get("Location");
                final Block block = loc.getWorld().getBlockAt(loc);

                if (block.getType() == Material.AIR) continue;

                final Materials type = Materials.getMaterials(block.getType(), block.getData());

                if (type == null) continue;

                final int size = currentSection.getInt("Size");

                if (size == 0) continue;

                this.addStack(new Stackable(UUID.fromString(key), loc, type, size));

            }

        }

    }

    public void unregisterStackables() {
        stackableMaterials.clear();
    }

    public Set<Materials> getStackableMaterials() {
        return Collections.unmodifiableSet(stackableMaterials);
    }

    public boolean isStackableMaterial(Materials material) {
        return stackableMaterials.contains(material);
    }

    public Map<Location, Stackable> getStacks() {
        return Collections.unmodifiableMap(stacks);
    }

    public boolean isStacked(Location location) {
        return stacks.containsKey(location);
    }

    public Stackable getStack(Location location, Materials material) {
        Stackable stackable = stacks.get(location);

        return stackable != null && stackable.getMaterial() == material ? stackable : null;
    }

    public Stackable addStack(Stackable stackable) {
        return stacks.put(stackable.getLocation(), stackable);
    }

    public void removeStack(Stackable stackable) {
        stackable.setSize(0);
        stacks.remove(stackable.getLocation());
    }

    public long getStackSizeOf(Location loc, Materials type) {
        final Stackable stack = getStack(loc, type);

        return stack == null ? 0 : stack.getSize();
    }
}
