package me.goodandevil.skyblock.utils.version;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

/**
 * A Biome wrapper for supporting Biomes in 1.8-1.13+
 */
public enum SBiome {
    
    BADLANDS(true, Materials.DEAD_BUSH),
    COLD_OCEAN(true, Materials.ICE),
    DARK_FOREST("ROOFED_FOREST", Materials.DARK_OAK_SAPLING),
    DESERT(Materials.SAND),
    FOREST(Materials.FERN),
    JUNGLE(Materials.VINE),
    MOUNTAINS("EXTREME_HILLS", Materials.EMERALD_ORE),
    MUSHROOM_FIELDS("MUSHROOM_ISLAND", Materials.RED_MUSHROOM),
    NETHER("HELL", Materials.NETHERRACK),
    PLAINS(Materials.SUNFLOWER),
    RIVER(Materials.COD),
    SAVANNA(Materials.ACACIA_SAPLING),
    SNOWY_BEACH("COLD_BEACH", Materials.SNOWBALL),
    SWAMP("SWAMPLAND", Materials.SLIME_BALL),
    TAIGA(Materials.SPRUCE_SAPLING),
    THE_END(true, Materials.END_STONE),
    THE_VOID("SKY", Materials.OBSIDIAN),
    WARM_OCEAN(true, Materials.TROPICAL_FISH);
    
    private static boolean isPostVersion = NMSUtil.getVersionNumber() >= 13;
    
    private String legacyName;
    private boolean isPost13;
    private Materials guiIcon;
    
    SBiome(Materials guiIcon) {
        this(null, false, guiIcon);
    }

    SBiome(String legacyName, Materials guiIcon) {
        this(legacyName, false, guiIcon);
    }
    
    SBiome(boolean isPost13, Materials guiIcon) {
        this(null, isPost13, guiIcon);
    }
    
    SBiome(String legacyName, boolean is13only, Materials guiIcon) {
        this.legacyName = legacyName;
        this.isPost13 = is13only;
        this.guiIcon = guiIcon;
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
        if (!this.isAvailable())
            return null;
        if (isPostVersion || this.legacyName == null)
            return Biome.valueOf(this.name());
        return Biome.valueOf(this.legacyName);
    }
    
    /**
     * Gets the name of the Biome formatted for a Gui
     * 
     * @return The formatted Biome name
     */
    public String getFormattedBiomeName() {
        if (!this.isAvailable())
            return null;
        return WordUtils.capitalizeFully(this.getBiome().name().replaceAll("_", " "));
    }
    
    /**
     * Gets the Gui icon that represents this Biome
     * 
     * @return The Gui icon that represents this Biome
     */
    public ItemStack getGuiIcon() {
        if (!this.isAvailable())
            return null;
        return this.guiIcon.parseItem();
    }
    
    /**
     * Gets an SBiome based on its Gui icon
     * 
     * @return An SBiome with a matching Gui icon
     */
    @SuppressWarnings("deprecation")
    public static SBiome getFromGuiIcon(Material material, byte data) {
        for (SBiome biome : values())
            if (biome.isAvailable() && biome.getGuiIcon().getType().equals(material) && (isPostVersion || biome.getGuiIcon().getData().getData() == data))
                return biome;
        return null;
    }

}
