package com.songoda.skyblock.menus.admin;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.item.nInventoryUtil;

import com.songoda.skyblock.utils.version.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Settings {

    private static Settings instance;

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }

        return instance;
    }

    public void open(Player player, Settings.Type menuType, IslandRole role) {
        SkyBlock skyblock = SkyBlock.getInstance();

        MessageManager messageManager = skyblock.getMessageManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config mainConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
        Config languageConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = languageConfig.getFileConfiguration();

        if (menuType == Settings.Type.Categories) {
            nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                if (!(player.hasPermission("fabledskyblock.admin.settings") || player.hasPermission("fabledskyblock.admin.*")
                        || player.hasPermission("fabledskyblock.*"))) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Settings.Permission.Message"));
                    soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

                    return;
                }

                ItemStack is = event.getItem();

                if ((is.getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Settings.Categories.Item.Exit.Displayname"))))) {
                    soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
                } else if ((is.hasItemMeta()) && (is.getItemMeta().getDisplayName()
                        .equals(ChatColor.translateAlternateColorCodes('&', configLoad
                                .getString("Menu.Admin.Settings.Categories.Item.Visitor.Displayname"))))) {
                    soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, IslandRole.Visitor), 1L);
                } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Settings.Categories.Item.Member.Displayname"))))) {
                    soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, IslandRole.Member), 1L);
                } else if ((is.getType() == Material.ITEM_FRAME) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName()
                        .equals(ChatColor.translateAlternateColorCodes('&', configLoad
                                .getString("Menu.Admin.Settings.Categories.Item.Operator.Displayname"))))) {
                    soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, IslandRole.Operator), 1L);
                } else if ((is.getType() == Material.NAME_TAG) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Settings.Categories.Item.Coop.Displayname"))))) {
                    soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, IslandRole.Coop), 1L);
                } else if ((is.getType() == CompatibleMaterial.OAK_SAPLING.getMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Settings.Categories.Item.Owner.Displayname"))))) {
                    soundManager.playSound(player, Sounds.NOTE_PLING.bukkitSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, IslandRole.Owner), 1L);
                }
            });

            nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.OAK_SIGN.getMaterial()),
                    configLoad.getString("Menu.Admin.Settings.Categories.Item.Visitor.Displayname"),
                    configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Visitor.Lore"), null, null, null), 2);
            nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                    configLoad.getString("Menu.Admin.Settings.Categories.Item.Member.Displayname"),
                    configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Member.Lore"), null, null, null), 3);
            nInv.addItem(nInv.createItem(new ItemStack(Material.ITEM_FRAME),
                    configLoad.getString("Menu.Admin.Settings.Categories.Item.Operator.Displayname"),
                    configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Operator.Lore"), null, null, null),
                    4);

            if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml")).getFileConfiguration()
                    .getBoolean("Island.Coop.Enable")) {
                nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                        configLoad.getString("Menu.Admin.Settings.Categories.Item.Exit.Displayname"), null, null, null,
                        null), 0);
                nInv.addItem(nInv.createItem(new ItemStack(Material.NAME_TAG),
                        configLoad.getString("Menu.Admin.Settings.Categories.Item.Coop.Displayname"),
                        configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Coop.Lore"), null, null, null),
                        6);
                nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_SAPLING.getItem(),
                        configLoad.getString("Menu.Admin.Settings.Categories.Item.Owner.Displayname"),
                        configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Owner.Lore"), null, null, null),
                        7);
            } else {
                nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                        configLoad.getString("Menu.Admin.Settings.Categories.Item.Exit.Displayname"), null, null, null,
                        null), 0, 8);
                nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_SAPLING.getItem(),
                        configLoad.getString("Menu.Admin.Settings.Categories.Item.Owner.Displayname"),
                        configLoad.getStringList("Menu.Admin.Settings.Categories.Item.Owner.Lore"), null, null, null),
                        6);
            }

            nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Settings.Categories.Title")));
            nInv.setRows(1);

            Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
        } else if (menuType == Settings.Type.Role) {
            nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                if (!(player.hasPermission("fabledskyblock.admin.settings") || player.hasPermission("fabledskyblock.admin.*")
                        || player.hasPermission("fabledskyblock.*"))) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Settings.Permission.Message"));
                    soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

                    return;
                }

                ItemStack is = event.getItem();

                if ((is.getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial()) && (is.hasItemMeta()) && (is
                        .getItemMeta().getDisplayName()
                        .equals(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Menu.Admin.Settings.Visitor.Item.Return.Displayname")))
                        || is.getItemMeta().getDisplayName()
                        .equals(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Menu.Admin.Settings.Member.Item.Return.Displayname")))
                        || is.getItemMeta().getDisplayName()
                        .equals(ChatColor.translateAlternateColorCodes('&',
                                configLoad
                                        .getString("Menu.Admin.Settings.Operator.Item.Return.Displayname")))
                        || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Settings.Owner.Item.Return.Displayname"))))) {
                    soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Categories, null), 1L);
                } else if (is.hasItemMeta()) {
                    String roleName = getRoleName(role);

                    FileConfiguration settingsConfigLoad = skyblock.getFileManager()
                            .getConfig(new File(skyblock.getDataFolder(), "settings.yml")).getFileConfiguration();

                    for (String settingList : settingsConfigLoad.getConfigurationSection("Settings." + role.name())
                            .getKeys(false)) {
                        if (is.getItemMeta().getDisplayName()
                                .equals(ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Admin.Settings." + roleName + ".Item.Setting."
                                                + settingList + ".Displayname")))) {
                            if (settingsConfigLoad.getBoolean("Settings." + role.name() + "." + settingList)) {
                                settingsConfigLoad.set("Settings." + role.name() + "." + settingList, false);
                            } else {
                                settingsConfigLoad.set("Settings." + role.name() + "." + settingList, true);
                            }

                            Bukkit.getServer().getScheduler().runTaskAsynchronously(skyblock, () -> {
                                try {
                                    Config config = skyblock.getFileManager()
                                            .getConfig(new File(skyblock.getDataFolder(), "settings.yml"));
                                    config.getFileConfiguration().save(config.getFile());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                            break;
                        }
                    }

                    soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, Type.Role, role), 1L);
                }
            });

            if (role == IslandRole.Visitor
                    || role == IslandRole.Member
                    || role == IslandRole.Coop) {
                nInv.addItemStack(createItem(role, "Destroy", new ItemStack(Material.DIAMOND_PICKAXE)), 9);
                nInv.addItemStack(createItem(role, "Place", new ItemStack(Material.GRASS)), 10);
                nInv.addItemStack(createItem(role, "Anvil", new ItemStack(Material.ANVIL)), 11);
                nInv.addItemStack(createItem(role, "ArmorStandUse", new ItemStack(Material.ARMOR_STAND)), 12);
                nInv.addItemStack(createItem(role, "Beacon", new ItemStack(Material.BEACON)), 13);
                nInv.addItemStack(createItem(role, "Bed", CompatibleMaterial.WHITE_BED.getItem()), 14);
                nInv.addItemStack(createItem(role, "AnimalBreeding", new ItemStack(Material.WHEAT)), 15);
                nInv.addItemStack(
                        createItem(role, "Brewing", new ItemStack(CompatibleMaterial.BREWING_STAND.getMaterial())),
                        16);
                nInv.addItemStack(createItem(role, "Bucket", new ItemStack(Material.BUCKET)), 17);
                nInv.addItemStack(createItem(role, "WaterCollection", new ItemStack(Material.POTION)), 18);
                nInv.addItemStack(createItem(role, "Storage", new ItemStack(Material.CHEST)), 19);
                nInv.addItemStack(createItem(role, "Workbench", CompatibleMaterial.CRAFTING_TABLE.getItem()), 20);
                nInv.addItemStack(createItem(role, "Crop", CompatibleMaterial.WHEAT_SEEDS.getItem()), 21);
                nInv.addItemStack(createItem(role, "Door", CompatibleMaterial.OAK_DOOR.getItem()), 22);
                nInv.addItemStack(createItem(role, "Gate", CompatibleMaterial.OAK_FENCE_GATE.getItem()), 23);
                nInv.addItemStack(createItem(role, "Projectile", new ItemStack(Material.ARROW)), 24);
                nInv.addItemStack(createItem(role, "Enchant", CompatibleMaterial.ENCHANTING_TABLE.getItem()), 25);
                nInv.addItemStack(createItem(role, "Fire", new ItemStack(Material.FLINT_AND_STEEL)), 26);
                nInv.addItemStack(createItem(role, "Furnace", new ItemStack(Material.FURNACE)), 27);
                nInv.addItemStack(createItem(role, "HorseInventory", CompatibleMaterial.CHEST_MINECART.getItem()), 28);
                nInv.addItemStack(createItem(role, "MobRiding", new ItemStack(Material.SADDLE)), 29);
                nInv.addItemStack(createItem(role, "MonsterHurting", CompatibleMaterial.BONE.getItem()), 30);
                nInv.addItemStack(createItem(role, "MobHurting", CompatibleMaterial.WOODEN_SWORD.getItem()), 31);
                nInv.addItemStack(createItem(role, "MobTaming", CompatibleMaterial.POPPY.getItem()), 32);
                nInv.addItemStack(createItem(role, "Leash", CompatibleMaterial.LEAD.getItem()), 33);
                nInv.addItemStack(createItem(role, "LeverButton", new ItemStack(Material.LEVER)), 34);
                nInv.addItemStack(createItem(role, "Milking", new ItemStack(Material.MILK_BUCKET)), 35);
                nInv.addItemStack(createItem(role, "Jukebox", new ItemStack(Material.JUKEBOX)), 36);
                nInv.addItemStack(createItem(role, "PressurePlate", CompatibleMaterial.OAK_PRESSURE_PLATE.getItem()), 37);
                nInv.addItemStack(createItem(role, "Redstone", new ItemStack(Material.REDSTONE)), 38);
                nInv.addItemStack(createItem(role, "Shearing", new ItemStack(Material.SHEARS)), 39);
                nInv.addItemStack(createItem(role, "Trading", new ItemStack(Material.EMERALD)), 40);
                nInv.addItemStack(createItem(role, "ItemDrop", new ItemStack(Material.PUMPKIN_SEEDS)), 41);
                nInv.addItemStack(createItem(role, "ItemPickup", new ItemStack(Material.MELON_SEEDS)), 42);
                nInv.addItemStack(createItem(role, "Fishing", new ItemStack(Material.FISHING_ROD)), 43);
                nInv.addItemStack(createItem(role, "DropperDispenser", new ItemStack(Material.DISPENSER)), 44);
                nInv.addItemStack(createItem(role, "SpawnEgg", new ItemStack(Material.EGG)), 45);
                nInv.addItemStack(createItem(role, "HangingDestroy", new ItemStack(Material.ITEM_FRAME)), 46);
                nInv.addItemStack(createItem(role, "Cake", new ItemStack(Material.CAKE)), 47);
                nInv.addItemStack(createItem(role, "DragonEggUse", new ItemStack(Material.DRAGON_EGG)), 48);
                nInv.addItemStack(createItem(role, "MinecartBoat", new ItemStack(Material.MINECART)), 49);
                nInv.addItemStack(createItem(role, "Portal", new ItemStack(Material.ENDER_PEARL)), 50);
                nInv.addItemStack(createItem(role, "Hopper", new ItemStack(Material.HOPPER)), 51);
                nInv.addItemStack(createItem(role, "EntityPlacement", new ItemStack(Material.ARMOR_STAND)), 52);
                nInv.addItemStack(createItem(role, "ExperienceOrbPickup", CompatibleMaterial.EXPERIENCE_BOTTLE.getItem()), 53);

                nInv.setRows(6);
            } else if (role == IslandRole.Operator) {
                if (mainConfig.getFileConfiguration().getBoolean("Island.Visitor.Banning")) {
                    if (mainConfig.getFileConfiguration().getBoolean("Island.Coop.Enable")) {
                        if (mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
                            nInv.addItemStack(createItem(role, "Invite", CompatibleMaterial.WRITABLE_BOOK.getItem()), 9);
                            nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 10);
                            nInv.addItemStack(createItem(role, "Ban", new ItemStack(Material.IRON_AXE)), 11);
                            nInv.addItemStack(createItem(role, "Unban", CompatibleMaterial.RED_DYE.getItem()), 12);
                            nInv.addItemStack(createItem(role, "Visitor", new ItemStack(CompatibleMaterial.OAK_SIGN.getMaterial())), 13);
                            nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 14);
                            nInv.addItemStack(createItem(role, "Island", CompatibleMaterial.OAK_SAPLING.getItem()), 15);
                            nInv.addItemStack(createItem(role, "Coop", new ItemStack(Material.NAME_TAG)), 16);
                            nInv.addItemStack(createItem(role, "CoopPlayers", new ItemStack(Material.BOOK)), 17);
                            nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
                            nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                    21);
                            nInv.addItemStack(createItem(role, "Border", new ItemStack(Material.BEACON)), 22);
                            nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 23);
                            nInv.addItemStack(createItem(role, "Weather", CompatibleMaterial.CLOCK.getItem()), 24);
                        } else {
                            nInv.addItemStack(createItem(role, "Invite", CompatibleMaterial.WRITABLE_BOOK.getItem()), 9);
                            nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 10);
                            nInv.addItemStack(createItem(role, "Ban", new ItemStack(Material.IRON_AXE)), 11);
                            nInv.addItemStack(createItem(role, "Unban", CompatibleMaterial.RED_DYE.getItem()), 12);
                            nInv.addItemStack(createItem(role, "Visitor", new ItemStack(CompatibleMaterial.OAK_SIGN.getMaterial())), 13);
                            nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 14);
                            nInv.addItemStack(createItem(role, "Island", CompatibleMaterial.OAK_SAPLING.getItem()), 15);
                            nInv.addItemStack(createItem(role, "Coop", new ItemStack(Material.NAME_TAG)), 16);
                            nInv.addItemStack(createItem(role, "CoopPlayers", new ItemStack(Material.BOOK)), 17);
                            nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
                            nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                    21);
                            nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 23);
                            nInv.addItemStack(createItem(role, "Weather", CompatibleMaterial.CLOCK.getItem()), 24);
                        }
                    } else {
                        if (mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
                            nInv.addItemStack(createItem(role, "Invite", CompatibleMaterial.WRITABLE_BOOK.getItem()), 10);
                            nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 11);
                            nInv.addItemStack(createItem(role, "Ban", new ItemStack(Material.IRON_AXE)), 12);
                            nInv.addItemStack(createItem(role, "Unban", CompatibleMaterial.RED_DYE.getItem()), 13);
                            nInv.addItemStack(createItem(role, "Visitor", new ItemStack(CompatibleMaterial.OAK_SIGN.getMaterial())), 14);
                            nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 15);
                            nInv.addItemStack(createItem(role, "Island", CompatibleMaterial.OAK_SAPLING.getItem()), 16);
                            nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
                            nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                    21);
                            nInv.addItemStack(createItem(role, "Border", new ItemStack(Material.BEACON)), 22);
                            nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 23);
                            nInv.addItemStack(createItem(role, "Weather", CompatibleMaterial.CLOCK.getItem()), 24);
                        } else {
                            nInv.addItemStack(createItem(role, "Invite", CompatibleMaterial.WRITABLE_BOOK.getItem()), 10);
                            nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 11);
                            nInv.addItemStack(createItem(role, "Ban", new ItemStack(Material.IRON_AXE)), 12);
                            nInv.addItemStack(createItem(role, "Unban", CompatibleMaterial.RED_DYE.getItem()), 13);
                            nInv.addItemStack(createItem(role, "Visitor", new ItemStack(CompatibleMaterial.OAK_SIGN.getMaterial())), 14);
                            nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 15);
                            nInv.addItemStack(createItem(role, "Island", CompatibleMaterial.OAK_SAPLING.getItem()), 16);
                            nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
                            nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                    21);
                            nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 23);
                            nInv.addItemStack(createItem(role, "Weather", CompatibleMaterial.CLOCK.getItem()), 24);
                        }
                    }

                    nInv.setRows(3);
                } else {
                    if (mainConfig.getFileConfiguration().getBoolean("Island.Coop.Enable")) {
                        if (mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
                            nInv.addItemStack(createItem(role, "Invite", CompatibleMaterial.WRITABLE_BOOK.getItem()), 10);
                            nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 11);
                            nInv.addItemStack(createItem(role, "Visitor", new ItemStack(CompatibleMaterial.OAK_SIGN.getMaterial())), 12);
                            nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 13);
                            nInv.addItemStack(createItem(role, "Island", CompatibleMaterial.OAK_SAPLING.getItem()), 14);
                            nInv.addItemStack(createItem(role, "Coop", new ItemStack(Material.NAME_TAG)), 15);
                            nInv.addItemStack(createItem(role, "CoopPlayers", new ItemStack(Material.BOOK)), 16);
                            nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
                            nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                    21);
                            nInv.addItemStack(createItem(role, "Border", new ItemStack(Material.BEACON)), 22);
                            nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 23);
                            nInv.addItemStack(createItem(role, "Weather", CompatibleMaterial.CLOCK.getItem()), 24);
                        } else {
                            nInv.addItemStack(createItem(role, "Invite", CompatibleMaterial.WRITABLE_BOOK.getItem()), 10);
                            nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 11);
                            nInv.addItemStack(createItem(role, "Visitor", new ItemStack(CompatibleMaterial.OAK_SIGN.getMaterial())), 12);
                            nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 13);
                            nInv.addItemStack(createItem(role, "Island", CompatibleMaterial.OAK_SAPLING.getItem()), 14);
                            nInv.addItemStack(createItem(role, "Coop", new ItemStack(Material.NAME_TAG)), 15);
                            nInv.addItemStack(createItem(role, "CoopPlayers", new ItemStack(Material.BOOK)), 16);
                            nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 20);
                            nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                    21);
                            nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 23);
                            nInv.addItemStack(createItem(role, "Weather", CompatibleMaterial.CLOCK.getItem()), 24);
                        }

                        nInv.setRows(3);
                    } else {
                        if (mainConfig.getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
                            nInv.addItemStack(createItem(role, "Invite", CompatibleMaterial.WRITABLE_BOOK.getItem()), 10);
                            nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 11);
                            nInv.addItemStack(createItem(role, "Visitor", new ItemStack(CompatibleMaterial.OAK_SIGN.getMaterial())), 12);
                            nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 13);
                            nInv.addItemStack(createItem(role, "Island", CompatibleMaterial.OAK_SAPLING.getItem()), 14);
                            nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 15);
                            nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                    16);
                            nInv.addItemStack(createItem(role, "Border", new ItemStack(Material.BEACON)), 21);
                            nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 22);
                            nInv.addItemStack(createItem(role, "Weather", CompatibleMaterial.CLOCK.getItem()), 23);

                            nInv.setRows(3);
                        } else {
                            nInv.addItemStack(createItem(role, "Invite", CompatibleMaterial.WRITABLE_BOOK.getItem()), 9);
                            nInv.addItemStack(createItem(role, "Kick", new ItemStack(Material.IRON_DOOR)), 10);
                            nInv.addItemStack(createItem(role, "Visitor", new ItemStack(CompatibleMaterial.OAK_SIGN.getMaterial())), 11);
                            nInv.addItemStack(createItem(role, "Member", new ItemStack(Material.PAINTING)), 12);
                            nInv.addItemStack(createItem(role, "Island", CompatibleMaterial.OAK_SAPLING.getItem()), 13);
                            nInv.addItemStack(createItem(role, "MainSpawn", new ItemStack(Material.EMERALD)), 14);
                            nInv.addItemStack(createItem(role, "VisitorSpawn", new ItemStack(Material.NETHER_STAR)),
                                    15);
                            nInv.addItemStack(createItem(role, "Biome", new ItemStack(Material.MAP)), 16);
                            nInv.addItemStack(createItem(role, "Weather", CompatibleMaterial.CLOCK.getItem()), 17);

                            nInv.setRows(2);
                        }
                    }
                }
            } else if (role == IslandRole.Owner) {
                if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.PvP.Enable")) {
                    if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
                        if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Damage.Enable")) {
                            if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()), 9);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 12);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 13);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 14);
                                nInv.addItemStack(
                                        createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 15);
                                nInv.addItemStack(createItem(role, "Damage", CompatibleMaterial.RED_DYE.getItem()), 16);
                                nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 17);
                            } else {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()), 9);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 12);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 15);
                                nInv.addItemStack(
                                        createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 16);
                                nInv.addItemStack(createItem(role, "Damage", CompatibleMaterial.RED_DYE.getItem()), 17);
                            }
                        } else {
                            if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()), 9);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 12);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 15);
                                nInv.addItemStack(
                                        createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 16);
                                nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 17);
                            } else {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        11);
                                nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 13);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 15);
                                nInv.addItemStack(
                                        createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 16);
                            }
                        }
                    } else {
                        if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Damage.Enable")) {
                            if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()), 9);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 11);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 12);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 15);
                                nInv.addItemStack(createItem(role, "Damage", CompatibleMaterial.RED_DYE.getItem()), 16);
                                nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 17);
                            } else {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        11);
                                nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 13);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 15);
                                nInv.addItemStack(createItem(role, "Damage", CompatibleMaterial.RED_DYE.getItem()), 16);
                            }
                        } else {
                            if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        11);
                                nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 13);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 15);
                                nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 16);
                            } else {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        11);
                                nInv.addItemStack(createItem(role, "PvP", new ItemStack(Material.DIAMOND_SWORD)), 12);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 14);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 15);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 16);
                            }
                        }
                    }
                } else {
                    if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.KeepItemsOnDeath.Enable")) {
                        if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Damage.Enable")) {
                            if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()), 9);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 11);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 12);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 14);
                                nInv.addItemStack(
                                        createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 15);
                                nInv.addItemStack(createItem(role, "Damage", CompatibleMaterial.RED_DYE.getItem()), 16);
                                nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 17);
                            } else {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        11);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 12);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 13);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 14);
                                nInv.addItemStack(
                                        createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 15);
                                nInv.addItemStack(createItem(role, "Damage", CompatibleMaterial.RED_DYE.getItem()), 16);
                            }
                        } else {
                            if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        11);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 12);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 13);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 14);
                                nInv.addItemStack(
                                        createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 15);
                                nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 16);
                            } else {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        11);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 12);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 15);
                                nInv.addItemStack(
                                        createItem(role, "KeepItemsOnDeath", new ItemStack(Material.ITEM_FRAME)), 16);
                            }
                        }
                    } else {
                        if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Damage.Enable")) {
                            if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        11);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 12);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 13);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 14);
                                nInv.addItemStack(createItem(role, "Damage", CompatibleMaterial.RED_DYE.getItem()), 15);
                                nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 16);
                            } else {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        11);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 12);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 15);
                                nInv.addItemStack(createItem(role, "Damage", CompatibleMaterial.RED_DYE.getItem()), 16);
                            }
                        } else {
                            if (mainConfig.getFileConfiguration().getBoolean("Island.Settings.Hunger.Enable")) {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()),
                                        10);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        11);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 12);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 15);
                                nInv.addItemStack(createItem(role, "Hunger", new ItemStack(Material.COOKED_BEEF)), 16);
                            } else {
                                nInv.addItemStack(
                                        createItem(role, "NaturalMobSpawning", CompatibleMaterial.PIG_SPAWN_EGG.getItem()),
                                        11);
                                nInv.addItemStack(createItem(role, "MobGriefing", CompatibleMaterial.IRON_SHOVEL.getItem()),
                                        12);
                                nInv.addItemStack(createItem(role, "Explosions", CompatibleMaterial.GUNPOWDER.getItem()), 13);
                                nInv.addItemStack(
                                        createItem(role, "FireSpread", new ItemStack(Material.FLINT_AND_STEEL)), 14);
                                nInv.addItemStack(createItem(role, "LeafDecay", CompatibleMaterial.OAK_LEAVES.getItem()), 15);
                            }
                        }
                    }
                }

                nInv.setRows(2);
            }

            nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                    configLoad.getString("Menu.Admin.Settings." + role.name() + ".Item.Return.Displayname"), null, null,
                    null, null), 0, 8);
            nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Settings." + role.name() + ".Title")));

            Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
        }
    }

    private ItemStack createItem(IslandRole role, String setting, ItemStack is) {
        SkyBlock skyblock = SkyBlock.getInstance();

        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        List<String> itemLore = new ArrayList<>();

        ItemMeta im = is.getItemMeta();

        String roleName = role.name();

        if (role == IslandRole.Visitor
                || role == IslandRole.Member
                || role == IslandRole.Coop) {
            roleName = "Default";
        }

        im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                configLoad.getString("Menu.Admin.Settings." + roleName + ".Item.Setting." + setting + ".Displayname")));

        if (fileManager.getConfig(new File(skyblock.getDataFolder(), "settings.yml")).getFileConfiguration()
                .getBoolean("Settings." + role.name() + "." + setting)) {
            for (String itemLoreList : configLoad
                    .getStringList("Menu.Admin.Settings." + roleName + ".Item.Setting.Status.Enabled.Lore")) {
                itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
            }
        } else {
            for (String itemLoreList : configLoad
                    .getStringList("Menu.Admin.Settings." + roleName + ".Item.Setting.Status.Disabled.Lore")) {
                itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
            }
        }

        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        im.setLore(itemLore);
        is.setItemMeta(im);

        return is;
    }

    private String getRoleName(IslandRole role) {
        if (role == IslandRole.Visitor
                || role == IslandRole.Member
                || role == IslandRole.Coop) {
            return "Default";
        }

        return role.name();
    }

    public enum Type {

        Categories, Panel, Role

    }
}
