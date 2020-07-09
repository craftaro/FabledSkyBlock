package com.songoda.skyblock.blockscanner;

import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public final class MaterialIDHelper {

    private final static int VERSION = NMSUtil.getVersionNumber();

    private MaterialIDHelper() {

    }

    private final static Map<Integer, Material> MATERIALS;

    static {
        MATERIALS = new HashMap<>();

        if (VERSION > 12) {
            for (Material type : Material.values()) {
                if (type.isLegacy()) MATERIALS.put(type.getId(), type);
            }
        } else {
            for (Material type : Material.values()) {
                MATERIALS.put(type.getId(), type);
            }
        }

    }

    public static Material getLegacyMaterial(int id) {
        return MATERIALS.get(id);
    }

}
