package com.songoda.skyblock.utils.structure;

public class Storage {

    private String blocks;
    private String entities;
    private String originLocation;

    private long time;

    private int version;

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