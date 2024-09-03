package com.craftaro.skyblock.menus;

import com.craftaro.core.gui.AnvilGui;
import com.craftaro.core.utils.SkullItemCreator;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.placeholder.Placeholder;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.item.nInventoryUtil;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import com.craftaro.third_party.com.cryptomorin.xseries.profiles.builder.XSkull;
import com.craftaro.third_party.com.cryptomorin.xseries.profiles.objects.Profileable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class Ownership {
    private static Ownership instance;

    public static Ownership getInstance() {
        if (instance == null) {
            instance = new Ownership();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();

        if (playerDataManager.hasPlayerData(player)) {
            FileConfiguration configLoad = plugin.getLanguage();

            nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                if (playerDataManager.hasPlayerData(player)) {
                    PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
                    Island island = islandManager.getIsland(player);

                    if (island == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Ownership.Owner.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        return;
                    } else if (!island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Ownership.Role.Message"));
                        soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                        return;
                    }

                    if (playerData.getType() == null) {
                        playerData.setType(Visibility.HIDDEN);
                    }

                    ItemStack is = event.getItem();

                    if ((XMaterial.OAK_FENCE_GATE.isSimilar(is)) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Ownership.Item.Exit.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
                    } else if ((XMaterial.PLAYER_HEAD.isSimilar(is)) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Ownership.Item.Original.Displayname"))))) {
                        soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((XMaterial.BLACK_STAINED_GLASS_PANE.isSimilar(is))
                            && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Ownership.Item.Barrier.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((XMaterial.WRITABLE_BOOK.isSimilar(is)) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Ownership.Item.Assign.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                            AnvilGui gui = new AnvilGui(player);
                            gui.setAction(event1 -> {
                                if (playerDataManager.hasPlayerData(player)) {
                                    Island island1 = islandManager.getIsland(player);

                                    if (island1 == null) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Command.Island.Ownership.Owner.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                        return;
                                    } else if (!island1.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Command.Island.Ownership.Role.Message"));
                                        soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                                        return;
                                    }

                                    Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().dispatchCommand(player,
                                            "island ownership " + gui.getInputText()));
                                }
                                player.closeInventory();
                            });

                            ItemStack is1 = new ItemStack(Material.NAME_TAG);
                            ItemMeta im = is1.getItemMeta();
                            im.setDisplayName(configLoad.getString("Menu.Ownership.Item.Assign.Word.Enter"));
                            is1.setItemMeta(im);

                            gui.setInput(is);
                            plugin.getGuiManager().showGUI(player, gui);
                        }, 1L);
                    } else if ((XMaterial.MAP.isSimilar(is)) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Ownership.Item.Password.Displayname"))))) {
                        if (island.hasPassword()) {
                            if (event.getClick() == ClickType.MIDDLE) {
                                Visibility visibility = (Visibility) playerData.getType();

                                if (visibility == Visibility.HIDDEN) {
                                    playerData.setType(Visibility.VISIBLE);
                                } else {
                                    playerData.setType(Visibility.HIDDEN);
                                }

                                soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                                return;
                            } else if (event.getClick() == ClickType.RIGHT) {
                                island.setPassword(null);
                                soundManager.playSound(player, XSound.ENTITY_GENERIC_EXPLODE);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);

                                return;
                            } else if (event.getClick() != ClickType.LEFT) {
                                event.setWillClose(false);
                                event.setWillDestroy(false);

                                return;
                            }
                        }

                        soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                            AnvilGui gui = new AnvilGui(player);
                            gui.setAction(event1 -> {
                                if (playerDataManager.hasPlayerData(player)) {
                                    Island island12 = islandManager.getIsland(player);

                                    if (island12 == null) {
                                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Owner.Message"));
                                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                        return;
                                    } else if (!island12.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Ownership.Role.Message"));
                                        soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                                        return;
                                    }

                                    island12.setPassword(gui.getInputText().replace("&", "").replace(" ", ""));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_USE);

                                    Bukkit.getServer().getScheduler()
                                            .runTaskLater(plugin, () -> open(player), 1L);
                                }
                                player.closeInventory();
                            });

                            ItemStack is12 = new ItemStack(Material.NAME_TAG);
                            ItemMeta im = is12.getItemMeta();
                            im.setDisplayName(
                                    configLoad.getString("Menu.Ownership.Item.Password.Hidden.Word.Enter"));
                            is12.setItemMeta(im);

                            gui.setInput(is);
                            plugin.getGuiManager().showGUI(player, gui);
                        }, 1L);
                    }
                }
            });

            Island island = plugin.getIslandManager().getIsland(player);

            UUID originalOwnerUUID = island.getOriginalOwnerUUID();

            String originalOwnerName, ownershipPassword = island.getPassword();
            String[] playerTexture;

            org.bukkit.OfflinePlayer targetPlayer = Bukkit.getServer().getPlayer(originalOwnerUUID);

            if (targetPlayer == null) {
                OfflinePlayer offlinePlayer = new OfflinePlayer(originalOwnerUUID);
                originalOwnerName = offlinePlayer.getName();
                playerTexture = offlinePlayer.getTexture();
            } else {
                originalOwnerName = targetPlayer.getName();
                playerTexture = playerDataManager.getPlayerData(targetPlayer.getUniqueId()).getTexture();
            }


            ItemStack phead;
            if (playerTexture.length >= 1 && playerTexture[0] != null) {
                phead = XSkull.createItem().profile(new Profileable.PlayerProfileable(player)).apply();
            } else {
                phead = XSkull.createItem().profile(new Profileable.PlayerProfileable(player)).apply();
            }

            nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                    configLoad.getString("Menu.Ownership.Item.Exit.Displayname"), null, null, null, null), 0);
            nInv.addItem(nInv.createItem(phead,
                    configLoad.getString("Menu.Ownership.Item.Original.Displayname"),
                    configLoad.getStringList("Menu.Ownership.Item.Original.Lore"),
                    new Placeholder[]{new Placeholder("%player", originalOwnerName)}, null, null), 1);
            nInv.addItem(
                    nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                            configLoad.getString("Menu.Ownership.Item.Barrier.Displayname"), null, null, null, null),
                    2);
            nInv.addItem(nInv.createItem(XMaterial.WRITABLE_BOOK.parseItem(),
                    configLoad.getString("Menu.Ownership.Item.Assign.Displayname"),
                    configLoad.getStringList("Menu.Ownership.Item.Assign.Lore"), null, null, null), 3);

            if (island.hasPassword()) {
                if (playerDataManager.getPlayerData(player).getType() == Ownership.Visibility.HIDDEN) {
                    nInv.addItem(nInv.createItem(XMaterial.MAP.parseItem(),
                            configLoad.getString("Menu.Ownership.Item.Password.Displayname"),
                            configLoad.getStringList("Menu.Ownership.Item.Password.Hidden.Lore"), null, null, null), 4);
                } else {
                    nInv.addItem(
                            nInv.createItem(XMaterial.MAP.parseItem(),
                                    configLoad.getString("Menu.Ownership.Item.Password.Displayname"),
                                    configLoad.getStringList("Menu.Ownership.Item.Password.Visible.Lore"),
                                    new Placeholder[]{new Placeholder("%password", ownershipPassword)}, null, null),
                            4);
                }
            } else {
                nInv.addItem(
                        nInv.createItem(XMaterial.MAP.parseItem(),
                                configLoad.getString("Menu.Ownership.Item.Password.Displayname"),
                                configLoad.getStringList("Menu.Ownership.Item.Password.Unset.Lore"), null, null, null),
                        4);
            }

            nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Ownership.Title")));
            nInv.setType(InventoryType.HOPPER);

            Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
        }
    }

    public enum Visibility {
        VISIBLE, HIDDEN
    }
}
