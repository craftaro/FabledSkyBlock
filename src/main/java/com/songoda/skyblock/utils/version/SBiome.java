package com.songoda.skyblock.utils.version;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.ServerVersion;
import com.songoda.skyblock.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * A Biome wrapper for supporting Biomes in 1.8-1.13+
 */

//TODO: Use CompatibleBiome
public enum SBiome {
    BADLANDS(true, CompatibleMaterial.DEAD_BUSH),
    COLD_OCEAN(true, CompatibleMaterial.ICE),
    DARK_FOREST("ROOFED_FOREST", CompatibleMaterial.DARK_OAK_SAPLING),
    DESERT(CompatibleMaterial.SAND),
    FOREST(CompatibleMaterial.FERN),
    JUNGLE(CompatibleMaterial.VINE),
    MOUNTAINS("EXTREME_HILLS", CompatibleMaterial.EMERALD_ORE),
    MUSHROOM_FIELDS("MUSHROOM_ISLAND", CompatibleMaterial.RED_MUSHROOM),
    NETHER("HELL", CompatibleMaterial.NETHERRACK),
    PLAINS(CompatibleMaterial.SUNFLOWER),
    RIVER(CompatibleMaterial.COD),
    SAVANNA(CompatibleMaterial.ACACIA_SAPLING),
    SNOWY_BEACH("COLD_BEACH", CompatibleMaterial.SNOWBALL),
    SWAMP("SWAMPLAND", CompatibleMaterial.SLIME_BALL),
    TAIGA(CompatibleMaterial.SPRUCE_SAPLING),
    THE_END(true, CompatibleMaterial.END_STONE),
    THE_VOID("SKY", CompatibleMaterial.OBSIDIAN),
    WARM_OCEAN(true, CompatibleMaterial.TROPICAL_FISH);

    private static final boolean isPostVersion = ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13);

    private final String legacyName;
    private final boolean isPost13;
    private final CompatibleMaterial guiIcon;

    SBiome(CompatibleMaterial guiIcon) {
        this(null, false, guiIcon);
    }

    SBiome(String legacyName, CompatibleMaterial guiIcon) {
        this(legacyName, false, guiIcon);
    }

    SBiome(boolean isPost13, CompatibleMaterial guiIcon) {
        this(null, isPost13, guiIcon);
    }

    SBiome(String legacyName, boolean is13only, CompatibleMaterial guiIcon) {
        this.legacyName = legacyName;
        this.isPost13 = is13only;
        this.guiIcon = guiIcon;
    }

    /**
     * Gets an SBiome based on its Gui icon
     *
     * @return An SBiome with a matching Gui icon
     */
    @SuppressWarnings("deprecation")
    public static SBiome getFromGuiIcon(Material material, byte data) {
        return Arrays.stream(values()).filter(biome -> {
            return biome.isAvailable() &&
                    biome.getGuiIcon().getType() == material &&
                    (isPostVersion || biome.getGuiIcon().getData().getData() == data);
        }).findFirst().orElse(null);
    }

    /**
     * Checks if this Biome can be used in the current server version
     *
     * @return True if the current server version supports this Biome, otherwise false
     */
    public boolean isAvailable() {
        return !this.isPost13 || isPostVersion;
    }

    /**
     * Gets an SBiome as it's Bukkit Biome counterpart
     *
     * @return The Biome this SBiome represents, or null if it is not available in this server version
     */
    public Biome getBiome() {
        return !this.isAvailable() ? null : isPostVersion || this.legacyName == null ? Biome.valueOf(this.name()) : Biome.valueOf(this.legacyName);
    }

    /**
     * Gets the name of the Biome formatted for a Gui
     *
     * @return The formatted Biome name
     */
    public String getFormattedBiomeName() {
        return !this.isAvailable() ? null : StringUtil.capitalizeWord(this.getBiome().name().replaceAll("_", " "));
    }

    /**
     * Gets the Gui icon that represents this Biome
     *
     * @return The Gui icon that represents this Biome
     */
    public ItemStack getGuiIcon() {
        return !this.isAvailable() ? null : this.guiIcon.getItem();
    }
}
