package com.songoda.skyblock.hologram;

import com.songoda.core.hooks.HologramManager;
import com.songoda.skyblock.SkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public class Hologram {
    private final String hologramId;

    private final HologramType type;
    private final Location location;

    public Hologram(HologramType type, Location location, List<String> lines) {
        this.hologramId = location.toString() + UUID.randomUUID();

        this.type = type;
        this.location = location;

        Bukkit.getScheduler().runTask(SkyBlock.getInstance(),
                () -> HologramManager.createHologram(hologramId, location, lines));
    }

    public HologramType getType() {
        return type;
    }


    public Location getLocation() {
        return location;
    }

    public void remove() {
        HologramManager.removeHologram(hologramId);
        Bukkit.getScheduler().runTask(SkyBlock.getInstance(), () -> HologramManager.removeHologram(hologramId));
    }

    public void update(List<String> lines) {
        Bukkit.getScheduler().runTask(SkyBlock.getInstance(), () -> {
            if (HologramManager.isHologramLoaded(hologramId)) {
                HologramManager.updateHologram(hologramId, lines);
                return;
            }

            HologramManager.createHologram(hologramId, location, lines);
        });

    }
}
