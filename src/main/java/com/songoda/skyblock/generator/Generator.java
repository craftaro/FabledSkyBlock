package com.songoda.skyblock.generator;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.island.IslandWorld;

import java.util.List;

public class Generator {

    private final String name;
    private final IslandWorld isWorld;
    private final CompatibleMaterial materials;
    private final List<GeneratorMaterial> generatorMaterials;
    private final long level;
    private boolean permission;

    public Generator(String name, IslandWorld isWorld, CompatibleMaterial materials, List<GeneratorMaterial> generatorMaterials, long level, boolean permission) {
        this.name = name;
        this.isWorld = isWorld;
        this.materials = materials;
        this.generatorMaterials = generatorMaterials;
        this.level = level;
        this.permission = permission;
    }

    public String getName() {
        return name;
    }

    public IslandWorld getIsWorld() {
        return isWorld;
    }

    public CompatibleMaterial getMaterials() {
        return materials;
    }

    public List<GeneratorMaterial> getGeneratorMaterials() {
        return generatorMaterials;
    }

    public boolean isPermission() {
        return permission;
    }

    public String getPermission() {
        return "fabledskyblock.generator." + name.toLowerCase().replace(" ", "_");
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }
    
    public long getLevel() {
        return level;
    }
}
