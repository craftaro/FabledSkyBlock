package com.songoda.skyblock.localization.type.impl;

import org.bukkit.Material;

import com.songoda.skyblock.utils.version.Materials;

public class MaterialsToMaterialLocalization extends EnumLocalization<Material> {

    public MaterialsToMaterialLocalization(String keysPath) {
        super(keysPath, Material.class);
    }

    @Override
    public Material parseEnum(String input) {
        Materials material = Materials.fromString(input);
        return material == null ? null : material.parseMaterial();
    }

}
