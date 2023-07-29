package com.songoda.skyblock.utils.item;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.placeholder.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class nInventoryUtil {
    private final Player player;
    private Listener listener;

    private Inventory inv;

    private String title;
    private int size = 9;
    private InventoryType type;
    private final Map<Integer, ItemStack> items = new HashMap<>();

    public nInventoryUtil(Player player, final ClickEventHandler handler) {
        this.player = player;

        if (handler != null) {
            this.listener = new Listener() {
                @EventHandler
                public void onInventoryClick(InventoryClickEvent event) {
                    if (!(event.getWhoClicked() instanceof Player)) {
                        return;
                    }

                    if (nInventoryUtil.this.inv != null && event.getInventory().equals(nInventoryUtil.this.inv)) {
                        event.setCancelled(true);

                        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                            return;
                        }

                        ClickEvent clickEvent = new ClickEvent(event.getClick(), event.getSlot(), event.getCurrentItem());
                        handler.onClick(clickEvent);

                        if (!clickEvent.getCancelled()) {
                            event.setCancelled(false);
                        }

                        if (clickEvent.getWillClose()) {
                            event.getWhoClicked().closeInventory();
                        }

                        if (clickEvent.getWillDestroy()) {
                            destroy();
                        }
                    }
                }

                @EventHandler
                public void onInventoryClose(InventoryCloseEvent event) {
                    if (!(event.getPlayer() instanceof Player)) {
                        return;
                    }

                    Inventory inv = event.getInventory();

                    if (inv.equals(nInventoryUtil.this.inv)) {
                        inv.clear();
                        destroy();
                    }
                }

                @EventHandler
                public void onPlayerQuit(PlayerQuitEvent event) {
                    if (event.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                        destroy();
                    }
                }
            };

            Bukkit.getPluginManager().registerEvents(this.listener, SkyBlock.getPlugin(SkyBlock.class));
        }
    }

    public void addItem(Item item, int... slots) {
        Arrays.stream(slots).forEachOrdered(slotList -> this.items.put(slotList, item.prepareItem()));
    }

    public void addItemStack(ItemStack is, int... slots) {
        Arrays.stream(slots).forEachOrdered(slotList -> this.items.put(slotList, is));
    }

    public Map<Integer, ItemStack> getItems() {
        return this.items;
    }

    public Item createItem(ItemStack is, String itemDisplayname, List<String> itemLore, Placeholder[] placeholders, Enchantment[] itemEnchantments, ItemFlag[] itemFlags) {
        return new Item(is, itemDisplayname, itemLore, placeholders, itemEnchantments, itemFlags);
    }

    public void open() {
        createInventory();
        this.player.openInventory(this.inv);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(InventoryType type) {
        this.type = type;
    }

    public void setRows(int rows) {
        if (rows > 6 || rows < 0) {
            this.size = 9;
            return;
        }

        this.size = rows * 9;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void createInventory() {
        if (this.type == null) {
            if (this.title == null) {
                this.inv = Bukkit.createInventory(null, this.size);
            } else {
                this.inv = Bukkit.createInventory(null, this.size, this.title);
            }
        } else {
            if (this.title == null) {
                this.inv = Bukkit.createInventory(null, this.type);
            } else {
                this.inv = Bukkit.createInventory(null, this.type, this.title);
            }
        }

        this.items.forEach((key, value) -> this.inv.setItem(key, value));
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public void setInventory(Inventory inv) {
        this.inv = inv;
    }

    public void destroy() {
        if (this.listener != null) {
            HandlerList.unregisterAll(this.listener);
        }

        this.title = null;
        this.type = null;
        this.inv = null;
        this.items.clear();

        this.listener = null;
    }

    public interface ClickEventHandler {
        void onClick(ClickEvent event);
    }

    public static class Item {
        private final ItemStack is;
        private final String itemDisplayname;
        private List<String> itemLore;
        private final Placeholder[] placeholders;
        private final Enchantment[] itemEnchantments;
        private final ItemFlag[] itemFlags;

        public Item(ItemStack is, String itemDisplayname, List<String> itemLore, Placeholder[] placeholders, Enchantment[] itemEnchantments, ItemFlag[] itemFlags) {
            this.is = is;
            this.itemDisplayname = itemDisplayname == null ? null : ChatColor.translateAlternateColorCodes('&', itemDisplayname);
            this.itemLore = itemLore;
            this.placeholders = placeholders;
            this.itemEnchantments = itemEnchantments;
            this.itemFlags = itemFlags;
        }

        public void setLore() {
            if (this.itemLore != null) {
                List<String> formattedItemLore = new ArrayList<>(this.itemLore.size());

                for (String itemLoreList : this.itemLore) {
                    if (this.placeholders != null) {
                        for (Placeholder placeholderList : this.placeholders) {
                            if (itemLoreList.contains(placeholderList.getPlaceholder())) {
                                itemLoreList = ChatColor.translateAlternateColorCodes('&', itemLoreList.replace(placeholderList.getPlaceholder(), placeholderList.getResult()));
                            }
                        }
                    }

                    formattedItemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
                }

                this.itemLore.clear();
                this.itemLore = formattedItemLore;
            }
        }

        public void setItemMeta() {
            ItemMeta im = this.is.hasItemMeta() ? this.is.getItemMeta() : Bukkit.getItemFactory().getItemMeta(this.is.getType());
            im.setDisplayName(this.itemDisplayname);
            im.setLore(this.itemLore);

            if (this.itemFlags != null) {
                im.addItemFlags(this.itemFlags);
            }

            if (this.itemEnchantments != null) {
                for (Enchantment itemEnchantmentList : this.itemEnchantments) {
                    im.addEnchant(itemEnchantmentList, 1, true);
                }
            }

            this.is.setItemMeta(im);
        }

        public ItemStack prepareItem() {
            setLore();
            setItemMeta();

            return this.is;
        }
    }

    public static class ClickEvent {
        private final ClickType click;
        private final int slot;
        private final ItemStack is;

        private boolean close = true;
        private boolean destroy = true;
        private boolean cancelled = true;

        public ClickEvent(ClickType click, int slot, ItemStack is) {
            this.click = click;
            this.slot = slot;
            this.is = is;
        }

        public ClickType getClick() {
            return this.click;
        }

        public int getSlot() {
            return this.slot;
        }

        public ItemStack getItem() {
            return this.is;
        }

        public boolean getWillClose() {
            return this.close;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public boolean getWillDestroy() {
            return this.destroy;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }

        public boolean getCancelled() {
            return this.cancelled;
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }
}
