package com.craftaro.skyblock.menus;

import com.craftaro.core.utils.SkullItemCreator;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.island.IslandStatus;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.placeholder.Placeholder;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.item.nInventoryUtil;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import com.craftaro.skyblock.visit.Visit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Information {
    private static Information instance;

    public static Information getInstance() {
        if (instance == null) {
            instance = new Information();
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

            if (playerData.getViewer() != null) {
                Information.Viewer viewer = (Information.Viewer) playerData.getViewer();
                org.bukkit.OfflinePlayer targetOfflinePlayer = Bukkit.getServer().getOfflinePlayer(viewer.getOwner());

                if (islandManager.getIsland(targetOfflinePlayer) == null) {
                    islandManager.loadIsland(targetOfflinePlayer);
                }

                FileConfiguration configLoad = plugin.getLanguage();
                Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(viewer.getOwner()));

                if (island == null) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Information.Island.Message"));
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    return;
                }

                if (viewer.getType() == Information.Viewer.Type.VISITORS) {
                    if (island.getStatus() != IslandStatus.CLOSED) {
                        if (islandManager.getVisitorsAtIsland(island).isEmpty()) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Information.Visitors.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            playerData.setViewer(
                                    new Information.Viewer(viewer.getOwner(), Information.Viewer.Type.CATEGORIES));
                            open(player);

                            return;
                        }
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Island.Information.Closed.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        playerData.setViewer(
                                new Information.Viewer(viewer.getOwner(), Information.Viewer.Type.CATEGORIES));
                        open(player);

                        return;
                    }
                }

                Visit visit = island.getVisit();

                String islandOwnerName = "";
                org.bukkit.OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(viewer.getOwner());

                if (targetPlayer == null) {
                    islandOwnerName = new OfflinePlayer(viewer.getOwner()).getName();
                } else {
                    islandOwnerName = targetPlayer.getName();
                }

                if (viewer.getType() == Information.Viewer.Type.CATEGORIES) {
                    nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                        if (playerDataManager.hasPlayerData(player)) {
                            PlayerData playerData13 = playerDataManager.getPlayerData(player);
                            ItemStack is = event.getItem();

                            if (XMaterial.OAK_FENCE_GATE.isSimilar(is) && is.hasItemMeta()
                                    && (is.getItemMeta().getDisplayName().equals(plugin.formatText(configLoad.getString("Menu.Information.Categories.Item.Exit.Displayname"))))) {
                                soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
                            } else if ((XMaterial.ITEM_FRAME.isSimilar(is)) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(plugin.formatText(configLoad.getString("Menu.Information.Categories.Item.Members.Displayname"))))) {
                                playerData13.setViewer(new Viewer(((Viewer) playerData13.getViewer()).getOwner(), Viewer.Type.MEMBERS));
                                soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player), 1L);
                            } else if ((XMaterial.MAP.isSimilar(is))
                                    && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    plugin.formatText(configLoad.getString(
                                            "Menu.Information.Categories.Item.Information.Displayname"))))) {
                                soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else if ((XMaterial.PAINTING.isSimilar(is)) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    plugin.formatText(configLoad.getString(
                                            "Menu.Information.Categories.Item.Visitors.Displayname"))))) {
                                playerData13.setViewer(new Viewer(
                                        ((Viewer) playerData13.getViewer()).getOwner(),
                                        Viewer.Type.VISITORS));
                                soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player), 1L);
                            }
                        }
                    });

                    nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Information.Categories.Item.Exit.Displayname"), null, null, null,
                            null), 0, 4);
                    nInv.addItem(nInv.createItem(XMaterial.ITEM_FRAME.parseItem(),
                            configLoad.getString("Menu.Information.Categories.Item.Members.Displayname"),
                            configLoad.getStringList("Menu.Information.Categories.Item.Members.Lore"), null, null,
                            null), 1);
                    nInv.addItem(nInv.createItem(XMaterial.PAINTING.parseItem(),
                            configLoad.getString("Menu.Information.Categories.Item.Visitors.Displayname"),
                            configLoad.getStringList("Menu.Information.Categories.Item.Visitors.Lore"), null, null,
                            null), 3);

                    List<String> itemLore = new ArrayList<>();

                    String safety;
                    if (visit.getSafeLevel() > 0) {
                        safety = configLoad.getString("Menu.Information.Categories.Item.Information.Vote.Word.Unsafe");
                    } else {
                        safety = configLoad.getString("Menu.Information.Categories.Item.Information.Vote.Word.Safe");
                    }

                    if (plugin.getConfiguration().getBoolean("Island.Visitor.Vote")) {
                        if (plugin.getConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
                            for (String itemLoreList : configLoad.getStringList(
                                    "Menu.Information.Categories.Item.Information.Vote.Enabled.Signature.Enabled.Lore")) {
                                if (itemLoreList.contains("%signature")) {
                                    List<String> islandSignature = visit.getSignature();

                                    if (islandSignature.isEmpty()) {
                                        itemLore.add(configLoad.getString(
                                                "Menu.Information.Categories.Item.Information.Vote.Word.Empty"));
                                    } else {
                                        itemLore.addAll(islandSignature);
                                    }
                                } else {
                                    itemLore.add(itemLoreList);
                                }
                            }
                        } else {
                            itemLore.addAll(configLoad.getStringList(
                                    "Menu.Information.Categories.Item.Information.Vote.Enabled.Signature.Disabled.Lore"));
                        }

                        nInv.addItem(nInv.createItem(XMaterial.MAP.parseItem(),
                                configLoad.getString("Menu.Information.Categories.Item.Information.Displayname"),
                                itemLore,
                                new Placeholder[]{new Placeholder("%level", "" + visit.getLevel().getLevel()),
                                        new Placeholder("%members", "" + visit.getMembers()),
                                        new Placeholder("%votes", "" + visit.getVoters().size()),
                                        new Placeholder("%visits", "" + visit.getVisitors().size()),
                                        new Placeholder("%players",
                                                "" + islandManager.getPlayersAtIsland(island).size()),
                                        new Placeholder("%player_capacity",
                                                "" + plugin.getConfiguration()
                                                        .getInt("Island.Visitor.Capacity")),
                                        new Placeholder("%owner", islandOwnerName),
                                        new Placeholder("%safety", safety)},
                                null, null), 2);
                    } else {
                        if (plugin.getConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
                            for (String itemLoreList : configLoad.getStringList(
                                    "Menu.Information.Categories.Item.Information.Vote.Disabled.Signature.Enabled.Lore")) {
                                if (itemLoreList.contains("%signature")) {
                                    List<String> islandSignature = visit.getSignature();

                                    if (islandSignature.isEmpty()) {
                                        itemLore.add(configLoad.getString(
                                                "Menu.Information.Categories.Item.Information.Vote.Word.Empty"));
                                    } else {
                                        for (String signatureList : islandSignature) {
                                            itemLore.add(signatureList);
                                        }
                                    }
                                } else {
                                    itemLore.add(itemLoreList);
                                }
                            }
                        } else {
                            itemLore.addAll(configLoad.getStringList(
                                    "Menu.Information.Categories.Item.Information.Vote.Disabled.Signature.Disabled.Lore"));
                        }

                        nInv.addItem(nInv.createItem(XMaterial.MAP.parseItem(),
                                configLoad.getString("Menu.Information.Categories.Item.Information.Displayname"),
                                itemLore,
                                new Placeholder[]{new Placeholder("%level", "" + visit.getLevel().getLevel()),
                                        new Placeholder("%members", "" + visit.getMembers()),
                                        new Placeholder("%visits", "" + visit.getVisitors().size()),
                                        new Placeholder("%players",
                                                "" + islandManager.getPlayersAtIsland(island).size()),
                                        new Placeholder("%player_capacity",
                                                "" + plugin.getConfiguration()
                                                        .getInt("Island.Visitor.Capacity")),
                                        new Placeholder("%owner", islandOwnerName),
                                        new Placeholder("%safety", safety)},
                                null, null), 2);
                    }

                    nInv.setTitle(plugin.formatText(
                            configLoad.getString("Menu.Information.Categories.Title")));
                    nInv.setType(InventoryType.HOPPER);

                    Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
                } else if (viewer.getType() == Information.Viewer.Type.MEMBERS) {
                    nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                        if (playerDataManager.hasPlayerData(player)) {
                            PlayerData playerData1 = playerDataManager.getPlayerData(player);
                            ItemStack is = event.getItem();

                            if ((XMaterial.OAK_FENCE_GATE.isSimilar(is)) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Members.Item.Return.Displayname"))))) {
                                playerData1.setViewer(new Viewer(((Viewer) playerData1.getViewer()).getOwner(), Viewer.Type.CATEGORIES));
                                soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player), 1L);
                            } else if ((XMaterial.PAINTING.isSimilar(is)) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Members.Item.Statistics.Displayname"))))) {
                                soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else if ((XMaterial.BLACK_STAINED_GLASS_PANE.isSimilar(is))
                                    && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Members.Item.Barrier.Displayname"))))) {
                                soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else if ((XMaterial.PLAYER_HEAD.isSimilar(is))
                                    && (is.hasItemMeta())) {
                                if (is.getItemMeta().getDisplayName()
                                        .equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                "Menu.Information.Members.Item.Previous.Displayname")))) {
                                    playerData1.setPage(MenuType.INFORMATION, playerData1.getPage(MenuType.INFORMATION) - 1);
                                    soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else if (is.getItemMeta().getDisplayName()
                                        .equals(ChatColor.translateAlternateColorCodes('&', configLoad
                                                .getString("Menu.Information.Members.Item.Next.Displayname")))) {
                                    playerData1.setPage(MenuType.INFORMATION, playerData1.getPage(MenuType.INFORMATION) + 1);
                                    soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else {
                                    soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                }
                            }
                        }
                    });

                    List<UUID> displayedMembers = new ArrayList<>();

                    Set<UUID> islandMembers = island.getRole(IslandRole.MEMBER);
                    Set<UUID> islandOperators = island.getRole(IslandRole.OPERATOR);

                    displayedMembers.add(island.getOwnerUUID());
                    displayedMembers.addAll(islandOperators);
                    displayedMembers.addAll(islandMembers);

                    nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Information.Members.Item.Return.Displayname"), null, null, null,
                            null), 0, 8);
                    nInv.addItem(
                            nInv.createItem(new ItemStack(Material.PAINTING),
                                    configLoad.getString("Menu.Information.Members.Item.Statistics.Displayname"),
                                    configLoad.getStringList("Menu.Information.Members.Item.Statistics.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%island_members",
                                                    "" + (islandMembers.size() + islandOperators.size() + 1)),
                                            new Placeholder("%island_capacity",
                                                    "" + island.getMaxMembers(player)),
                                            new Placeholder("%members", "" + islandMembers.size()),
                                            new Placeholder("%operators", "" + islandOperators.size())},
                                    null, null),
                            4);
                    nInv.addItem(nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                            configLoad.getString("Menu.Information.Members.Item.Barrier.Displayname"), null, null, null,
                            null), 9, 10, 11, 12, 13, 14, 15, 16, 17);

                    int playerMenuPage = playerData.getPage(MenuType.INFORMATION),
                            nextEndIndex = displayedMembers.size() - playerMenuPage * 36;

                    if (playerMenuPage != 1) {
                        ItemStack Lhead = SkullItemCreator.byTextureUrlHash("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
                        nInv.addItem(nInv.createItem(Lhead,
                                configLoad.getString("Menu.Information.Members.Item.Previous.Displayname"), null, null,
                                null, null), 1);
                    }

                    if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                        ItemStack Rhead = SkullItemCreator.byTextureUrlHash("1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b");
                        nInv.addItem(nInv.createItem(Rhead,
                                configLoad.getString("Menu.Information.Members.Item.Next.Displayname"), null, null,
                                null, null), 7);
                    }

                    int index = playerMenuPage * 36 - 36,
                            endIndex = index >= displayedMembers.size() ? displayedMembers.size() - 1 : index + 36,
                            inventorySlot = 17;

                    for (; index < endIndex; index++) {
                        if (displayedMembers.size() > index) {
                            inventorySlot++;

                            UUID playerUUID = displayedMembers.get(index);

                            String[] playerTexture;
                            String playerName, islandRole;

                            targetPlayer = Bukkit.getServer().getOfflinePlayer(playerUUID);

                            if (targetPlayer == null) {
                                OfflinePlayer offlinePlayer = new OfflinePlayer(playerUUID);
                                playerName = offlinePlayer.getName();
                                playerTexture = offlinePlayer.getTexture();
                            } else {
                                playerName = targetPlayer.getName();
                                playerData = plugin.getPlayerDataManager().getPlayerData(targetPlayer.getUniqueId());
                                playerTexture = playerData.getTexture();
                            }

                            if (islandMembers.contains(playerUUID)) {
                                islandRole = configLoad.getString("Menu.Information.Members.Item.Member.Word.Member");
                            } else if (islandOperators.contains(playerUUID)) {
                                islandRole = configLoad.getString("Menu.Information.Members.Item.Member.Word.Operator");
                            } else {
                                islandRole = configLoad.getString("Menu.Information.Members.Item.Member.Word.Owner");
                            }

                            ItemStack phead;
                            if (playerTexture.length >= 1 && playerTexture[0] != null) {
                                phead = SkullItemCreator.byTextureValue(playerTexture[0]);
                            } else {
                                try {
                                    phead = SkullItemCreator.byUuid(playerUUID).get();
                                } catch (InterruptedException | ExecutionException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }

                            nInv.addItem(
                                    nInv.createItem(phead,
                                            configLoad.getString("Menu.Information.Members.Item.Member.Displayname")
                                                    .replace("%player", playerName),
                                            configLoad.getStringList("Menu.Information.Members.Item.Member.Lore"),
                                            new Placeholder[]{new Placeholder("%role", islandRole)}, null, null),
                                    inventorySlot);
                        }
                    }

                    nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Information.Members.Title")));
                    nInv.setRows(6);

                    Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
                } else if (viewer.getType() == Information.Viewer.Type.VISITORS) {
                    nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                        if (playerDataManager.hasPlayerData(player)) {
                            PlayerData playerData12 = playerDataManager.getPlayerData(player);
                            ItemStack is = event.getItem();

                            if ((XMaterial.OAK_FENCE_GATE.isSimilar(is)) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Visitors.Item.Return.Displayname"))))) {
                                playerData12.setViewer(new Viewer(
                                        ((Viewer) playerData12.getViewer()).getOwner(),
                                        Viewer.Type.CATEGORIES));
                                soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player), 1L);
                            } else if ((XMaterial.PAINTING.isSimilar(is)) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Visitors.Item.Statistics.Displayname"))))) {
                                soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else if ((XMaterial.BLACK_STAINED_GLASS_PANE.isSimilar(is))
                                    && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Visitors.Item.Barrier.Displayname"))))) {
                                soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else if ((XMaterial.PLAYER_HEAD.isSimilar(is))
                                    && (is.hasItemMeta())) {
                                if (is.getItemMeta().getDisplayName()
                                        .equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                "Menu.Information.Visitors.Item.Previous.Displayname")))) {
                                    playerData12.setPage(MenuType.INFORMATION, playerData12.getPage(MenuType.INFORMATION) - 1);
                                    soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else if (is.getItemMeta().getDisplayName()
                                        .equals(ChatColor.translateAlternateColorCodes('&', configLoad
                                                .getString("Menu.Information.Visitors.Item.Next.Displayname")))) {
                                    playerData12.setPage(MenuType.INFORMATION, playerData12.getPage(MenuType.INFORMATION) + 1);
                                    soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else {
                                    soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                }
                            }
                        }
                    });

                    List<UUID> displayedVisitors = new ArrayList<>(islandManager.getVisitorsAtIsland(island));

                    nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Information.Visitors.Item.Return.Displayname"), null, null, null,
                            null), 0, 8);
                    nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                            configLoad.getString("Menu.Information.Visitors.Item.Statistics.Displayname"),
                            configLoad.getStringList("Menu.Information.Visitors.Item.Statistics.Lore"),
                            new Placeholder[]{new Placeholder("%island_visitors", "" + displayedVisitors.size())},
                            null, null), 4);
                    nInv.addItem(nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                            configLoad.getString("Menu.Information.Visitors.Item.Barrier.Displayname"), null, null,
                            null, null), 9, 10, 11, 12, 13, 14, 15, 16, 17);

                    int playerMenuPage = playerData.getPage(MenuType.INFORMATION),
                            nextEndIndex = displayedVisitors.size() - playerMenuPage * 36;

                    if (playerMenuPage != 1) {
                        ItemStack Lhead = SkullItemCreator.byTextureUrlHash("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
                        nInv.addItem(nInv.createItem(Lhead,
                                configLoad.getString("Menu.Information.Visitors.Item.Previous.Displayname"), null, null,
                                null, null), 1);
                    }

                    if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                        ItemStack Rhead = SkullItemCreator.byTextureUrlHash("1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b");
                        nInv.addItem(nInv.createItem(Rhead,
                                configLoad.getString("Menu.Information.Visitors.Item.Next.Displayname"), null, null,
                                null, null), 7);
                    }

                    int index = playerMenuPage * 36 - 36,
                            endIndex = index >= displayedVisitors.size() ? displayedVisitors.size() - 1 : index + 36,
                            inventorySlot = 17;

                    for (; index < endIndex; index++) {
                        if (displayedVisitors.size() > index) {
                            inventorySlot++;

                            UUID playerUUID = displayedVisitors.get(index);

                            String[] playerTexture;
                            String playerName;

                            //org.bukkit.OfflinePlayer targetPlayer = Bukkit.getServer().getOfflinePlayer(playerUUID);

                            if (targetPlayer == null) {
                                OfflinePlayer offlinePlayer = new OfflinePlayer(playerUUID);
                                playerName = offlinePlayer.getName();
                                playerTexture = offlinePlayer.getTexture();
                            } else {
                                playerName = targetPlayer.getName();
                                playerData = plugin.getPlayerDataManager().getPlayerData(targetPlayer.getUniqueId());
                                playerTexture = playerData.getTexture();
                            }


                            ItemStack phead;
                            if (playerTexture.length >= 1 && playerTexture[0] != null) {
                                phead = SkullItemCreator.byTextureValue(playerTexture[0]);
                            } else {
                                try {
                                    phead = SkullItemCreator.byUuid(playerUUID).get();
                                } catch (InterruptedException | ExecutionException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }

                            nInv.addItem(
                                    nInv.createItem(phead,
                                            configLoad.getString("Menu.Information.Visitors.Item.Visitor.Displayname")
                                                    .replace("%player", playerName),
                                            null, null, null, null),
                                    inventorySlot);
                        }
                    }

                    nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Information.Visitors.Title")));
                    nInv.setRows(6);

                    Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
                }

                islandManager.unloadIsland(island, null);
            }
        }
    }

    public static class Viewer {
        private final UUID islandOwnerUUID;
        private final Type type;

        public Viewer(UUID islandOwnerUUID, Type type) {
            this.islandOwnerUUID = islandOwnerUUID;
            this.type = type;
        }

        public UUID getOwner() {
            return this.islandOwnerUUID;
        }

        public Type getType() {
            return this.type;
        }

        public enum Type {
            CATEGORIES, MEMBERS, VISITORS
        }
    }
}
