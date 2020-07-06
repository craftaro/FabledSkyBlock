package com.songoda.skyblock.gui.biome;

import com.songoda.core.compatibility.CompatibleBiome;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.Map;

public class BiomeIcon {
    public final CompatibleBiome biome;
    public final ItemStack displayItem;
    public final boolean permission;
    public final boolean normal;
    public final boolean nether;
    public final boolean end;

    public BiomeIcon(SkyBlock plugin, CompatibleBiome biome){
        this.biome = biome;
        FileConfiguration biomeConfig = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(),"biomes.yml")).getFileConfiguration();
        
        CompatibleMaterial tempMat = CompatibleMaterial.getMaterial(biomeConfig.getString("Biomes." + biome.name() + ".DisplayItem.Material"));
        if(tempMat == null){
            tempMat = CompatibleMaterial.STONE;
        }
        byte tempData = (byte) biomeConfig.getInt("Biomes." + biome.name() + ".DisplayItem.Data", 0);
        
        CompatibleMaterial displayMaterial = CompatibleMaterial.getMaterial(tempMat.getMaterial(), tempData);
        if(displayMaterial == null) {
            displayMaterial = CompatibleMaterial.STONE;
        }
        this.displayItem = displayMaterial.getItem();
        ItemMeta im = displayItem.getItemMeta();
        if(im != null){
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', biomeConfig.getString("Biomes." + biome.name() + ".DisplayName", biome.name())));
            displayItem.setItemMeta(im);
        }
        this.permission = biomeConfig.getBoolean("Biomes." + biome.name() + ".Permission", true);
        this.normal = biomeConfig.getBoolean("Biomes." + biome.name() + ".Normal", false);
        this.nether = biomeConfig.getBoolean("Biomes." + biome.name() + ".Nether", false);
        this.end = biomeConfig.getBoolean("Biomes." + biome.name() + ".End", false);
    }

    public BiomeIcon(CompatibleBiome biome, ItemStack displayItem, String displayName, boolean permission, boolean normal, boolean nether, boolean end){
        this.biome = biome;
        this.displayItem = displayItem;
        ItemMeta im = displayItem.getItemMeta();
        if(im != null){
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            displayItem.setItemMeta(im);
        }
        this.permission = permission;
        this.normal = normal;
        this.nether = nether;
        this.end = end;
    }
    
    public void enchant(){
        ItemMeta im = displayItem.getItemMeta();
        if(im != null){
            im.addEnchant(Enchantment.DURABILITY,1, true);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            displayItem.setItemMeta(im);
        }
    }
}
