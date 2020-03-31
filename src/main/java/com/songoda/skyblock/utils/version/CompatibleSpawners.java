package com.songoda.skyblock.utils.version;

import com.songoda.core.compatibility.CompatibleMaterial;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;

public enum CompatibleSpawners {

    SPAWNER("MOB_SPAWNER", 0),
    SPAWNER_BAT(null, -1),
    SPAWNER_BLAZE(null, -1),
    SPAWNER_CAVE_SPIDER(null, -1),
    SPAWNER_CHICKEN(null, -1),
    SPAWNER_COD(null, -1),
    SPAWNER_COW(null, -1),
    SPAWNER_CREEPER(null, -1),
    SPAWNER_DOLPHIN(null, -1),
    SPAWNER_DONKEY(null, -1),
    SPAWNER_DROWNED(null, -1),
    SPAWNER_ELDER_GUARDIAN(null, -1),
    SPAWNER_ENDER_DRAGON(null, -1),
    SPAWNER_ENDERMAN(null, -1),
    SPAWNER_ENDERMITE(null, -1),
    SPAWNER_EVOKER(null, -1),
    SPAWNER_GHAST(null, -1),
    SPAWNER_GIANT(null, -1),
    SPAWNER_GUARDIAN(null, -1),
    SPAWNER_HORSE(null, -1),
    SPAWNER_HUSK(null, -1),
    SPAWNER_ILLUSIONER(null, -1),
    SPAWNER_IRON_GOLEM(null, -1),
    SPAWNER_LLAMA(null, -1),
    SPAWNER_MAGMA_CUBE(null, -1),
    SPAWNER_MULE(null, -1),
    SPAWNER_MUSHROOM_COW(null, -1),
    SPAWNER_OCELOT(null, -1),
    SPAWNER_PARROT(null, -1),
    SPAWNER_PHANTOM(null, -1),
    SPAWNER_PIG(null, -1),
    SPAWNER_PIG_ZOMBIE(null, -1),
    SPAWNER_POLAR_BEAR(null, -1),
    SPAWNER_PUFFERFISH(null, -1),
    SPAWNER_RABBIT(null, -1),
    SPAWNER_SALMON(null, -1),
    SPAWNER_SHEEP(null, -1),
    SPAWNER_SHULKER(null, -1),
    SPAWNER_SILVERFISH(null, -1),
    SPAWNER_SKELETON(null, -1),
    SPAWNER_SKELETON_HORSE(null, -1),
    SPAWNER_SLIME(null, -1),
    SPAWNER_SNOWMAN(null, -1),
    SPAWNER_SPIDER(null, -1),
    SPAWNER_SQUID(null, -1),
    SPAWNER_STRAY(null, -1),
    SPAWNER_TROPICAL_FISH(null, -1),
    SPAWNER_TURTLE(null, -1),
    SPAWNER_VEX(null, -1),
    SPAWNER_VILLAGER(null, -1),
    SPAWNER_VINDICATOR(null, -1),
    SPAWNER_WITCH(null, -1),
    SPAWNER_WITHER(null, -1),
    SPAWNER_WITHER_SKELETON(null, -1),
    SPAWNER_WOLF(null, -1),
    SPAWNER_ZOMBIE(null, -1),
    SPAWNER_ZOMBIE_HORSE(null, -1),
    SPAWNER_ZOMBIE_VILLAGER(null, -1);

    private static final Set<CompatibleSpawners> ALL = Collections.unmodifiableSet(EnumSet.allOf(CompatibleSpawners.class));

    static int newV = -1;
    private static Map<String, CompatibleSpawners> cachedSearch = new HashMap<>();
    String old13Mat;
    String old12Mat;
    int data;
    boolean is13Plusonly;
    private Material cachedMaterial;
    private boolean isMaterialParsed = false;
    private String actualMaterials;

    CompatibleSpawners(String old13Mat, String old12Mat, int data, boolean is13Plusonly) {
        this.old13Mat = old13Mat;
        this.old12Mat = old12Mat;
        this.data = data;
        this.is13Plusonly = is13Plusonly;
    }

    CompatibleSpawners(String old12Mat, int data) {
        this(null, old12Mat, data, false);
    }

