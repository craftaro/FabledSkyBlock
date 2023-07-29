package com.songoda.skyblock.challenge.challenge;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class ItemChallenge {
    private Challenge challenge;
    private final boolean show;
    private final int row;
    private final int col;
    private final CompatibleMaterial type;
    private final int amount;
    private final List<String> lore;

    private final String itemTitle;

    public ItemChallenge(boolean show, int row, int col, CompatibleMaterial type, int amount, List<String> lore) {
        this.show = show;
        this.row = row;
        this.col = col;
        this.type = type;
        this.amount = amount;
        this.lore = lore;
        FileConfiguration langConfigLoad = SkyBlock.getPlugin(SkyBlock.class).getLanguage();
        this.itemTitle = langConfigLoad.getString("Challenge.Inventory.Item.Title");
    }

    public ItemStack createItem(UUID player, int amount) {
        FileConfiguration langConfigLoad = SkyBlock.getPlugin(SkyBlock.class).getLanguage();

        ItemStack is = this.type.getItem();
        is.setAmount(this.amount);
        // Air
        ItemMeta im = is.getItemMeta();
        if (im != null) {
            String maxAmount;
            if (this.challenge.getMaxTimes() == Integer.MAX_VALUE) {
                maxAmount = langConfigLoad.getString("Challenge.Inventory.Unlimited.Message");
            } else {
                maxAmount = String.valueOf(this.challenge.getMaxTimes());
            }
            im.setDisplayName(SkyBlock.getPlugin(SkyBlock.class).formatText(this.challenge.getName()).replace("%amount", Integer.toString(amount)).replace("%max", maxAmount));
            im.setLore(this.lore);
            is.setItemMeta(im);
        }
        return is;
    }

    // GETTERS & SETTERS
    public Challenge getChallenge() {
        return this.challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public boolean isShow() {
        return this.show;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public CompatibleMaterial getType() {
        return this.type;
    }

    public int getAmount() {
        return this.amount;
    }

    public List<String> getLore() {
        return this.lore;
    }
}
