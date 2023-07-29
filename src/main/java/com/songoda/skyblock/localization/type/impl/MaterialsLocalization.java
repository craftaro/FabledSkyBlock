package com.songoda.skyblock.localization.type.impl;

import com.songoda.core.compatibility.CompatibleMaterial;

public class MaterialsLocalization extends EnumLocalization<CompatibleMaterial> {
    public MaterialsLocalization(String keysPath) {
        super(keysPath, CompatibleMaterial.class);
    }

    @Override
    public CompatibleMaterial parseEnum(String input) {
        return CompatibleMaterial.getMaterial(input);
    }

    @Override
    public String getDefaultLocaleFor(CompatibleMaterial obj) {
        return super.getDefaultLocaleFor(obj).replace("_", " ");
    }
}
