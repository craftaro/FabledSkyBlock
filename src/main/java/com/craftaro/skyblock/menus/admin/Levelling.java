package com.craftaro.skyblock.menus.admin;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.MajorServerVersion;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.gui.AnvilGui;
import com.craftaro.core.utils.SkullItemCreator;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.NumberUtils;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.config.FileManager.Config;
import com.craftaro.skyblock.levelling.IslandLevelManager;
import com.craftaro.skyblock.levelling.LevellingMaterial;
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
            if (x.getMaterials() == XMaterial.SPAWNER) {
                return false;
            }
            if (x.getItemStack() == null) {
                return false;
            }
            ItemStack itemStack = x.getMaterials().parseItem();
            itemStack.setAmount(1);
            itemStack.setDurability(x.getItemStack().getDurability());
            if (itemStack.getItemMeta() == null) {
                return false;
            }
            testInventory.clear();
            testInventory.setItem(0, itemStack);
            return testInventory.getItem(0) != null;
        }).collect(Collectors.toList());

        FileConfiguration configLoad = plugin.getLanguage();

        nInventoryUtil nInv = new nInventoryUtil(player, null);
        nInv.addItem(
                nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                        configLoad.getString("Menu.Admin.Levelling.Item.Exit.Displayname"), null, null, null, null),
                0, 8);
        nInv.addItem(
                nInv.createItem(new ItemStack(XMaterial.OAK_SIGN.parseMaterial()),
                        configLoad.getString("Menu.Admin.Levelling.Item.Information.Displayname"),
                        configLoad.getStringList("Menu.Admin.Levelling.Item.Information.Lore"),
                        new Placeholder[]{new Placeholder("%materials", "" + levellingMaterials.size()),
                                new Placeholder("%division",
                                        "" + fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"))
                                                .getFileConfiguration().getInt("Island.Levelling.Division"))},
                        null, null),
                4);
        nInv.addItem(
                nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                        configLoad.getString("Menu.Admin.Levelling.Item.Barrier.Displayname"), null, null, null, null),
                9, 10, 11, 12, 13, 14, 15, 16, 17);

        int playerMenuPage = playerData.getPage(MenuType.ADMIN_LEVELLING), nextEndIndex = levellingMaterials.size() - playerMenuPage * 36;

        if (playerMenuPage != 1) {
            ItemStack Lhead = SkullItemCreator.byTextureUrlHash("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
            nInv.addItem(nInv.createItem(Lhead,
                    configLoad.getString("Menu.Admin.Levelling.Item.Previous.Displayname"), null, null, null, null), 1);
        }

        if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
            ItemStack Rhead = SkullItemCreator.byTextureUrlHash("1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b");
            nInv.addItem(nInv.createItem(Rhead,
                    configLoad.getString("Menu.Admin.Levelling.Item.Next.Displayname"), null, null, null, null), 7);
        }

        if (levellingMaterials.isEmpty()) {
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
                    ItemStack itemStack = material.getMaterials().parseItem();
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

        Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
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
            if (MajorServerVersion.isServerVersionAbove(MajorServerVersion.V1_13)) {
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
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    return;
                }

                if ((event.getCurrentItem().getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial())
                        && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Levelling.Item.Barrier.Displayname"))))) {
                    soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                    return;
                } else if ((event.getCurrentItem().getType() == XMaterial.OAK_FENCE_GATE.parseMaterial())
                        && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Levelling.Item.Exit.Displayname"))))) {
                    soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
                    player.closeInventory();

                    return;
                } else if ((event.getCurrentItem().getType() == XMaterial.OAK_SIGN.parseMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Levelling.Item.Information.Displayname"))))) {
                    soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);


                    AnvilGui gui = new AnvilGui(player);
                    gui.setAction(event1 -> {
                        if (!(player.hasPermission("fabledskyblock.admin.level")
                                || player.hasPermission("fabledskyblock.admin.*")
                                || player.hasPermission("fabledskyblock.*"))) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Admin.Levelling.Permission.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                        } else {
                            try {
                                double pointDivision = Double.parseDouble(gui.getInputText());

                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Levelling.Division.Message")
                                                .replace("%division", NumberUtils.formatNumber(pointDivision)));
                                soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                    Config config12 = fileManager
                                            .getConfig(new File(plugin.getDataFolder(), "config.yml"));
                                    FileConfiguration configLoad12 = config12.getFileConfiguration();

                                    configLoad12.set("Island.Levelling.Division", pointDivision);

                                    try {
                                        configLoad12.save(config12.getFile());
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                });

                                player.closeInventory();

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                            } catch (NumberFormatException ignored) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Levelling.Numerical.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                            }
                        }
                        player.closeInventory();
                    });

                    is = new ItemStack(Material.NAME_TAG);
                    ItemMeta im = is.getItemMeta();
                    im.setDisplayName(configLoad.getString("Menu.Admin.Levelling.Item.Information.Word.Enter"));
                    is.setItemMeta(im);

                    gui.setInput(is);
                    plugin.getGuiManager().showGUI(player, gui);

                    return;
                } else if ((event.getCurrentItem().getType() == Material.BARRIER) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Admin.Levelling.Item.Nothing.Displayname"))))) {
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    return;
                } else if ((event.getCurrentItem().getType() == XMaterial.PLAYER_HEAD.parseMaterial())
                        && (is.hasItemMeta())) {
                    if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Admin.Levelling.Item.Previous.Displayname")))) {
                        player.closeInventory();

                        playerData.setPage(MenuType.ADMIN_LEVELLING, playerData.getPage(MenuType.ADMIN_LEVELLING) - 1);
                        soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                        return;
                    } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Admin.Levelling.Item.Next.Displayname")))) {
                        player.closeInventory();

                        playerData.setPage(MenuType.ADMIN_LEVELLING, playerData.getPage(MenuType.ADMIN_LEVELLING) + 1);
                        soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                        return;
                    }
                }

                if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                    for (LevellingMaterial materialList : levellingManager.getWorthsAsLevelingMaterials()) {
                        XMaterial materials = materialList.getMaterials();

                        if (CompatibleMaterial.getMaterial(materials.parseMaterial()) != null
                                && event.getCurrentItem().getType() == CompatibleMaterial.getMaterial(materials.parseMaterial()).get().parseMaterial()
                                && ChatColor.stripColor(is.getItemMeta().getDisplayName()).equals(materials.name())) {

                            if (event.getClick() == ClickType.LEFT) {
                                soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                                AnvilGui gui = new AnvilGui(player);
                                gui.setAction(ev -> {
                                    if (!(player.hasPermission("fabledskyblock.admin.level")
                                            || player.hasPermission("fabledskyblock.admin.*")
                                            || player.hasPermission("fabledskyblock.*"))) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Levelling.Permission.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                    } else if (levellingManager.hasWorth(materials)) {
                                        try {
                                            double materialPoints = Double.parseDouble(gui.getInputText());
                                            materialList.setPoints(materialPoints);

                                            messageManager.sendMessage(player, configLoad
                                                    .getString("Island.Admin.Levelling.Points.Message")
                                                    .replace("%material", materials.name()).replace("%points",
                                                            NumberUtils.formatNumber(materialPoints)));
                                            soundManager.playSound(player, XSound.ENTITY_PLAYER_LEVELUP);
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
                                                        } catch (IOException ex) {
                                                            ex.printStackTrace();
                                                        }
                                                    });
                                        } catch (NumberFormatException ignored) {
                                            messageManager.sendMessage(player, configLoad
                                                    .getString("Island.Admin.Levelling.Numerical.Message"));
                                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                        }
                                    } else {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Admin.Levelling.Exist.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                                    }
                                });

                                is = new ItemStack(Material.NAME_TAG);
                                ItemMeta im = is.getItemMeta();
                                im.setDisplayName(
                                        configLoad.getString("Menu.Admin.Levelling.Item.Material.Word.Enter"));
                                is.setItemMeta(im);

                                gui.setOutput(is);
                                plugin.getGuiManager().showGUI(player, gui);
                            } else if (event.getClick() == ClickType.RIGHT) {
                                levellingManager.removeWorth(materialList.getMaterials());
                                open(player);

                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Levelling.Removed.Message")
                                                .replace("%material", materials.name()));
                                soundManager.playSound(player, XSound.ENTITY_IRON_GOLEM_ATTACK);

                                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                                    Config config13 = fileManager
                                            .getConfig(new File(plugin.getDataFolder(), "levelling.yml"));
                                    FileConfiguration configLoad13 = config13.getFileConfiguration();

                                    configLoad13.set("Materials." + materials.name(), null);

                                    try {
                                        configLoad13.save(config13.getFile());
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                });
                            }

                            return;
                        }
                    }
                }

                XMaterial materials = CompatibleMaterial.getMaterial(event.getCurrentItem().getType()).get();

                if (MajorServerVersion.isServerVersionBelow(MajorServerVersion.V1_13)) {
                    materials.parseItem().setData(event.getCurrentItem().getData());
                }

                if (levellingManager.hasWorth(materials)) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Already.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                    return;
                }

                levellingManager.addWorth(materials, 0);
                open(player);

                messageManager.sendMessage(player, configLoad.getString("Island.Admin.Levelling.Added.Message")
                        .replace("%material", materials.name()));
                soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);

                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    Config config14 = fileManager.getConfig(new File(plugin.getDataFolder(), "levelling.yml"));
                    FileConfiguration configLoad14 = config14.getFileConfiguration();

                    configLoad14.set("Materials." + materials.name() + ".Points", 0);

                    try {
                        configLoad14.save(config14.getFile());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        }
    }
}
