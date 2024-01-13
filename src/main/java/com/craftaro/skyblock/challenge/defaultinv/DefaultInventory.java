package com.craftaro.skyblock.challenge.defaultinv;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.skyblock.SkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultInventory {
    private final Item defaultItem = new Item(new ItemStack(Material.AIR));
    private final int size;
    private final Item[][] items;

    public DefaultInventory(SkyBlock plugin) {
        FileConfiguration configLoad = plugin.getChallenges();
        this.size = configLoad.getInt("inventory.size");
        this.items = new Item[9][this.size];
        ConfigurationSection section = configLoad.getConfigurationSection("inventory.items");
        if (section == null)
        // No items
        {
            return;
        }
        for (String key : section.getKeys(false)) {
            String k = "inventory.items." + key;
            int row = configLoad.getInt(k + ".row");
            int col = configLoad.getInt(k + ".col");
            String strItem = configLoad.getString(k + ".item");
            int amount = configLoad.getInt(k + ".amount");
            String name = plugin.formatText(configLoad.getString(k + ".name"));
            List<String> lore = toColor(configLoad.getStringList(k + ".lore"));
            int redirect = configLoad.getInt(k + ".redirect");
            Optional<XMaterial> material = CompatibleMaterial.getMaterial(strItem);
            if (!material.isPresent() || CompatibleMaterial.isAir(material.get())) {
                Bukkit.getLogger().warning("Item " + strItem + " is not a Material");
                continue;
            }

            ItemStack is = material.get().parseItem();
            is.setAmount(amount);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(name);
            im.setLore(lore);
            is.setItemMeta(im);
            this.items[col - 1][row - 1] = new Item(is, redirect);
        }
    }

    private List<String> toColor(List<String> list) {
        List<String> copy = new ArrayList<>();
        if (list == null) {
            return copy;
        }
        for (String str : list) {
            copy.add(ChatColor.translateAlternateColorCodes('&', str));
        }
        return copy;
    }

    public Item get(int row, int col) {
        Item is = this.items[col][row];
        if (is == null) {
            is = this.defaultItem;
        }
        // Clone it
        return new Item(is.getItemStack().clone(), is.getRedirect());
    }

    public int getSize() {
        return this.size;
    }
}
