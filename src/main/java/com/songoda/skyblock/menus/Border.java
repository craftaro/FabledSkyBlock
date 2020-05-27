package com.songoda.skyblock.menus;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.item.nInventoryUtil;

import com.songoda.skyblock.utils.world.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class Border {

    private static Border instance;

    public static Border getInstance() {
        if (instance == null) {
            instance = new Border();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock skyblock = SkyBlock.getInstance();

        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        FileConfiguration configLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                .getFileConfiguration();

        nInventoryUtil nInv = new nInventoryUtil(player, event -> {
            Island island = islandManager.getIsland(player);

            if (island == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Border.Owner.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                return;
            } else if (!((island.hasRole(IslandRole.Operator, player.getUniqueId())
                    && skyblock.getPermissionManager().hasPermission(island,"Border", IslandRole.Operator))
                    || island.hasRole(IslandRole.Owner, player.getUniqueId()))) {
                messageManager.sendMessage(player,
                        configLoad.getString("Command.Island.Border.Permission.Message"));
                soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                return;
            } else if (!fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                    .getFileConfiguration().getBoolean("Island.WorldBorder.Enable")) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Border.Disabled.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                return;
            }

            ItemStack is = event.getItem();

            if ((is.getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial()) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Border.Item.Exit.Displayname"))))) {
                soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1.0F, 1.0F);
            } else if ((is.getType() == Material.TRIPWIRE_HOOK) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    configLoad.getString("Menu.Border.Item.Toggle.Displayname"))))) {
                if (island.isBorder()) {
                    island.setBorder(false);
                } else {
                    island.setBorder(true);
                }

                islandManager.updateBorder(island);
                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
            } else if ((is.getType() == CompatibleMaterial.LIGHT_BLUE_DYE.getMaterial()) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName()
                    .equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                                    configLoad.getString("Menu.Border.Item.Word.Blue")))))) {
                if (island.getBorderColor() == WorldBorder.Color.Blue) {
                    soundManager.playSound(player, CompatibleSound.ENTITY_CHICKEN_EGG.getSound(), 1.0F, 1.0F);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                } else {
                    island.setBorderColor(WorldBorder.Color.Blue);
                    islandManager.updateBorder(island);

                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                }
            } else if ((is.getType() == CompatibleMaterial.LIME_DYE.getMaterial()) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName()
                    .equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                                    configLoad.getString("Menu.Border.Item.Word.Green")))))) {
                if (island.getBorderColor() == WorldBorder.Color.Green) {
                    soundManager.playSound(player, CompatibleSound.ENTITY_CHICKEN_EGG.getSound(), 1.0F, 1.0F);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                } else {
                    island.setBorderColor(WorldBorder.Color.Green);
                    islandManager.updateBorder(island);

                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                }
            } else if ((is.getType() == CompatibleMaterial.RED_DYE.getMaterial()) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName()
                    .equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                                    configLoad.getString("Menu.Border.Item.Word.Red")))))) {
                if (island.getBorderColor() == WorldBorder.Color.Red) {
                    soundManager.playSound(player, CompatibleSound.ENTITY_CHICKEN_EGG.getSound(), 1.0F, 1.0F);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                } else {
                    island.setBorderColor(WorldBorder.Color.Red);
                    islandManager.updateBorder(island);

                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                }
            }
        });

        Island island = islandManager.getIsland(player);

        nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                configLoad.getString("Menu.Border.Item.Exit.Displayname"), null, null, null, null), 0);

        WorldBorder.Color borderColor = island.getBorderColor();
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

        if (borderColor == WorldBorder.Color.Blue) {
            nInv.addItem(nInv.createItem(CompatibleMaterial.LIGHT_BLUE_DYE.getItem(),
                    configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                            configLoad.getString("Menu.Border.Item.Word.Blue")),
                    configLoad.getStringList("Menu.Border.Item.Color.Selected.Lore"),
                    new Placeholder[]{new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Blue"))},
                    null, null), 2);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.LIGHT_BLUE_DYE.getItem(),
                    configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                            configLoad.getString("Menu.Border.Item.Word.Blue")),
                    configLoad.getStringList("Menu.Border.Item.Color.Unselected.Lore"),
                    new Placeholder[]{new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Blue"))},
                    null, null), 2);
        }

        if (borderColor == WorldBorder.Color.Green) {
            nInv.addItem(nInv.createItem(CompatibleMaterial.LIME_DYE.getItem(),
                    configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                            configLoad.getString("Menu.Border.Item.Word.Green")),
                    configLoad.getStringList("Menu.Border.Item.Color.Selected.Lore"),
                    new Placeholder[]{
                            new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Green"))},
                    null, null), 3);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.LIME_DYE.getItem(),
                    configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                            configLoad.getString("Menu.Border.Item.Word.Green")),
                    configLoad.getStringList("Menu.Border.Item.Color.Unselected.Lore"),
                    new Placeholder[]{
                            new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Green"))},
                    null, null), 3);
        }

        if (borderColor == WorldBorder.Color.Red) {
            nInv.addItem(nInv.createItem(CompatibleMaterial.RED_DYE.getItem(),
                    configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                            configLoad.getString("Menu.Border.Item.Word.Red")),
                    configLoad.getStringList("Menu.Border.Item.Color.Selected.Lore"),
                    new Placeholder[]{new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Red"))},
                    null, null), 4);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.RED_DYE.getItem(),
                    configLoad.getString("Menu.Border.Item.Color.Displayname").replace("%color",
                            configLoad.getString("Menu.Border.Item.Word.Red")),
                    configLoad.getStringList("Menu.Border.Item.Color.Unselected.Lore"),
                    new Placeholder[]{new Placeholder("%color", configLoad.getString("Menu.Border.Item.Word.Red"))},
                    null, null), 4);
        }

        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Border.Title")));
        nInv.setType(InventoryType.HOPPER);

        Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
    }
}
