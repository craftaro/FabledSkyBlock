/**
 *
 */
package com.songoda.skyblock.challenge.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ClickableItem {
    private final ItemStack item;
    private final Consumer<InventoryClickEvent> event;

    private ClickableItem(ItemStack item, Consumer<InventoryClickEvent> event) {
        this.item = item;
        this.event = event;
    }

    public void run(InventoryClickEvent e) {
        this.event.accept(e);
    }

    public ItemStack getItemStack() {
        return this.item;
    }

    public Consumer<InventoryClickEvent> getEvent() {
        return this.event;
    }

    public static ClickableItem of(ItemStack is) {
        return new ClickableItem(is, e -> {
        });
    }

    public static ClickableItem of(ItemStack is, Consumer<InventoryClickEvent> event) {
        return new ClickableItem(is, event);
    }
}
