package com.craftaro.skyblock.localization.type.impl;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;

public class MaterialsLocalization extends EnumLocalization<XMaterial> {
    public MaterialsLocalization(String keysPath) {
        super(keysPath, XMaterial.class);
    }

    @Override
    public XMaterial parseEnum(String input) {
        return CompatibleMaterial.getMaterial(input).get();
    }

    @Override
    public String getDefaultLocaleFor(XMaterial obj) {
        return super.getDefaultLocaleFor(obj).replace("_", " ");
    }
}
