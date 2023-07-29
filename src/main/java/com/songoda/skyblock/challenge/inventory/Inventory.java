/**
 *
 */
package com.songoda.skyblock.challenge.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Inventory {
    public static final String TICK = "tick";
    private final HashMap<String, Object> values;
    private final Player player;
    private final InventoryProvider inventoryProvider;
    private final int size;

    private final List<Integer> excluseCases;
    private final ClickableItem[] items;
    private final org.bukkit.inventory.Inventory bukkitInventory;

    public Inventory(Player player, InventoryProvider inventoryProvider, Consumer<Inventory> params) {
        this.values = new HashMap<>();
        this.player = player;
        this.inventoryProvider = inventoryProvider;
        params.accept(this);
        this.excluseCases = inventoryProvider.excluseCases(this);
        this.size = inventoryProvider.rows(this);
        this.items = new ClickableItem[9 * this.size];
        this.bukkitInventory = Bukkit.createInventory(player, this.size * 9, inventoryProvider.title(this));
        put(TICK, 0);
    }

    public Player getPlayer() {
        return this.player;
    }

    public InventoryProvider getInventoryProvider() {
        return this.inventoryProvider;
    }

    public org.bukkit.inventory.Inventory getBukkitInventory() {
        return this.bukkitInventory;
    }

    public int getRows() {
        return this.size;
    }

    public List<Integer> getExcludeCases() {
        return this.excluseCases;
    }

    public void set(int col, int row, ClickableItem item) {
        if (col < 1 || col > 9) {
            throw new IllegalArgumentException("col must be between 1 and 9 but is " + col);
        }
        if (row < 1 || row > getRows()) {
            throw new IllegalArgumentException("row must be between 1 and " + getRows());
        }

        set(locToPos(col, row), item);
    }

    public void set(int pos, ClickableItem item) {
        if (pos < 0 || pos > this.size * 9 - 1) {
            throw new IllegalArgumentException("pos must be between 0 and " + (this.size * 9 - 1) + ", but is " + pos);
        }
        this.items[pos] = item;
        this.bukkitInventory.setItem(pos, item == null ? null : item.getItemStack());
    }

    /**
     * Update the item but keep the old event
     */
    public void updateItem(int col, int row, ItemStack is) {
        if (col < 1 || col > 9) {
            throw new IllegalArgumentException("col must be between 1 and 9 but is " + col);
        }
        if (row < 1 || row > getRows()) {
            throw new IllegalArgumentException("row must be between 1 and " + getRows());
        }
        updateItem(locToPos(col, row), is);
    }

    /**
     * Update the item but keep the old event
     */
    public void updateItem(int pos, ItemStack is) {
        if (pos < 0 || pos > this.size * 9 - 1) {
            throw new IllegalArgumentException("pos must be between 0 and " + (this.size * 9 - 1) + ", but is " + pos);
        }
        ClickableItem item = this.items[pos];
        if (item == null) {
            item = ClickableItem.of(is);
        } else {
            item = ClickableItem.of(is, item.getEvent());
        }
        set(pos, item);
    }

    public void fill(ClickableItem item) {
        for (int row = 0; row < this.size; row++) {
            for (int col = 0; col < 9; col++) {
                set(row * 9 + col, item);
            }
        }
    }

    public void rectangle(int col, int row, int width, int height, ClickableItem item) {
        if (col < 1 || col > 9) {
            throw new IllegalArgumentException("col must be between 1 and 9");
        }
        if (row < 1 || row > getRows()) {
            throw new IllegalArgumentException("row must be between 1 and the maximum number of rows, but is " + row);
        }
        // 10 - col because width starts with 1 and not 0
        if (width < 1 || width > 10 - col) {
            throw new IllegalArgumentException("The width must be between 1 and " + (10 - col) + ", but is " + width);
        }
        if (height < 1 || height > getRows() + 1 - row) {
            throw new IllegalArgumentException("The height must be between 1 and " + (getRows() + 1 - row));
        }
        rectangle(locToPos(col, row), width, height, item);
    }

    public void rectangle(int pos, int width, int height, ClickableItem item) {
        if (pos < 0 || pos > this.size * 9) {
            throw new IllegalArgumentException("pos must be between 0 and " + (this.size * 9) + ", but is " + pos);
        }
        int[] colRow = posToLoc(pos);
        int row = colRow[0];
        int col = colRow[1];
        if (col < 1 || col > 9) {
            throw new IllegalArgumentException("col must be between 1 and 9, but is " + col);
        }
        if (row < 1 || row > 6) {
            throw new IllegalArgumentException("row must be between 1 and the maximum number of rows, but is " + row);
        }
        // 10 - col because width starts with 1 and not 0
        if (width < 1 || width > 10 - col) {
            throw new IllegalArgumentException("The width must be between 1 and " + (10 - col) + ", but is " + width);
        }
        if (height < 1 || height > getRows() + 1 - row) {
            throw new IllegalArgumentException(
                    "The height must be between 1 and " + (getRows() + 1 - row) + ", but is " + height);
        }
        for (int i = col; i < col + width; i++) {
            for (int j = row; j < row + height; j++)
            // Around
            {
                if (i == col || i == col + width - 1 || j == row || j == row + height - 1) {
                    set(i, j, item);
                }
            }
        }
    }

    public void fillRectangle(int col, int row, int width, int height, ClickableItem item) {
        if (col < 1 || col > 9) {
            throw new IllegalArgumentException("col must be between 1 and 9, but is " + col);
        }
        if (row < 1 || row > 6) {
            throw new IllegalArgumentException("row must be between 1 and the maximum number of rows, but is " + row);
        }
        // 10 - col because width starts with 1 and not 0
        if (width < 1 || width > 10 - col) {
            throw new IllegalArgumentException("The width must be between 1 and " + (10 - col) + ", but is " + width);
        }
        if (height < 1 || height > getRows() + 1 - row) {
            throw new IllegalArgumentException(
                    "The height must be between 1 and " + (getRows() + 1 - row) + ", but is " + height);
        }

        fillRectangle(locToPos(col, row), width, height, item);
    }

    public void fillRectangle(int pos, int width, int height, ClickableItem item) {
        if (pos < 0 || pos > this.size * 9) {
            throw new IllegalArgumentException("pos must be between 0 and " + (this.size * 9) + ", but is " + pos);
        }
        int[] colRow = posToLoc(pos);
        int row = colRow[0];
        int col = colRow[1];

        if (col < 1 || col > 9) {
            throw new IllegalArgumentException("col must be between 1 and 9, but is " + col);
        }
        if (row < 1 || row > 6) {
            throw new IllegalArgumentException("row must be between 1 and the maximum number of rows, but is " + row);
        }
        // 10 - col because width starts with 1 and not 0
        if (width < 1 || width > 10 - col) {
            throw new IllegalArgumentException("The width must be between 1 and " + (10 - col) + ", but is " + width);
        }
        if (height < 1 || height > getRows() + 1 - row) {
            throw new IllegalArgumentException(
                    "The height must be between 1 and " + (getRows() + 1 - row) + ", but is " + height);
        }
        for (int i = col; i < col + width; i++) {
            for (int j = row; j < row + height; j++) {
                set(i, j, item);
            }
        }
    }

    public void open() {
        this.player.openInventory(this.bukkitInventory);
    }

    public void handler(InventoryClickEvent e) {
        int pos = e.getSlot();
        if (pos < 0 || pos > this.items.length) {
            return;
        }
        ClickableItem item = this.items[pos];
        if (item == null) {
            // Nothing to do
            return;
        }
        item.run(e);
    }

    public void put(String key, Object value) {
        this.values.put(key, value);
    }

    public Object get(String key) {
        return this.values.get(key);
    }

    public int[] posToLoc(int pos) {
        return new int[]{(pos / 9) + 1, (pos % 9) + 1};
    }

    public int locToPos(int col, int row) {
        return (row - 1) * 9 + (col - 1);
    }
}
