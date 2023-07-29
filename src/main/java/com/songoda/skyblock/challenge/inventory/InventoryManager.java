/**
 *
 */
package com.songoda.skyblock.challenge.inventory;

import com.songoda.skyblock.SkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class InventoryManager implements Listener {
    private final SkyBlock plugin;
    private final HashMap<UUID, Inventory> inventories;

    public InventoryManager(SkyBlock plugin) {
        this.plugin = plugin;
        this.inventories = new HashMap<>();
    }

    public void init() {
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
        int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            if (this.inventories.isEmpty()) {
                return;
            }
            for (Inventory inv : this.inventories.values()) {
                int tick = 0;
                Object currentTick = inv.get(Inventory.TICK);
                if (currentTick instanceof Integer) {
                    tick = Integer.parseInt(currentTick.toString());
                }
                inv.put(Inventory.TICK, tick + 1);
                inv.getInventoryProvider().update(inv);
            }
        }, 1, 1);
    }

    public Inventory openInventory(InventoryProvider provider, Player p) {
        return openInventory(provider, p, null);
    }

    public Inventory openInventory(InventoryProvider provider, Player p, Consumer<Inventory> params) {
        Inventory inv = new Inventory(p, provider, params);
        inv.getInventoryProvider().init(inv);
        this.inventories.put(inv.getPlayer().getUniqueId(), inv);
        inv.open();
        return inv;
    }

    public Inventory getInventory(Player p) {
        return this.inventories.get(p.getUniqueId());
    }

    public boolean hasInventoryOpened(Player p) {
        return this.inventories.containsKey(p.getUniqueId());
    }

    public void closeInventory(Player p) {
        p.closeInventory();
    }

    /**
     * Close all inventories
     */
    public void closeInventories() {
        // New ArrayList to prevent CurrentModificationException
        for (Inventory inv : new ArrayList<>(this.inventories.values())) {
            closeInventory(inv.getPlayer());
        }
        this.inventories.clear();
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent e) {
        org.bukkit.inventory.Inventory clickedInventory = e.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        Inventory inv = getInventory(p);
        if (inv == null) {
            return;
        }
        if (e.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            e.setCancelled(true);
            return;
        }
        if (e.getRawSlot() >= e.getInventory().getSize() && !e.isShiftClick()) {
            return;
        }
        if (inv.getExcludeCases() == null || !inv.getExcludeCases().contains(e.getSlot())) {
            e.setCancelled(true);
        }
        if (!inv.getBukkitInventory().equals(clickedInventory)) {
            // The player doesn't click on the correct inventory
            return;
        }
        inv.handler(e);
    }

    @EventHandler
    public void onPlayerInventoryDrag(InventoryDragEvent e) {
        if (!this.inventories.containsKey(e.getWhoClicked().getUniqueId())) {
            return;
        }
        e.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInventoryClose(InventoryCloseEvent e) {
        if (!this.inventories.containsKey(e.getPlayer().getUniqueId())) {
            return;
        }
        org.bukkit.inventory.Inventory invopen = e.getInventory();
        Inventory inv = this.inventories.get(e.getPlayer().getUniqueId());
        if (!inv.getBukkitInventory().equals(invopen)) {
            return;
        }
        inv.getInventoryProvider().onClose(e, inv);
        this.inventories.remove(e.getPlayer().getUniqueId());
    }
}
