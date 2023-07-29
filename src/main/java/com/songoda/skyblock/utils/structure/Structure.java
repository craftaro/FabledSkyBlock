package com.songoda.skyblock.utils.structure;

public class Structure {
    private final Storage storage;
    private final String file;

    public Structure(Storage storage, String file) {
        this.storage = storage;
        this.file = file;
    }

    public Storage getStructureStorage() {
        return this.storage;
    }

    public String getStructureFile() {
        return this.file;
    }
}
