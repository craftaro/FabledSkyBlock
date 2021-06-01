package com.songoda.skyblock.menus.admin;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.utils.ItemUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.levelling.LevellingMaterial;
import com.songoda.skyblock.menus.MenuType;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.AbstractAnvilGUI;
import com.songoda.core.utils.NumberUtils;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Levelling implements Listener {

    private static Levelling instance;

    public static Levelling getInstance() {
        if (instance == null) {
            instance = new Levelling();
        }

        return instance;
    }

    @SuppressWarnings("deprecation")
    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getInstance();

        IslandLevelManager levellingManager = plugin.getLevellingManager();
        FileManager fileManager = plugin.getFileManager();

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

        List<LevellingMaterial> levellingMaterials = levellingManager.getWorthsAsLevelingMaterials();

        // Filter out materials that won't be displayed in the GUI properly
        Inventory testInventory = Bukkit.createInventory(null, 9);
        levellingMaterials = levellingMaterials.stream().filter(x -> {
            if (x.getMaterials() == CompatibleMaterial.SPAWNER) return false;
            if (x.getItemStack() == null) return false;
            ItemStack itemStack = x.getMaterials().getItem();
            itemStack.setAmount(1);
            itemStack.setDurability(x.getItemStack().getDurability());
            if (itemStack.getItemMeta() == null) return false;
            testInventory.clear();
            testInventory.setItem(0, itemStack);
            return testInventory.getItem(0) != null;
        }).collect(Collectors.toList());

        FileConfiguration configLoad = plugin.getLanguage();

        nInventoryUtil nInv = new nInventoryUtil(player, null);
        nInv.addItem(
                nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                        configLoad.getString("Menu.Admin.Levelling.Item.Exit.Displayname"), null, null, null, null),
                0, 8);
        nInv.addItem(
                nInv.createItem(new ItemStack(CompatibleMaterial.OAK_SIGN.getMaterial()),
                        configLoad.getString("Menu.Admin.Levelling.Item.Information.Displayname"),
                        configLoad.getStringList("Menu.Admin.Levelling.Item.Information.Lore"),
                        new Placeholder[]{new Placeholder("%materials", "" + levellingMaterials.size()),
                                new Placeholder("%division",
                                        "" + fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"))
                                                .getFileConfiguration().getInt("Island.Levelling.Division"))},
                        null, null),
                4);
        nInv.addItem(
                nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                        configLoad.getString("Menu.Admin.Levelling.Item.Barrier.Displayname"), null, null, null, null),
                9, 10, 11, 12, 13, 14, 15, 16, 17);

        int playerMenuPage = playerData.getPage(MenuType.ADMIN_LEVELLING), nextEndIndex = levellingMaterials.size() - playerMenuPage * 36;

        if (playerMenuPage != 1) {
            nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(
                    "ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=",
                    "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="),
                    configLoad.getString("Menu.Admin.Levelling.Item.Previous.Displayname"), null, null, null, null), 1);
        }

        if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
            nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(
                    "wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=",
                    "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="),
                    configLoad.getString("Menu.Admin.Levelling.Item.Next.Displayname"), null, null, null, null), 7);
        }

        if (levellingMaterials.size() == 0) {
            nInv.addItem(nInv.createItem(new ItemStack(Material.BARRIER),
                    configLoad.getString("Menu.Admin.Levelling.Item.Nothing.Displayname"), null, null, null, null), 31);
        } else {
            int index = playerMenuPage * 36 - 36,
                    endIndex = index >= levellingMaterials.size() ? levellingMaterials.size() - 1 : index + 36,
                    inventorySlot = 17;

            for (; index < endIndex; index++) {
                if (levellingMaterials.size() > index) {
                    inventorySlot++;

                    LevellingMaterial material = levellingMaterials.get(index);
                    ItemStack itemStack = material.getMaterials().getItem();
                    itemStack.setAmount(1);
                    itemStack.setDurability(material.getItemStack().getDurability());
                    nInv.addItem(
                            nInv.createItem(
                                    itemStack,
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Admin.Levelling.Item.Material.Displayname")
                                                    .replace("%material", material.getMaterials().name())),
                                    configLoad.getStringList("Menu.Admin.Levelling.Item.Material.Lore"),
                                    new Placeholder[]{new Placeholder("%points",
                                            NumberUtils.formatNumber(material.getPoints()))},
                                    null, null),
                            inventorySlot);
                }
            }
        }

        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Levelling.Title")));
        nInv.setRows(6);

        Bukkit.getServer().getScheduler().runTask(plugin, () -> nInv.open());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack is = event.getCurrentItem();

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            SkyBlock plugin = SkyBlock.getInstance();

            IslandLevelManager levellingManager = plugin.getLevellingManager();
            MessageManager messageManager = plugin.getMessageManager();
            SoundManager soundManager = plugin.getSoundManager();
            FileManager fileManager = plugin.getFileManager();

            FileConfiguration configLoad = plugin.getLanguage();

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

            if (inventoryName.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Levelling.Title")))) {
                event.setCancelled(true);
                PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

                if (!(player.hasPermission("fabledskyblock.admin.level") || player.hasPermission("fabledskyblock.admin.*")
                        || player.hasPermission("fabledskyblock.*"))) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Levelling.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    return;
                }

                if ((event.getCurrentItem().getType() == CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getMaterial())
                        && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Levelling.Item.Barrier.Displayname"))))) {
                    soundManager.playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                    return;
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial())
                        && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Levelling.Item.Exit.Displayname"))))) {
                    soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1.0F, 1.0F);
                    player.closeInventory();

                    return;
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.OAK_SIGN.getMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Levelling.Item.Information.Displayname"))))) {
                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                    AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event1 -> {
                        if (event1.getSlot() == AbstractAnvilGUI.AnvilSlot.OUTPUT) {
                            if (!(player.hasPermission("fabledskyblock.admin.level")
                                    || player.hasPermission("fabledskyblock.admin.*")
                                    || player.hasPermission("fabledskyblock.*"))) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Levelling.Permission.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            } else {
                                try {
                                    double pointDivision = Double.parseDouble(event1.getName());
    
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Levelling.Division.Message")
                                                    .replace("%division", NumberUtils.formatNumber(pointDivision)));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
    
                                    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                        Config config12 = fileManager
                                                .getConfig(new File(plugin.getDataFolder(), "config.yml"));
                                        FileConfiguration configLoad12 = config12.getFileConfiguration();
        
                                        configLoad12.set("Island.Levelling.Division", pointDivision);
        
                                        try {
                                            configLoad12.save(config12.getFile());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
    
                                    player.closeInventory();
    
                                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                                } catch (NumberFormatException ignored) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Levelling.Numerical.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                                }
                            }

                            event1.setWillClose(true);
                            event1.setWillDestroy(true);
                        } else {
                            event1.setWillClose(false);
                            event1.setWillDestroy(false);
                        }
                    });

                    is = new ItemStack(Material.NAME_TAG);
                    ItemMeta im = is.getItemMeta();
                    im.setDisplayName(configLoad.getString("Menu.Admin.Levelling.Item.Information.Word.Enter"));
                    is.setItemMeta(im);

                    gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, is);
                    gui.open();

                    return;
                } else if ((event.getCurrentItem().getType() == Material.BARRIER) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Levelling.Item.Nothing.Displayname"))))) {
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    return;
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.PLAYER_HEAD.getMaterial())
                        && (is.hasItemMeta())) {
                    if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Admin.Levelling.Item.Previous.Displayname")))) {
                        player.closeInventory();

                        playerData.setPage(MenuType.ADMIN_LEVELLING, playerData.getPage(MenuType.ADMIN_LEVELLING) - 1);
                        soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                        return;
                    } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Admin.Levelling.Item.Next.Displayname")))) {
                        player.closeInventory();

                        playerData.setPage(MenuType.ADMIN_LEVELLING, playerData.getPage(MenuType.ADMIN_LEVELLING) + 1);
                        soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                        return;
                    }
                }

                if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                    for (LevellingMaterial materialList : levellingManager.getWorthsAsLevelingMaterials()) {
                        CompatibleMaterial materials = materialList.getMaterials();

                        if (CompatibleMaterial.getMaterial(materials.getMaterial()) != null
                                &&  event.getCurrentItem().getType().equals(CompatibleMaterial.getMaterial(materials.getMaterial()).getMaterial())
                                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equals(materials.name())) {

                            if (event.getClick() == ClickType.LEFT) {
                                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event1 -> {
                                    if (event1.getSlot() == AbstractAnvilGUI.AnvilSlot.OUTPUT) {
                                        if (!(player.hasPermission("fabledskyblock.admin.level")
                                                || player.hasPermission("fabledskyblock.admin.*")
                                                || player.hasPermission("fabledskyblock.*"))) {
                                            messageManager.sendMessage(player,
                                                    configLoad.getString("Island.Admin.Levelling.Permission.Message"));
                                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                                        } else if (levellingManager.hasWorth(materials)) {
                                            try {
                                                double materialPoints = Double.parseDouble(event1.getName());
                                                materialList.setPoints(materialPoints);

                                                messageManager.sendMessage(player, configLoad
                                                        .getString("Island.Admin.Levelling.Points.Message")
                                                        .replace("%material", materials.name()).replace("%points",
                                                                NumberUtils.formatNumber(materialPoints)));
                                                soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F,
                                                        1.0F);
                                                player.closeInventory();

                                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                        () -> open(player), 1L);

                                                levellingManager.addWorth(materials, materialPoints);

                                                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin,
                                                        () -> {
                                                            Config config1 = fileManager.getConfig(new File(
                                                                    plugin.getDataFolder(), "levelling.yml"));
                                                            FileConfiguration configLoad1 = config1
                                                                    .getFileConfiguration();

                                                            configLoad1.set(
                                                                    "Materials." + materials.name() + ".Points",
                                                                    materialPoints);

                                                            try {
                                                                configLoad1.save(config1.getFile());
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        });
                                            } catch (NumberFormatException ignored) {
                                                messageManager.sendMessage(player, configLoad
                                                        .getString("Island.Admin.Levelling.Numerical.Message"));
                                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                        1.0F);
                                            }
                                        } else {
                                            messageManager.sendMessage(player,
                                                    configLoad.getString("Island.Admin.Levelling.Exist.Message"));
                                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                                        }

                                        event1.setWillClose(true);
                                        event1.setWillDestroy(true);
                                    } else {
                                        event1.setWillClose(false);
                                        event1.setWillDestroy(false);
                                    }
                                });

                                is = new ItemStack(Material.NAME_TAG);
                                ItemMeta im = is.getItemMeta();
                                im.setDisplayName(
                                        configLoad.getString("Menu.Admin.Levelling.Item.Material.Word.Enter"));
                                is.setItemMeta(im);

                                gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, is);
                                gui.open();
                            } else if (event.getClick() == ClickType.RIGHT) {
                                levellingManager.removeWorth(materialList.getMaterials());
                                open(player);

                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Levelling.Removed.Message")
                                                .replace("%material", materials.name()));
                                soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                    Config config13 = fileManager
                                            .getConfig(new File(plugin.getDataFolder(), "levelling.yml"));
                                    FileConfiguration configLoad13 = config13.getFileConfiguration();

                                    configLoad13.set("Materials." + materials.name(), null);

                                    try {
                                        configLoad13.save(config13.getFile());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }

                            return;
                        }
                    }
                }

                CompatibleMaterial materials = CompatibleMaterial.getMaterial(event.getCurrentItem().getType());

                if (NMSUtil.getVersionNumber() < 13) {
                    materials.getItem().setData(event.getCurrentItem().getData());
                }

                if (levellingManager.hasWorth(materials)) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Already.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                    return;
                }

                levellingManager.addWorth(materials, 0);
                open(player);

                messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Added.Message")
                        .replace("%material", materials.name()));
                soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);

                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    Config config14 = fileManager.getConfig(new File(plugin.getDataFolder(), "levelling.yml"));
                    FileConfiguration configLoad14 = config14.getFileConfiguration();

                    configLoad14.set("Materials." + materials.name() + ".Points", 0);

                    try {
                        configLoad14.save(config14.getFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
