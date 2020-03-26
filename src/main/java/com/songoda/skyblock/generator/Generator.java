package com.songoda.skyblock.generator;

import com.songoda.core.compatibility.CompatibleMaterial;
 

import java.util.List;

public class Generator {

    private String name;
    private CompatibleMaterial materials;
    private List<GeneratorMaterial> generatorMaterials;
    private boolean permission;

    public Generator(String name, CompatibleMaterial materials, List<GeneratorMaterial> generatorMaterials, boolean permission) {
        this.name = name;
        this.materials = materials;
        this.generatorMaterials = generatorMaterials;
        this.permission = permission;
    }

    public String getName() {
        return name;
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
}