    CompatibleSpawners(String old12Mat, int data, boolean is13Plusonly) {
        this(null, old12Mat, data, is13Plusonly);
    }

    public CompatibleSpawners getActualMaterials() {
        return actualMaterials == null ? null : CompatibleSpawners.valueOf(actualMaterials);
    }

    public static boolean isNewVersion() {
        if (newV == 0) return false;
        if (newV == 1) return true;
        Material mat = Material.matchMaterial("RED_WOOL");
        if (mat != null) {
            newV = 1;
            return true;
        }
        newV = 0;
        return false;
    }

    public static CompatibleSpawners requestMaterials(String name, byte data) {

        final String blockName = name.toUpperCase() + "," + data;
        CompatibleSpawners cached = cachedSearch.get(blockName);

        if (cached != null) return cached;

        CompatibleSpawners pmat = internalRequestMaterials(name, data);
        if (pmat != null || data == 0) {
            cachedSearch.put(blockName, pmat);
            return pmat;
        }

        pmat = internalRequestMaterials(name, (byte) 0);
        cachedSearch.put(blockName, pmat);
        return pmat;
    }

    private static CompatibleSpawners internalRequestMaterials(String name, byte data) {
        CompatibleSpawners pmat = null;

        // Try 1.13+ names
        for (CompatibleSpawners mat : ALL) {
            if (name.equalsIgnoreCase(mat.name())) {
                if (pmat == null) pmat = mat;

                final CompatibleSpawners actual = mat.getActualMaterials();

                if (actual != null && mat.is13Plusonly) return actual;
                if (((byte) mat.data) == data) return mat;
            }
        }

        // Try 1.12- names
        for (CompatibleSpawners mat : ALL) {
            if (name.equalsIgnoreCase(mat.old12Mat)) {
                if (pmat == null) pmat = mat;

                final CompatibleSpawners actual = mat.getActualMaterials();

                if (actual != null && mat.is13Plusonly) return actual;
                if (((byte) mat.data) == data) return mat;
            }
        }

        return pmat;
    }

    public static CompatibleSpawners getSpawner(EntityType spawnerType) {
        return fromString("SPAWNER_" + spawnerType.name());
    }

    public static CompatibleSpawners fromString(String key) {
        CompatibleSpawners xmat = null;
        try {
            xmat = CompatibleSpawners.valueOf(key);
            return xmat;
        } catch (IllegalArgumentException e) {
            String[] split = key.split(":");
            if (split.length == 1) {
                xmat = requestMaterials(key, (byte) 0);
            } else {
                xmat = requestMaterials(split[0], (byte) Integer.parseInt(split[1]));
            }
            return xmat;
        }
    }

    public static CompatibleSpawners getMaterials(Material material, byte data) {
        if (NMSUtil.getVersionNumber() > 12) {
            return fromString(material.name());
        } else {
            return requestMaterials(material.name(), data);
        }
    }

    public boolean isAvailable() {
        if (this.isSpawner() && this != CompatibleSpawners.SPAWNER) {
            String spawnerType = this.name().replace("SPAWNER_", "");
            for (EntityType entityType : EntityType.values())
                if (entityType.name().equalsIgnoreCase(spawnerType)) return true;
            return false;
        }

        return isNewVersion() || !this.is13Plusonly;
    }

    public boolean isSpawner() {
        return this.name().startsWith("SPAWNER");
    }

    public CompatibleSpawners fromMaterial(Material mat) {
        try {
            return CompatibleSpawners.valueOf(mat.toString());
        } catch (IllegalArgumentException e) {
            for (CompatibleSpawners xmat : ALL) {
                if (xmat.old12Mat.equalsIgnoreCase(mat.toString())) {
                    return xmat;
                }
            }
        }
        return null;
    }

    public Material getMaterial() {
        if (this.cachedMaterial != null || this.isMaterialParsed) return this.cachedMaterial;

        if (this.isSpawner() && this != CompatibleSpawners.SPAWNER) {
            this.cachedMaterial = CompatibleMaterial.SPAWNER.getMaterial();
            return this.cachedMaterial;
        }

        this.cachedMaterial = Material.matchMaterial(old12Mat);
        this.isMaterialParsed = true;
        return this.cachedMaterial;
    }
}