package com.songoda.skyblock.localization.type.impl;

import com.songoda.skyblock.utils.version.Materials;

public class MaterialsLocalization extends EnumLocalization<Materials> {

    public MaterialsLocalization(String keysPath) {
        super(keysPath, Materials.class);
    }

    @Override
    public Materials parseEnum(String input) {
        return Materials.fromString(input);
    }

    @Override
    public String getDefaultLocaleFor(Materials obj) {
        return super.getDefaultLocaleFor(obj).replace("_", " ");
    }

}
