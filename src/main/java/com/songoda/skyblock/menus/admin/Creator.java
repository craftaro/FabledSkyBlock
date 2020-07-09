package com.songoda.skyblock.menus.admin;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.menus.MenuType;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.structure.Structure;
import com.songoda.skyblock.structure.StructureManager;
import com.songoda.skyblock.utils.AbstractAnvilGUI;
import com.songoda.skyblock.utils.item.SkullUtil;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import com.songoda.skyblock.utils.version.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Creator implements Listener {

    private static Creator instance;

    public static Creator getInstance() {
        if (instance == null) {
            instance = new Creator();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getInstance();

        StructureManager structureManager = plugin.getStructureManager();
        FileManager fileManager = plugin.getFileManager();

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

        Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        nInventoryUtil nInv = new nInventoryUtil(player, null);

        if (playerData.getViewer() == null) {
            List<Structure> structures = structureManager.getStructures();

            nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                    configLoad.getString("Menu.Admin.Creator.Browse.Item.Exit.Displayname"), null, null, null, null), 0,
                    8);
            nInv.addItem(
                    nInv.createItem(new ItemStack(CompatibleMaterial.OAK_SIGN.getItem()),
                            configLoad.getString("Menu.Admin.Creator.Browse.Item.Information.Displayname"),
                            configLoad.getStringList("Menu.Admin.Creator.Browse.Item.Information.Lore"),
                            new Placeholder[]{new Placeholder("%structures", "" + structures.size())}, null, null),
                    4);
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.Admin.Creator.Browse.Item.Barrier.Displayname"), null, null, null, null),
                    9, 10, 11, 12, 13, 14, 15, 16, 17);

            int playerMenuPage = playerData.getPage(MenuType.ADMIN_CREATOR), nextEndIndex = structures.size() - playerMenuPage * 36;

            if (playerMenuPage != 1) {
                nInv.addItem(nInv.createItem(SkullUtil.create(
                        "ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=",
                        "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="),
                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Previous.Displayname"), null, null, null,
                        null), 1);
            }

            if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                nInv.addItem(nInv.createItem(SkullUtil.create(
                        "wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=",
                        "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="),
                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Next.Displayname"), null, null, null,
                        null), 7);
            }

            if (structures.size() == 0) {
                nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.BARRIER.getMaterial()),
                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Nothing.Displayname"), null, null, null,
                        null), 31);
            } else {
                int index = playerMenuPage * 36 - 36,
                        endIndex = index >= structures.size() ? structures.size() - 1 : index + 36, inventorySlot = 17;

                for (; index < endIndex; index++) {
                    if (structures.size() > index) {
                        inventorySlot++;

                        Structure structure = structures.get(index);
                        nInv.addItem(nInv.createItem(structure.getMaterial().getItem(),
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Structure.Displayname")
                                                .replace("%structure", structure.getName())),
                                configLoad.getStringList("Menu.Admin.Creator.Browse.Item.Structure.Lore"), null, null,
                                null), inventorySlot);
                    }
                }
            }

            nInv.setRows(6);
        } else {
            Structure structure = structureManager.getStructure(((Creator.Viewer) playerData.getViewer()).getName());

            nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.Return.Displayname"), null, null, null, null),
                    0, 8);

            String displayName = ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Creator.Options.Item.Word.Unset"));

            if (structure.getDisplayname() != null && !structure.getDisplayname().isEmpty()) {
                displayName = ChatColor.translateAlternateColorCodes('&', structure.getDisplayname());
            }

            nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.NAME_TAG.getMaterial()),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.Displayname.Displayname"),
                    configLoad.getStringList("Menu.Admin.Creator.Options.Item.Displayname.Lore"),
                    new Placeholder[]{new Placeholder("%displayname", displayName)}, null, null), 1);

            List<String> descriptionLore = new ArrayList<>();

            if (structure.getDescription() == null || structure.getDescription().size() == 0) {
                for (String itemLore : configLoad
                        .getStringList("Menu.Admin.Creator.Options.Item.Description.Unset.Lore")) {
                    if (itemLore.contains("%description")) {
                        descriptionLore.add(configLoad.getString("Menu.Admin.Creator.Options.Item.Word.Unset"));
                    } else {
                        descriptionLore.add(itemLore);
                    }
                }
            } else {
                for (String itemLore : configLoad
                        .getStringList("Menu.Admin.Creator.Options.Item.Description.Set.Lore")) {
                    if (itemLore.contains("%description")) {
                        for (String descriptionList : structure.getDescription()) {
                            descriptionLore.add(descriptionList);
                        }
                    } else {
                        descriptionLore.add(itemLore);
                    }
                }
            }

            nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.ENCHANTED_BOOK.getMaterial()),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.Description.Displayname"), descriptionLore,
                    null, null, null), 2);

            List<String> commandsLore = new ArrayList<>();

            if (structure.getCommands() == null || structure.getCommands().size() == 0) {
                for (String itemLore : configLoad
                        .getStringList("Menu.Admin.Creator.Options.Item.Commands.Unset.Lore")) {
                    if (itemLore.contains("%commands")) {
                        commandsLore.add(configLoad.getString("Menu.Admin.Creator.Options.Item.Word.Unset"));
                    } else {
                        commandsLore.add(itemLore);
                    }
                }
            } else {
                for (String itemLore : configLoad.getStringList("Menu.Admin.Creator.Options.Item.Commands.Set.Lore")) {
                    if (itemLore.contains("%commands")) {
                        for (String commandList : structure.getCommands()) {
                            commandsLore.add(commandList);
                        }
                    } else {
                        commandsLore.add(itemLore);
                    }
                }
            }

            nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.BOOK.getMaterial()),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.Commands.Displayname"), commandsLore, null,
                    null, null), 3);

            List<String> permissionLore = new ArrayList<>();

            if (structure.isPermission()) {
                permissionLore = configLoad.getStringList("Menu.Admin.Creator.Options.Item.Permission.Disable.Lore");
            } else {
                permissionLore = configLoad.getStringList("Menu.Admin.Creator.Options.Item.Permission.Enable.Lore");
            }

            nInv.addItem(nInv.createItem(CompatibleMaterial.MAP.getItem(),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.Permission.Displayname"), permissionLore,
                    new Placeholder[]{new Placeholder("%permission", structure.getPermission())}, null, null), 4);

            String fileName = ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Creator.Options.Item.Word.Unset")), overworldFileName,
                    netherFileName, endFileName;

            if (structure.getOverworldFile() != null && !structure.getOverworldFile().isEmpty()) {
                overworldFileName = structure.getOverworldFile();
            } else {
                overworldFileName = fileName;
            }

            if (structure.getNetherFile() != null && !structure.getNetherFile().isEmpty()) {
                netherFileName = structure.getNetherFile();
            } else {
                netherFileName = fileName;
            }

            if (structure.getEndFile() != null && !structure.getEndFile().isEmpty()) {
                endFileName = structure.getEndFile();
            } else {
                endFileName = fileName;
            }

            nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.PAPER.getMaterial()),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.File.Displayname"),
                    configLoad.getStringList("Menu.Admin.Creator.Options.Item.File.Lore"),
                    new Placeholder[]{new Placeholder("%overworld_file", overworldFileName),
                            new Placeholder("%nether_file", netherFileName),
                            new Placeholder("%end_file", endFileName)},
                    null, null), 5);
            nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.DIAMOND.getMaterial()),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.Item.Displayname"),
                    configLoad.getStringList("Menu.Admin.Creator.Options.Item.Item.Lore"),
                    new Placeholder[]{new Placeholder("%material", structure.getMaterial().name())}, null, null),
                    6);
            nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.GOLD_NUGGET.getMaterial()),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.DeletionCost.Displayname"),
                    configLoad.getStringList("Menu.Admin.Creator.Options.Item.DeletionCost.Lore"),
                    new Placeholder[]{new Placeholder("%cost", "" + structure.getDeletionCost())}, null, null), 7);

            nInv.setRows(1);
        }

        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Creator.Title")));

        Bukkit.getServer().getScheduler().runTask(plugin, () -> nInv.open());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack is = event.getCurrentItem();

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != CompatibleMaterial.AIR.getMaterial()) {
            SkyBlock plugin = SkyBlock.getInstance();

            StructureManager structureManager = plugin.getStructureManager();
            MessageManager messageManager = plugin.getMessageManager();
            SoundManager soundManager = plugin.getSoundManager();
            FileManager fileManager = plugin.getFileManager();

            Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
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

            if (inventoryName.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Creator.Title")))) {
                event.setCancelled(true);

                PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

                if (!(player.hasPermission("fabledskyblock.admin.create") || player.hasPermission("fabledskyblock.admin.*")
                        || player.hasPermission("fabledskyblock.*"))) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Admin.Creator.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    return;
                }

                if ((event.getCurrentItem().getType() == CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getMaterial())
                        && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Barrier.Displayname"))))) {
                    soundManager.playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                    return;
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial())
                        && (is.hasItemMeta())) {
                    if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Admin.Creator.Browse.Item.Exit.Displayname")))) {
                        soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1.0F, 1.0F);
                        player.closeInventory();

                        return;
                    } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Admin.Creator.Options.Item.Return.Displayname")))) {
                        playerData.setViewer(null);
                        soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                        return;
                    }
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.OAK_SIGN.getMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Information.Displayname"))))) {
                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                    AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event1 -> {
                        if (event1.getSlot() == AbstractAnvilGUI.AnvilSlot.OUTPUT) {
                            if (!(player.hasPermission("fabledskyblock.admin.creator")
                                    || player.hasPermission("fabledskyblock.admin.*")
                                    || player.hasPermission("fabledskyblock.*"))) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Creator.Permission.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            } else if (structureManager.containsStructure(event1.getName())) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Creator.Already.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            } else if (!event1.getName().replace(" ", "").matches("^[a-zA-Z0-9]+$")) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Creator.Characters.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                            } else {
                                structureManager.addStructure(event1.getName(), CompatibleMaterial.GRASS_BLOCK, null, null, null,
                                        null, false, new ArrayList<>(), new ArrayList<>(), 0.0D);

                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Creator.Created.Message")
                                                .replace("%structure", event1.getName()));
                                soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                    Config config111 = fileManager
                                            .getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                    FileConfiguration configLoad111 = config111.getFileConfiguration();

                                    configLoad111.set("Structures." + event1.getName() + ".Name", event1.getName());

                                    try {
                                        configLoad111.save(config111.getFile());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });

                                player.closeInventory();

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                            }

                            event1.setWillClose(true);
                            event1.setWillDestroy(true);
                        } else {
                            event1.setWillClose(false);
                            event1.setWillDestroy(false);
                        }
                    });

                    is = new ItemStack(CompatibleMaterial.NAME_TAG.getMaterial());
                    ItemMeta im = is.getItemMeta();
                    im.setDisplayName(configLoad.getString("Menu.Admin.Creator.Browse.Item.Information.Word.Enter"));
                    is.setItemMeta(im);

                    gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, is);
                    gui.open();

                    return;
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.BARRIER.getMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Nothing.Displayname"))))) {
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    return;
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.NAME_TAG.getMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Options.Item.Displayname.Displayname"))))) {
                    if (playerData.getViewer() == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else {
                        String name = ((Creator.Viewer) playerData.getViewer()).getName();

                        if (structureManager.containsStructure(name)) {
                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event1 -> {
                                if (event1.getSlot() == AbstractAnvilGUI.AnvilSlot.OUTPUT) {
                                    if (!(player.hasPermission("fabledskyblock.admin.creator")
                                            || player.hasPermission("fabledskyblock.admin.*")
                                            || player.hasPermission("fabledskyblock.*"))) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Permission.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                                    } else if (playerData.getViewer() == null) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    } else if (!structureManager.containsStructure(name)) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    } else {
                                        Structure structure = structureManager.getStructure(name);
                                        structure.setDisplayname(event1.getName());

                                        soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);

                                        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin,
                                                () -> {
                                                    Config config1 = fileManager.getConfig(
                                                            new File(plugin.getDataFolder(), "structures.yml"));
                                                    FileConfiguration configLoad1 = config1.getFileConfiguration();

                                                    configLoad1.set(
                                                            "Structures." + structure.getName() + ".Displayname",
                                                            event1.getName());

                                                    try {
                                                        configLoad1.save(config1.getFile());
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                });

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    }

                                    event1.setWillClose(true);
                                    event1.setWillDestroy(true);
                                } else {
                                    event1.setWillClose(false);
                                    event1.setWillDestroy(false);
                                }
                            });

                            is = new ItemStack(CompatibleMaterial.NAME_TAG.getMaterial());
                            ItemMeta im = is.getItemMeta();
                            im.setDisplayName(
                                    configLoad.getString("Menu.Admin.Creator.Options.Item.Displayname.Word.Enter"));
                            is.setItemMeta(im);

                            gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, is);
                            gui.open();
                        } else {
                            playerData.setViewer(null);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }
                    }

                    return;
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.ENCHANTED_BOOK.getMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Options.Item.Description.Displayname"))))) {
                    if (playerData.getViewer() == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else {
                        String name = ((Creator.Viewer) playerData.getViewer()).getName();

                        if (structureManager.containsStructure(name)) {
                            Structure structure = structureManager.getStructure(name);

                            if (structure.getDescription() != null && !structure.getDescription().isEmpty()) {
                                if (event.getClick() == ClickType.RIGHT) {
                                    structure.removeLine(structure.getDescription().size() - 1);
                                    soundManager.playSound(player, CompatibleSound.ENTITY_GENERIC_EXPLODE.getSound(), 1.0F, 1.0F);

                                    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                        Config config12 = fileManager
                                                .getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                        FileConfiguration configLoad12 = config12.getFileConfiguration();

                                        configLoad12.set("Structures." + structure.getName() + ".Description",
                                                structure.getDescription());

                                        try {
                                            configLoad12.save(config12.getFile());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);

                                    return;
                                } else if (event.getClick() != ClickType.LEFT) {
                                    return;
                                }
                            }

                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event1 -> {
                                if (event1.getSlot() == AbstractAnvilGUI.AnvilSlot.OUTPUT) {
                                    if (!(player.hasPermission("fabledskyblock.admin.creator")
                                            || player.hasPermission("fabledskyblock.admin.*")
                                            || player.hasPermission("fabledskyblock.*"))) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Permission.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                                    } else if (playerData.getViewer() == null) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    } else if (!structureManager.containsStructure(name)) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    } else {
                                        structure.addLine(event1.getName());

                                        soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);

                                        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin,
                                                () -> {
                                                    Config config13 = fileManager.getConfig(
                                                            new File(plugin.getDataFolder(), "structures.yml"));
                                                    FileConfiguration configLoad13 = config13.getFileConfiguration();

                                                    configLoad13.set(
                                                            "Structures." + structure.getName() + ".Description",
                                                            structure.getDescription());

                                                    try {
                                                        configLoad13.save(config13.getFile());
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                });

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    }

                                    event1.setWillClose(true);
                                    event1.setWillDestroy(true);
                                } else {
                                    event1.setWillClose(false);
                                    event1.setWillDestroy(false);
                                }
                            });

                            is = new ItemStack(CompatibleMaterial.NAME_TAG.getMaterial());
                            ItemMeta im = is.getItemMeta();
                            im.setDisplayName(
                                    configLoad.getString("Menu.Admin.Creator.Options.Item.Description.Word.Enter"));
                            is.setItemMeta(im);

                            gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, is);
                            gui.open();
                        } else {
                            playerData.setViewer(null);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }
                    }

                    return;
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.BOOK.getMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Options.Item.Commands.Displayname"))))) {
                    if (playerData.getViewer() == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else {
                        String name = ((Creator.Viewer) playerData.getViewer()).getName();

                        if (structureManager.containsStructure(name)) {
                            Structure structure = structureManager.getStructure(name);

                            if (structure.getCommands() != null && !structure.getCommands().isEmpty()) {
                                if (event.getClick() == ClickType.RIGHT) {
                                    structure.removeCommand(structure.getCommands().size() - 1);
                                    soundManager.playSound(player, CompatibleSound.ENTITY_GENERIC_EXPLODE.getSound(), 1.0F, 1.0F);

                                    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                        Config config14 = fileManager
                                                .getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                        FileConfiguration configLoad14 = config14.getFileConfiguration();

                                        configLoad14.set("Structures." + structure.getName() + ".Commands",
                                                structure.getCommands());

                                        try {
                                            configLoad14.save(config14.getFile());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);

                                    return;
                                } else if (event.getClick() != ClickType.LEFT) {
                                    return;
                                }
                            }

                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event1 -> {
                                if (event1.getSlot() == AbstractAnvilGUI.AnvilSlot.OUTPUT) {
                                    if (!(player.hasPermission("fabledskyblock.admin.creator")
                                            || player.hasPermission("fabledskyblock.admin.*")
                                            || player.hasPermission("fabledskyblock.*"))) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Permission.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                                    } else if (playerData.getViewer() == null) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    } else if (!structureManager.containsStructure(name)) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    } else {
                                        structure.addCommand(event1.getName());

                                        soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);

                                        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin,
                                                () -> {
                                                    Config config15 = fileManager.getConfig(
                                                            new File(plugin.getDataFolder(), "structures.yml"));
                                                    FileConfiguration configLoad15 = config15.getFileConfiguration();

                                                    configLoad15.set(
                                                            "Structures." + structure.getName() + ".Commands",
                                                            structure.getCommands());

                                                    try {
                                                        configLoad15.save(config15.getFile());
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                });

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    }

                                    event1.setWillClose(true);
                                    event1.setWillDestroy(true);
                                } else {
                                    event1.setWillClose(false);
                                    event1.setWillDestroy(false);
                                }
                            });

                            is = new ItemStack(CompatibleMaterial.NAME_TAG.getMaterial());
                            ItemMeta im = is.getItemMeta();
                            im.setDisplayName(
                                    configLoad.getString("Menu.Admin.Creator.Options.Item.Commands.Word.Enter"));
                            is.setItemMeta(im);

                            gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, is);
                            gui.open();
                        } else {
                            playerData.setViewer(null);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }
                    }

                    return;
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.MAP.getMaterial())
                        && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Options.Item.Permission.Displayname"))))) {
                    if (playerData.getViewer() == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else {
                        String name = ((Creator.Viewer) playerData.getViewer()).getName();

                        if (structureManager.containsStructure(name)) {
                            Structure structure = structureManager.getStructure(name);

                            if (structure.isPermission()) {
                                structure.setPermission(false);
                            } else {
                                structure.setPermission(true);
                            }

                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                Config config16 = fileManager
                                        .getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                FileConfiguration configLoad16 = config16.getFileConfiguration();

                                configLoad16.set("Structures." + structure.getName() + ".Permission",
                                        structure.isPermission());

                                try {
                                    configLoad16.save(config16.getFile());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else {
                            playerData.setViewer(null);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }
                    }

                    return;
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.PAPER.getMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Options.Item.File.Displayname"))))) {
                    if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.MIDDLE
                            || event.getClick() == ClickType.RIGHT) {
                        if (playerData.getViewer() == null) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Selected.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else {
                            String name = ((Creator.Viewer) playerData.getViewer()).getName();

                            if (structureManager.containsStructure(name)) {
                                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event1 -> {
                                    if (event1.getSlot() == AbstractAnvilGUI.AnvilSlot.OUTPUT) {
                                        if (!(player.hasPermission("fabledskyblock.admin.creator")
                                                || player.hasPermission("fabledskyblock.admin.*")
                                                || player.hasPermission("fabledskyblock.*"))) {
                                            messageManager.sendMessage(player,
                                                    configLoad.getString("Island.Admin.Creator.Permission.Message"));
                                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                                        } else if (playerData.getViewer() == null) {
                                            messageManager.sendMessage(player,
                                                    configLoad.getString("Island.Admin.Creator.Selected.Message"));
                                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                            player.closeInventory();

                                            Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                    () -> open(player), 1L);
                                        } else if (!structureManager.containsStructure(name)) {
                                            messageManager.sendMessage(player,
                                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                            player.closeInventory();

                                            Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                    () -> open(player), 1L);
                                        } else {
                                            String fileName = event1.getName();
                                            if (fileManager.isFileExist(new File(plugin.getDataFolder().toString() + "/structures", fileName)) ||
                                                    fileManager.isFileExist(new File(plugin.getDataFolder().toString() + "/schematics", fileName))) {
                                                if (event.getClick() == ClickType.LEFT) {
                                                    Structure structure = structureManager.getStructure(name);
                                                    structure.setOverworldFile(fileName);

                                                    soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(),
                                                            1.0F, 1.0F);

                                                    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin,
                                                            () -> {
                                                                Config config17 = fileManager.getConfig(
                                                                        new File(plugin.getDataFolder(),
                                                                                "structures.yml"));
                                                                FileConfiguration configLoad17 = config17
                                                                        .getFileConfiguration();

                                                                configLoad17.set("Structures." + structure.getName() + ".File.Overworld", fileName);

                                                                try {
                                                                    configLoad17.save(config17.getFile());
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            });
                                                } else if (event.getClick() == ClickType.MIDDLE) {
                                                    Structure structure = structureManager.getStructure(name);
                                                    structure.setNetherFile(fileName);

                                                    soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(),
                                                            1.0F, 1.0F);

                                                    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin,
                                                            () -> {
                                                                Config config18 = fileManager.getConfig(
                                                                        new File(plugin.getDataFolder(),
                                                                                "structures.yml"));
                                                                FileConfiguration configLoad18 = config18
                                                                        .getFileConfiguration();

                                                                configLoad18.set("Structures." + structure.getName()
                                                                        + ".File.Nether", fileName);

                                                                try {
                                                                    configLoad18.save(config18.getFile());
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            });
                                                } else {
                                                    Structure structure = structureManager.getStructure(name);
                                                    structure.setEndFile(fileName);

                                                    soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(),
                                                            1.0F, 1.0F);

                                                    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin,
                                                            () -> {
                                                                Config config19 = fileManager.getConfig(
                                                                        new File(plugin.getDataFolder(),
                                                                                "structures.yml"));
                                                                FileConfiguration configLoad19 = config19
                                                                        .getFileConfiguration();

                                                                configLoad19.set("Structures." + structure.getName()
                                                                        + ".File.End", fileName);

                                                                try {
                                                                    configLoad19.save(config19.getFile());
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            });
                                                }
                                            } else {
                                                messageManager.sendMessage(player,
                                                        configLoad.getString("Island.Admin.Creator.File.Message"));
                                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                        1.0F);
                                            }

                                            player.closeInventory();

                                            Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                    () -> open(player), 1L);
                                        }

                                        event1.setWillClose(true);
                                        event1.setWillDestroy(true);
                                    } else {
                                        event1.setWillClose(false);
                                        event1.setWillDestroy(false);
                                    }
                                });

                                is = new ItemStack(CompatibleMaterial.NAME_TAG.getMaterial());
                                ItemMeta im = is.getItemMeta();
                                im.setDisplayName(
                                        configLoad.getString("Menu.Admin.Creator.Options.Item.File.Word.Enter"));
                                is.setItemMeta(im);

                                gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, is);
                                gui.open();
                            } else {
                                playerData.setViewer(null);

                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                player.closeInventory();

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                            }
                        }
                    }

                    return;
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.DIAMOND.getMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Options.Item.Item.Displayname"))))) {
                    if (playerData.getViewer() == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else {
                        Creator.Viewer viewer = (Viewer) playerData.getViewer();
                        String name = viewer.getName();

                        if (viewer.isItem()) {
                            viewer.setItem(false);
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Item.Cancelled.Message"));
                            soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
                        } else {
                            if (structureManager.containsStructure(name)) {
                                viewer.setItem(true);
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Creator.Item.Added.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);
                            } else {
                                playerData.setViewer(null);

                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                player.closeInventory();

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                            }
                        }
                    }

                    return;
                } else if ((event.getCurrentItem().getType() == CompatibleMaterial.GOLD_NUGGET.getMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Options.Item.DeletionCost.Displayname"))))) {
                    if (playerData.getViewer() == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else {
                        String name = ((Creator.Viewer) playerData.getViewer()).getName();

                        if (structureManager.containsStructure(name)) {
                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            AbstractAnvilGUI gui = new AbstractAnvilGUI(player, event1 -> {
                                if (event1.getSlot() == AbstractAnvilGUI.AnvilSlot.OUTPUT) {
                                    if (!(player.hasPermission("fabledskyblock.admin.creator")
                                            || player.hasPermission("fabledskyblock.admin.*")
                                            || player.hasPermission("fabledskyblock.*"))) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Permission.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        return;
                                    } else if (playerData.getViewer() == null) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);

                                        return;
                                    } else if (!structureManager.containsStructure(name)) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);

                                        return;
                                    } else if (!(event1.getName().matches("[0-9]+")
                                            || event1.getName().matches("([0-9]*)\\.([0-9]{1,2}$)"))) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Numerical.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        event1.setWillClose(false);
                                        event1.setWillDestroy(false);

                                        return;
                                    }

                                    double deletionCost = Double.valueOf(event1.getName());

                                    Structure structure = structureManager.getStructure(name);
                                    structure.setDeletionCost(deletionCost);

                                    soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);

                                    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                        Config config112 = fileManager
                                                .getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                        FileConfiguration configLoad112 = config112.getFileConfiguration();

                                        configLoad112.set("Structures." + structure.getName() + ".Deletion.Cost",
                                                deletionCost);

                                        try {
                                            configLoad112.save(config112.getFile());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else {
                                    event1.setWillClose(false);
                                    event1.setWillDestroy(false);
                                }
                            });

                            is = new ItemStack(CompatibleMaterial.NAME_TAG.getMaterial());
                            ItemMeta im = is.getItemMeta();
                            im.setDisplayName(
                                    configLoad.getString("Menu.Admin.Creator.Options.Item.DeletionCost.Word.Enter"));
                            is.setItemMeta(im);

                            gui.setSlot(AbstractAnvilGUI.AnvilSlot.INPUT_LEFT, is);
                            gui.open();
                        } else {
                            playerData.setViewer(null);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }
                    }

                    return;
                }

                if (playerData.getViewer() != null) {
                    Creator.Viewer viewer = (Viewer) playerData.getViewer();

                    if (viewer.isItem()) {
                        if (structureManager.containsStructure(viewer.getName())) {
                            Structure structure = structureManager.getStructure(viewer.getName());
                            CompatibleMaterial materials = CompatibleMaterial.getMaterial(event.getCurrentItem().getType());
                            materials.getItem().setData(event.getCurrentItem().getData());

                            if (materials != null) {
                                structure.setMaterial(materials);

                                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                    Config config113 = fileManager
                                            .getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                    FileConfiguration configLoad113 = config113.getFileConfiguration();

                                    configLoad113.set("Structures." + structure.getName() + ".Item.Material",
                                            structure.getMaterial().name());

                                    try {
                                        configLoad113.save(config113.getFile());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }

                            viewer.setItem(false);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Item.Removed.Message"));
                            soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else {
                            playerData.setViewer(null);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }

                        return;
                    }
                }

                if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                    for (Structure structureList : structureManager.getStructures()) {
                        if (event.getCurrentItem().getType() == structureList.getMaterial().getMaterial()
                                && ChatColor.stripColor(is.getItemMeta().getDisplayName())
                                .equals(structureList.getName())) {
                            if (event.getClick() == ClickType.LEFT) {
                                playerData.setViewer(new Viewer(structureList.getName()));
                                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                player.closeInventory();

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                            } else if (event.getClick() == ClickType.RIGHT) {
                                structureManager.removeStructure(structureList);

                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Creator.Removed.Message")
                                                .replace("%structure", structureList.getName()));
                                soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                    Config config110 = fileManager
                                            .getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                    FileConfiguration configLoad110 = config110.getFileConfiguration();

                                    configLoad110.set("Structures." + structureList.getName(), null);

                                    try {
                                        configLoad110.save(config110.getFile());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });

                                player.closeInventory();

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                            }

                            return;
                        }
                    }

                    messageManager.sendMessage(player, configLoad.getString("Island.Admin.Creator.Exist.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    player.closeInventory();

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        SkyBlock plugin = SkyBlock.getInstance();

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
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

        if (inventoryName.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Creator.Title")))) {
            PlayerDataManager playerDataManager = plugin.getPlayerDataManager();

            if (playerDataManager.hasPlayerData(player)) {
                Creator.Viewer viewer = (Viewer) playerDataManager.getPlayerData(player).getViewer();

                if (viewer != null) {
                    if (viewer.isItem()) {
                        viewer.setItem(false);
                        plugin.getMessageManager().sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Item.Removed.Message"));
                        plugin.getSoundManager().playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    public class Viewer {

        private String name;
        private boolean item = false;

        public Viewer(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean isItem() {
            return item;
        }

        public void setItem(boolean item) {
            this.item = item;
        }
    }
}
