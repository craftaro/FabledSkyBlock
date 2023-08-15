package com.songoda.skyblock.utils;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;

public class MaterialUtils {
    public static boolean isTall(XMaterial material) {
        switch (material) {
            case SUNFLOWER:
            case LILAC:
            case LARGE_FERN:
            case ROSE_BUSH:
            case PEONY:
            case TALL_GRASS:
                return true;
            default:
                return false;
        }
    }
}
