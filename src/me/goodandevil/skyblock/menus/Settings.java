package me.goodandevil.skyblock.menus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.goodandevil.skyblock.Main;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.island.IslandManager;
import me.goodandevil.skyblock.island.Message;
import me.goodandevil.skyblock.island.Role;
import me.goodandevil.skyblock.playerdata.PlayerDataManager;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.AnvilGUI;
import me.goodandevil.skyblock.utils.item.InventoryUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;
import me.goodandevil.skyblock.visit.Visit;

public class Settings implements Listener {

    private static Settings instance;

    public static Settings getInstance(){
        if(instance == null) {
            instance = new Settings();
        }
        
        return instance;
    }
    
    public void open(Player player, Settings.Type menuType, me.goodandevil.skyblock.island.Settings.Role role, Settings.Panel panel) {
    	Main plugin = Main.getInstance();
    	
    	Island island = plugin.getIslandManager().getIsland(plugin.getPlayerDataManager().getPlayerData(player).getOwner());
    	InventoryUtil inv = null;
    	
		Config mainConfig = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
    	Config languageConfig = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = languageConfig.getFileConfiguration();
    	
    	if (menuType == Settings.Type.Categories) {
    		inv = new InventoryUtil(configLoad.getString("Menu.Settings.Categories.Title"), null, 1);
    		inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"), null, null, null, null), 0, 8);
    		inv.addItem(inv.createItem(new ItemStack(Material.SIGN), configLoad.getString("Menu.Settings.Categories.Item.Visitor.Displayname"), configLoad.getStringList("Menu.Settings.Categories.Item.Visitor.Lore"), null, null, null), 2);
    		inv.addItem(inv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Settings.Categories.Item.Member.Displayname"), configLoad.getStringList("Menu.Settings.Categories.Item.Member.Lore"), null, null, null), 3);
    		inv.addItem(inv.createItem(new ItemStack(Material.ITEM_FRAME), configLoad.getString("Menu.Settings.Categories.Item.Operator.Displayname"), configLoad.getStringList("Menu.Settings.Categories.Item.Operator.Lore"), null, null, null), 4);
    		inv.addItem(inv.createItem(Materials.OAK_SAPLING.parseItem(), configLoad.getString("Menu.Settings.Categories.Item.Owner.Displayname"), configLoad.getStringList("Menu.Settings.Categories.Item.Owner.Lore"), null, null, null), 6);
    	} else if (menuType == Settings.Type.Role) {
    		if (role == me.goodandevil.skyblock.island.Settings.Role.Visitor || role == me.goodandevil.skyblock.island.Settings.Role.Member) {
    			inv = new InventoryUtil(configLoad.getString("Menu.Settings." + role.name() + ".Title"), null, 6);
    			
    			if (role == me.goodandevil.skyblock.island.Settings.Role.Visitor) {
    				Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
    				Visit visit = island.getVisit();
    				
    				if (config.getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
        				inv.addItem(inv.createItem(new ItemStack(Material.PAPER), configLoad.getString("Menu.Settings.Visitor.Item.Signature.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Item.Signature.Lore"), null, null, null), 3);
    				}
    				
    				if (config.getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
    					if (visit.isOpen()) {
    						inv.addItem(inv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Item.Statistics.Vote.Enabled.Open.Lore"), inv.createItemLoreVariable(new String[] { "%visits#" + visit.getVisitors().size(), "%votes#" + visit.getVoters().size(), "%visitors#" + island.getVisitors().size() }), null, null), 4);
    					} else {
    						inv.addItem(inv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Item.Statistics.Vote.Enabled.Closed.Lore"), inv.createItemLoreVariable(new String[] { "%visits#" + visit.getVisitors().size(), "%votes#" + visit.getVoters().size(), "%visitors#" + island.getVisitors().size() }), null, null), 4);
    					}
    				} else {
    					if (visit.isOpen()) {
    						inv.addItem(inv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Item.Statistics.Vote.Disabled.Open.Lore"), inv.createItemLoreVariable(new String[] { "%visits#" + visit.getVisitors().size(), "%visitors#" + island.getVisitors().size() }), null, null), 4);
    					} else {
    						inv.addItem(inv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Item.Statistics.Vote.Disabled.Closed.Lore"), inv.createItemLoreVariable(new String[] { "%visits#" + visit.getVisitors().size(), "%visitors#" + island.getVisitors().size() }), null, null), 4);
    					}
    				}
    				
    				if (config.getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable")) {
        				inv.addItem(inv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(), configLoad.getString("Menu.Settings.Visitor.Item.Welcome.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Item.Welcome.Lore"), null, null, null), 5);
    				}
    			}
    			
    			inv.addItemStack(createItem(island, role, "Destroy", Material.DIAMOND_PICKAXE), 9);
    			inv.addItemStack(createItem(island, role, "Place", Material.GRASS), 10);
    			inv.addItemStack(createItem(island, role, "Anvil", Material.ANVIL), 11);
    			inv.addItemStack(createItem(island, role, "ArmorStand", Material.ARMOR_STAND), 12);
    			inv.addItemStack(createItem(island, role, "Beacon", Material.BEACON), 13);
    			inv.addItemStack(createItem(island, role, "Bed", Materials.WHITE_BED.parseMaterial()), 14);
    			inv.addItemStack(createItem(island, role, "AnimalBreeding", Material.WHEAT), 15);
    			inv.addItemStack(createItem(island, role, "Brewing", Materials.LEGACY_BREWING_STAND.getPostMaterial()), 16);
    			inv.addItemStack(createItem(island, role, "Bucket", Material.BUCKET), 17);
    			inv.addItemStack(createItem(island, role, "WaterCollection", Material.POTION), 18);
    			inv.addItemStack(createItem(island, role, "Storage", Material.CHEST), 19);
    			inv.addItemStack(createItem(island, role, "Workbench", Materials.CRAFTING_TABLE.parseMaterial()), 20);
    			inv.addItemStack(createItem(island, role, "Crop", Materials.WHEAT_SEEDS.parseMaterial()), 21);
    			inv.addItemStack(createItem(island, role, "Door", Materials.OAK_DOOR.parseMaterial()), 22);
    			inv.addItemStack(createItem(island, role, "Gate", Materials.OAK_FENCE_GATE.parseMaterial()), 23);
    			inv.addItemStack(createItem(island, role, "Projectile", Material.ARROW), 24);
    			inv.addItemStack(createItem(island, role, "Enchant", Materials.ENCHANTING_TABLE.parseMaterial()), 25);
    			inv.addItemStack(createItem(island, role, "Fire", Material.FLINT_AND_STEEL), 26);
    			inv.addItemStack(createItem(island, role, "Furnace", Material.FURNACE), 27);
    			inv.addItemStack(createItem(island, role, "HorseInventory", Materials.CHEST_MINECART.parseMaterial()), 28);
    			inv.addItemStack(createItem(island, role, "MobRiding", Material.SADDLE), 29);
    			inv.addItemStack(createItem(island, role, "MobHurting", Materials.WOODEN_SWORD.parseMaterial()), 30);
    			inv.addItemStack(createItem(island, role, "MobTaming", Materials.POPPY.parseMaterial()), 31);
    			inv.addItemStack(createItem(island, role, "Leash", Materials.LEAD.parseMaterial()), 32);
    			inv.addItemStack(createItem(island, role, "LeverButton", Material.LEVER), 33);
    			inv.addItemStack(createItem(island, role, "Milking", Material.MILK_BUCKET), 34);
    			inv.addItemStack(createItem(island, role, "Jukebox", Material.JUKEBOX), 35);
    			inv.addItemStack(createItem(island, role, "PressurePlate", Materials.OAK_PRESSURE_PLATE.parseMaterial()), 37);
    			inv.addItemStack(createItem(island, role, "Redstone", Material.REDSTONE), 38);
    			inv.addItemStack(createItem(island, role, "Shearing", Material.SHEARS), 39);
    			inv.addItemStack(createItem(island, role, "Trading", Material.EMERALD), 40);
    			inv.addItemStack(createItem(island, role, "ItemDrop", Material.PUMPKIN_SEEDS), 41);
    			inv.addItemStack(createItem(island, role, "ItemPickup", Material.MELON_SEEDS), 42);
    			inv.addItemStack(createItem(island, role, "Fishing", Material.FISHING_ROD), 43);
    			inv.addItemStack(createItem(island, role, "DropperDispenser", Material.DISPENSER), 46);
    			inv.addItemStack(createItem(island, role, "SpawnEgg", Material.EGG), 47);
    			inv.addItemStack(createItem(island, role, "Cake", Material.CAKE), 48);
    			inv.addItemStack(createItem(island, role, "MinecartBoat", Material.MINECART), 50);
    			inv.addItemStack(createItem(island, role, "Portal", Material.ENDER_PEARL), 51);
    			inv.addItemStack(createItem(island, role, "Hopper", Material.HOPPER), 52);
    		} else if (role == me.goodandevil.skyblock.island.Settings.Role.Operator) {
    			if (mainConfig.getFileConfiguration().getBoolean("Island.Visitor.Banning")) {
        			inv = new InventoryUtil(configLoad.getString("Menu.Settings." + role.name() + ".Title"), null, 3);
        			inv.addItemStack(createItem(island, role, "Invite", Materials.WRITABLE_BOOK.parseMaterial()), 10);
        			inv.addItemStack(createItem(island, role, "Kick", Material.IRON_DOOR), 11);
        			inv.addItemStack(createItem(island, role, "Ban", Material.IRON_AXE), 12);
        			inv.addItemStack(createItem(island, role, "Unban", Material.NAME_TAG), 13);
        			inv.addItemStack(createItem(island, role, "Visitor", Material.SIGN), 14);
        			inv.addItemStack(createItem(island, role, "Member", Material.PAINTING), 15);
        			inv.addItemStack(createItem(island, role, "Island", Materials.OAK_SAPLING.parseMaterial()), 16);
        			inv.addItemStack(createItem(island, role, "MainSpawn", Material.EMERALD), 20);
        			inv.addItemStack(createItem(island, role, "VisitorSpawn", Material.NETHER_STAR), 21);
        			inv.addItemStack(createItem(island, role, "Biome", Material.MAP), 23);
        			inv.addItemStack(createItem(island, role, "Weather", Materials.CLOCK.parseMaterial()), 24);
    			} else {
        			inv = new InventoryUtil(configLoad.getString("Menu.Settings." + role.name() + ".Title"), null, 2);
        			inv.addItemStack(createItem(island, role, "Invite", Materials.WRITABLE_BOOK.parseMaterial()), 9);
        			inv.addItemStack(createItem(island, role, "Kick", Material.IRON_DOOR), 10);
        			inv.addItemStack(createItem(island, role, "Visitor", Material.SIGN), 11);
        			inv.addItemStack(createItem(island, role, "Member", Material.PAINTING), 12);
        			inv.addItemStack(createItem(island, role, "Island", Materials.OAK_SAPLING.parseMaterial()), 13);
        			inv.addItemStack(createItem(island, role, "MainSpawn", Material.EMERALD), 14);
        			inv.addItemStack(createItem(island, role, "VisitorSpawn", Material.NETHER_STAR), 15);
        			inv.addItemStack(createItem(island, role, "Biome", Material.MAP), 16);
        			inv.addItemStack(createItem(island, role, "Weather", Materials.CLOCK.parseMaterial()), 17);
    			}
    		} else if (role == me.goodandevil.skyblock.island.Settings.Role.Owner) {
    			inv = new InventoryUtil(configLoad.getString("Menu.Settings." + role.name() + ".Title"), null, 2);
    			inv.addItemStack(createItem(island, role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseMaterial()), 10);
    			inv.addItemStack(createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseMaterial()), 11);
    			inv.addItemStack(createItem(island, role, "PvP", Material.DIAMOND_SWORD), 12);
    			inv.addItemStack(createItem(island, role, "Explosions", Materials.GUNPOWDER.parseMaterial()), 13);
    			inv.addItemStack(createItem(island, role, "FireSpread", Material.FLINT_AND_STEEL), 14);
    			inv.addItemStack(createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseMaterial()), 15);
    			inv.addItemStack(createItem(island, role, "KeepItemsOnDeath", Material.ITEM_FRAME), 16);
    		}
    		
			inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Settings." + role.name() + ".Item.Return.Displayname"), null, null, null, null), 0, 8);
    	} else if (menuType == Settings.Type.Panel) {
    		inv = new InventoryUtil(configLoad.getString("Menu.Settings.Visitor.Panel." + panel.name() + ".Title"), InventoryType.HOPPER, 0);
    		
    		if (panel == Settings.Panel.Welcome) {
    			inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Return.Displayname"), null, null, null, null), 0, 4);
    		
    			List<String> welcomeMessage = island.getMessage(Message.Welcome);
    			
    			if (welcomeMessage.size() == mainConfig.getFileConfiguration().getInt("Island.Visitor.Welcome.Lines")) {
    				inv.addItem(inv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Limit.Lore"), null, null, null), 1);
    			} else {
    				inv.addItem(inv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.More.Lore"), null, null, null), 1);
    			}
    			
    			if (welcomeMessage.size() == 0) {
    				List<String> itemLore = new ArrayList<>();
    				itemLore.add(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Word.Empty"));
    				inv.addItem(inv.createItem(new ItemStack(Material.SIGN), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname"), itemLore, null, null, null), 2);
    				inv.addItem(inv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.None.Lore"), null, null, null), 3);
    			} else {
    				inv.addItem(inv.createItem(new ItemStack(Material.SIGN, welcomeMessage.size()), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname"), welcomeMessage, null, null, null), 2);
    				inv.addItem(inv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Lines.Lore"), null, null, null), 3);
    			}
    		} else if (panel == Settings.Panel.Signature) {
    			inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Return.Displayname"), null, null, null, null), 0, 4);
        		
    			List<String> signature = island.getMessage(Message.Signature);
    			
    			if (signature.size() == mainConfig.getFileConfiguration().getInt("Island.Visitor.Welcome.Lines")) {
    				inv.addItem(inv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Limit.Lore"), null, null, null), 1);
    			} else {
    				inv.addItem(inv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.More.Lore"), null, null, null), 1);
    			}
    			
    			if (signature.size() == 0) {
    				List<String> itemLore = new ArrayList<>();
    				itemLore.add(configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Word.Empty"));
    				inv.addItem(inv.createItem(new ItemStack(Material.SIGN), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname"), itemLore, null, null, null), 2);
    				inv.addItem(inv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.None.Lore"), null, null, null), 3);
    			} else {
    				inv.addItem(inv.createItem(new ItemStack(Material.SIGN, signature.size()), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname"), signature, null, null, null), 2);
    				inv.addItem(inv.createItem(new ItemStack(Material.ARROW), configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname"), configLoad.getStringList("Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Lines.Lore"), null, null, null), 3);
    			}
    		}
    	}
    	
    	player.openInventory(inv.getInventory());
    }
    
    private ItemStack createItem(Island island, me.goodandevil.skyblock.island.Settings.Role role, String setting, Material material) {
		Main plugin = Main.getInstance();
    	
    	Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
    	
		List<String> itemLore = new ArrayList<>();
		
    	ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		
		String roleName = role.name();
		
		if (role == me.goodandevil.skyblock.island.Settings.Role.Visitor || role == me.goodandevil.skyblock.island.Settings.Role.Member) {
			roleName = "Default";
		}
		
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings." + roleName + ".Item.Setting." + setting + ".Displayname")));
		
		if (island.getSetting(role, setting).getStatus()) {
			for (String itemLoreList : configLoad.getStringList("Menu.Settings." + roleName + ".Item.Setting.Status.Enabled.Lore")) {
				itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
			}
		} else {
			for (String itemLoreList : configLoad.getStringList("Menu.Settings." + roleName + ".Item.Setting.Status.Disabled.Lore")) {
				itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
			}
		}
		
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		im.setLore(itemLore);
		is.setItemMeta(im);
		
		return is;
    }
    
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack is = event.getCurrentItem();

		if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
			Main plugin = Main.getInstance();
			
			Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Member.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Operator.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Owner.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Title")))) {
				event.setCancelled(true);
				
				PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
				IslandManager islandManager = plugin.getIslandManager();
				SoundManager soundManager = plugin.getSoundManager();
				
				Island island;
				
				if (islandManager.hasIsland(player)) {
					island = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
					
					if (!(island.isRole(Role.Operator, player.getUniqueId()) || island.isRole(Role.Owner, player.getUniqueId()))) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Role.Message")));
						soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						player.closeInventory();
						
						return;
					}
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Owner.Message")));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					player.closeInventory();
					
					return;
				}
				
				if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Title")))) {
			    	if ((event.getCurrentItem().getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Item.Exit.Displayname"))))) {
			    		soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
			    		player.closeInventory();
			    	} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Item.Visitor.Displayname"))))) {
						if (island.isRole(Role.Operator, player.getUniqueId()) && !island.getSetting(me.goodandevil.skyblock.island.Settings.Role.Operator, "Visitor").getStatus()) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Permission.Access.Message")));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							
							return;
						}
			    		
			    		open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Visitor, null);
			    		soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
			    	} else if ((event.getCurrentItem().getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Item.Member.Displayname"))))) {
						if (island.isRole(Role.Operator, player.getUniqueId()) && !island.getSetting(me.goodandevil.skyblock.island.Settings.Role.Operator, "Member").getStatus()) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Permission.Access.Message")));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							
							return;
						}
			    		
						open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Member, null);
			    		soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
			    	} else if ((event.getCurrentItem().getType() == Material.ITEM_FRAME) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Item.Operator.Displayname"))))) {
						if (island.isRole(Role.Operator, player.getUniqueId())) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Permission.Access.Message")));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							
							return;
						}
			    		
						open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Operator, null);
			    		soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
			    	} else if ((event.getCurrentItem().getType() == Materials.OAK_SAPLING.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Categories.Item.Owner.Displayname"))))) {
			    		if (island.isRole(Role.Operator, player.getUniqueId()) && !island.getSetting(me.goodandevil.skyblock.island.Settings.Role.Operator, "Island").getStatus()) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Permission.Access.Message")));
							soundManager.playSound(player, Sounds.VILLAGER_NO.bukkitSound(), 1.0F, 1.0F);
							
							return;
			    		}
			    		
			    		open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Owner, null);
			    		soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
			    	}
				} else if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Member.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Operator.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Owner.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Title")))) {
			    	if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Title")))) {
						if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Island.Settings.Visitor.Signature.Disabled.Message")));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							
							return;
						}
			    	} else if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Title")))) {
						if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable")) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Island.Settings.Visitor.Welcome.Disabled.Message")));
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
							
							return;
						}
			    	}
					
					if ((event.getCurrentItem().getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Item.Return.Displayname"))) || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Member.Item.Return.Displayname"))) || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Operator.Item.Return.Displayname"))) || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Owner.Item.Return.Displayname"))) || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Return.Displayname"))) || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Return.Displayname"))))) {
			    		if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Title")))) {
			    			open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Visitor, null);
			    		} else {
				    		open(player, Settings.Type.Categories, null, null);
			    		}
			    		
			    		soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
			    	} else if ((event.getCurrentItem().getType() == Material.PAPER) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Item.Signature.Displayname"))))) {
			    		open(player, Settings.Type.Panel, null, Settings.Panel.Signature);
						soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
			    	} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Item.Welcome.Displayname"))))) {
			    		open(player, Settings.Type.Panel, null, Settings.Panel.Welcome);
						soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
					} else if ((event.getCurrentItem().getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Item.Statistics.Displayname"))))) {
						if (island.isOpen()) {
							islandManager.closeIsland(island);
							soundManager.playSound(player, Sounds.DOOR_CLOSE.bukkitSound(), 1.0F, 1.0F);
						} else {
							island.setOpen(true);
							soundManager.playSound(player, Sounds.DOOR_OPEN.bukkitSound(), 1.0F, 1.0F);
						}
						
						open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Settings.Role.Visitor, null);
					} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Message.Displayname"))) || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Message.Displayname"))))) {
						soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);
					} else if ((event.getCurrentItem().getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Displayname")))) && (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Title"))))) {
			    		if (island.getMessage(Message.Welcome).size() >= plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getInt("Island.Visitor.Welcome.Lines")) {
			    			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			    		} else {
			    			soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
			    			
							AnvilGUI gui = new AnvilGUI(player, event1 -> {
							    if (event1.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
							    	Island island1;
							    	
									if (islandManager.hasIsland(player)) {
										island1 = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
										
										if (!(island1.isRole(Role.Operator, player.getUniqueId()) || island1.isRole(Role.Owner, player.getUniqueId()))) {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Role.Message")));
											soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
											player.closeInventory();
											
											event1.setWillClose(true);
									        event1.setWillDestroy(true);
											
											return;
										} else if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Welcome.Enable")) {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Island.Settings.Visitor.Welcome.Disabled.Message")));
											soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
											
											event1.setWillClose(true);
									        event1.setWillDestroy(true);
											
											return;
										}
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Owner.Message")));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
										player.closeInventory();
										
										event1.setWillClose(true);
								        event1.setWillDestroy(true);
										
										return;
									}
									
									Config config1 = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
									FileConfiguration configLoad1 = config1.getFileConfiguration();
									
									if (island1.getMessage(Message.Welcome).size() > configLoad1.getInt("Island.Visitor.Welcome.Lines") || event1.getName().length() > configLoad1.getInt("Island.Visitor.Welcome.Length")) {
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
									} else {
										List<String> welcomeMessage = island1.getMessage(Message.Welcome);
										welcomeMessage.add(event1.getName());
										island1.setMessage(Message.Welcome, player.getName(), welcomeMessage);
										soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
									}
									
									new BukkitRunnable() {
										@Override
										public void run() {
											open(player, Settings.Type.Panel, null, Settings.Panel.Welcome);
										}
									}.runTaskLater(plugin, 3L);
									
							        event1.setWillClose(true);
							        event1.setWillDestroy(true);
							    } else {
							        event1.setWillClose(false);
							        event1.setWillDestroy(false);
							    }
							});
				    		
				            is = new ItemStack(Material.NAME_TAG);
				            ItemMeta im = is.getItemMeta();
				            im.setDisplayName(configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Add.Word.Enter"));
				            is.setItemMeta(im);
				            
				            gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
				            gui.open();
			    		}
					} else if ((event.getCurrentItem().getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Item.Line.Remove.Displayname")))) && (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Welcome.Title"))))) {
						List<String> welcomeMessage = island.getMessage(Message.Welcome);
						
						if (welcomeMessage.size() == 0) {
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else {
							welcomeMessage.remove(welcomeMessage.size() - 1);
							island.setMessage(Message.Welcome, island.getMessageAuthor(Message.Welcome), welcomeMessage);
							soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 1.0F, 1.0F);
							open(player, Settings.Type.Panel, null, Settings.Panel.Welcome);
						}
					} else if ((event.getCurrentItem().getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Displayname")))) && (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Title"))))) {
			    		if (island.getMessage(Message.Signature).size() >= plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getInt("Island.Visitor.Signature.Lines")) {
			    			soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
			    		} else {
			    			soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
			    			
							AnvilGUI gui = new AnvilGUI(player, event1 -> {
							    if (event1.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
							    	Island island1;
							    	
									if (islandManager.hasIsland(player)) {
										island1 = islandManager.getIsland(playerDataManager.getPlayerData(player).getOwner());
										
										if (!(island1.isRole(Role.Operator, player.getUniqueId()) || island1.isRole(Role.Owner, player.getUniqueId()))) {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Role.Message")));
											soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
											player.closeInventory();
											
											return;
										} else if (!plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml")).getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Island.Settings.Visitor.Signature.Disabled.Message")));
											soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
											
											return;
										}
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Owner.Message")));
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
										player.closeInventory();
										
										return;
									}
							    	
									Config config1 = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "config.yml"));
									FileConfiguration configLoad1 = config1.getFileConfiguration();
									
									if (island1.getMessage(Message.Signature).size() > configLoad1.getInt("Island.Visitor.Signature.Lines") || event1.getName().length() > configLoad1.getInt("Island.Visitor.Signature.Length")) {
										soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
									} else {
										List<String> signature = island1.getMessage(Message.Signature);
										signature.add(event1.getName());
										island1.setMessage(Message.Signature, player.getName(), signature);
										soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
									}
									
									new BukkitRunnable() {
										@Override
										public void run() {
											open(player, Settings.Type.Panel, null, Settings.Panel.Signature);
										}
									}.runTaskLater(plugin, 3L);
									
							        event1.setWillClose(true);
							        event1.setWillDestroy(true);
							    } else {
							        event1.setWillClose(false);
							        event1.setWillDestroy(false);
							    }
							});
				    		
				            is = new ItemStack(Material.NAME_TAG);
				            ItemMeta im = is.getItemMeta();
				            im.setDisplayName(configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Add.Word.Enter"));
				            is.setItemMeta(im);
				            
				            gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, is);
				            gui.open();
			    		}
					} else if ((event.getCurrentItem().getType() == Material.ARROW) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Item.Line.Remove.Displayname")))) && (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Panel.Signature.Title"))))) {
						List<String> signature = island.getMessage(Message.Signature);
						
						if (signature.size() == 0) {
							soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
						} else {
							signature.remove(signature.size() - 1);
							island.setMessage(Message.Signature, island.getMessageAuthor(Message.Signature), signature);
							soundManager.playSound(player, Sounds.EXPLODE.bukkitSound(), 1.0F, 1.0F);
							open(player, Settings.Type.Panel, null, Settings.Panel.Signature);
						}
					} else if (is.hasItemMeta()) {
						me.goodandevil.skyblock.island.Settings.Role role = null;
						String roleName = null;
						
						if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Title")))) {
							role = me.goodandevil.skyblock.island.Settings.Role.Visitor;
							roleName = "Default";
							
							if (island.isRole(Role.Operator, player.getUniqueId()) && !island.getSetting(me.goodandevil.skyblock.island.Settings.Role.Operator, "Visitor").getStatus()) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Permission.Change.Message")));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
								
								return;
							}
						} else if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Member.Title")))) {
							role = me.goodandevil.skyblock.island.Settings.Role.Member;
							roleName = "Default";
							
							if (island.isRole(Role.Operator, player.getUniqueId()) && !island.getSetting(me.goodandevil.skyblock.island.Settings.Role.Operator, "Member").getStatus()) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Permission.Change.Message")));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
								
								return;
							}
						} else if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Operator.Title")))) {
							role = me.goodandevil.skyblock.island.Settings.Role.Operator;
							roleName = role.name();
							
							if (!island.isRole(Role.Owner, player.getUniqueId())) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Permission.Change.Message")));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
								
								return;
							}
						} else if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Owner.Title")))) {
							role = me.goodandevil.skyblock.island.Settings.Role.Owner;
							roleName = role.name();
							
							if (island.isRole(Role.Operator, player.getUniqueId()) && !island.getSetting(me.goodandevil.skyblock.island.Settings.Role.Operator, "Island").getStatus()) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getFileConfiguration().getString("Command.Island.Settings.Permission.Change.Message")));
								soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
								
								return;
							}
						}
			    		
						for (String settingList : island.getSettings(role).keySet()) {
							if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings." + roleName + ".Item.Setting." + settingList + ".Displayname")))) {
								me.goodandevil.skyblock.island.Settings setting = island.getSettings(role).get(settingList);
								
								if (setting != null) {
									if (setting.getStatus()) {
										setting.setStatus(false);
									} else {
										setting.setStatus(true);
									}
								}
								
								break;
							}
						}
						
						soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
						open(player, Settings.Type.Role, role, null);
					}
				}
			}
		}
	}
    
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		
		Main plugin = Main.getInstance();
		
		Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
		
		if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Visitor.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Settings.Member.Title")))) {
			IslandManager islandManager = plugin.getIslandManager();
			
			if (islandManager.hasIsland(player)) {
				new BukkitRunnable() {
					@Override
					public void run() {
						islandManager.getIsland(plugin.getPlayerDataManager().getPlayerData(player).getOwner()).save();
					}
				}.runTaskAsynchronously(plugin);
			}
		}
	}
	
	public enum Panel {
		
		Welcome,
		Signature;
		
	}
	
	public enum Type {
		
		Categories,
		Panel,
		Role;
		
	}
}
