package me.goodandevil.skyblock.stackable;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.listeners.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;

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
            } catch (Exception ignored) {}
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

        if (stackable.getMaterial() == material)
            return stacks.get(location);
        else
            return null;
    }

    public Stackable addStack(Stackable stackable) {
        return stacks.put(stackable.getLocation(), stackable);
    }

    public void removeStack(Stackable stackable) {
        stackable.setSize(0);
        stackable.removeDisplay();
        stacks.remove(stackable.getLocation());
    }
}
