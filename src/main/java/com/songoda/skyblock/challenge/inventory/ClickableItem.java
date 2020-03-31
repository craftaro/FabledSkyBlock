/**
 * 
 */
package com.songoda.skyblock.challenge.inventory;

import java.util.function.Consumer;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ClickableItem {
	private ItemStack item;
	private Consumer<InventoryClickEvent> event;

	private ClickableItem(ItemStack item, Consumer<InventoryClickEvent> event) {
		this.item = item;
		this.event = event;
	}

	public void run(InventoryClickEvent e) {
		event.accept(e);
	}

	public ItemStack getItemStack() {
		return item;
	}
	
	public Consumer<InventoryClickEvent> getEvent() {
		return event;
	}

	public static ClickableItem of(ItemStack is) {
		return new ClickableItem(is, e -> {
		});
	}

	public static ClickableItem of(ItemStack is, Consumer<InventoryClickEvent> event) {
		return new ClickableItem(is, event);
	}
}
