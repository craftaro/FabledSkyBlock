package com.songoda.skyblock.utils.structure;

public class Storage {

    private final String blocks;
    private final String entities;
    private final String originLocation;

    private final long time;

    private final int version;

    public Storage(String blocks, String entities, String originLocation, long time, int version) {
        this.blocks = blocks;
        this.entities = entities;
        this.originLocation = originLocation;
        this.time = time;
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public String getBlocks() {
        return blocks;
    }

    public String getEntities() {
        return entities;
    }

    public String getOriginLocation() {
        return originLocation;
    }

    public long getTime() {
        return time;
    }
}