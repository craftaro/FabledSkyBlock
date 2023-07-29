package com.songoda.skyblock.menus.admin;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.utils.ItemUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.generator.GeneratorManager;
import com.songoda.skyblock.generator.GeneratorMaterial;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.menus.MenuType;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.item.nInventoryUtil;
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
import java.util.ArrayList;
import java.util.List;

public class Generator implements Listener {
    private static Generator instance;

    public static Generator getInstance() {
        if (instance == null) {
            instance = new Generator();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        GeneratorManager generatorManager = plugin.getGeneratorManager();
        FileManager fileManager = plugin.getFileManager();

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

        FileConfiguration configLoad = plugin.getLanguage();

        nInventoryUtil nInv = new nInventoryUtil(player, null);

        if (playerData.getViewer() == null) {
            List<com.songoda.skyblock.generator.Generator> generators = generatorManager.getGenerators();

            nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                            configLoad.getString("Menu.Admin.Generator.Browse.Item.Exit.Displayname"), null, null, null, null),
                    0, 8);
            nInv.addItem(
                    nInv.createItem(new ItemStack(CompatibleMaterial.OAK_SIGN.getMaterial()),
                            configLoad.getString("Menu.Admin.Generator.Browse.Item.Information.Displayname"),
                            configLoad.getStringList("Menu.Admin.Generator.Browse.Item.Information.Lore"),
                            new Placeholder[]{new Placeholder("%generators", "" + generators.size())}, null, null),
                    4);
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.Admin.Generator.Browse.Item.Barrier.Displayname"), null, null, null,
                    null), 9, 10, 11, 12, 13, 14, 15, 16, 17);

            int playerMenuPage = playerData.getPage(MenuType.ADMIN_GENERATOR), nextEndIndex = generators.size() - playerMenuPage * 36;

            if (playerMenuPage != 1) {
                nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(
                                "ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=",
                                "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="),
                        configLoad.getString("Menu.Admin.Generator.Browse.Item.Previous.Displayname"), null, null, null,
                        null), 1);
            }

            if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(
                                "wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=",
                                "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="),
                        configLoad.getString("Menu.Admin.Generator.Browse.Item.Next.Displayname"), null, null, null,
                        null), 7);
            }

            if (generators.isEmpty()) {
                nInv.addItem(nInv.createItem(new ItemStack(Material.BARRIER),
                        configLoad.getString("Menu.Admin.Generator.Browse.Item.Nothing.Displayname"), null, null, null,
                        null), 31);
            } else {
                int index = playerMenuPage * 36 - 36,
                        endIndex = index >= generators.size() ? generators.size() - 1 : index + 36, inventorySlot = 17;

                for (; index < endIndex; index++) {
                    if (generators.size() > index) {
                        inventorySlot++;

                        com.songoda.skyblock.generator.Generator generator = generators.get(index);
                        nInv.addItem(nInv.createItem(generator.getMaterials().getItem(),
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Admin.Generator.Browse.Item.Generator.Displayname")
                                                .replace("%generator", generator.getName())),
                                configLoad.getStringList("Menu.Admin.Generator.Browse.Item.Generator.Lore"), null, null,
                                null), inventorySlot);
                    }
                }
            }
        } else {
            com.songoda.skyblock.generator.Generator generator = generatorManager
                    .getGenerator(((Generator.Viewer) playerData.getViewer()).getName());

            final List<String> permissionLore;

            if (generator.isPermission()) {
                permissionLore = configLoad
                        .getStringList("Menu.Admin.Generator.Generator.Item.Information.Permission.Disable.Lore");
            } else {
                permissionLore = configLoad
                        .getStringList("Menu.Admin.Generator.Generator.Item.Information.Permission.Enable.Lore");
            }

            nInv.addItem(nInv.createItem(CompatibleMaterial.MAP.getItem(),
                    configLoad.getString("Menu.Admin.Generator.Generator.Item.Information.Displayname"), permissionLore,
                    new Placeholder[]{new Placeholder("%name", generator.getName()),
                            new Placeholder("%materials", "" + generator.getGeneratorMaterials().size()),
                            new Placeholder("%permission", generator.getPermission())},
                    null, null), 4);
            nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                    configLoad.getString("Menu.Admin.Generator.Generator.Item.Return.Displayname"), null, null, null,
                    null), 0, 8);
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.Admin.Generator.Generator.Item.Barrier.Displayname"), null, null, null,
                    null), 9, 10, 11, 12, 13, 14, 15, 16, 17);

            List<GeneratorMaterial> generatorMaterials = generator.getGeneratorMaterials();

            if (generatorMaterials.isEmpty()) {
                nInv.addItem(nInv.createItem(new ItemStack(Material.BARRIER),
                        configLoad.getString("Menu.Admin.Generator.Generator.Item.Nothing.Displayname"), null, null,
                        null, null), 31);
            } else {
                int index = 0,
                        endIndex = index + 36,
                        inventorySlot = 17;

                for (; index < endIndex; index++) {
                    if (generatorMaterials.size() <= index) {
                        continue;
                    }
                    inventorySlot++;

                    GeneratorMaterial generatorMaterial = generatorMaterials.get(index);
                    nInv.addItem(nInv.createItem(generatorMaterial.getMaterials().getItem(),
                            ChatColor.translateAlternateColorCodes('&',
                                    configLoad.getString("Menu.Admin.Generator.Generator.Item.Material.Displayname")
                                            .replace("%material", generatorMaterial.getMaterials().name())),
                            configLoad.getStringList("Menu.Admin.Generator.Generator.Item.Material.Lore"),
                            new Placeholder[]{new Placeholder("%chance", "" + generatorMaterial.getChance())},
                            null, null), inventorySlot);
                }
            }
        }

        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Generator.Title")));
        nInv.setRows(6);

        Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack is = event.getCurrentItem();

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        GeneratorManager generatorManager = plugin.getGeneratorManager();
        MessageManager messageManager = plugin.getMessageManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        FileConfiguration configLoad = plugin.getLanguage();

        String inventoryName = "";
        if (ServerVersion.isServerVersionAbove(ServerVersion.V1_13)) {
            inventoryName = event.getView().getTitle();
        } else {
            try {
                inventoryName = (String) Inventory.class.getMethod("getName").invoke(event.getInventory());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (!inventoryName.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Generator.Title")))) {
            return;
        }
        event.setCancelled(true);

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

        if (!(player.hasPermission("fabledskyblock.admin.generator") || player.hasPermission("fabledskyblock.admin.*")
                || player.hasPermission("fabledskyblock.*"))) {
            messageManager.sendMessage(player,
                    configLoad.getString("Island.Admin.Generator.Permission.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

            return;
        }

        if (generatorManager == null) {
            messageManager.sendMessage(player, configLoad.getString("Island.Admin.Generator.Disabled.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

            return;
        }

        if ((event.getCurrentItem().getType() == CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getMaterial())
                && (is.hasItemMeta())) {
            if (is.getItemMeta().getDisplayName()
                    .equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Admin.Generator.Browse.Item.Barrier.Displayname")))
                    || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Generator.Generator.Item.Barrier.Displayname")))) {
                soundManager.playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                return;
            }
        } else if ((event.getCurrentItem().getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial())
                && (is.hasItemMeta())) {
            if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Generator.Browse.Item.Exit.Displayname")))) {
                soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1.0F, 1.0F);
                player.closeInventory();

                return;
            } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Generator.Generator.Item.Return.Displayname")))) {
                playerData.setViewer(null);
                soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);

                player.closeInventory();

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                return;
            }
        } else if ((event.getCurrentItem().getType() == CompatibleMaterial.OAK_SIGN.getMaterial()) && (is.hasItemMeta())
                && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                configLoad.getString("Menu.Admin.Generator.Browse.Item.Information.Displayname"))))) {
            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

            AnvilGui gui = new AnvilGui(player);
            gui.setAction(event1 -> {
                if (!(player.hasPermission("fabledskyblock.admin.generator")
                        || player.hasPermission("fabledskyblock.admin.*")
                        || player.hasPermission("fabledskyblock.*"))) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Generator.Permission.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                } else if (generatorManager.containsGenerator(gui.getInputText())) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Generator.Already.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                } else if (!gui.getInputText().replace(" ", "").matches("^[a-zA-Z0-9]+$")) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Generator.Characters.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                } else {
                    generatorManager.addGenerator(gui.getInputText(), IslandWorld.NORMAL, new ArrayList<>(), 0, false);

                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Generator.Created.Message")
                                    .replace("%generator", gui.getInputText()));
                    soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                        Config config14 = fileManager
                                .getConfig(new File(plugin.getDataFolder(), "generators.yml"));
                        FileConfiguration configLoad14 = plugin.getGenerators();

                        configLoad14.set("Generators." + gui.getInputText() + ".Name", gui.getInputText());

                        try {
                            configLoad14.save(config14.getFile());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });

                    player.closeInventory();

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                }
                player.closeInventory();
            });

            is = new ItemStack(Material.NAME_TAG);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(configLoad.getString("Menu.Admin.Generator.Browse.Item.Information.Word.Enter"));
            is.setItemMeta(im);

            gui.setInput(is);
            plugin.getGuiManager().showGUI(player, gui);

            return;
        } else if ((event.getCurrentItem().getType() == CompatibleMaterial.MAP.getMaterial())
                && (is.hasItemMeta())
                && (is.getItemMeta().getDisplayName()
                .equals(ChatColor.translateAlternateColorCodes('&', configLoad
                        .getString("Menu.Admin.Generator.Generator.Item.Information.Displayname"))))) {
            if (playerData.getViewer() == null) {
                messageManager.sendMessage(player,
                        configLoad.getString("Island.Admin.Generator.Selected.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                player.closeInventory();

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
            } else {
                String name = ((Generator.Viewer) playerData.getViewer()).getName();

                if (generatorManager.containsGenerator(name)) {
                    com.songoda.skyblock.generator.Generator generator = generatorManager.getGenerator(name);

                    generator.setPermission(!generator.isPermission());

                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                        Config config1 = fileManager
                                .getConfig(new File(plugin.getDataFolder(), "generators.yml"));
                        FileConfiguration configLoad1 = config1.getFileConfiguration();

                        configLoad1.set("Generators." + generator.getName() + ".Permission",
                                generator.isPermission());

                        try {
                            configLoad1.save(config1.getFile());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    playerData.setViewer(null);

                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Generator.Exist.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                }

                player.closeInventory();
                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
            }

            return;
        } else if ((event.getCurrentItem().getType() == Material.BARRIER) && (is.hasItemMeta())) {
            if (is.getItemMeta().getDisplayName()
                    .equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Admin.Generator.Browse.Item.Nothing.Displayname")))
                    || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Generator.Generator.Item.Nothing.Displayname")))) {
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                return;
            }
        } else if ((event.getCurrentItem().getType() == CompatibleMaterial.PLAYER_HEAD.getMaterial())
                && (is.hasItemMeta())) {
            if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Generator.Browse.Item.Previous.Displayname")))) {
                playerData.setPage(MenuType.ADMIN_GENERATOR, playerData.getPage(MenuType.ADMIN_GENERATOR) - 1);
                soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);

                player.closeInventory();

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                return;
            } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Generator.Browse.Item.Next.Displayname")))) {
                playerData.setPage(MenuType.ADMIN_GENERATOR, playerData.getPage(MenuType.ADMIN_GENERATOR) + 1);
                soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);

                player.closeInventory();

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                return;
            }
        }

        if (playerData.getViewer() != null) {
            Generator.Viewer viewer = (Viewer) playerData.getViewer();

            if (generatorManager.containsGenerator(viewer.getName())) {
                com.songoda.skyblock.generator.Generator generator = generatorManager
                        .getGenerator(viewer.getName());

                if (generator.getGeneratorMaterials() != null) {
                    for (GeneratorMaterial generatorMaterialList : generator.getGeneratorMaterials()) {
                        if ((event.getCurrentItem().getType() == generatorMaterialList.getMaterials().getMaterial())
                                && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName().equals(
                                ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                "Menu.Admin.Generator.Generator.Item.Material.Displayname")
                                        .replace("%material",
                                                generatorMaterialList.getMaterials().name()))))) {
                            if (event.getClick() == ClickType.LEFT) {
                                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                AnvilGui gui = new AnvilGui(player);
                                gui.setAction(event1 -> {
                                    if (!(player.hasPermission("fabledskyblock.admin.generator")
                                            || player.hasPermission("fabledskyblock.admin.*")
                                            || player.hasPermission("fabledskyblock.*"))) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Island.Admin.Generator.Permission.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(),
                                                1.0F, 1.0F);
                                    } else if (generatorManager.containsGenerator(gui.getInputText())) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Island.Admin.Generator.Already.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(),
                                                1.0F, 1.0F);
                                    } else if (!gui.getInputText().replace(" ", "")
                                            .matches("^[a-zA-Z0-9|.]+$")) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Island.Admin.Generator.Characters.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(),
                                                1.0F, 1.0F);
                                    } else if (!generator.getGeneratorMaterials()
                                            .contains(generatorMaterialList)) {
                                        messageManager.sendMessage(player, configLoad.getString(
                                                "Island.Admin.Generator.Material.Exist.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(),
                                                1.0F, 1.0F);
                                    } else if (!gui.getInputText().matches("-?\\d+(?:\\.\\d+)?")) {
                                        messageManager.sendMessage(player, configLoad.getString(
                                                "Island.Admin.Generator.Chance.Numerical.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(),
                                                1.0F, 1.0F);
                                    } else {
                                        double materialChance = Double.parseDouble(gui.getInputText());
                                        double totalMaterialChance = materialChance;

                                        for (GeneratorMaterial generatorMaterialList1 : generator.getGeneratorMaterials()) {
                                            if (generatorMaterialList1 != generatorMaterialList) {
                                                totalMaterialChance = totalMaterialChance
                                                        + generatorMaterialList1.getChance();
                                            }
                                        }

                                        if (totalMaterialChance > 100) {
                                            messageManager.sendMessage(player, configLoad.getString(
                                                    "Island.Admin.Generator.Chance.Over.Message"));
                                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(),
                                                    1.0F, 1.0F);
                                        } else {
                                            generatorMaterialList
                                                    .setChance(Double.valueOf(gui.getInputText()));
                                            soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(),
                                                    1.0F, 1.0F);

                                            Bukkit.getServer().getScheduler()
                                                    .runTaskAsynchronously(plugin, () -> {
                                                        Config config12 = fileManager.getConfig(
                                                                new File(plugin.getDataFolder(),
                                                                        "generators.yml"));
                                                        FileConfiguration configLoad12 = config12
                                                                .getFileConfiguration();

                                                        configLoad12.set("Generators."
                                                                + generator.getName() + ".Materials."
                                                                + generatorMaterialList.getMaterials()
                                                                .name()
                                                                + ".Chance", materialChance);

                                                        try {
                                                            configLoad12.save(config12.getFile());
                                                        } catch (IOException ex) {
                                                            ex.printStackTrace();
                                                        }
                                                    });

                                            player.closeInventory();

                                            Bukkit.getServer().getScheduler()
                                                    .runTaskLater(plugin, () -> open(player), 1L);
                                        }
                                    }
                                    player.closeInventory();
                                });

                                is = new ItemStack(Material.NAME_TAG);
                                ItemMeta im = is.getItemMeta();
                                im.setDisplayName(configLoad.getString("Menu.Admin.Generator.Generator.Item.Material.Word.Enter"));
                                is.setItemMeta(im);

                                gui.setInput(is);
                                plugin.getGuiManager().showGUI(player, gui);
                            } else if (event.getClick() == ClickType.RIGHT) {
                                generator.getGeneratorMaterials().remove(generatorMaterialList);

                                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin,
                                        () -> {
                                            Config config15 = fileManager.getConfig(
                                                    new File(plugin.getDataFolder(), "generators.yml"));
                                            FileConfiguration configLoad15 = config15.getFileConfiguration();

                                            configLoad15.set(
                                                    "Generators." + generator.getName() + ".Materials."
                                                            + generatorMaterialList.getMaterials().name(),
                                                    null);

                                            try {
                                                configLoad15.save(config15.getFile());
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                        });

                                soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
                                player.closeInventory();

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                            }

                            return;
                        }
                    }
                }

                if (generator.getGeneratorMaterials() != null
                        && generator.getGeneratorMaterials().size() == 36) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Generator.Material.Limit.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                } else {
                    CompatibleMaterial materials = CompatibleMaterial.getMaterial(event.getCurrentItem().getType());
                    materials.getItem().setData(event.getCurrentItem().getData());

                    for (GeneratorMaterial generatorMaterialList : generator.getGeneratorMaterials()) {
                        if (generatorMaterialList.getMaterials() == materials) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Generator.Material.Already.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            return;
                        }
                    }

                    generator.getGeneratorMaterials().add(new GeneratorMaterial(materials, 0));

                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Generator.Material.Added.Message")
                                    .replace("%material", materials.name())
                                    .replace("%generator", generator.getName()));
                    soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                        Config config16 = fileManager
                                .getConfig(new File(plugin.getDataFolder(), "generators.yml"));
                        FileConfiguration configLoad16 = config16.getFileConfiguration();

                        configLoad16.set("Generators." + generator.getName() + ".Materials."
                                + materials.name() + ".Chance", 0);

                        try {
                            configLoad16.save(config16.getFile());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });

                    player.closeInventory();

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                }

                return;
            } else {
                playerData.setViewer(null);

                messageManager.sendMessage(player,
                        configLoad.getString("Island.Admin.Generator.Exist.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                player.closeInventory();

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
            }

            return;
        }

        if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            for (com.songoda.skyblock.generator.Generator generatorList : generatorManager.getGenerators()) {
                if (event.getCurrentItem().getType() == generatorList.getMaterials().getMaterial()
                        && ChatColor.stripColor(is.getItemMeta().getDisplayName())
                        .equals(generatorList.getName())) {
                    if (event.getClick() == ClickType.LEFT) {
                        playerData.setViewer(new Viewer(generatorList.getName()));
                        soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else if (event.getClick() == ClickType.RIGHT) {
                        generatorManager.removeGenerator(generatorList);

                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Generator.Removed.Message")
                                        .replace("%generator", generatorList.getName()));
                        soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);

                        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                            Config config13 = fileManager
                                    .getConfig(new File(plugin.getDataFolder(), "generators.yml"));
                            FileConfiguration configLoad13 = config13.getFileConfiguration();

                            configLoad13.set("Generators." + generatorList.getName(), null);

                            try {
                                configLoad13.save(config13.getFile());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        });

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    }

                    return;
                }
            }

            messageManager.sendMessage(player, configLoad.getString("Island.Admin.Generator.Exist.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

            open(player);
        }
    }

    public static class Viewer {
        private final String name;

        public Viewer(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
