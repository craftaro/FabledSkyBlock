package com.songoda.skyblock.challenge.defaultinv;

import org.bukkit.inventory.ItemStack;

public class Item {
    private final ItemStack itemStack;
    private final int redirect;

    public Item(ItemStack itemStack) {
        this(itemStack, 0);
    }

    public Item(ItemStack itemStack, int redirect) {
        this.itemStack = itemStack;
        this.redirect = redirect;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public int getRedirect() {
        return this.redirect;
    }
}
