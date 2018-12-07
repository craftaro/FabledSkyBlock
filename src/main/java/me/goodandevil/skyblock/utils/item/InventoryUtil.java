package me.goodandevil.skyblock.utils.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryUtil {

	private Inventory inventory;

	public InventoryUtil(String inventoryTitle, InventoryType inventoryType, int inventoryRows) {
		if (inventoryType == null) {
			inventory = Bukkit.getServer().createInventory(null, inventoryRows * 9,
					ChatColor.translateAlternateColorCodes('&', inventoryTitle));
		} else {
			inventory = Bukkit.getServer().createInventory(null, inventoryType,
					ChatColor.translateAlternateColorCodes('&', inventoryTitle));
		}
	}

	public void addItem(Item item, int... inventorySlots) {
		for (int inventorySlot : inventorySlots) {
			inventory.setItem(inventorySlot, item.prepareItem());
		}
	}

	public void addItemStack(ItemStack is, int... inventorySlots) {
		for (int inventorySlot : inventorySlots) {
			inventory.setItem(inventorySlot, is);
		}
	}

	public Item createItem(ItemStack is, String itemDisplayname, List<String> itemLore,
			Map<String, String> itemLoreVariables, Enchantment[] itemEnchantments, ItemFlag[] itemFlags) {
		return new Item(is, itemDisplayname, itemLore, itemLoreVariables, itemEnchantments, itemFlags);
	}

	public Map<String, String> createItemLoreVariable(String[] itemLoreVariables) {
		Map<String, String> itemLoreVariablesFormatted = new HashMap<>();

		for (String itemLoreVariableList : itemLoreVariables) {
			String variableName = itemLoreVariableList.split("#")[0];
			String variableObject;

			if (itemLoreVariableList.split("#").length == 1) {
				variableObject = "null";
			} else {
				variableObject = itemLoreVariableList.split("#")[1];
			}

			itemLoreVariablesFormatted.put(variableName, variableObject);
		}

		return itemLoreVariablesFormatted;
	}

	public Inventory getInventory() {
		return inventory;
	}

	private class Item {

		private ItemStack is;
		private String itemDisplayname;
		private Map<String, String> itemLoreVariables;
		private List<String> itemLore;
		private Enchantment[] itemEnchantments;
		private ItemFlag[] itemFlags;

		public Item(ItemStack is, String itemDisplayname, List<String> itemLore, Map<String, String> itemLoreVariables,
				Enchantment[] itemEnchantments, ItemFlag[] itemFlags) {
			this.is = is;
			this.itemDisplayname = ChatColor.translateAlternateColorCodes('&', itemDisplayname);
			this.itemLore = itemLore;
			this.itemLoreVariables = itemLoreVariables;
			this.itemEnchantments = itemEnchantments;
			this.itemFlags = itemFlags;
		}

		public void setLore() {
			if (itemLore != null) {
				ArrayList<String> formattedItemLore = new ArrayList<>();

				for (String itemLoreList : itemLore) {
					if (itemLoreVariables != null) {
						for (String itemLoreVariableList : itemLoreVariables.keySet()) {
							if (itemLoreList.contains(itemLoreVariableList)) {
								itemLoreList = ChatColor.translateAlternateColorCodes('&', itemLoreList
										.replace(itemLoreVariableList, itemLoreVariables.get(itemLoreVariableList)));
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
}
