package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.playerdata.PlayerData;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.AnvilGUI;
import me.goodandevil.skyblock.utils.OfflinePlayer;
import me.goodandevil.skyblock.utils.item.InventoryUtil;
import me.goodandevil.skyblock.utils.item.SkullUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Ownership implements Listener {

    private static Ownership instance;

    public static Ownership getInstance(){
        if(instance == null) {
            instance = new Ownership();
        }
        
        return instance;
    }
    
    public void open(Player player) {
    	Main plugin = Main.getInstance();
    	
    	PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
    	
    	Island island = plugin.getIslandManager().getIsland(playerDataManager.getPlayerData(player).getOwner());
    	
    	UUID originalOwnerUUID = island.getOriginalOwnerUUID();
    	
    	String originalOwnerName, ownershipPassword = island.getPassword();
    	String[] playerTexture;
    	
    	Player targetPlayer = Bukkit.getServer().getPlayer(island.getOriginalOwnerUUID());
    	
    	if (targetPlayer == null) {
    		OfflinePlayer offlinePlayer = new OfflinePlayer(originalOwnerUUID);
    		originalOwnerName = offlinePlayer.getName();
    		playerTexture = offlinePlayer.getTexture();
    	} else {
    		originalOwnerName = targetPlayer.getName();
    		playerTexture = playerDataManager.getPlayerData(targetPlayer).getTexture();
    	}
    	
		Config languageConfig = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = languageConfig.getFileConfiguration();
		
		InventoryUtil inv = new InventoryUtil(configLoad.getString("Menu.Ownership.Title"), InventoryType.HOPPER, 0);
		inv.addItem(inv.createItem(SkullUtil.create(playerTexture[0], playerTexture[1]), configLoad.getString("Menu.Ownership.Item.Original.Displayname"), configLoad.getStringList("Menu.Ownership.Item.Original.Lore"), inv.createItemLoreVariable(new String[] { "%player#" + originalOwnerName }), null, null), 0);
		inv.addItem(inv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(), configLoad.getString("Menu.Ownership.Item.Barrier.Displayname"), null, null, null, null), 1);
		inv.addItem(inv.createItem(Materials.WRITABLE_BOOK.parseItem(), configLoad.getString("Menu.Ownership.Item.Assign.Displayname"), configLoad.getStringList("Menu.Ownership.Item.Assign.Lore"), null, null, null), 2);
		
		if (island.hasPassword()) {
			if (playerDataManager.getPlayerData(player).getType() == Ownership.Visibility.Hidden) {
				inv.addItem(inv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(), configLoad.getString("Menu.Ownership.Item.Password.Displayname"), configLoad.getStringList("Menu.Ownership.Item.Password.Hidden.Lore"), null, null, null), 3);
			} else {
				inv.addItem(inv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(), configLoad.getString("Menu.Ownership.Item.Password.Displayname"), configLoad.getStringList("Menu.Ownership.Item.Password.Visible.Lore"), inv.createItemLoreVariable(new String[] { "%password#" + ownershipPassword }), null, null), 3);
			}
		} else {
			inv.addItem(inv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(), configLoad.getString("Menu.Ownership.Item.Password.Displayname"), configLoad.getStringList("Menu.Ownership.Item.Password.Unset.Lore"), null, null, null), 3);
		}
		
		player.openInventory(inv.getInventory());
    }
    
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack is = event.getCurrentItem();

		if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			Main plugin = Main.getInstance();
			
			Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Ownership.Title")))) {
				event.setCancelled(true);
				
				PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
				IslandManager islandManager = plugin.getIslandManager();
				SoundManager soundManager = plugin.getSoundManager();
				
				Island island = null;
				
				if (islandManager.hasIsland(player)) {
					island = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
					
					if (!island.isRole(Role.Owner, player.getUniqueId())) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Ownership.Role.Message")));
						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
						player.closeInventory();
						
						return;
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Ownership.Owner.Message")));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					player.closeInventory();
					
					return;
				}
				
				PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
				
				if (playerData.getType() == null) {
					playerData.setType(Ownership.Visibility.Hidden);
				}
				
		    	if ((event.getCurrentItem().getType() == SkullUtil.createItemStack().getType()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Ownership.Item.Original.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Ownership.Item.Barrier.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);
		    	} else if ((event.getCurrentItem().getType() == Materials.WRITABLE_BOOK.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Ownership.Item.Assign.Displayname"))))) {
		    		soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
	    			
					AnvilGUI gui = new AnvilGUI(player, new AnvilGUI.AnvilClickEventHandler() {
		                @Override
		                public void onAnvilClick(final AnvilGUI.AnvilClickEvent event) {
		                    if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
		                    	Island island;
		                    	
		                    	player.closeInventory();
		                    	
		        				if (islandManager.hasIsland(player)) {
		        					island = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
		        					
		        					if (!island.isRole(Role.Owner, player.getUniqueId())) {
		        						player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Ownership.Role.Message")));
		        						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
		        						
		        						return;
		        					}
		        				} else {
		        					player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Ownership.Owner.Message")));
		        					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		        					
		        					return;
		        				}
		                    	
		        				Bukkit.getServer().dispatchCommand(player, "island ownership " + event.getName());
		        				
			                    event.setWillClose(true);
			                    event.setWillDestroy(true);
		                    } else {
		                        event.setWillClose(false);
		                        event.setWillDestroy(false);
		                    }
		                }
		            });
		    		
		            is = new ItemStack(Material.NAME_TAG);
		            ItemMeta im = is.getItemMeta();
		            im.setDisplayName(configLoad.getString("Menu.Ownership.Item.Assign.Word.Enter"));
		            is.setItemMeta(im);
		            
		            gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
		            gui.open();
		    	} else if ((event.getCurrentItem().getType() == Materials.LEGACY_EMPTY_MAP.getPostMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Ownership.Item.Password.Displayname"))))) {
		    		if (island.hasPassword()) {
		    			if (event.getClick() == ClickType.MIDDLE) {
		    				Ownership.Visibility visibility = (Ownership.Visibility) playerData.getType();
		    				
		    				if (visibility == Ownership.Visibility.Hidden) {
		    					playerData.setType(Ownership.Visibility.Visible);
		    				} else {
		    					playerData.setType(Ownership.Visibility.Hidden);
		    				}
		    				
		    				open(player);
		    				soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
		    				return;
		    			} else if (event.getClick() == ClickType.RIGHT) {
		    				island.setPassword(null);
		    				open(player);
		    				soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 1.0F, 1.0F);
		    				return;
		    			} else if (event.getClick() != ClickType.LEFT) {
		    				return;
		    			}
		    		}
		    		
		    		soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
	    			
					AnvilGUI gui = new AnvilGUI(player, new AnvilGUI.AnvilClickEventHandler() {
		                @Override
		                public void onAnvilClick(final AnvilGUI.AnvilClickEvent event) {
		                    if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
		                    	Island island;
		                    	
		        				if (islandManager.hasIsland(player)) {
		        					island = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
		        					
		        					if (!island.isRole(Role.Owner, player.getUniqueId())) {
		        						player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Ownership.Role.Message")));
		        						soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
		        						player.closeInventory();
		        						
		        						return;
		        					}
		        				} else {
		        					player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Ownership.Owner.Message")));
		        					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
		        					player.closeInventory();
		        					
		        					return;
		        				}
		                    	
		        				island.setPassword(event.getName().replace("&", "").replace(" ", ""));
					    		
					    		new BukkitRunnable() {
					    			public void run() {
					    				open(player);
					    				soundManager.playSound(player, Sounds.ANVIL_USE.bukkitSound(), 1.0F, 1.0F);
					    			}
					    		}.runTaskLater(plugin, 3L);
					    		
			                    event.setWillClose(true);
			                    event.setWillDestroy(true);
		                    } else {
		                        event.setWillClose(false);
		                        event.setWillDestroy(false);
		                    }
		                }
		            });
		    		
		            is = new ItemStack(Material.NAME_TAG);
		            ItemMeta im = is.getItemMeta();
		            im.setDisplayName(configLoad.getString("Menu.Ownership.Item.Password.Hidden.Word.Enter"));
		            is.setItemMeta(im);
		            
		            gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
		            gui.open();
		    	}
		    }
		}
	}
    
    public enum Visibility {
    	
    	Visible,
    	Hidden;
    	
    }
}
