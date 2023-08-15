package com.craftaro.skyblock.hologram;

import com.craftaro.core.hooks.HologramManager;
import com.craftaro.skyblock.SkyBlock;
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

        Bukkit.getScheduler().runTask(SkyBlock.getPlugin(SkyBlock.class), () -> HologramManager.createHologram(this.hologramId, location, lines));
    }

    public HologramType getType() {
        return this.type;
    }


    public Location getLocation() {
        return this.location;
    }

    public void remove() {
        HologramManager.removeHologram(this.hologramId);
        Bukkit.getScheduler().runTask(SkyBlock.getPlugin(SkyBlock.class), () -> HologramManager.removeHologram(this.hologramId));
    }

    public void update(List<String> lines) {
        Bukkit.getScheduler().runTask(SkyBlock.getPlugin(SkyBlock.class), () -> {
            if (HologramManager.isHologramLoaded(this.hologramId)) {
                HologramManager.updateHologram(this.hologramId, lines);
                return;
            }

            HologramManager.createHologram(this.hologramId, this.location, lines);
        });
    }
}
