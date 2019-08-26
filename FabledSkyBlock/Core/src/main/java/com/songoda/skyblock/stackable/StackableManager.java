package com.songoda.skyblock.stackable;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class StackableManager {

    //ToDO: Should pobably be a GUI for this

    private final SkyBlock skyblock;
    private List<Material> stackableMaterials = new ArrayList<>();
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
                this.stackableMaterials.add(Material.valueOf(stackableStr));
            } catch (Exception ignored) {
            }
        }
    }

    public void loadSavedStackables() {
        FileManager fileManager = SkyBlock.getInstance().getFileManager();
        String path = SkyBlock.getInstance().getDataFolder().toString() + "/island-data";
        File[] files = new File(path).listFiles();
        if (files == null) return;
        for (File file : files) {
            File configFile = new File(path);
            FileManager.Config config = fileManager.getConfig(new File(configFile, file.getName()));
            FileConfiguration configLoad = config.getFileConfiguration();
            ConfigurationSection cs = configLoad.getConfigurationSection("Stackables");
            if (cs == null || cs.getKeys(false) == null) continue;
            for (String uuid : cs.getKeys(false)) {
                ConfigurationSection section = configLoad.getConfigurationSection("Stackables." + uuid);
                Location location = (Location) section.get("Location");
                org.bukkit.Material material = org.bukkit.Material.valueOf(section.getString("Material"));
                int size = section.getInt("Size");
                if (size == 0) continue;
                this.addStack(new Stackable(UUID.fromString(uuid), location, material, size));
            }
        }
    }

    public void unregisterStackables() {
        stackableMaterials.clear();
    }

    public List<Material> getStackableMaterials() {
        return Collections.unmodifiableList(stackableMaterials);
    }

    public Map<Location, Stackable> getStacks() {
        return Collections.unmodifiableMap(stacks);
    }

    public boolean isStacked(Location location) {
        return stacks.containsKey(location);
    }

    public Stackable getStack(Location location, Material material) {
        Stackable stackable = stacks.get(location);

        if (stackable != null && stackable.getMaterial() == material)
            return stacks.get(location);
        else
            return null;
    }

    public Stackable addStack(Stackable stackable) {
        return stacks.put(stackable.getLocation(), stackable);
    }

    public void removeStack(Stackable stackable) {
        stackable.setSize(0);
        stacks.remove(stackable.getLocation());
    }
}
