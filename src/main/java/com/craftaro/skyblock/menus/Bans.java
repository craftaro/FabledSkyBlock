package com.craftaro.skyblock.menus;

import com.craftaro.core.gui.AnvilGui;
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
import com.craftaro.third_party.com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.craftaro.third_party.com.cryptomorin.xseries.profiles.objects.Profileable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Set;
import java.util.UUID;

public class Bans {
    private static Bans instance;

    public static Bans getInstance() {
        if (instance == null) {
            instance = new Bans();
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
            PlayerData playerData = playerDataManager.getPlayerData(player);
            Island island = plugin.getIslandManager().getIsland(player);

            FileConfiguration configLoad = plugin.getLanguage();

            nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                if (playerDataManager.hasPlayerData(player)) {
                    PlayerData playerData1 = playerDataManager.getPlayerData(player);
                    Island island1 = islandManager.getIsland(player);

                    if (island1 == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Bans.Owner.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        return;
                    } else if (!plugin.getConfiguration().getBoolean("Island.Visitor.Banning")) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Bans.Disabled.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        return;
                    }

                    ItemStack is = event.getItem();

                    if ((XMaterial.BLACK_STAINED_GLASS_PANE.isSimilar(is)) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                            configLoad.getString("Menu.Bans.Item.Barrier.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((XMaterial.OAK_FENCE_GATE.isSimilar(is)) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                            configLoad.getString("Menu.Bans.Item.Exit.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
                    } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                            configLoad.getString("Menu.Bans.Item.Information.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                            AnvilGui gui = new AnvilGui(player);
                            gui.setAction(event1 -> {
                                Bukkit.getServer().dispatchCommand(player,
                                        "island ban " + gui.getInputText());
                                Bukkit.getServer().getScheduler()
                                        .runTaskLater(plugin, () -> open(player), 1L);
                                player.closeInventory();
                            });

                            ItemStack is1 = new ItemStack(Material.NAME_TAG);
                            ItemMeta im = is1.getItemMeta();
                            im.setDisplayName(configLoad.getString("Menu.Bans.Item.Word.Enter"));
                            is1.setItemMeta(im);

                            gui.setInput(is);
                            plugin.getGuiManager().showGUI(player, gui);
                        }, 1L);
                    } else if ((is.getType() == Material.BARRIER) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                            configLoad.getString("Menu.Bans.Item.Nothing.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((XMaterial.PLAYER_HEAD.isSimilar(is)) && (is.hasItemMeta())) {
                        if (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                                configLoad.getString("Menu.Bans.Item.Previous.Displayname")))) {
                            playerData1.setPage(MenuType.BANS, playerData1.getPage(MenuType.BANS) - 1);
                            soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else if (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                                configLoad.getString("Menu.Bans.Item.Next.Displayname")))) {
                            playerData1.setPage(MenuType.BANS, playerData1.getPage(MenuType.BANS) + 1);
                            soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else {
                            if ((island1.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                    && plugin.getPermissionManager().hasPermission(island1, "Unban", IslandRole.OPERATOR))
                                    || island1.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                                String playerName = ChatColor.stripColor(is.getItemMeta().getDisplayName());
                                Bukkit.getServer().dispatchCommand(player, "island unban " + playerName);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player), 3L);
                            } else {
                                messageManager.sendMessage(player, configLoad.getString("Command.Island.Bans.Permission.Message"));
                                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            }
                        }
                    }
                }
            });

            Set<UUID> islandBans = island.getBan().getBans();

            nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                    configLoad.getString("Menu.Bans.Item.Exit.Displayname"), null, null, null, null), 0, 8);
            nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                    configLoad.getString("Menu.Bans.Item.Information.Displayname"),
                    configLoad.getStringList("Menu.Bans.Item.Information.Lore"),
                    new Placeholder[]{new Placeholder("%bans", "" + islandBans.size())}, null, null), 4);
            nInv.addItem(
                    nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                            configLoad.getString("Menu.Bans.Item.Barrier.Displayname"), null, null, null, null),
                    9, 10, 11, 12, 13, 14, 15, 16, 17);

            int playerMenuPage = playerData.getPage(MenuType.BANS), nextEndIndex = islandBans.size() - playerMenuPage * 36;

            if (playerMenuPage != 1) {
                ItemStack Lhead = XSkull.createItem().profile(new Profileable.StringProfileable("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23", ProfileInputType.TEXTURE_HASH)).apply();
                nInv.addItem(nInv.createItem(Lhead,
                        configLoad.getString("Menu.Bans.Item.Previous.Displayname"), null, null, null, null), 1);
            }

            if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                ItemStack Rhead = XSkull.createItem().profile(new Profileable.StringProfileable("1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b", ProfileInputType.TEXTURE_HASH)).apply();
                nInv.addItem(nInv.createItem(Rhead,
                        configLoad.getString("Menu.Bans.Item.Next.Displayname"), null, null, null, null), 7);
            }

            if (islandBans.isEmpty()) {
                nInv.addItem(
                        nInv.createItem(new ItemStack(Material.BARRIER),
                                configLoad.getString("Menu.Bans.Item.Nothing.Displayname"), null, null, null, null),
                        31);
            } else {
                int index = playerMenuPage * 36 - 36,
                        endIndex = index >= islandBans.size() ? islandBans.size() - 1 : index + 36, inventorySlot = 17;

                for (; index < endIndex; index++) {
                    if (islandBans.size() > index) {
                        inventorySlot++;

                        UUID targetPlayerUUID = (UUID) islandBans.toArray()[index];
                        String targetPlayerName;
                        String[] targetPlayerTexture;

                        org.bukkit.OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(targetPlayerUUID);

                        if (targetPlayer == null) {
                            OfflinePlayer offlinePlayer = new OfflinePlayer(targetPlayerUUID);
                            targetPlayerName = offlinePlayer.getName();
                            targetPlayerTexture = offlinePlayer.getTexture();
                        } else {
                            targetPlayerName = targetPlayer.getName();

                            if (playerDataManager.hasPlayerData(targetPlayer.getUniqueId())) {
                                targetPlayerTexture = playerDataManager.getPlayerData(targetPlayer.getUniqueId()).getTexture();
                            } else {
                                targetPlayerTexture = new String[]{null, null};
                            }
                        }
                        ItemStack phead = XSkull.createItem().profile(new Profileable.OfflinePlayerProfileable(targetPlayer)).apply();
                        nInv.addItem(
                                nInv.createItem(phead,
                                        plugin.formatText(
                                                configLoad.getString("Menu.Bans.Item.Ban.Displayname")
                                                        .replace("%player", targetPlayerName == null ? "" : targetPlayerName)),
                                        configLoad.getStringList("Menu.Bans.Item.Ban.Lore"), null, null, null),
                                inventorySlot);
                    }
                }
            }

            nInv.setTitle(plugin.formatText(configLoad.getString("Menu.Bans.Title")));
            nInv.setRows(6);

            Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
        }
    }
}
