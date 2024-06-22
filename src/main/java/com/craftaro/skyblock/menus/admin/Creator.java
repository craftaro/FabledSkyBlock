package com.craftaro.skyblock.menus.admin;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.gui.AnvilGui;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.config.FileManager.Config;
import com.craftaro.skyblock.menus.MenuType;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.placeholder.Placeholder;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.structure.Structure;
import com.craftaro.skyblock.structure.StructureManager;
import com.craftaro.skyblock.utils.item.nInventoryUtil;
import com.craftaro.third_party.com.cryptomorin.xseries.profiles.builder.XSkull;
import com.craftaro.third_party.com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.craftaro.third_party.com.cryptomorin.xseries.profiles.objects.Profileable;
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
import org.bukkit.inventory.meta.SkullMeta;

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
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        StructureManager structureManager = plugin.getStructureManager();
        FileManager fileManager = plugin.getFileManager();

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

        FileConfiguration configLoad = plugin.getLanguage();

        nInventoryUtil nInv = new nInventoryUtil(player, null);

        if (playerData.getViewer() == null) {
            List<Structure> structures = structureManager.getStructures();

            nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Admin.Creator.Browse.Item.Exit.Displayname"), null, null, null, null), 0,
                    8);
            nInv.addItem(
                    nInv.createItem(new ItemStack(XMaterial.OAK_SIGN.parseItem()),
                            configLoad.getString("Menu.Admin.Creator.Browse.Item.Information.Displayname"),
                            configLoad.getStringList("Menu.Admin.Creator.Browse.Item.Information.Lore"),
                            new Placeholder[]{new Placeholder("%structures", "" + structures.size())}, null, null),
                    4);
            nInv.addItem(nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                            configLoad.getString("Menu.Admin.Creator.Browse.Item.Barrier.Displayname"), null, null, null, null),
                    9, 10, 11, 12, 13, 14, 15, 16, 17);

            int playerMenuPage = playerData.getPage(MenuType.ADMIN_CREATOR), nextEndIndex = structures.size() - playerMenuPage * 36;

            if (playerMenuPage != 1) {
                ItemStack Lhead = XSkull.createItem().profile(new Profileable.StringProfileable("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23", ProfileInputType.TEXTURE_HASH)).apply();
                nInv.addItem(nInv.createItem(Lhead,
                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Previous.Displayname"), null, null, null,
                        null), 1);
            }

            if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                ItemStack Rhead = XSkull.createItem().profile(new Profileable.StringProfileable("1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b", ProfileInputType.TEXTURE_HASH)).apply();
                nInv.addItem(nInv.createItem(Rhead,
                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Next.Displayname"), null, null, null,
                        null), 7);
            }

            if (structures.isEmpty()) {
                nInv.addItem(nInv.createItem(new ItemStack(XMaterial.BARRIER.parseMaterial()),
                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Nothing.Displayname"), null, null, null,
                        null), 31);
            } else {
                int index = playerMenuPage * 36 - 36,
                        endIndex = index >= structures.size() ? structures.size() - 1 : index + 36, inventorySlot = 17;

                for (; index < endIndex; index++) {
                    if (structures.size() > index) {
                        inventorySlot++;

                        Structure structure = structures.get(index);
                        nInv.addItem(nInv.createItem(structure.getMaterial().parseItem(),
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

            nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Admin.Creator.Options.Item.Return.Displayname"), null, null, null, null),
                    0, 8);

            String displayName = ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Admin.Creator.Options.Item.Word.Unset"));

            if (structure.getDisplayname() != null && !structure.getDisplayname().isEmpty()) {
                displayName = ChatColor.translateAlternateColorCodes('&', structure.getDisplayname());
            }

            nInv.addItem(nInv.createItem(new ItemStack(XMaterial.NAME_TAG.parseMaterial()),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.Displayname.Displayname"),
                    configLoad.getStringList("Menu.Admin.Creator.Options.Item.Displayname.Lore"),
                    new Placeholder[]{new Placeholder("%displayname", displayName)}, null, null), 1);

            List<String> descriptionLore = new ArrayList<>();

            if (structure.getDescription() == null || structure.getDescription().isEmpty()) {
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

            nInv.addItem(nInv.createItem(new ItemStack(XMaterial.ENCHANTED_BOOK.parseMaterial()),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.Description.Displayname"), descriptionLore,
                    null, null, null), 2);

            List<String> commandsLore = new ArrayList<>();

            if (structure.getCommands() == null || structure.getCommands().isEmpty()) {
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

            nInv.addItem(nInv.createItem(new ItemStack(XMaterial.BOOK.parseMaterial()),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.Commands.Displayname"), commandsLore, null,
                    null, null), 3);

            List<String> permissionLore;
            if (structure.isPermission()) {
                permissionLore = configLoad.getStringList("Menu.Admin.Creator.Options.Item.Permission.Disable.Lore");
            } else {
                permissionLore = configLoad.getStringList("Menu.Admin.Creator.Options.Item.Permission.Enable.Lore");
            }

            nInv.addItem(nInv.createItem(XMaterial.MAP.parseItem(),
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

            nInv.addItem(nInv.createItem(new ItemStack(XMaterial.PAPER.parseMaterial()),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.File.Displayname"),
                    configLoad.getStringList("Menu.Admin.Creator.Options.Item.File.Lore"),
                    new Placeholder[]{new Placeholder("%overworld_file", overworldFileName),
                            new Placeholder("%nether_file", netherFileName),
                            new Placeholder("%end_file", endFileName)},
                    null, null), 5);
            nInv.addItem(nInv.createItem(new ItemStack(XMaterial.DIAMOND.parseMaterial()),
                            configLoad.getString("Menu.Admin.Creator.Options.Item.Item.Displayname"),
                            configLoad.getStringList("Menu.Admin.Creator.Options.Item.Item.Lore"),
                            new Placeholder[]{new Placeholder("%material", structure.getMaterial().name())}, null, null),
                    6);
            nInv.addItem(nInv.createItem(new ItemStack(XMaterial.GOLD_NUGGET.parseMaterial()),
                    configLoad.getString("Menu.Admin.Creator.Options.Item.DeletionCost.Displayname"),
                    configLoad.getStringList("Menu.Admin.Creator.Options.Item.DeletionCost.Lore"),
                    new Placeholder[]{new Placeholder("%cost", "" + structure.getDeletionCost())}, null, null), 7);

            nInv.setRows(1);
        }

        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Creator.Title")));

        Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack is = event.getCurrentItem();

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != XMaterial.AIR.parseMaterial()) {
            SkyBlock plugin = SkyBlock.getInstance();

            StructureManager structureManager = plugin.getStructureManager();
            MessageManager messageManager = plugin.getMessageManager();
            SoundManager soundManager = plugin.getSoundManager();
            FileManager fileManager = plugin.getFileManager();

            Config config = fileManager.getConfig(new File(plugin.getDataFolder(), "language.yml"));
            FileConfiguration configLoad = config.getFileConfiguration();

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

            if (inventoryName.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Creator.Title")))) {
                event.setCancelled(true);

                PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

                if (!(player.hasPermission("fabledskyblock.admin.create") || player.hasPermission("fabledskyblock.admin.*")
                        || player.hasPermission("fabledskyblock.*"))) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Admin.Creator.Permission.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    return;
                }

                if ((event.getCurrentItem().getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial())
                        && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Barrier.Displayname"))))) {
                    soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                    return;
                } else if ((event.getCurrentItem().getType() == XMaterial.OAK_FENCE_GATE.parseMaterial())
                        && (is.hasItemMeta())) {
                    if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Admin.Creator.Browse.Item.Exit.Displayname")))) {
                        soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
                        player.closeInventory();

                        return;
                    } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Admin.Creator.Options.Item.Return.Displayname")))) {
                        playerData.setViewer(null);
                        soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                        return;
                    }
                } else if ((event.getCurrentItem().getType() == XMaterial.OAK_SIGN.parseMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Information.Displayname"))))) {
                    soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                    AnvilGui gui = new AnvilGui(player);
                    gui.setAction(event1 -> {
                        if (!(player.hasPermission("fabledskyblock.admin.creator")
                                || player.hasPermission("fabledskyblock.admin.*")
                                || player.hasPermission("fabledskyblock.*"))) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Permission.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else if (structureManager.containsStructure(gui.getInputText())) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Already.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else if (!gui.getInputText().replace(" ", "").matches("^[a-zA-Z0-9]+$")) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Characters.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else {
                            structureManager.addStructure(gui.getInputText(), XMaterial.GRASS_BLOCK, null, null, null,
                                    null, false, new ArrayList<>(), new ArrayList<>(), 0.0D);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Created.Message")
                                            .replace("%structure", gui.getInputText()));
                            soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                Config config111 = fileManager
                                        .getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                FileConfiguration configLoad111 = config111.getFileConfiguration();

                                configLoad111.set("Structures." + gui.getInputText() + ".Name", gui.getInputText());

                                try {
                                    configLoad111.save(config111.getFile());
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }
                        player.closeInventory();
                    });

                    is = new ItemStack(XMaterial.NAME_TAG.parseMaterial());
                    ItemMeta im = is.getItemMeta();
                    im.setDisplayName(configLoad.getString("Menu.Admin.Creator.Browse.Item.Information.Word.Enter"));
                    is.setItemMeta(im);

                    gui.setInput(is);
                    plugin.getGuiManager().showGUI(player, gui);

                    return;
                } else if ((event.getCurrentItem().getType() == XMaterial.BARRIER.parseMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Browse.Item.Nothing.Displayname"))))) {
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    return;
                } else if ((event.getCurrentItem().getType() == XMaterial.NAME_TAG.parseMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Options.Item.Displayname.Displayname"))))) {
                    if (playerData.getViewer() == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else {
                        String name = ((Creator.Viewer) playerData.getViewer()).getName();

                        if (structureManager.containsStructure(name)) {
                            soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                            AnvilGui gui = new AnvilGui(player);
                            gui.setAction(event1 -> {

                                if (!(player.hasPermission("fabledskyblock.admin.creator")
                                        || player.hasPermission("fabledskyblock.admin.*")
                                        || player.hasPermission("fabledskyblock.*"))) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Permission.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                } else if (playerData.getViewer() == null) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Selected.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else if (!structureManager.containsStructure(name)) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else {
                                    Structure structure = structureManager.getStructure(name);
                                    structure.setDisplayname(gui.getInputText());

                                    soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                                    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin,
                                            () -> {
                                                Config config1 = fileManager.getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                                FileConfiguration configLoad1 = config1.getFileConfiguration();

                                                configLoad1.set("Structures." + structure.getName() + ".Displayname", gui.getInputText());

                                                try {
                                                    configLoad1.save(config1.getFile());
                                                } catch (IOException ex) {
                                                    ex.printStackTrace();
                                                }
                                            });

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                }
                                player.closeInventory();
                            });

                            is = new ItemStack(XMaterial.NAME_TAG.parseMaterial());
                            ItemMeta im = is.getItemMeta();
                            im.setDisplayName(
                                    configLoad.getString("Menu.Admin.Creator.Options.Item.Displayname.Word.Enter"));
                            is.setItemMeta(im);

                            gui.setInput(is);
                            plugin.getGuiManager().showGUI(player, gui);
                        } else {
                            playerData.setViewer(null);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }
                    }

                    return;
                } else if ((event.getCurrentItem().getType() == XMaterial.ENCHANTED_BOOK.parseMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Options.Item.Description.Displayname"))))) {
                    if (playerData.getViewer() == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else {
                        String name = ((Creator.Viewer) playerData.getViewer()).getName();

                        if (structureManager.containsStructure(name)) {
                            Structure structure = structureManager.getStructure(name);

                            if (structure.getDescription() != null && !structure.getDescription().isEmpty()) {
                                if (event.getClick() == ClickType.RIGHT) {
                                    structure.removeLine(structure.getDescription().size() - 1);
                                    soundManager.playSound(player, XSound.ENTITY_GENERIC_EXPLODE);

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

                            soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                            AnvilGui gui = new AnvilGui(player);
                            gui.setAction(event1 -> {

                                if (!(player.hasPermission("fabledskyblock.admin.creator")
                                        || player.hasPermission("fabledskyblock.admin.*")
                                        || player.hasPermission("fabledskyblock.*"))) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Permission.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                } else if (playerData.getViewer() == null) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Selected.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else if (!structureManager.containsStructure(name)) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else {
                                    structure.addLine(gui.getInputText());

                                    soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

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
                                                } catch (IOException ex) {
                                                    ex.printStackTrace();
                                                }
                                            });

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                }
                                player.closeInventory();
                            });

                            is = new ItemStack(XMaterial.NAME_TAG.parseMaterial());
                            ItemMeta im = is.getItemMeta();
                            im.setDisplayName(
                                    configLoad.getString("Menu.Admin.Creator.Options.Item.Description.Word.Enter"));
                            is.setItemMeta(im);

                            gui.setInput(is);
                            plugin.getGuiManager().showGUI(player, gui);

                        } else {
                            playerData.setViewer(null);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }
                    }

                    return;
                } else if ((event.getCurrentItem().getType() == XMaterial.BOOK.parseMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Options.Item.Commands.Displayname"))))) {
                    if (playerData.getViewer() == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else {
                        String name = ((Creator.Viewer) playerData.getViewer()).getName();

                        if (structureManager.containsStructure(name)) {
                            Structure structure = structureManager.getStructure(name);

                            if (structure.getCommands() != null && !structure.getCommands().isEmpty()) {
                                if (event.getClick() == ClickType.RIGHT) {
                                    structure.removeCommand(structure.getCommands().size() - 1);
                                    soundManager.playSound(player, XSound.ENTITY_GENERIC_EXPLODE);

                                    Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                        Config config14 = fileManager
                                                .getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                        FileConfiguration configLoad14 = config14.getFileConfiguration();

                                        configLoad14.set("Structures." + structure.getName() + ".Commands",
                                                structure.getCommands());

                                        try {
                                            configLoad14.save(config14.getFile());
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                    });

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                                    return;
                                } else if (event.getClick() != ClickType.LEFT) {
                                    return;
                                }
                            }

                            soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                            AnvilGui gui = new AnvilGui(player);
                            gui.setAction(event1 -> {
                                if (!(player.hasPermission("fabledskyblock.admin.creator")
                                        || player.hasPermission("fabledskyblock.admin.*")
                                        || player.hasPermission("fabledskyblock.*"))) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Permission.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                } else if (playerData.getViewer() == null) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Selected.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else if (!structureManager.containsStructure(name)) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else {
                                    structure.addCommand(gui.getInputText());

                                    soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

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
                                                } catch (IOException ex) {
                                                    ex.printStackTrace();
                                                }
                                            });

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                }
                                player.closeInventory();
                            });

                            is = new ItemStack(XMaterial.NAME_TAG.parseMaterial());
                            ItemMeta im = is.getItemMeta();
                            im.setDisplayName(
                                    configLoad.getString("Menu.Admin.Creator.Options.Item.Commands.Word.Enter"));
                            is.setItemMeta(im);

                            gui.setInput(is);
                            plugin.getGuiManager().showGUI(player, gui);

                        } else {
                            playerData.setViewer(null);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }
                    }

                    return;
                } else if ((event.getCurrentItem().

                        getType() == XMaterial.MAP.parseMaterial())
                        && (is.hasItemMeta())
                        && (is.getItemMeta().

                        getDisplayName().

                        equals(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Menu.Admin.Creator.Options.Item.Permission.Displayname"))))) {
                    if (playerData.getViewer() == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else {
                        String name = ((Creator.Viewer) playerData.getViewer()).getName();

                        if (structureManager.containsStructure(name)) {
                            Structure structure = structureManager.getStructure(name);

                            structure.setPermission(!structure.isPermission());

                            soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

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
                        } else {
                            playerData.setViewer(null);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        }

                        player.closeInventory();
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    }

                    return;
                } else if ((event.getCurrentItem().

                        getType() == XMaterial.PAPER.parseMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().

                        getDisplayName().

                        equals(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Menu.Admin.Creator.Options.Item.File.Displayname"))))) {
                    if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.MIDDLE
                            || event.getClick() == ClickType.RIGHT) {
                        if (playerData.getViewer() == null) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Selected.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            player.closeInventory();

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else {
                            String name = ((Creator.Viewer) playerData.getViewer()).getName();

                            if (structureManager.containsStructure(name)) {
                                soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                                AnvilGui gui = new AnvilGui(player);
                                gui.setAction(event1 -> {

                                    if (!(player.hasPermission("fabledskyblock.admin.creator")
                                            || player.hasPermission("fabledskyblock.admin.*")
                                            || player.hasPermission("fabledskyblock.*"))) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Permission.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                    } else if (playerData.getViewer() == null) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    } else if (!structureManager.containsStructure(name)) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    } else {
                                        String fileName = gui.getInputText();
                                        if (fileManager.isFileExist(new File(plugin.getDataFolder() + "/structures", fileName)) ||
                                                fileManager.isFileExist(new File(plugin.getDataFolder() + "/schematics", fileName))) {
                                            if (event.getClick() == ClickType.LEFT) {
                                                Structure structure = structureManager.getStructure(name);
                                                structure.setOverworldFile(fileName);

                                                soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

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

                                                soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

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
                                                            } catch (IOException ex) {
                                                                ex.printStackTrace();
                                                            }
                                                        });
                                            } else {
                                                Structure structure = structureManager.getStructure(name);
                                                structure.setEndFile(fileName);

                                                soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

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
                                                            } catch (IOException ex) {
                                                                ex.printStackTrace();
                                                            }
                                                        });
                                            }
                                        } else {
                                            messageManager.sendMessage(player, configLoad.getString("Island.Admin.Creator.File.Message"));
                                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                        }

                                        player.closeInventory();

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    }
                                    player.closeInventory();
                                });

                                is = new ItemStack(XMaterial.NAME_TAG.parseMaterial());
                                ItemMeta im = is.getItemMeta();
                                im.setDisplayName(
                                        configLoad.getString("Menu.Admin.Creator.Options.Item.File.Word.Enter"));
                                is.setItemMeta(im);

                                gui.setInput(is);
                                plugin.getGuiManager().showGUI(player, gui);

                            } else {
                                playerData.setViewer(null);

                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                player.closeInventory();

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                            }
                        }
                    }

                    return;
                } else if ((event.getCurrentItem().getType() == XMaterial.DIAMOND.parseMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Options.Item.Item.Displayname"))))) {
                    if (playerData.getViewer() == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else {
                        Creator.Viewer viewer = (Viewer) playerData.getViewer();
                        String name = viewer.getName();

                        if (viewer.isItem()) {
                            viewer.setItem(false);
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Item.Cancelled.Message"));
                            soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);
                        } else {
                            if (structureManager.containsStructure(name)) {
                                viewer.setItem(true);
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Creator.Item.Added.Message"));
                                soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);
                            } else {
                                playerData.setViewer(null);

                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                player.closeInventory();

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                            }
                        }
                    }

                    return;
                } else if ((event.getCurrentItem().getType() == XMaterial.GOLD_NUGGET.parseMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Creator.Options.Item.DeletionCost.Displayname"))))) {
                    if (playerData.getViewer() == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Selected.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        player.closeInventory();

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else {
                        String name = ((Creator.Viewer) playerData.getViewer()).getName();

                        if (structureManager.containsStructure(name)) {
                            soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                            AnvilGui gui = new AnvilGui(player);
                            gui.setAction(event1 -> {

                                if (!(player.hasPermission("fabledskyblock.admin.creator")
                                        || player.hasPermission("fabledskyblock.admin.*")
                                        || player.hasPermission("fabledskyblock.*"))) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Permission.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    return;
                                } else if (playerData.getViewer() == null) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Selected.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);

                                    return;
                                } else if (!structureManager.containsStructure(name)) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Exist.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    player.closeInventory();

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                                    return;
                                } else if (!(gui.getInputText().matches("[0-9]+")
                                        || gui.getInputText().matches("([0-9]*)\\.([0-9]{1,2}$)"))) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Admin.Creator.Numerical.Message"));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    player.closeInventory();

                                    return;
                                }

                                double deletionCost = Double.parseDouble(gui.getInputText());

                                Structure structure = structureManager.getStructure(name);
                                structure.setDeletionCost(deletionCost);

                                soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                    Config config112 = fileManager
                                            .getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                    FileConfiguration configLoad112 = config112.getFileConfiguration();

                                    configLoad112.set("Structures." + structure.getName() + ".Deletion.Cost",
                                            deletionCost);

                                    try {
                                        configLoad112.save(config112.getFile());
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                });

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player), 1L);
                                player.closeInventory();
                            });

                            is = new ItemStack(XMaterial.NAME_TAG.parseMaterial());
                            ItemMeta im = is.getItemMeta();
                            im.setDisplayName(
                                    configLoad.getString("Menu.Admin.Creator.Options.Item.DeletionCost.Word.Enter"));
                            is.setItemMeta(im);

                            gui.setInput(is);
                            plugin.getGuiManager().showGUI(player, gui);

                        } else {
                            playerData.setViewer(null);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

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
                            XMaterial materials = CompatibleMaterial.getMaterial(event.getCurrentItem().getType()).get();
                            materials.parseItem().setData(event.getCurrentItem().getData());

                            structure.setMaterial(materials);

                            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                Config config113 = fileManager
                                        .getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                FileConfiguration configLoad113 = config113.getFileConfiguration();

                                configLoad113.set("Structures." + structure.getName() + ".Item.Material",
                                        structure.getMaterial().name());

                                try {
                                    configLoad113.save(config113.getFile());
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });

                            viewer.setItem(false);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Item.Removed.Message"));
                            soundManager.playSound(player, XSound.ENTITY_PLAYER_LEVELUP);
                        } else {
                            playerData.setViewer(null);

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Creator.Exist.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        }

                        player.closeInventory();
                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                        return;
                    }
                }

                if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                    for (Structure structureList : structureManager.getStructures()) {
                        if (structureList.getMaterial().isSimilar(event.getCurrentItem())
                                && ChatColor.stripColor(is.getItemMeta().getDisplayName())
                                .equals(structureList.getName())) {
                            if (event.getClick() == ClickType.LEFT) {
                                playerData.setViewer(new Viewer(structureList.getName()));
                                soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                                player.closeInventory();

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                            } else if (event.getClick() == ClickType.RIGHT) {
                                structureManager.removeStructure(structureList);

                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Creator.Removed.Message")
                                                .replace("%structure", structureList.getName()));
                                soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);

                                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                    Config config110 = fileManager
                                            .getConfig(new File(plugin.getDataFolder(), "structures.yml"));
                                    FileConfiguration configLoad110 = config110.getFileConfiguration();

                                    configLoad110.set("Structures." + structureList.getName(), null);

                                    try {
                                        configLoad110.save(config110.getFile());
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

                    messageManager.sendMessage(player, configLoad.getString("Island.Admin.Creator.Exist.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    player.closeInventory();

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

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

        if (inventoryName.equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Admin.Creator.Title")))) {
            PlayerDataManager playerDataManager = plugin.getPlayerDataManager();

            if (playerDataManager.hasPlayerData(player)) {
                Creator.Viewer viewer = (Viewer) playerDataManager.getPlayerData(player).getViewer();

                if (viewer != null) {
                    if (viewer.isItem()) {
                        viewer.setItem(false);
                        plugin.getMessageManager().sendMessage(player,
                                configLoad.getString("Island.Admin.Creator.Item.Removed.Message"));
                        plugin.getSoundManager().playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);
                    }
                }
            }
        }
    }

    public class Viewer {
        private final String name;
        private boolean item = false;

        public Viewer(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public boolean isItem() {
            return this.item;
        }

        public void setItem(boolean item) {
            this.item = item;
        }
    }
}
