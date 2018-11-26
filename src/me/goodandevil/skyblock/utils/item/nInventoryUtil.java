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
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.goodandevil.skyblock.SkyBlock;

public class nInventoryUtil {

	private Player player;
	private Inventory inv;
	private Listener listener;
	
	public nInventoryUtil(Player player, String inventoryTitle, InventoryType inventoryType, int inventoryRows, final ClickEventHandler handler) {
		this.player = player;
		
		if (inventoryType == null) {
			inv = Bukkit.getServer().createInventory(null, inventoryRows * 9, ChatColor.translateAlternateColorCodes('&', inventoryTitle));
		} else {
			inv = Bukkit.getServer().createInventory(null, inventoryType, ChatColor.translateAlternateColorCodes('&', inventoryTitle));
		}
		
        this.listener = new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (!(event.getWhoClicked() instanceof Player)) {
                	return;
                }
                
                if (event.getInventory().equals(inv)) {
                	if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                		return;
                	}
                	
                    ClickEvent clickEvent = new ClickEvent(event.getAction(), event.getSlot(), event.getCurrentItem());
                    handler.onClick(clickEvent);
                    
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
                
                if (inv.equals(inv)) {
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
	
	public void addItem(Item item, int... inventorySlots) {
		for (int inventorySlot : inventorySlots) {
			inv.setItem(inventorySlot, item.prepareItem());
		}
	}
	
	public void addItemStack(ItemStack is, int... inventorySlots) {
		for (int inventorySlot : inventorySlots) {
			inv.setItem(inventorySlot, is);
		}
	}
	
	public Item createItem(ItemStack is, String itemDisplayname, List<String> itemLore, Map<String, String> itemLoreVariables, Enchantment[] itemEnchantments, ItemFlag[] itemFlags) {
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
	
	public void open() {
		player.openInventory(inv);
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	private class Item {
		
		private ItemStack is;
		private String itemDisplayname;
		private Map<String, String> itemLoreVariables;
		private List<String> itemLore;
		private Enchantment[] itemEnchantments;
		private ItemFlag[] itemFlags;
		
		public Item(ItemStack is, String itemDisplayname, List<String> itemLore, Map<String, String> itemLoreVariables, Enchantment[] itemEnchantments, ItemFlag[] itemFlags) {
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
								itemLoreList = ChatColor.translateAlternateColorCodes('&', itemLoreList.replace(itemLoreVariableList, itemLoreVariables.get(itemLoreVariableList)));
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
    	HandlerList.unregisterAll(listener);
    	
        inv = null;
        listener = null;
    }
	
    public interface ClickEventHandler {
        void onClick(ClickEvent event);
    }
	
    public class ClickEvent {
    	
    	private InventoryAction action;
    	private int slot;
    	private ItemStack is;
    	
        private boolean close = true;
        private boolean destroy = true;

        public ClickEvent(InventoryAction inventoryAction, int slot, ItemStack is) {
        	this.action = inventoryAction;
        	this.slot = slot;
        	this.is = is;
        }
        
        public InventoryAction getAction() {
        	return action;
        }
        
        public int getSlot() {
        	return slot;
        }
        
        public ItemStack getItemStack() {
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
    }
}
