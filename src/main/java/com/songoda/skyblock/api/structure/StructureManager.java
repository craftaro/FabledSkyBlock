package com.songoda.skyblock.api.structure;

import java.util.ArrayList;
import java.util.List;

public class StructureManager {
    private final com.songoda.skyblock.structure.StructureManager structureManager;

    public StructureManager(com.songoda.skyblock.structure.StructureManager structureManager) {
        this.structureManager = structureManager;
    }

    /**
     * @return The Structure for an Island
     */
    public Structure getStructure(String structure) {
        return this.structureManager.getStructure(structure);
    }

    /**
     * @return true of conditions met, false otherwise
     */
    public boolean hasStructure(String structure) {
        return this.structureManager.containsStructure(structure);
    }

    /**
     * @return A List of Structures for an Island
     */
    public List<Structure> getStructures() {
        return new ArrayList<>(this.structureManager.getStructures());
    }
}
