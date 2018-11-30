package me.goodandevil.skyblock.menus.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.goodandevil.skyblock.SkyBlock;
import me.goodandevil.skyblock.config.FileManager;
import me.goodandevil.skyblock.config.FileManager.Config;
import me.goodandevil.skyblock.island.Island;
import me.goodandevil.skyblock.sound.SoundManager;
import me.goodandevil.skyblock.utils.item.InventoryUtil;
import me.goodandevil.skyblock.utils.version.Materials;
import me.goodandevil.skyblock.utils.version.Sounds;

public class Settings implements Listener {

    private static Settings instance;

    public static Settings getInstance(){
        if(instance == null) {
            instance = new Settings();
        }
        
        return instance;
    }
    
    public void open(Player player, Settings.Type menuType, me.goodandevil.skyblock.island.Setting.Role role) {
    	SkyBlock skyblock = SkyBlock.getInstance();
    	
    	Island island = skyblock.getIslandManager().getIsland(skyblock.getPlayerDataManager().getPlayerData(player).getOwner());
    	InventoryUtil inv = null;
    	
		Config mainConfig = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "config.yml"));
    	Config languageConfig = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = languageConfig.getFileConfiguration();
    	
    	if (menuType == Settings.Type.Categories) {
    		inv = new InventoryUtil(configLoad.getString("Menu.Admin.Settings.Categories.Title"), null, 1);
    		inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Admin.Settings.Categories.Item.Exit.Displayname"), null, null, null, null), 0, 8);
    		inv.addItem(inv.createItem(new ItemStack(Material.SIGN), configLoad.getString("Menu.Admin.Settings.Categories.Item.Visitor.Displayname"), configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Visitor.Lore"), null, null, null), 2);
    		inv.addItem(inv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Admin.Settings.Categories.Item.Member.Displayname"), configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Member.Lore"), null, null, null), 3);
    		inv.addItem(inv.createItem(new ItemStack(Material.ITEM_FRAME), configLoad.getString("Menu.Admin.Settings.Categories.Item.Operator.Displayname"), configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Operator.Lore"), null, null, null), 4);
    		inv.addItem(inv.createItem(Materials.OAK_SAPLING.parseItem(), configLoad.getString("Menu.Admin.Settings.Categories.Item.Owner.Displayname"), configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Owner.Lore"), null, null, null), 6);
    	} else if (menuType == Settings.Type.Role) {
    		if (role == me.goodandevil.skyblock.island.Setting.Role.Visitor || role == me.goodandevil.skyblock.island.Setting.Role.Member) {
    			inv = new InventoryUtil(configLoad.getString("Menu.Admin.Settings." + role.name() + ".Title"), null, 6);
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
    			inv.addItemStack(createItem(island, role, "DragonEggUse", Material.DRAGON_EGG), 49);
    			inv.addItemStack(createItem(island, role, "MinecartBoat", Material.MINECART), 50);
    			inv.addItemStack(createItem(island, role, "Portal", Material.ENDER_PEARL), 51);
    			inv.addItemStack(createItem(island, role, "Hopper", Material.HOPPER), 52);
    		} else if (role == me.goodandevil.skyblock.island.Setting.Role.Operator) {
    			if (mainConfig.getFileConfiguration().getBoolean("Island.Visitor.Banning")) {
        			inv = new InventoryUtil(configLoad.getString("Menu.Admin.Settings." + role.name() + ".Title"), null, 3);
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
        			inv = new InventoryUtil(configLoad.getString("Menu.Admin.Settings." + role.name() + ".Title"), null, 2);
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
    		} else if (role == me.goodandevil.skyblock.island.Setting.Role.Owner) {
    			inv = new InventoryUtil(configLoad.getString("Menu.Admin.Settings." + role.name() + ".Title"), null, 2);
    			inv.addItemStack(createItem(island, role, "NaturalMobSpawning", Materials.PIG_SPAWN_EGG.parseMaterial()), 10);
    			inv.addItemStack(createItem(island, role, "MobGriefing", Materials.IRON_SHOVEL.parseMaterial()), 11);
    			inv.addItemStack(createItem(island, role, "PvP", Material.DIAMOND_SWORD), 12);
    			inv.addItemStack(createItem(island, role, "Explosions", Materials.GUNPOWDER.parseMaterial()), 13);
    			inv.addItemStack(createItem(island, role, "FireSpread", Material.FLINT_AND_STEEL), 14);
    			inv.addItemStack(createItem(island, role, "LeafDecay", Materials.OAK_LEAVES.parseMaterial()), 15);
    			inv.addItemStack(createItem(island, role, "KeepItemsOnDeath", Material.ITEM_FRAME), 16);
    		}
    		
			inv.addItem(inv.createItem(Materials.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Admin.Settings." + role.name() + ".Item.Return.Displayname"), null, null, null, null), 0, 8);
    	}
    	
    	player.openInventory(inv.getInventory());
    }
    
    private ItemStack createItem(Island island, me.goodandevil.skyblock.island.Setting.Role role, String setting, Material material) {
		SkyBlock skyblock = SkyBlock.getInstance();
    	
		FileManager fileManager = skyblock.getFileManager();
		
    	Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
		FileConfiguration configLoad = config.getFileConfiguration();
    	
		List<String> itemLore = new ArrayList<>();
		
    	ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		
		String roleName = role.name();
		
		if (role == me.goodandevil.skyblock.island.Setting.Role.Visitor || role == me.goodandevil.skyblock.island.Setting.Role.Member) {
			roleName = "Default";
		}
		
		im.setDisplayName(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings." + roleName + ".Item.Setting." + setting + ".Displayname")));
		
		if (fileManager.getConfig(new File(skyblock.getDataFolder(), "settings.yml")).getFileConfiguration().getBoolean(role.name() + "." + setting)) {
			for (String itemLoreList : configLoad.getStringList("Menu.Admin.Settings." + roleName + ".Item.Setting.Status.Enabled.Lore")) {
				itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
			}
		} else {
			for (String itemLoreList : configLoad.getStringList("Menu.Admin.Settings." + roleName + ".Item.Setting.Status.Disabled.Lore")) {
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
			SkyBlock skyblock = SkyBlock.getInstance();
			
			SoundManager soundManager = skyblock.getSoundManager();
			FileManager fileManager = skyblock.getFileManager();
			
			Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
			FileConfiguration configLoad = config.getFileConfiguration();
			
			if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Categories.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Visitor.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Member.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Operator.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Owner.Title")))) {
				event.setCancelled(true);
				
				if (!(player.hasPermission("skyblock.admin.settings") || player.hasPermission("skyblock.admin.*") || player.hasPermission("skyblock.*"))) {
					skyblock.getMessageManager().sendMessage(player, configLoad.getString("Island.Admin.Settings.Permission.Message"));
					soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);
					
					return;
				}
				
				if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Categories.Title")))) {
			    	if ((event.getCurrentItem().getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Categories.Item.Exit.Displayname"))))) {
			    		soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
			    		player.closeInventory();
			    	} else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Categories.Item.Visitor.Displayname"))))) {
			    		open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Setting.Role.Visitor);
			    		soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
			    	} else if ((event.getCurrentItem().getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Categories.Item.Member.Displayname"))))) {
						open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Setting.Role.Member);
			    		soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
			    	} else if ((event.getCurrentItem().getType() == Material.ITEM_FRAME) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Categories.Item.Operator.Displayname"))))) {
						open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Setting.Role.Operator);
			    		soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
			    	} else if ((event.getCurrentItem().getType() == Materials.OAK_SAPLING.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Categories.Item.Owner.Displayname"))))) {
			    		open(player, Settings.Type.Role, me.goodandevil.skyblock.island.Setting.Role.Owner);
			    		soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);
			    	}
				} else if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Visitor.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Member.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Operator.Title"))) || event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Owner.Title")))) {
					if ((event.getCurrentItem().getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta()) && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Visitor.Item.Return.Displayname"))) || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Member.Item.Return.Displayname"))) || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Operator.Item.Return.Displayname"))) || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Owner.Item.Return.Displayname"))))) {
						open(player, Settings.Type.Categories, null);
			    		soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);
					} else if (is.hasItemMeta()) {
						me.goodandevil.skyblock.island.Setting.Role role = null;
						String roleName = null, rolePermissionName = null;
						
						if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Visitor.Title")))) {
							role = me.goodandevil.skyblock.island.Setting.Role.Visitor;
							roleName = "Visitor";
							rolePermissionName = "Default";
						} else if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Member.Title")))) {
							role = me.goodandevil.skyblock.island.Setting.Role.Member;
							roleName = "Member";
							rolePermissionName = "Default";
						} else if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Operator.Title")))) {
							role = me.goodandevil.skyblock.island.Setting.Role.Operator;
							roleName = role.name();
							rolePermissionName = role.name();
						} else if (event.getInventory().getName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings.Owner.Title")))) {
							role = me.goodandevil.skyblock.island.Setting.Role.Owner;
							roleName = role.name();
							rolePermissionName = role.name();
						}
			    		
						FileConfiguration settingsConfigLoad = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "settings.yml")).getFileConfiguration();
						
						for (String settingList : settingsConfigLoad.getConfigurationSection(roleName).getKeys(false)) {
							if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Settings." + rolePermissionName + ".Item.Setting." + settingList + ".Displayname")))) {
								if (settingsConfigLoad.getBoolean(roleName + "." + settingList)) {
									settingsConfigLoad.set(roleName + "." + settingList, false);
								} else {
									settingsConfigLoad.set(roleName + "." + settingList, true);
								}
								
								Bukkit.getServer().getScheduler().runTaskAsynchronously(skyblock, new Runnable() {
									@Override
									public void run() {
										try {
											Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "settings.yml"));
											config.getFileConfiguration().save(config.getFile());
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								});
								
								break;
							}
						}
						
						soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);
						open(player, Settings.Type.Role, role);
					}
				}
			}
		}
	}
	
	public enum Type {
		
		Categories,
		Panel,
		Role;
		
	}
}
