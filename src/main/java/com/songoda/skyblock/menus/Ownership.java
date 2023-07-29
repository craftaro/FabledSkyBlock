package com.songoda.skyblock.menus;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.utils.ItemUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
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
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        return;
                    } else if (!island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Ownership.Role.Message"));
                        soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                        return;
                    }

                    if (playerData.getType() == null) {
                        playerData.setType(Visibility.HIDDEN);
                    }

                    ItemStack is = event.getItem();

                    if ((is.getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Ownership.Item.Exit.Displayname"))))) {
                        soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1.0F, 1.0F);
                    } else if ((is.getType() == CompatibleMaterial.PLAYER_HEAD.getMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Ownership.Item.Original.Displayname"))))) {
                        soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_YES.getSound(), 1.0F, 1.0F);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getMaterial())
                            && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Ownership.Item.Barrier.Displayname"))))) {
                        soundManager.playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == CompatibleMaterial.WRITABLE_BOOK.getMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Ownership.Item.Assign.Displayname"))))) {
                        soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                            AnvilGui gui = new AnvilGui(player);
                            gui.setAction(event1 -> {
                                if (playerDataManager.hasPlayerData(player)) {
                                    Island island1 = islandManager.getIsland(player);

                                    if (island1 == null) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Command.Island.Ownership.Owner.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(),
                                                1.0F, 1.0F);

                                        return;
                                    } else if (!island1.hasRole(IslandRole.OWNER,
                                            player.getUniqueId())) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Command.Island.Ownership.Role.Message"));
                                        soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(),
                                                1.0F, 1.0F);

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
                    } else if ((is.getType() == CompatibleMaterial.MAP.getMaterial()) && (is.hasItemMeta())
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

                                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player), 1L);

                                return;
                            } else if (event.getClick() == ClickType.RIGHT) {
                                island.setPassword(null);
                                soundManager.playSound(player, CompatibleSound.ENTITY_GENERIC_EXPLODE.getSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player), 1L);

                                return;
                            } else if (event.getClick() != ClickType.LEFT) {
                                event.setWillClose(false);
                                event.setWillDestroy(false);

                                return;
                            }
                        }

                        soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                            AnvilGui gui = new AnvilGui(player);
                            gui.setAction(event1 -> {
                                if (playerDataManager.hasPlayerData(player)) {
                                    Island island12 = islandManager.getIsland(player);

                                    if (island12 == null) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Command.Island.Ownership.Owner.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(),
                                                1.0F, 1.0F);

                                        return;
                                    } else if (!island12.hasRole(IslandRole.OWNER,
                                            player.getUniqueId())) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Command.Island.Ownership.Role.Message"));
                                        soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(),
                                                1.0F, 1.0F);

                                        return;
                                    }

                                    island12.setPassword(
                                            gui.getInputText().replace("&", "").replace(" ", ""));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_USE.getSound(), 1.0F,
                                            1.0F);

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

            Player targetPlayer = Bukkit.getServer().getPlayer(island.getOriginalOwnerUUID());

            if (targetPlayer == null) {
                OfflinePlayer offlinePlayer = new OfflinePlayer(originalOwnerUUID);
                originalOwnerName = offlinePlayer.getName();
                playerTexture = offlinePlayer.getTexture();
            } else {
                originalOwnerName = targetPlayer.getName();
                playerTexture = playerDataManager.getPlayerData(targetPlayer).getTexture();
            }

            nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                    configLoad.getString("Menu.Ownership.Item.Exit.Displayname"), null, null, null, null), 0);
            nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(playerTexture[0], playerTexture[1]),
                    configLoad.getString("Menu.Ownership.Item.Original.Displayname"),
                    configLoad.getStringList("Menu.Ownership.Item.Original.Lore"),
                    new Placeholder[]{new Placeholder("%player", originalOwnerName)}, null, null), 1);
            nInv.addItem(
                    nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                            configLoad.getString("Menu.Ownership.Item.Barrier.Displayname"), null, null, null, null),
                    2);
            nInv.addItem(nInv.createItem(CompatibleMaterial.WRITABLE_BOOK.getItem(),
                    configLoad.getString("Menu.Ownership.Item.Assign.Displayname"),
                    configLoad.getStringList("Menu.Ownership.Item.Assign.Lore"), null, null, null), 3);

            if (island.hasPassword()) {
                if (playerDataManager.getPlayerData(player).getType() == Ownership.Visibility.HIDDEN) {
                    nInv.addItem(nInv.createItem(CompatibleMaterial.MAP.getItem(),
                            configLoad.getString("Menu.Ownership.Item.Password.Displayname"),
                            configLoad.getStringList("Menu.Ownership.Item.Password.Hidden.Lore"), null, null, null), 4);
                } else {
                    nInv.addItem(
                            nInv.createItem(CompatibleMaterial.MAP.getItem(),
                                    configLoad.getString("Menu.Ownership.Item.Password.Displayname"),
                                    configLoad.getStringList("Menu.Ownership.Item.Password.Visible.Lore"),
                                    new Placeholder[]{new Placeholder("%password", ownershipPassword)}, null, null),
                            4);
                }
            } else {
                nInv.addItem(
                        nInv.createItem(CompatibleMaterial.MAP.getItem(),
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
