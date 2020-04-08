package com.songoda.skyblock.hologram;

import com.songoda.core.hooks.HologramManager;
import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    private HologramType type;
    private Location location;

    public Hologram(HologramType type, Location location, List<String> lines) {
        this.type = type;
        this.location = location;
        HologramManager.createHologram(location, lines);
    }

    public HologramType getType() {
        return type;
    }


    public Location getLocation() {
        return location;
    }

    public void remove() {
        HologramManager.removeHologram(location);
    }

    public void update(List<String> lines) {
        HologramManager.updateHologram(location, lines);

    }
}
