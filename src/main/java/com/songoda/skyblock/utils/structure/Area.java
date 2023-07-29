package com.songoda.skyblock.utils.structure;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class Area {
    private final Map<Integer, Location> positions;

    public Area() {
        this.positions = new HashMap<>();
    }

    public Location getPosition(int position) {
        return this.positions.get(position);
    }

    public void setPosition(int position, Location location) {
        this.positions.put(position, location);
    }
}
