/**
 *
 */
package com.songoda.skyblock.challenge.inventory;

import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;

public interface InventoryProvider {
    String title(Inventory inv);

    int rows(Inventory inv);

    void init(Inventory inv);

    void update(Inventory inv);

    default List<Integer> excluseCases(Inventory inv) {
        return new ArrayList<>();
    }

    default void onClose(InventoryCloseEvent e, Inventory inv) {
    }
}
