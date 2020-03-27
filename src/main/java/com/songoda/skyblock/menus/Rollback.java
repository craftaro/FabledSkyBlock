package com.songoda.skyblock.menus;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
 
import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Rollback implements Listener {

    private static Rollback instance;

    public static Rollback getInstance() {
        if (instance == null) {
            instance = new Rollback();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock skyblock = SkyBlock.getInstance();

        Config languageConfig = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = languageConfig.getFileConfiguration();

        Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER,
                ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Rollback.Title")));

        ItemStack is;
        ItemMeta im;

        List<String> itemLore = new ArrayList<>();

        is = CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem();
        im = is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                configLoad.getString("Menu.Rollback.Item.Barrier.Displayname")));
        is.setItemMeta(im);
        inv.setItem(1, is);

        is = new ItemStack(CompatibleMaterial.WRITABLE_BOOK.getMaterial());
        im = is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                configLoad.getString("Menu.Rollback.Item.Save.Displayname")));

        for (String itemLoreList : configLoad.getStringList("Menu.Rollback.Item.Save.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
        }

        im.setLore(itemLore);
        is.setItemMeta(im);
        inv.setItem(2, is);
        itemLore.clear();

        is = new ItemStack(Material.ENCHANTED_BOOK);
        im = is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                configLoad.getString("Menu.Rollback.Item.Load.Displayname")));

        for (String itemLoreList : configLoad.getStringList("Menu.Rollback.Item.Load.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
        }

        im.setLore(itemLore);
        is.setItemMeta(im);
        inv.setItem(3, is);
        itemLore.clear();

        is = new ItemStack(Material.HOPPER);
        im = is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&',
                configLoad.getString("Menu.Rollback.Item.Reset.Displayname")));

        for (String itemLoreList : configLoad.getStringList("Menu.Rollback.Item.Reset.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
        }

        im.setLore(itemLore);
        is.setItemMeta(im);
        inv.setItem(4, is);
        itemLore.clear();

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack is = event.getCurrentItem();

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            SkyBlock skyblock = SkyBlock.getInstance();

            MessageManager messageManager = skyblock.getMessageManager();
            IslandManager islandManager = skyblock.getIslandManager();
            SoundManager soundManager = skyblock.getSoundManager();

            Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

            String inventoryName = "";
            if (NMSUtil.getVersionNumber() > 13) {
                inventoryName = event.getView().getTitle();
            } else {
                try {
                    inventoryName = (String) Inventory.class.getMethod("getName").invoke(event.getInventory());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (inventoryName.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Rollback.Title")))) {
                event.setCancelled(true);

                Island island = islandManager.getIsland(player);

                if (island == null) {
                    messageManager.sendMessage(player,
                            config.getFileConfiguration().getString("Command.Island.Rollback.Owner.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    player.closeInventory();

                    return;
                } else if (!island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                    messageManager.sendMessage(player,
                            config.getFileConfiguration().getString("Command.Island.Rollback.Role.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    player.closeInventory();

                    return;
                }

                if ((event.getCurrentItem().getType() == Material.NAME_TAG) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Rollback.Item.Info.Displayname"))))) {
                    soundManager.playSound(player, CompatibleSound.ENTITY_CHICKEN_EGG.getSound(), 1.0F, 1.0F);
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getMaterial())
                        && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Rollback.Item.Barrier.Displayname"))))) {
                    soundManager.playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.WRITABLE_BOOK.getMaterial())
                        && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Rollback.Item.Save.Displayname"))))) {
                    /*
                     * new BukkitRunnable() { public void run() { for (Location.World worldList :
                     * Location.World.values()) { Location islandLocation =
                     * island.getLocation(worldList, Location.Environment.Island);
                     *
                     * try { Schematic.getInstance().save(new File(new
                     * File(skyblock.getDataFolder().toString() + "/rollback-data/" +
                     * island.getOwnerUUID().toString()), worldList.name() + ".schematic"), new
                     * Location(islandLocation.getWorld(), islandLocation.getBlockX() + 85,
                     * islandLocation.getBlockY(), islandLocation.getBlockZ() + 85), new
                     * Location(islandLocation.getWorld(), islandLocation.getBlockX() - 85,
                     * islandLocation.getBlockY(), islandLocation.getBlockZ() - 85)); } catch
                     * (Exception e) { e.printStackTrace(); } } } }.runTaskAsynchronously(skyblock);
                     */

                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_USE.getSound(), 1.0F, 1.0F);
                } else if ((event.getCurrentItem().getType() == Material.ENCHANTED_BOOK) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Rollback.Item.Load.Displayname"))))) {
                    /*
                     * new BukkitRunnable() { public void run() { for (Location.World worldList :
                     * Location.World.values()) { Location islandLocation =
                     * island.getLocation(worldList, Location.Environment.Island);
                     *
                     * try { Schematic.getInstance().paste(new File(new
                     * File(skyblock.getDataFolder().toString() + "/rollback-data/" +
                     * island.getOwnerUUID().toString()), "Normal.schematic"), new
                     * Location(islandLocation.getWorld(), islandLocation.getBlockX() - 85, 0,
                     * islandLocation.getBlockZ() - 85), true); } catch (Exception e) {
                     * e.printStackTrace(); } } } }.runTaskAsynchronously(skyblock);
                     */

                    soundManager.playSound(player, CompatibleSound.BLOCK_PISTON_EXTEND.getSound(), 1.0F, 1.0F);
                }
            }
        }
    }
}
