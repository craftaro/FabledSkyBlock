package com.songoda.skyblock.utils.item;

import com.songoda.skyblock.utils.version.Materials;
import org.bukkit.Material;

public class MaterialUtil {

    public static Material correctMaterial(Material material) {
        if (material == Material.REDSTONE_WIRE) {
            material = Material.REDSTONE;
        } else if (material == Materials.LEGACY_DOUBLE_SLAB.getPostMaterial()) {
            material = Materials.SMOOTH_STONE.parseMaterial();
        } else if (material == Materials.FERN.parseMaterial()) {
            material = Material.GRASS;
        } else if (material == Materials.LEGACY_NETHER_WARTS.getPostMaterial()) {
            material = Materials.LEGACY_NETHER_STALK.getPostMaterial();
        } else if (material == Materials.LEGACY_SIGN_POST.getPostMaterial() || material == Materials.OAK_WALL_SIGN.parseMaterial()) {
            material = Materials.OAK_SIGN.parseMaterial();
        } else if (material == Materials.BIRCH_WALL_SIGN.parseMaterial()) {
            material = Materials.BIRCH_SIGN.parseMaterial();
        } else if (material == Materials.SPRUCE_WALL_SIGN.parseMaterial()) {
            material = Materials.SPRUCE_SIGN.parseMaterial();
        } else if (material == Materials.JUNGLE_WALL_SIGN.parseMaterial()) {
            material = Materials.JUNGLE_SIGN.parseMaterial();
        } else if (material == Materials.ACACIA_WALL_SIGN.parseMaterial()) {
            material = Materials.ACACIA_SIGN.parseMaterial();
        } else if (material == Materials.DARK_OAK_WALL_SIGN.parseMaterial()) {
            material = Materials.DARK_OAK_SIGN.parseMaterial();
        } else if (material == Materials.LEGACY_SUGAR_CANE_BLOCK.getPostMaterial()) {
            material = Material.SUGAR_CANE;
        } else if (material == Material.TRIPWIRE) {
            material = Material.STRING;
        } else if (material == Material.FLOWER_POT) {
            material = Materials.LEGACY_FLOWER_POT_ITEM.getPostMaterial();
        } else if (material.name().startsWith("POTTED_")) {
            material = Material.FLOWER_POT;
        } else if (material == Materials.LEGACY_IRON_DOOR_BLOCK.getPostMaterial()) {
            material = Material.IRON_DOOR;
        } else if (material == Material.CAULDRON) {
            material = Materials.LEGACY_CAULDRON_ITEM.getPostMaterial();
        } else if (material == Material.BREWING_STAND) {
            material = Materials.LEGACY_BREWING_STAND.getPostMaterial();
        } else if (material.name().equals("BED_BLOCK")) {
            material = Materials.RED_BED.getPostMaterial();
        } else if (material == Materials.SWEET_BERRY_BUSH.parseMaterial()) {
            material = Materials.SWEET_BERRIES.parseMaterial();
        } else if (material == Materials.BAMBOO_SAPLING.parseMaterial()) {
            material = Materials.BAMBOO.parseMaterial();
        }

        return material;
    }

    public static Material getMaterial(int NMSVersion, int blockVersion, String material, int data) {
        if (NMSVersion > 12) {
            if (blockVersion > 12) {
                return Material.valueOf(material);
            } else {
                return Materials.requestMaterials(material, (byte) data).getPostMaterial();
            }
        } else {
            try {
                if (blockVersion > 12) {
                    return Materials.fromString(material).parseMaterial();
                } else {
                    return Material.valueOf(material);
                }
            } catch (Exception e) {
                return Material.STONE;
            }
        }
    }
}
