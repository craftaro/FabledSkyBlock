/**
 * 
 */
package com.songoda.skyblock.challenge.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.inventory.InventoryCloseEvent;

public interface InventoryProvider {

	public String title(Inventory inv);

	public int rows(Inventory inv);

	public void init(Inventory inv);

	public void update(Inventory inv);

	public default List<Integer> excluseCases(Inventory inv) {
		return new ArrayList<>();
	}

	public default void onClose(InventoryCloseEvent e, Inventory inv) {
	}
}
