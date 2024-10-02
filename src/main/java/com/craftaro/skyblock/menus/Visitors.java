package com.craftaro.skyblock.menus;

import com.craftaro.core.utils.SkullItemCreator;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.permission.PermissionManager;
import com.craftaro.skyblock.placeholder.Placeholder;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.NumberUtil;
import com.craftaro.skyblock.utils.item.nInventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public class Visitors {
    private static Visitors instance;

    public static Visitors getInstance() {
        if (instance == null) {
            instance = new Visitors();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        IslandManager islandManager = plugin.getIslandManager();
        PermissionManager permissionManager = plugin.getPermissionManager();
        SoundManager soundManager = plugin.getSoundManager();

        if (playerDataManager.hasPlayerData(player)) {
            FileConfiguration configLoad = plugin.getLanguage();

            nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                if (playerDataManager.hasPlayerData(player)) {
                    PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
                    Island island = islandManager.getIsland(player);

                    if (island == null) {
                        plugin.getMessageManager().sendMessage(player,
                                configLoad.getString("Command.Island.Visitors.Owner.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        return;
                    }

                    ItemStack is = event.getItem();

                    if ((is.getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Visitors.Item.Barrier.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == XMaterial.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Visitors.Item.Exit.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
                    } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Visitors.Item.Statistics.Displayname"))))) {
                        soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == Material.BARRIER) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Visitors.Item.Nothing.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) && (is.hasItemMeta())) {
                        if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Menu.Visitors.Item.Previous.Displayname")))) {
                            playerData.setPage(MenuType.VISITORS, playerData.getPage(MenuType.VISITORS) - 1);
                            soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes(
                                '&', configLoad.getString("Menu.Visitors.Item.Next.Displayname")))) {
                            playerData.setPage(MenuType.VISITORS, playerData.getPage(MenuType.VISITORS) + 1);
                            soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else {
                            boolean isOperator = island.hasRole(IslandRole.OPERATOR, player.getUniqueId()),
                                    isOwner = island.hasRole(IslandRole.OWNER, player.getUniqueId()),
                                    canKick = permissionManager.hasPermission(island, "Kick", IslandRole.OPERATOR),
                                    canBan = permissionManager.hasPermission(island, "Ban", IslandRole.OPERATOR),
                                    banningEnabled = plugin.getConfiguration().getBoolean("Island.Visitor.Banning");
                            String playerName = ChatColor.stripColor(is.getItemMeta().getDisplayName());

                            if ((isOperator && canKick) || isOwner) {
                                if (banningEnabled && ((isOperator && canBan) || isOwner)) {
                                    if (event.getClick() == ClickType.LEFT) {
                                        Bukkit.getServer().dispatchCommand(player, "island kick " + playerName);
                                    } else if (event.getClick() == ClickType.RIGHT) {
                                        Bukkit.getServer().dispatchCommand(player, "island ban " + playerName);
                                    } else {
                                        soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                                        event.setWillClose(false);
                                        event.setWillDestroy(false);

                                        return;
                                    }
                                } else {
                                    Bukkit.getServer().dispatchCommand(player, "island kick " + playerName);
                                }
                            } else {
                                if (banningEnabled && ((isOperator && canBan))) {
                                    Bukkit.getServer().dispatchCommand(player, "island ban " + playerName);
                                } else {
                                    soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);

                                    return;
                                }
                            }

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }
                    }
                }
            });

            PlayerData playerData = playerDataManager.getPlayerData(player);
            Island island = plugin.getIslandManager().getIsland(player);

            Set<UUID> islandVisitors = islandManager.getVisitorsAtIsland(island);
            Map<Integer, UUID> sortedIslandVisitors = new TreeMap<>();

            for (UUID islandVisitorList : islandVisitors) {
                Player targetPlayer = Bukkit.getPlayer(islandVisitorList);
                if (targetPlayer != null && player.canSee(targetPlayer)) { // Remove vanished players
                    sortedIslandVisitors.put(
                            playerDataManager.getPlayerData(targetPlayer).getVisitTime(),
                            islandVisitorList);
                }
            }

            nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                    configLoad.getString("Menu.Visitors.Item.Exit.Displayname"), null, null, null, null), 0, 8);
            nInv.addItem(
                    nInv.createItem(new ItemStack(Material.PAINTING),
                            configLoad.getString("Menu.Visitors.Item.Statistics.Displayname"),
                            configLoad.getStringList("Menu.Visitors.Item.Statistics.Lore"),
                            new Placeholder[]{new Placeholder("%visitors", "" + sortedIslandVisitors.size())}, null, null),
                    4);
            nInv.addItem(
                    nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                            configLoad.getString("Menu.Visitors.Item.Barrier.Displayname"), null, null, null, null),
                    9, 10, 11, 12, 13, 14, 15, 16, 17);


            islandVisitors.clear();

            for (int sortedIslandVisitorList : sortedIslandVisitors.keySet()) {
                islandVisitors.add(sortedIslandVisitors.get(sortedIslandVisitorList));
            }

            int playerMenuPage = playerData.getPage(MenuType.VISITORS), nextEndIndex = sortedIslandVisitors.size() - playerMenuPage * 36;

            if (playerMenuPage != 1) {
                ItemStack Lhead = SkullItemCreator.byTextureUrlHash("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
                nInv.addItem(nInv.createItem(Lhead,
                        configLoad.getString("Menu.Visitors.Item.Previous.Displayname"), null, null, null, null), 1);
            }

            if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                ItemStack Rhead = SkullItemCreator.byTextureUrlHash("1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b");
                nInv.addItem(nInv.createItem(Rhead,
                        configLoad.getString("Menu.Visitors.Item.Next.Displayname"), null, null, null, null), 7);
            }

            if (islandVisitors.size() == 0) {
                nInv.addItem(
                        nInv.createItem(new ItemStack(Material.BARRIER),
                                configLoad.getString("Menu.Visitors.Item.Nothing.Displayname"), null, null, null, null),
                        31);
            } else {
                boolean isOperator = island.hasRole(IslandRole.OPERATOR, player.getUniqueId()),
                        isOwner = island.hasRole(IslandRole.OWNER, player.getUniqueId()),
                        canKick = plugin.getPermissionManager().hasPermission(island, "Kick", IslandRole.OPERATOR),
                        canBan = plugin.getPermissionManager().hasPermission(island, "Ban", IslandRole.OPERATOR),
                        banningEnabled = plugin.getConfiguration().getBoolean("Island.Visitor.Banning");
                int index = playerMenuPage * 36 - 36,
                        endIndex = index >= islandVisitors.size() ? islandVisitors.size() - 1 : index + 36,
                        inventorySlot = 17;

                for (; index < endIndex; index++) {
                    if (islandVisitors.size() > index) {
                        inventorySlot++;

                        Player targetPlayer = Bukkit.getServer().getPlayer((UUID) islandVisitors.toArray()[index]);
                        PlayerData targetPlayerData = playerDataManager.getPlayerData(targetPlayer.getUniqueId());

                        String[] targetPlayerTexture = targetPlayerData.getTexture();
                        String islandVisitTimeFormatted;

                        long[] islandVisitTime = NumberUtil.getDuration(targetPlayerData.getVisitTime());

                        if (islandVisitTime[0] != 0) {
                            islandVisitTimeFormatted = islandVisitTime[0] + " "
                                    + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Days") + ", "
                                    + islandVisitTime[1] + " "
                                    + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Hours") + ", "
                                    + islandVisitTime[2] + " "
                                    + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Minutes") + ", "
                                    + islandVisitTime[3] + " "
                                    + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Seconds");
                        } else if (islandVisitTime[1] != 0) {
                            islandVisitTimeFormatted = islandVisitTime[1] + " "
                                    + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Hours") + ", "
                                    + islandVisitTime[2] + " "
                                    + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Minutes") + ", "
                                    + islandVisitTime[3] + " "
                                    + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Seconds");
                        } else if (islandVisitTime[2] != 0) {
                            islandVisitTimeFormatted = islandVisitTime[2] + " "
                                    + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Minutes") + ", "
                                    + islandVisitTime[3] + " "
                                    + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Seconds");
                        } else {
                            islandVisitTimeFormatted = islandVisitTime[3] + " "
                                    + configLoad.getString("Menu.Visitors.Item.Visitor.Word.Seconds");
                        }

                        List<String> itemLore = new ArrayList<>();

                        if ((isOperator && canKick) || isOwner) {
                            if (banningEnabled && ((isOperator && canBan) || isOwner)) {
                                itemLore.addAll(configLoad.getStringList(
                                        "Menu.Visitors.Item.Visitor.Kick.Permission.Ban.Permission.Lore"));
                            } else {
                                itemLore.addAll(configLoad.getStringList(
                                        "Menu.Visitors.Item.Visitor.Kick.Permission.Ban.NoPermission.Lore"));
                            }
                        } else {
                            if (banningEnabled && ((isOperator && canBan) || isOwner)) {
                                itemLore.addAll(configLoad.getStringList(
                                        "Menu.Visitors.Item.Visitor.Kick.NoPermission.Ban.Permission.Lore"));
                            } else {
                                itemLore.addAll(configLoad.getStringList(
                                        "Menu.Visitors.Item.Visitor.Kick.NoPermission.Ban.NoPermission.Lore"));
                            }
                        }

                        ItemStack phead = SkullItemCreator.byPlayer(targetPlayer);
                        nInv.addItem(
                                nInv.createItem(phead,
                                        ChatColor.translateAlternateColorCodes('&',
                                                configLoad.getString("Menu.Visitors.Item.Visitor.Displayname")
                                                        .replace("%player", targetPlayer.getName())),
                                        itemLore,
                                        new Placeholder[]{new Placeholder("%time", islandVisitTimeFormatted)}, null,
                                        null),
                                inventorySlot);
                    }
                }
            }

            nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visitors.Title")));
            nInv.setRows(6);

            Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
        }
    }
}
