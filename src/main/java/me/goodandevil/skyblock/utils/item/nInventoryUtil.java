package me.goodandevil.skyblock.utils.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.placeholder.Placeholder;

public class nInventoryUtil {

	private Player player;
	private Listener listener;

	private Inventory inv;

	private String title;
	private int size = 9;
	private InventoryType type;
	private Map<Integer, ItemStack> items = new HashMap<>();

	public nInventoryUtil(Player player, final ClickEventHandler handler) {
		this.player = player;

		if (handler != null) {
			this.listener = new Listener() {
				@EventHandler
				public void onInventoryClick(InventoryClickEvent event) {
					if (!(event.getWhoClicked() instanceof Player)) {
						return;
					}

					if (inv != null && event.getInventory().equals(inv)) {
						if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
							return;
						}

						event.setCancelled(true);

						ClickEvent clickEvent = new ClickEvent(event.getClick(), event.getSlot(),
								event.getCurrentItem());
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

			Bukkit.getPluginManager().registerEvents(listener, SkyBlock.getInstance());
		}
	}

	public void addItem(Item item, int... slots) {
		for (int slotList : slots) {
			items.put(slotList, item.prepareItem());
		}
	}

	public void addItemStack(ItemStack is, int... slots) {
		for (int slotList : slots) {
			items.put(slotList, is);
		}
	}

	public Map<Integer, ItemStack> getItems() {
		return items;
	}

	public Item createItem(ItemStack is, String itemDisplayname, List<String> itemLore, Placeholder[] placeholders,
			Enchantment[] itemEnchantments, ItemFlag[] itemFlags) {
		return new Item(is, itemDisplayname, itemLore, placeholders, itemEnchantments, itemFlags);
	}

	public void open() {
		createInventory();
		player.openInventory(inv);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(InventoryType type) {
		this.type = type;
	}

	public void setRows(int rows) {
		if (rows > 6 || rows < 0) {
			size = 9;

			return;
		}

		this.size = rows * 9;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void createInventory() {
		if (type == null) {
			if (title == null) {
				inv = Bukkit.createInventory(null, size);
			} else {
				inv = Bukkit.createInventory(null, size, title);
			}
		} else {
			if (title == null) {
				inv = Bukkit.createInventory(null, type);
			} else {
				inv = Bukkit.createInventory(null, type, title);
			}
		}

		for (int i = 0; i < items.size(); i++) {
			int slot = (int) items.keySet().toArray()[i];
			inv.setItem(slot, items.get(slot));
		}
	}

	public void setInventory(Inventory inv) {
		this.inv = inv;
	}

	public Inventory getInventory() {
		return inv;
	}

	public class Item {

		private ItemStack is;
		private String itemDisplayname;
		private List<String> itemLore;
		private Placeholder[] placeholders;
		private Enchantment[] itemEnchantments;
		private ItemFlag[] itemFlags;

		public Item(ItemStack is, String itemDisplayname, List<String> itemLore, Placeholder[] placeholders,
				Enchantment[] itemEnchantments, ItemFlag[] itemFlags) {
			this.is = is;
			this.itemDisplayname = ChatColor.translateAlternateColorCodes('&', itemDisplayname);
			this.itemLore = itemLore;
			this.placeholders = placeholders;
			this.itemEnchantments = itemEnchantments;
			this.itemFlags = itemFlags;
		}

		public void setLore() {
			if (itemLore != null) {
				ArrayList<String> formattedItemLore = new ArrayList<>();

				for (String itemLoreList : itemLore) {
					if (placeholders != null) {
						for (Placeholder placeholderList : placeholders) {
							if (itemLoreList.contains(placeholderList.getPlaceholder())) {
								itemLoreList = ChatColor.translateAlternateColorCodes('&', itemLoreList
										.replace(placeholderList.getPlaceholder(), placeholderList.getResult()));
							}
						}
					}

					formattedItemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
				}

				itemLore.clear();
				itemLore = formattedItemLore;
			}
		}

		public void setItemMeta() {
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(itemDisplayname);
			im.setLore(itemLore);

			if (itemFlags != null) {
				im.addItemFlags(itemFlags);
			}

			if (itemEnchantments != null) {
				for (Enchantment itemEnchantmentList : itemEnchantments) {
					im.addEnchant(itemEnchantmentList, 1, true);
				}
			}

			is.setItemMeta(im);
		}

		public ItemStack prepareItem() {
			setLore();
			setItemMeta();

			return is;
		}
	}

	public void destroy() {
		if (listener != null) {
			HandlerList.unregisterAll(listener);
		}

		title = null;
		type = null;
		inv = null;
		items.clear();

		listener = null;
	}

	public interface ClickEventHandler {
		void onClick(ClickEvent event);
	}

	public class ClickEvent {

		private ClickType click;
		private int slot;
		private ItemStack is;

		private boolean close = true;
		private boolean destroy = true;
		private boolean cancelled = true;

		public ClickEvent(ClickType click, int slot, ItemStack is) {
			this.click = click;
			this.slot = slot;
			this.is = is;
		}

		public ClickType getClick() {
			return click;
		}

		public int getSlot() {
			return slot;
		}

		public ItemStack getItem() {
			return is;
		}

		public boolean getWillClose() {
			return close;
		}

		public void setWillClose(boolean close) {
			this.close = close;
		}

		public boolean getWillDestroy() {
			return destroy;
		}

		public void setWillDestroy(boolean destroy) {
			this.destroy = destroy;
		}

		public boolean getCancelled() {
			return cancelled;
		}

		public void setCancelled(boolean cancelled) {
			this.cancelled = cancelled;
		}
	}
}
