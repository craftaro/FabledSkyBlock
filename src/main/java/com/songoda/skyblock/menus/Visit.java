package com.songoda.skyblock.menus;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.utils.NumberUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.player.PlayerVoteEvent;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandStatus;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.StringUtil;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.visit.VisitManager;
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
import java.util.UUID;

public class Visit {
    private static Visit instance;

    public static Visit getInstance() {
        if (instance == null) {
            instance = new Visit();
        }

        return instance;
    }

    public void open(Player player, Visit.Type type, Visit.Sort sort) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();
        VisitManager visitManager = plugin.getVisitManager();
        FileManager fileManager = plugin.getFileManager();

        FileConfiguration configLoad = plugin.getLanguage();

        nInventoryUtil nInv = new nInventoryUtil(player, event -> {
            if (playerDataManager.hasPlayerData(player)) {
                PlayerData playerData = playerDataManager.getPlayerData(player);

                if (playerData.getType() == null || playerData.getSort() == null) {
                    playerData.setType(Type.DEFAULT);
                    playerData.setSort(Sort.DEFAULT);
                }

                ItemStack is = event.getItem();

                if ((is.getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Visit.Item.Barrier.Displayname"))))) {
                    soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                } else if ((is.getType() == XMaterial.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Visit.Item.Exit.Displayname"))))) {
                    soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
                } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Visit.Item.Statistics.Displayname"))))) {
                    soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                } else if ((is.getType() == Material.HOPPER) && (is.hasItemMeta())) {
                    if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Visit.Item.Type.Displayname")))) {
                        Type type1 = (Type) playerData.getType();

                        if (type1.ordinal() + 1 == Type.values().length) {
                            playerData.setType(Type.DEFAULT);
                        } else {
                            playerData.setType(Type.values()[type1.ordinal() + 1]);
                        }
                    } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Visit.Item.Sort.Displayname")))) {
                        Sort sort1 = (Sort) playerData.getSort();

                        if (sort1.ordinal() + 1 == Sort.values().length) {
                            playerData.setSort(Sort.DEFAULT);
                        } else {
                            playerData.setSort(Sort.values()[sort1.ordinal() + 1]);
                        }
                    }

                    soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, (Type) playerData.getType(), (Sort) playerData.getSort()), 1L);
                } else if ((is.getType() == Material.BARRIER) && (is.hasItemMeta())
                        && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                        configLoad.getString("Menu.Visit.Item.Nothing.Displayname"))))) {
                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                } else if ((is.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) && (is.hasItemMeta())) {
                    if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Visit.Item.Previous.Displayname")))) {
                        playerData.setPage(MenuType.VISIT, playerData.getPage(MenuType.VISIT) - 1);
                        soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, (Type) playerData.getType(), (Sort) playerData.getSort()), 1L);
                    } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Visit.Item.Next.Displayname")))) {
                        playerData.setPage(MenuType.VISIT, playerData.getPage(MenuType.VISIT) + 1);
                        soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, (Type) playerData.getType(), (Sort) playerData.getSort()), 1L);
                    } else {
                        String targetPlayerName = ChatColor.stripColor(is.getItemMeta().getDisplayName());
                        UUID targetPlayerUUID;

                        Player targetPlayer = Bukkit.getServer().getPlayer(targetPlayerName);

                        if (targetPlayer == null) {
                            targetPlayerUUID = new OfflinePlayer(targetPlayerName).getUniqueId();
                        } else {
                            targetPlayerUUID = targetPlayer.getUniqueId();
                        }

                        if (visitManager.hasIsland(targetPlayerUUID)) {
                            com.songoda.skyblock.visit.Visit visit = visitManager.getIsland(targetPlayerUUID);
                            boolean isCoopPlayer = false;
                            boolean isWhitelistedPlayer = false;
                            org.bukkit.OfflinePlayer offlinePlayer = Bukkit.getServer()
                                    .getOfflinePlayer(targetPlayerUUID);

                            if (islandManager.containsIsland(targetPlayerUUID)) {
                                if (islandManager.getIsland(offlinePlayer).isCoopPlayer(player.getUniqueId())) {
                                    isCoopPlayer = true;
                                }
                                if (visit.getStatus().equals(IslandStatus.WHITELISTED) &&
                                        islandManager.getIsland(offlinePlayer).isPlayerWhitelisted(player.getUniqueId())) {
                                    isWhitelistedPlayer = true;
                                }
                            }

                            if (visit.getStatus() == IslandStatus.OPEN || isCoopPlayer || isWhitelistedPlayer || player.hasPermission("fabledskyblock.bypass")
                                    || player.hasPermission("fabledskyblock.bypass.*")
                                    || player.hasPermission("fabledskyblock.*")) {
                                if (!islandManager.containsIsland(targetPlayerUUID)) {
                                    islandManager.loadIsland(Bukkit.getServer().getOfflinePlayer(targetPlayerUUID));
                                }

                                Island island = islandManager.getIsland(offlinePlayer);

                                if ((!island.hasRole(IslandRole.MEMBER, player.getUniqueId())
                                        && !island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                        && !island.hasRole(IslandRole.OWNER, player.getUniqueId()))
                                        && plugin.getConfiguration().getBoolean("Island.Visitor.Vote")) {
                                    if (event.getClick() == ClickType.RIGHT) {
                                        if (playerData.getIsland() != null
                                                && playerData.getIsland().equals(island.getOwnerUUID())) {
                                            if (visit.getVoters().contains(player.getUniqueId())) {
                                                visit.removeVoter(player.getUniqueId());

                                                messageManager.sendMessage(player,
                                                        configLoad.getString("Island.Visit.Vote.Removed.Message")
                                                                .replace("%player", targetPlayerName));
                                                soundManager.playSound(player, XSound.ENTITY_GENERIC_EXPLODE);
                                            } else {
                                                PlayerVoteEvent playerVoteEvent = new PlayerVoteEvent(player, island.getAPIWrapper());
                                                Bukkit.getServer().getPluginManager().callEvent(playerVoteEvent);
                                                if (playerVoteEvent.isCancelled())
                                                    return;

                                                visit.addVoter(player.getUniqueId());

                                                messageManager.sendMessage(player,
                                                        configLoad.getString("Island.Visit.Vote.Added.Message")
                                                                .replace("%player", targetPlayerName));
                                                soundManager.playSound(player, XSound.ENTITY_PLAYER_LEVELUP);
                                            }

                                            soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                                            Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                    () -> open(player, (Type) playerData.getType(),
                                                            (Sort) playerData.getSort()), 1L);
                                        } else {
                                            messageManager.sendMessage(player,
                                                    configLoad.getString("Island.Visit.Vote.Island.Message"));
                                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                            event.setWillClose(false);
                                            event.setWillDestroy(false);
                                        }

                                        islandManager.unloadIsland(island, null);

                                        return;
                                    } else if (event.getClick() != ClickType.LEFT) {
                                        return;
                                    }
                                }

                                if (islandManager.isPlayerAtIsland(island, player)) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Visit.Already.Message").replace("%player",
                                                    targetPlayerName));
                                    soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);

                                    return;
                                }

                                islandManager.visitIsland(player, island);

                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Visit.Teleported.Message")
                                                .replace("%player", targetPlayerName));
                                soundManager.playSound(player, XSound.ENTITY_ENDERMAN_TELEPORT);
                            } else {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Visit.Closed.Menu.Message")
                                                .replace("%player", targetPlayerName));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, (Type) playerData.getType(), (Sort) playerData.getSort()), 1L);
                            }

                            return;
                        }

                        messageManager.sendMessage(player, configLoad.getString("Island.Visit.Exist.Message")
                                .replace("%player", targetPlayerName));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, (Type) playerData.getType(), (Sort) playerData.getSort()), 1L);
                    }
                }
            }
        });

        Map<UUID, com.songoda.skyblock.visit.Visit> openIslands = visitManager.getOpenIslands();
        List<com.songoda.skyblock.visit.Visit> visitIslands = new ArrayList<>();

        boolean keepBannedIslands = plugin.getConfiguration().getBoolean("Island.Visit.Menu.Bans");

        for (int i = 0; i < openIslands.size(); ++i) {
            UUID islandOwnerUUID = (UUID) openIslands.keySet().toArray()[i];
            com.songoda.skyblock.visit.Visit visit = openIslands.get(islandOwnerUUID);

            if (type == Visit.Type.SOLO) {
                if (visit.getMembers() != 1) {
                    continue;
                }
            } else if (type == Visit.Type.TEAM) {
                if (visit.getMembers() == 1) {
                    continue;
                }
            }

            if (!keepBannedIslands && visit.getBan().isBanned(player.getUniqueId())) {
                continue;
            }

            visitIslands.add(visit);
        }

        openIslands.clear();

        if (sort == Visit.Sort.PLAYERS || sort == Visit.Sort.LEVEL || sort == Visit.Sort.MEMBERS
                || sort == Visit.Sort.VISITS || sort == Visit.Sort.VOTES) {
            visitIslands.sort((visit1, visit2) -> {
                if (sort == Sort.PLAYERS) {
                    int playersAtIsland1 = 0;

                    if (islandManager.containsIsland(visit1.getOwnerUUID())) {
                        playersAtIsland1 = islandManager
                                .getPlayersAtIsland(islandManager
                                        .getIsland(Bukkit.getServer().getOfflinePlayer(visit1.getOwnerUUID())))
                                .size();
                    }

                    int playersAtIsland2 = 0;

                    if (islandManager.containsIsland(visit2.getOwnerUUID())) {
                        playersAtIsland2 = islandManager
                                .getPlayersAtIsland(islandManager
                                        .getIsland(Bukkit.getServer().getOfflinePlayer(visit2.getOwnerUUID())))
                                .size();
                    }

                    return Integer.compare(playersAtIsland2, playersAtIsland1);
                } else if (sort == Sort.LEVEL) {
                    return Long.compare(visit2.getLevel().getLevel(), visit1.getLevel().getLevel());
                } else if (sort == Sort.MEMBERS) {
                    return Integer.compare(visit2.getMembers(), visit1.getMembers());
                } else if (sort == Sort.VISITS) {
                    return Integer.compare(visit2.getVisitors().size(), visit1.getVisitors().size());
                } else if (sort == Sort.VOTES) {
                    return Integer.compare(visit2.getVoters().size(), visit1.getVoters().size());
                }

                return 0;
            });
        }

        int playerMenuPage = playerDataManager.getPlayerData(player).getPage(MenuType.VISIT),
                nextEndIndex = visitIslands.size() - playerMenuPage * 36,
                totalIslands = visitManager.getIslands().size();

        nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                configLoad.getString("Menu.Visit.Item.Exit.Displayname"), null, null, null, null), 0, 8);
        nInv.addItem(nInv.createItem(new ItemStack(Material.HOPPER),
                configLoad.getString("Menu.Visit.Item.Type.Displayname"),
                configLoad.getStringList("Menu.Visit.Item.Type.Lore"),
                new Placeholder[]{new Placeholder("%type", StringUtil.capitalizeUppercaseLetters(type.name()))},
                null, null), 3);
        nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                configLoad.getString("Menu.Visit.Item.Statistics.Displayname"),
                configLoad.getStringList("Menu.Visit.Item.Statistics.Lore"),
                new Placeholder[]{
                        new Placeholder("%islands_open", NumberUtils.formatNumber(visitIslands.size())),
                        new Placeholder("%islands_closed",
                                NumberUtils.formatNumber(totalIslands - visitIslands.size())),
                        new Placeholder("%islands", NumberUtils.formatNumber(totalIslands))},
                null, null), 4);
        nInv.addItem(nInv.createItem(new ItemStack(Material.HOPPER),
                configLoad.getString("Menu.Visit.Item.Sort.Displayname"),
                configLoad.getStringList("Menu.Visit.Item.Sort.Lore"),
                new Placeholder[]{new Placeholder("%sort", StringUtil.capitalizeUppercaseLetters(sort.name()))},
                null, null), 5);
        nInv.addItem(
                nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                        configLoad.getString("Menu.Visit.Item.Barrier.Displayname"), null, null, null, null),
                9, 10, 11, 12, 13, 14, 15, 16, 17);

        if (playerMenuPage != 1) {
            nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(
                            "ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=",
                            "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="),
                    configLoad.getString("Menu.Visit.Item.Previous.Displayname"), null, null, null, null), 1);
        }

        if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
            nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(
                            "wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=",
                            "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="),
                    configLoad.getString("Menu.Visit.Item.Next.Displayname"), null, null, null, null), 7);
        }

        if (visitIslands.size() == 0) {
            nInv.addItem(nInv.createItem(new ItemStack(Material.BARRIER),
                    configLoad.getString("Menu.Visit.Item.Nothing.Displayname"), null, null, null, null), 31);
        } else {
            int index = playerMenuPage * 36 - 36,
                    endIndex = index >= visitIslands.size() ? visitIslands.size() - 1 : index + 36, inventorySlot = 17,
                    playerCapacity = plugin.getConfiguration().getInt("Island.Visitor.Capacity");

            boolean voteEnabled = plugin.getConfiguration().getBoolean("Island.Visitor.Vote");
            boolean signatureEnabled = plugin.getConfiguration().getBoolean("Island.Visitor.Signature.Enable");

            for (; index < endIndex; index++) {
                if (visitIslands.size() > index) {
                    inventorySlot++;

                    com.songoda.skyblock.visit.Visit visit = visitIslands.get(index);
                    Player targetPlayer = Bukkit.getServer().getPlayer(visit.getOwnerUUID());

                    String targetPlayerName;
                    String[] targetPlayerTexture;

                    if (targetPlayer == null) {
                        OfflinePlayer offlinePlayer = new OfflinePlayer(visit.getOwnerUUID());
                        targetPlayerName = offlinePlayer.getName();
                        targetPlayerTexture = offlinePlayer.getTexture();
                    } else {
                        targetPlayerName = targetPlayer.getName();

                        if (playerDataManager.hasPlayerData(targetPlayer)) {
                            targetPlayerTexture = playerDataManager.getPlayerData(targetPlayer).getTexture();
                        } else {
                            targetPlayerTexture = new String[]{null, null};
                        }
                    }

                    Island island = null;

                    if (islandManager.containsIsland(visit.getOwnerUUID())) {
                        island = islandManager.getIsland(Bukkit.getServer().getOfflinePlayer(visit.getOwnerUUID()));
                    }

                    List<String> itemLore = new ArrayList<>();

                    String safety;

                    if (visit.getSafeLevel() > 0) {
                        safety = configLoad.getString("Menu.Visit.Item.Island.Vote.Word.Unsafe");
                    } else {
                        safety = configLoad.getString("Menu.Visit.Item.Island.Vote.Word.Safe");
                    }

                    if (voteEnabled) {
                        String voteAction;

                        if (visit.getVoters().contains(player.getUniqueId())) {
                            voteAction = configLoad
                                    .getString("Menu.Visit.Item.Island.Vote.Enabled.Signature.Word.Remove");
                        } else {
                            voteAction = configLoad.getString("Menu.Visit.Item.Island.Vote.Enabled.Signature.Word.Add");
                        }

                        if (signatureEnabled) {
                            List<String> correctItemLore;

                            if (island != null && (island.hasRole(IslandRole.MEMBER, player.getUniqueId())
                                    || island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                    || island.hasRole(IslandRole.OWNER, player.getUniqueId()))) {
                                correctItemLore = configLoad.getStringList(
                                        "Menu.Visit.Item.Island.Vote.Enabled.Signature.Enabled.Member.Lore");
                            } else {
                                correctItemLore = configLoad.getStringList(
                                        "Menu.Visit.Item.Island.Vote.Enabled.Signature.Enabled.Visitor.Lore");
                            }

                            for (String itemLoreList : correctItemLore) {
                                if (itemLoreList.contains("%signature")) {
                                    List<String> islandSignature = visit.getSiganture();

                                    if (islandSignature.size() == 0) {
                                        itemLore.add(configLoad.getString("Menu.Visit.Item.Island.Vote.Word.Empty"));
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
                            if (island != null && (island.hasRole(IslandRole.MEMBER, player.getUniqueId())
                                    || island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                    || island.hasRole(IslandRole.OWNER, player.getUniqueId()))) {
                                itemLore.addAll(configLoad.getStringList(
                                        "Menu.Visit.Item.Island.Vote.Enabled.Signature.Disabled.Member.Lore"));
                            } else {
                                itemLore.addAll(configLoad.getStringList(
                                        "Menu.Visit.Item.Island.Vote.Enabled.Signature.Disabled.Visitor.Lore"));
                            }
                        }

                        nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(targetPlayerTexture[0], targetPlayerTexture[1]),
                                configLoad.getString("Menu.Visit.Item.Island.Displayname").replace("%player",
                                        targetPlayerName),
                                itemLore,
                                new Placeholder[]{new Placeholder("%level", "" + visit.getLevel().getLevel()),
                                        new Placeholder("%members", "" + visit.getMembers()),
                                        new Placeholder("%votes", "" + visit.getVoters().size()),
                                        new Placeholder("%visits", "" + visit.getVisitors().size()),
                                        new Placeholder("%players",
                                                "" + islandManager.getPlayersAtIsland(island).size()),
                                        new Placeholder("%player_capacity", "" + playerCapacity),
                                        new Placeholder("%action", voteAction), new Placeholder("%safety", safety)},
                                null, null), inventorySlot);
                    } else {
                        if (signatureEnabled) {
                            for (String itemLoreList : configLoad
                                    .getStringList("Menu.Visit.Item.Island.Vote.Disabled.Signature.Enabled.Lore")) {
                                if (itemLoreList.contains("%signature")) {
                                    List<String> islandSignature = visit.getSiganture();

                                    if (islandSignature.size() == 0) {
                                        itemLore.add(configLoad.getString("Menu.Visit.Item.Island.Vote.Word.Empty"));
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
                            itemLore.addAll(configLoad
                                    .getStringList("Menu.Visit.Item.Island.Vote.Disabled.Signature.Disabled.Lore"));
                        }

                        nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(targetPlayerTexture[0], targetPlayerTexture[1]),
                                configLoad.getString("Menu.Visit.Item.Island.Displayname").replace("%player",
                                        targetPlayerName),
                                itemLore,
                                new Placeholder[]{new Placeholder("%level", "" + visit.getLevel().getLevel()),
                                        new Placeholder("%members", "" + visit.getMembers()),
                                        new Placeholder("%visits", "" + visit.getVisitors().size()),
                                        new Placeholder("%players",
                                                "" + islandManager.getPlayersAtIsland(island).size()),
                                        new Placeholder("%player_capacity", "" + playerCapacity),
                                        new Placeholder("%safety", safety)},
                                null, null), inventorySlot);
                    }
                }
            }
        }

        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Visit.Title")));
        nInv.setRows(6);

        Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
    }

    public enum Type {
        DEFAULT, SOLO, TEAM
    }

    public enum Sort {
        DEFAULT, PLAYERS, LEVEL, MEMBERS, VISITS, VOTES
    }
}
