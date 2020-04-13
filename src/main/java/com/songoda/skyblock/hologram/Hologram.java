package com.songoda.skyblock.hologram;

import com.songoda.core.hooks.HologramManager;
import com.songoda.skyblock.SkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public class Hologram {

    private HologramType type;
    private Location location;

    public Hologram(HologramType type, Location location, List<String> lines) {
        this.type = type;
        this.location = location;

        Bukkit.getScheduler().runTask(SkyBlock.getInstance(),
                () -> HologramManager.createHologram(location, lines));
    }

    public HologramType getType() {
        return type;
    }


    public Location getLocation() {
        return location;
    }

    public void remove() {
        HologramManager.removeHologram(location);
        Bukkit.getScheduler().runTask(SkyBlock.getInstance(),
                () -> HologramManager.removeHologram(location));
    }

    public void update(List<String> lines) {
        Bukkit.getScheduler().runTask(SkyBlock.getInstance(),
                () -> HologramManager.updateHologram(location, lines));

    }
}
