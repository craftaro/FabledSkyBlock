package com.songoda.skyblock.menus;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.item.SkullUtil;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.utils.version.Materials;
import com.songoda.skyblock.utils.version.Sounds;
import com.songoda.skyblock.visit.Visit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Information {

    private static Information instance;

    public static Information getInstance() {
        if (instance == null) {
            instance = new Information();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock skyblock = SkyBlock.getInstance();

        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        if (playerDataManager.hasPlayerData(player)) {
            PlayerData playerData = playerDataManager.getPlayerData(player);

            if (playerData.getViewer() != null) {
                Information.Viewer viewer = (Information.Viewer) playerData.getViewer();
                org.bukkit.OfflinePlayer targetOfflinePlayer = Bukkit.getServer().getOfflinePlayer(viewer.getOwner());

                if (islandManager.getIsland(targetOfflinePlayer) == null) {
                    islandManager.loadIsland(targetOfflinePlayer);
                }

                FileConfiguration configLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                        .getFileConfiguration();
                Island island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(viewer.getOwner()));

                if (island == null) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Information.Island.Message"));
                    soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

                    return;
                }

                if (viewer.getType() == Information.Viewer.Type.Visitors) {
                    if (island.isOpen()) {
                        if (islandManager.getVisitorsAtIsland(island).size() == 0) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Information.Visitors.Message"));
                            soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

                            playerData.setViewer(
                                    new Information.Viewer(viewer.getOwner(), Information.Viewer.Type.Categories));
                            open(player);

                            return;
                        }
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Island.Information.Closed.Message"));
                        soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

                        playerData.setViewer(
                                new Information.Viewer(viewer.getOwner(), Information.Viewer.Type.Categories));
                        open(player);

                        return;
                    }
                }

                Visit visit = island.getVisit();

                String islandOwnerName = "";
                Player targetPlayer = Bukkit.getServer().getPlayer(viewer.getOwner());

                if (targetPlayer == null) {
                    islandOwnerName = new OfflinePlayer(viewer.getOwner()).getName();
                } else {
                    islandOwnerName = targetPlayer.getName();
                }

                if (viewer.getType() == Information.Viewer.Type.Categories) {
                    nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                        if (playerDataManager.hasPlayerData(player)) {
                            PlayerData playerData13 = playerDataManager.getPlayerData(player);
                            ItemStack is = event.getItem();

                            if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Categories.Item.Exit.Displayname"))))) {
                                soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
                            } else if ((is.getType() == Materials.ITEM_FRAME.parseMaterial()) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Categories.Item.Members.Displayname"))))) {
                                playerData13.setViewer(new Viewer(
                                        ((Viewer) playerData13.getViewer()).getOwner(),
                                        Viewer.Type.Members));
                                soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                        () -> open(player), 1L);
                            } else if ((is.getType() == Materials.LEGACY_EMPTY_MAP.getPostMaterial())
                                    && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Categories.Item.Information.Displayname"))))) {
                                soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else if ((is.getType() == Materials.PAINTING.parseMaterial()) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Categories.Item.Visitors.Displayname"))))) {
                                playerData13.setViewer(new Viewer(
                                        ((Viewer) playerData13.getViewer()).getOwner(),
                                        Viewer.Type.Visitors));
                                soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                        () -> open(player), 1L);
                            }
                        }
                    });

                    nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Information.Categories.Item.Exit.Displayname"), null, null, null,
                            null), 0, 4);
                    nInv.addItem(nInv.createItem(Materials.ITEM_FRAME.parseItem(),
                            configLoad.getString("Menu.Information.Categories.Item.Members.Displayname"),
                            configLoad.getStringList("Menu.Information.Categories.Item.Members.Lore"), null, null,
                            null), 1);
                    nInv.addItem(nInv.createItem(Materials.PAINTING.parseItem(),
                            configLoad.getString("Menu.Information.Categories.Item.Visitors.Displayname"),
                            configLoad.getStringList("Menu.Information.Categories.Item.Visitors.Lore"), null, null,
                            null), 3);

                    Config mainConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));
                    List<String> itemLore = new ArrayList<>();

                    String safety = "";

                    if (visit.getSafeLevel() > 0) {
                        safety = configLoad.getString("Menu.Information.Categories.Item.Information.Vote.Word.Unsafe");
                    } else {
                        safety = configLoad.getString("Menu.Information.Categories.Item.Information.Vote.Word.Safe");
                    }

                    if (mainConfig.getFileConfiguration().getBoolean("Island.Visitor.Vote")) {
                        if (mainConfig.getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
                            for (String itemLoreList : configLoad.getStringList(
                                    "Menu.Information.Categories.Item.Information.Vote.Enabled.Signature.Enabled.Lore")) {
                                if (itemLoreList.contains("%signature")) {
                                    List<String> islandSignature = visit.getSiganture();

                                    if (islandSignature.size() == 0) {
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
                                    "Menu.Information.Categories.Item.Information.Vote.Enabled.Signature.Disabled.Lore"));
                        }

                        nInv.addItem(nInv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(),
                                configLoad.getString("Menu.Information.Categories.Item.Information.Displayname"),
                                itemLore,
                                new Placeholder[]{new Placeholder("%level", "" + visit.getLevel().getLevel()),
                                        new Placeholder("%members", "" + visit.getMembers()),
                                        new Placeholder("%votes", "" + visit.getVoters().size()),
                                        new Placeholder("%visits", "" + visit.getVisitors().size()),
                                        new Placeholder("%players",
                                                "" + islandManager.getPlayersAtIsland(island).size()),
                                        new Placeholder("%player_capacity",
                                                "" + mainConfig.getFileConfiguration()
                                                        .getInt("Island.Visitor.Capacity")),
                                        new Placeholder("%owner", islandOwnerName),
                                        new Placeholder("%safety", safety)},
                                null, null), 2);
                    } else {
                        if (mainConfig.getFileConfiguration().getBoolean("Island.Visitor.Signature.Enable")) {
                            for (String itemLoreList : configLoad.getStringList(
                                    "Menu.Information.Categories.Item.Information.Vote.Disabled.Signature.Enabled.Lore")) {
                                if (itemLoreList.contains("%signature")) {
                                    List<String> islandSignature = visit.getSiganture();

                                    if (islandSignature.size() == 0) {
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

                        nInv.addItem(nInv.createItem(Materials.LEGACY_EMPTY_MAP.getPostItem(),
                                configLoad.getString("Menu.Information.Categories.Item.Information.Displayname"),
                                itemLore,
                                new Placeholder[]{new Placeholder("%level", "" + visit.getLevel().getLevel()),
                                        new Placeholder("%members", "" + visit.getMembers()),
                                        new Placeholder("%visits", "" + visit.getVisitors().size()),
                                        new Placeholder("%players",
                                                "" + islandManager.getPlayersAtIsland(island).size()),
                                        new Placeholder("%player_capacity",
                                                "" + mainConfig.getFileConfiguration()
                                                        .getInt("Island.Visitor.Capacity")),
                                        new Placeholder("%owner", islandOwnerName),
                                        new Placeholder("%safety", safety)},
                                null, null), 2);
                    }

                    nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Information.Categories.Title")));
                    nInv.setType(InventoryType.HOPPER);

                    Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
                } else if (viewer.getType() == Information.Viewer.Type.Members) {
                    nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                        if (playerDataManager.hasPlayerData(player)) {
                            PlayerData playerData1 = playerDataManager.getPlayerData(player);
                            ItemStack is = event.getItem();

                            if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Members.Item.Return.Displayname"))))) {
                                playerData1.setViewer(new Viewer(
                                        ((Viewer) playerData1.getViewer()).getOwner(),
                                        Viewer.Type.Categories));
                                soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                        () -> open(player), 1L);
                            } else if ((is.getType() == Materials.PAINTING.parseMaterial()) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Members.Item.Statistics.Displayname"))))) {
                                soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else if ((is.getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial())
                                    && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Members.Item.Barrier.Displayname"))))) {
                                soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else if ((is.getType() == SkullUtil.createItemStack().getType())
                                    && (is.hasItemMeta())) {
                                if (is.getItemMeta().getDisplayName()
                                        .equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                "Menu.Information.Members.Item.Previous.Displayname")))) {
                                    playerData1.setPage(playerData1.getPage() - 1);
                                    soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

                                    Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                            () -> open(player), 1L);
                                } else if (is.getItemMeta().getDisplayName()
                                        .equals(ChatColor.translateAlternateColorCodes('&', configLoad
                                                .getString("Menu.Information.Members.Item.Next.Displayname")))) {
                                    playerData1.setPage(playerData1.getPage() + 1);
                                    soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

                                    Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                            () -> open(player), 1L);
                                } else {
                                    soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F,
                                            1.0F);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                }
                            }
                        }
                    });

                    List<UUID> displayedMembers = new ArrayList<>();

                    Set<UUID> islandMembers = island.getRole(IslandRole.Member);
                    Set<UUID> islandOperators = island.getRole(IslandRole.Operator);

                    displayedMembers.add(island.getOwnerUUID());
                    displayedMembers.addAll(islandOperators);
                    displayedMembers.addAll(islandMembers);

                    nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
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
                                                    "" + skyblock.getFileManager()
                                                            .getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                                                            .getFileConfiguration().getInt("Island.Member.Capacity")),
                                            new Placeholder("%members", "" + islandMembers.size()),
                                            new Placeholder("%operators", "" + islandOperators.size())},
                                    null, null),
                            4);
                    nInv.addItem(nInv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(),
                            configLoad.getString("Menu.Information.Members.Item.Barrier.Displayname"), null, null, null,
                            null), 9, 10, 11, 12, 13, 14, 15, 16, 17);

                    int playerMenuPage = playerData.getPage(),
                            nextEndIndex = displayedMembers.size() - playerMenuPage * 36;

                    if (playerMenuPage != 1) {
                        nInv.addItem(nInv.createItem(SkullUtil.create(
                                "ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=",
                                "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="),
                                configLoad.getString("Menu.Information.Members.Item.Previous.Displayname"), null, null,
                                null, null), 1);
                    }

                    if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                        nInv.addItem(nInv.createItem(SkullUtil.create(
                                "wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=",
                                "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="),
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

                            targetPlayer = Bukkit.getServer().getPlayer(playerUUID);

                            if (targetPlayer == null) {
                                OfflinePlayer offlinePlayer = new OfflinePlayer(playerUUID);
                                playerName = offlinePlayer.getName();
                                playerTexture = offlinePlayer.getTexture();
                            } else {
                                playerName = targetPlayer.getName();
                                playerData = skyblock.getPlayerDataManager().getPlayerData(targetPlayer);
                                playerTexture = playerData.getTexture();
                            }

                            if (islandMembers.contains(playerUUID)) {
                                islandRole = configLoad.getString("Menu.Information.Members.Item.Member.Word.Member");
                            } else if (islandOperators.contains(playerUUID)) {
                                islandRole = configLoad.getString("Menu.Information.Members.Item.Member.Word.Operator");
                            } else {
                                islandRole = configLoad.getString("Menu.Information.Members.Item.Member.Word.Owner");
                            }

                            nInv.addItem(
                                    nInv.createItem(SkullUtil.create(playerTexture[0], playerTexture[1]),
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

                    Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
                } else if (viewer.getType() == Information.Viewer.Type.Visitors) {
                    nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                        if (playerDataManager.hasPlayerData(player)) {
                            PlayerData playerData12 = playerDataManager.getPlayerData(player);
                            ItemStack is = event.getItem();

                            if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Visitors.Item.Return.Displayname"))))) {
                                playerData12.setViewer(new Viewer(
                                        ((Viewer) playerData12.getViewer()).getOwner(),
                                        Viewer.Type.Categories));
                                soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                        () -> open(player), 1L);
                            } else if ((is.getType() == Materials.PAINTING.parseMaterial()) && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Visitors.Item.Statistics.Displayname"))))) {
                                soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else if ((is.getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial())
                                    && (is.hasItemMeta())
                                    && (is.getItemMeta().getDisplayName().equals(
                                    ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                            "Menu.Information.Visitors.Item.Barrier.Displayname"))))) {
                                soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else if ((is.getType() == SkullUtil.createItemStack().getType())
                                    && (is.hasItemMeta())) {
                                if (is.getItemMeta().getDisplayName()
                                        .equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString(
                                                "Menu.Information.Visitors.Item.Previous.Displayname")))) {
                                    playerData12.setPage(playerData12.getPage() - 1);
                                    soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

                                    Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                            () -> open(player), 1L);
                                } else if (is.getItemMeta().getDisplayName()
                                        .equals(ChatColor.translateAlternateColorCodes('&', configLoad
                                                .getString("Menu.Information.Visitors.Item.Next.Displayname")))) {
                                    playerData12.setPage(playerData12.getPage() + 1);
                                    soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

                                    Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                            () -> open(player), 1L);
                                } else {
                                    soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F,
                                            1.0F);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                }
                            }
                        }
                    });

                    List<UUID> displayedVisitors = new ArrayList<>();
                    displayedVisitors.addAll(islandManager.getVisitorsAtIsland(island));

                    nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
                            configLoad.getString("Menu.Information.Visitors.Item.Return.Displayname"), null, null, null,
                            null), 0, 8);
                    nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                            configLoad.getString("Menu.Information.Visitors.Item.Statistics.Displayname"),
                            configLoad.getStringList("Menu.Information.Visitors.Item.Statistics.Lore"),
                            new Placeholder[]{new Placeholder("%island_visitors", "" + displayedVisitors.size())},
                            null, null), 4);
                    nInv.addItem(nInv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(),
                            configLoad.getString("Menu.Information.Visitors.Item.Barrier.Displayname"), null, null,
                            null, null), 9, 10, 11, 12, 13, 14, 15, 16, 17);

                    int playerMenuPage = playerData.getPage(),
                            nextEndIndex = displayedVisitors.size() - playerMenuPage * 36;

                    if (playerMenuPage != 1) {
                        nInv.addItem(nInv.createItem(SkullUtil.create(
                                "ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=",
                                "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="),
                                configLoad.getString("Menu.Information.Visitors.Item.Previous.Displayname"), null, null,
                                null, null), 1);
                    }

                    if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                        nInv.addItem(nInv.createItem(SkullUtil.create(
                                "wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=",
                                "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="),
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

                            targetPlayer = Bukkit.getServer().getPlayer(playerUUID);

                            if (targetPlayer == null) {
                                OfflinePlayer offlinePlayer = new OfflinePlayer(playerUUID);
                                playerName = offlinePlayer.getName();
                                playerTexture = offlinePlayer.getTexture();
                            } else {
                                playerName = targetPlayer.getName();
                                playerData = skyblock.getPlayerDataManager().getPlayerData(targetPlayer);
                                playerTexture = playerData.getTexture();
                            }

                            nInv.addItem(
                                    nInv.createItem(SkullUtil.create(playerTexture[0], playerTexture[1]),
                                            configLoad.getString("Menu.Information.Visitors.Item.Visitor.Displayname")
                                                    .replace("%player", playerName),
                                            null, null, null, null),
                                    inventorySlot);
                        }
                    }

                    nInv.setTitle(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Information.Visitors.Title")));
                    nInv.setRows(6);

                    Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
                }

                islandManager.unloadIsland(island, null);
            }
        }
    }

    public static class Viewer {

        private UUID islandOwnerUUID;
        private Type type;

        public Viewer(UUID islandOwnerUUID, Type type) {
            this.islandOwnerUUID = islandOwnerUUID;
            this.type = type;
        }

        public UUID getOwner() {
            return islandOwnerUUID;
        }

        public Type getType() {
            return type;
        }

        public enum Type {

            Categories, Members, Visitors

        }
    }
}
