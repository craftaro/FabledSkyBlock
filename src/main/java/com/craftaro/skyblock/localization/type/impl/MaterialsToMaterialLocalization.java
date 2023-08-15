package com.craftaro.skyblock.localization.type.impl;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;

public class MaterialsToMaterialLocalization extends EnumLocalization<XMaterial> {
    public MaterialsToMaterialLocalization(String keysPath) {
        super(keysPath, XMaterial.class);
    }

    @Override
    public XMaterial parseEnum(String input) {
        return CompatibleMaterial.getMaterial(input).get();
    }
}
