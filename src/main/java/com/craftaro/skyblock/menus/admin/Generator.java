package com.craftaro.skyblock.menus.admin;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.gui.AnvilGui;
import com.craftaro.core.utils.SkullItemCreator;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.config.FileManager.Config;
import com.craftaro.skyblock.generator.GeneratorManager;
import com.craftaro.skyblock.generator.GeneratorMaterial;
import com.craftaro.skyblock.island.IslandWorld;
import com.craftaro.skyblock.menus.MenuType;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.placeholder.Placeholder;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.item.nInventoryUtil;
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
            List<com.craftaro.skyblock.generator.Generator> generators = generatorManager.getGenerators();

            nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Admin.Generator.Browse.Item.Exit.Displayname"), null, null, null, null),
                    0, 8);
            nInv.addItem(
                    nInv.createItem(new ItemStack(XMaterial.OAK_SIGN.parseMaterial()),
                            configLoad.getString("Menu.Admin.Generator.Browse.Item.Information.Displayname"),
                            configLoad.getStringList("Menu.Admin.Generator.Browse.Item.Information.Lore"),
                            new Placeholder[]{new Placeholder("%generators", "" + generators.size())}, null, null),
                    4);
            nInv.addItem(nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                    configLoad.getString("Menu.Admin.Generator.Browse.Item.Barrier.Displayname"), null, null, null,
                    null), 9, 10, 11, 12, 13, 14, 15, 16, 17);

            int playerMenuPage = playerData.getPage(MenuType.ADMIN_GENERATOR), nextEndIndex = generators.size() - playerMenuPage * 36;

            if (playerMenuPage != 1) {
                ItemStack Lhead = SkullItemCreator.byTextureHash("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
                nInv.addItem(nInv.createItem(Lhead,
                        configLoad.getString("Menu.Admin.Generator.Browse.Item.Previous.Displayname"), null, null, null,
                        null), 1);
            }

            if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                ItemStack Rhead = SkullItemCreator.byTextureHash("1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b");
                nInv.addItem(nInv.createItem(Rhead,
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

                        com.craftaro.skyblock.generator.Generator generator = generators.get(index);
                        nInv.addItem(nInv.createItem(generator.getMaterials().parseItem(),
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Admin.Generator.Browse.Item.Generator.Displayname")
                                                .replace("%generator", generator.getName())),
                                configLoad.getStringList("Menu.Admin.Generator.Browse.Item.Generator.Lore"), null, null,
                                null), inventorySlot);
                    }
                }
            }
        } else {
            com.craftaro.skyblock.generator.Generator generator = generatorManager
                    .getGenerator(((Generator.Viewer) playerData.getViewer()).getName());

            final List<String> permissionLore;

            if (generator.isPermission()) {
                permissionLore = configLoad
                        .getStringList("Menu.Admin.Generator.Generator.Item.Information.Permission.Disable.Lore");
            } else {
                permissionLore = configLoad
                        .getStringList("Menu.Admin.Generator.Generator.Item.Information.Permission.Enable.Lore");
            }

            nInv.addItem(nInv.createItem(XMaterial.MAP.parseItem(),
                    configLoad.getString("Menu.Admin.Generator.Generator.Item.Information.Displayname"), permissionLore,
                    new Placeholder[]{new Placeholder("%name", generator.getName()),
                            new Placeholder("%materials", "" + generator.getGeneratorMaterials().size()),
                            new Placeholder("%permission", generator.getPermission())},
                    null, null), 4);
            nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                    configLoad.getString("Menu.Admin.Generator.Generator.Item.Return.Displayname"), null, null, null,
                    null), 0, 8);
            nInv.addItem(nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
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
                    nInv.addItem(nInv.createItem(generatorMaterial.getMaterials().parseItem(),
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
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

            return;
        }

        if (generatorManager == null) {
            messageManager.sendMessage(player, configLoad.getString("Island.Admin.Generator.Disabled.Message"));
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

            return;
        }

        if ((event.getCurrentItem().getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial())
                && (is.hasItemMeta())) {
            if (is.getItemMeta().getDisplayName()
                    .equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Admin.Generator.Browse.Item.Barrier.Displayname")))
                    || is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Generator.Generator.Item.Barrier.Displayname")))) {
                soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                return;
            }
        } else if ((event.getCurrentItem().getType() == XMaterial.OAK_FENCE_GATE.parseMaterial())
                && (is.hasItemMeta())) {
            if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Generator.Browse.Item.Exit.Displayname")))) {
                soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
                player.closeInventory();

                return;
            } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Generator.Generator.Item.Return.Displayname")))) {
                playerData.setViewer(null);
                soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                player.closeInventory();

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                return;
            }
        } else if ((event.getCurrentItem().getType() == XMaterial.OAK_SIGN.parseMaterial()) && (is.hasItemMeta())
                && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                configLoad.getString("Menu.Admin.Generator.Browse.Item.Information.Displayname"))))) {
            soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

            AnvilGui gui = new AnvilGui(player);
            gui.setAction(event1 -> {
                if (!(player.hasPermission("fabledskyblock.admin.generator")
                        || player.hasPermission("fabledskyblock.admin.*")
                        || player.hasPermission("fabledskyblock.*"))) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Generator.Permission.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                } else if (generatorManager.containsGenerator(gui.getInputText())) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Generator.Already.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                } else if (!gui.getInputText().replace(" ", "").matches("^[a-zA-Z0-9]+$")) {
                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Generator.Characters.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                } else {
                    generatorManager.addGenerator(gui.getInputText(), IslandWorld.NORMAL, new ArrayList<>(), 0, false);

                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Generator.Created.Message")
                                    .replace("%generator", gui.getInputText()));
                    soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

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
        } else if ((event.getCurrentItem().getType() == XMaterial.MAP.parseMaterial())
                && (is.hasItemMeta())
                && (is.getItemMeta().getDisplayName()
                .equals(ChatColor.translateAlternateColorCodes('&', configLoad
                        .getString("Menu.Admin.Generator.Generator.Item.Information.Displayname"))))) {
            if (playerData.getViewer() == null) {
                messageManager.sendMessage(player,
                        configLoad.getString("Island.Admin.Generator.Selected.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                player.closeInventory();

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
            } else {
                String name = ((Generator.Viewer) playerData.getViewer()).getName();

                if (generatorManager.containsGenerator(name)) {
                    com.craftaro.skyblock.generator.Generator generator = generatorManager.getGenerator(name);

                    generator.setPermission(!generator.isPermission());

                    soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

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
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                return;
            }
        } else if ((event.getCurrentItem().getType() == XMaterial.PLAYER_HEAD.parseMaterial())
                && (is.hasItemMeta())) {
            if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Generator.Browse.Item.Previous.Displayname")))) {
                playerData.setPage(MenuType.ADMIN_GENERATOR, playerData.getPage(MenuType.ADMIN_GENERATOR) - 1);
                soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                player.closeInventory();

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                return;
            } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Generator.Browse.Item.Next.Displayname")))) {
                playerData.setPage(MenuType.ADMIN_GENERATOR, playerData.getPage(MenuType.ADMIN_GENERATOR) + 1);
                soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                player.closeInventory();

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                return;
            }
        }

        if (playerData.getViewer() != null) {
            Generator.Viewer viewer = (Viewer) playerData.getViewer();

            if (generatorManager.containsGenerator(viewer.getName())) {
                com.craftaro.skyblock.generator.Generator generator = generatorManager
                        .getGenerator(viewer.getName());

                if (generator.getGeneratorMaterials() != null) {
                    for (GeneratorMaterial generatorMaterialList : generator.getGeneratorMaterials()) {
                        if ((generatorMaterialList.getMaterials().isSimilar(event.getCurrentItem()))
                                && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName().equals(
                                ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                "Menu.Admin.Generator.Generator.Item.Material.Displayname")
                                        .replace("%material",
                                                generatorMaterialList.getMaterials().name()))))) {
                            if (event.getClick() == ClickType.LEFT) {
                                soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                                AnvilGui gui = new AnvilGui(player);
                                gui.setAction(event1 -> {
                                    if (!(player.hasPermission("fabledskyblock.admin.generator")
                                            || player.hasPermission("fabledskyblock.admin.*")
                                            || player.hasPermission("fabledskyblock.*"))) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Island.Admin.Generator.Permission.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                    } else if (generatorManager.containsGenerator(gui.getInputText())) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Island.Admin.Generator.Already.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                    } else if (!gui.getInputText().replace(" ", "")
                                            .matches("^[a-zA-Z0-9|.]+$")) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Island.Admin.Generator.Characters.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                    } else if (!generator.getGeneratorMaterials()
                                            .contains(generatorMaterialList)) {
                                        messageManager.sendMessage(player, configLoad.getString(
                                                "Island.Admin.Generator.Material.Exist.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                    } else if (!gui.getInputText().matches("-?\\d+(?:\\.\\d+)?")) {
                                        messageManager.sendMessage(player, configLoad.getString(
                                                "Island.Admin.Generator.Chance.Numerical.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
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
                                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                        } else {
                                            generatorMaterialList
                                                    .setChance(Double.valueOf(gui.getInputText()));
                                            soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

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

                                soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);
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
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                } else {
                    XMaterial materials = CompatibleMaterial.getMaterial(event.getCurrentItem().getType()).get();
                    materials.parseItem().setData(event.getCurrentItem().getData());

                    for (GeneratorMaterial generatorMaterialList : generator.getGeneratorMaterials()) {
                        if (generatorMaterialList.getMaterials() == materials) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Generator.Material.Already.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            return;
                        }
                    }

                    generator.getGeneratorMaterials().add(new GeneratorMaterial(materials, 0));

                    messageManager.sendMessage(player,
                            configLoad.getString("Island.Admin.Generator.Material.Added.Message")
                                    .replace("%material", materials.name())
                                    .replace("%generator", generator.getName()));
                    soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

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
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                player.closeInventory();

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
            }

            return;
        }

        if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
            for (com.craftaro.skyblock.generator.Generator generatorList : generatorManager.getGenerators()) {
                if (generatorList.getMaterials().isSimilar(event.getCurrentItem())
                        && ChatColor.stripColor(is.getItemMeta().getDisplayName())
                        .equals(generatorList.getName())) {
                    if (event.getClick() == ClickType.LEFT) {
                        playerData.setViewer(new Viewer(generatorList.getName()));
                        soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else if (event.getClick() == ClickType.RIGHT) {
                        generatorManager.removeGenerator(generatorList);

                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Generator.Removed.Message")
                                        .replace("%generator", generatorList.getName()));
                        soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);

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
            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

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
