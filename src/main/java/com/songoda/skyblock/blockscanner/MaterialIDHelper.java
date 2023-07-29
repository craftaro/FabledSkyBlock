package com.songoda.skyblock.blockscanner;

import com.songoda.core.compatibility.ServerVersion;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public final class MaterialIDHelper {
    private MaterialIDHelper() {
    }

    private static final Map<Integer, Material> MATERIALS;

    static {
        MATERIALS = new HashMap<>();

        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
            for (Material type : Material.values()) {
                if (type.isLegacy()) {
                    MATERIALS.put(type.getId(), type);
                }
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
