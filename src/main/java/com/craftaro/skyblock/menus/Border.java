package com.craftaro.skyblock.menus;

import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.world.SWorldBorder;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.placeholder.Placeholder;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.item.nInventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class Border {
    private static Border instance;

    public static Border getInstance() {
        if (instance == null) {
            instance = new Border();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();

        FileConfiguration configLoad = plugin.getLanguage();

        nInventoryUtil nInv = new nInventoryUtil(player, event -> {
            Island island = islandManager.getIsland(player);

            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Border.Owner.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                return;
            } else if (!((island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                    && plugin.getPermissionManager().hasPermission(island, "Border", IslandRole.OPERATOR))
                    || island.hasRole(IslandRole.OWNER, player.getUniqueId()))) {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Border.Permission.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                return;
            } else if (!plugin.getConfiguration().getBoolean("Island.WorldBorder.Enable")) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Border.Disabled.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                return;
            }

            ItemStack is = event.getItem();

            if ((XMaterial.OAK_FENCE_GATE.isSimilar(is)) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Border.Item.Exit.Displayname"))))) {
                soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
            } else if ((is.getType() == Material.TRIPWIRE_HOOK) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Border.Item.Toggle.Displayname"))))) {
                island.setBorder(!island.isBorder());

                islandManager.updateBorder(island);
                soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
            } else if ((XMaterial.LIGHT_BLUE_DYE.isSimilar(is)) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName()
                    .equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                                    configLoad.getString("Menu.Border.Item.Word.Blue")))))) {
                if (island.getBorderColor() == SWorldBorder.Color.Blue) {
                    soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                } else {
                    island.setBorderColor(SWorldBorder.Color.Blue);
                    islandManager.updateBorder(island);

                    soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                }
            } else if ((XMaterial.LIME_DYE.isSimilar(is)) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName()
                    .equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                                    configLoad.getString("Menu.Border.Item.Word.Green")))))) {
                if (island.getBorderColor() == SWorldBorder.Color.Green) {
                    soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                } else {
                    island.setBorderColor(SWorldBorder.Color.Green);
                    islandManager.updateBorder(island);

                    soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                }
            } else if ((XMaterial.RED_DYE.isSimilar(is)) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName()
                    .equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                                    configLoad.getString("Menu.Border.Item.Word.Red")))))) {
                if (island.getBorderColor() == SWorldBorder.Color.Red) {
                    soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                } else {
                    island.setBorderColor(SWorldBorder.Color.Red);
                    islandManager.updateBorder(island);

                    soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                }
            }
        });

        Island island = islandManager.getIsland(player);

        nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                configLoad.getString("Menu.Border.Item.Exit.Displayname"), null, null, null, null), 0);

        SWorldBorder.Color borderColor = island.getBorderColor();
        String borderToggle;

        if (island.isBorder()) {
            borderToggle = configLoad.getString("Menu.Border.Item.Word.Disable");
        } else {
            borderToggle = configLoad.getString("Menu.Border.Item.Word.Enable");
        }

        nInv.addItem(nInv.createItem(new ItemStack(Material.TRIPWIRE_HOOK),
                configLoad.getString("Menu.Border.Item.Toggle.Displayname"),
                configLoad.getStringList("Menu.Border.Item.Toggle.Lore"),
                new Placeholder[]{new Placeholder("%toggle", borderToggle)}, null, null), 1);

        if (player.hasPermission("fabledskyblock.island.border.blue")) {
            if (borderColor == SWorldBorder.Color.Blue) {
                nInv.addItem(nInv.createItem(XMaterial.LIGHT_BLUE_DYE.parseItem(),
                        configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                                configLoad.getString("Menu.Border.Item.Word.Blue")),
                        configLoad.getStringList("Menu.Border.Item.Color.Selected.Lore"),
                        new Placeholder[]{new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Blue"))},
                        null, null), 2);
            } else {
                nInv.addItem(nInv.createItem(XMaterial.LIGHT_BLUE_DYE.parseItem(),
                        configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                                configLoad.getString("Menu.Border.Item.Word.Blue")),
                        configLoad.getStringList("Menu.Border.Item.Color.Unselected.Lore"),
                        new Placeholder[]{new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Blue"))},
                        null, null), 2);
            }
        } else {
            nInv.addItem(nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                    "", null, null, null, null), 2);
        }
        if (player.hasPermission("fabledskyblock.island.border.green")) {
            if (borderColor == SWorldBorder.Color.Green) {
                nInv.addItem(nInv.createItem(XMaterial.LIME_DYE.parseItem(),
                        configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                                configLoad.getString("Menu.Border.Item.Word.Green")),
                        configLoad.getStringList("Menu.Border.Item.Color.Selected.Lore"),
                        new Placeholder[]{
                                new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Green"))},
                        null, null), 3);
            } else {
                nInv.addItem(nInv.createItem(XMaterial.LIME_DYE.parseItem(),
                        configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                                configLoad.getString("Menu.Border.Item.Word.Green")),
                        configLoad.getStringList("Menu.Border.Item.Color.Unselected.Lore"),
                        new Placeholder[]{
                                new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Green"))},
                        null, null), 3);
            }
        } else {
            nInv.addItem(nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                    "", null, null, null, null), 3);
        }
        if (player.hasPermission("fabledskyblock.island.border.red")) {
            if (borderColor == SWorldBorder.Color.Red) {
                nInv.addItem(nInv.createItem(XMaterial.RED_DYE.parseItem(),
                        configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                                configLoad.getString("Menu.Border.Item.Word.Red")),
                        configLoad.getStringList("Menu.Border.Item.Color.Selected.Lore"),
                        new Placeholder[]{new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Red"))},
                        null, null), 4);
            } else {
                nInv.addItem(nInv.createItem(XMaterial.RED_DYE.parseItem(),
                        configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                                configLoad.getString("Menu.Border.Item.Word.Red")),
                        configLoad.getStringList("Menu.Border.Item.Color.Unselected.Lore"),
                        new Placeholder[]{new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Red"))},
                        null, null), 4);
            }
        } else {
            nInv.addItem(nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                    "", null, null, null, null), 4);
        }

        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Border.Title")));
        nInv.setType(InventoryType.HOPPER);

        Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
    }
}
