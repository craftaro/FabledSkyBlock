package com.craftaro.skyblock.menus;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.config.FileManager;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.island.IslandRole;
import com.craftaro.skyblock.permission.PermissionManager;
import com.craftaro.skyblock.placeholder.Placeholder;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.NumberUtil;
import com.craftaro.skyblock.utils.StringUtil;
import com.craftaro.skyblock.utils.item.nInventoryUtil;
import com.craftaro.skyblock.utils.player.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public class Members {
    private static Members instance;

    public static Members getInstance() {
        if (instance == null) {
            instance = new Members();
        }

        return instance;
    }

    public void open(Player player, Members.Type type, Members.Sort sort) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        IslandManager islandManager = plugin.getIslandManager();
        PermissionManager permissionManager = plugin.getPermissionManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        if (playerDataManager.hasPlayerData(player)) {
            FileConfiguration configLoad = plugin.getLanguage();

            nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                if (playerDataManager.hasPlayerData(player)) {
                    PlayerData playerData = playerDataManager.getPlayerData(player);

                    if (playerData.getType() == null || playerData.getSort() == null) {
                        playerData.setType(Type.DEFAULT);
                        playerData.setSort(Sort.DEFAULT);
                    }

                    ItemStack is = event.getItem();
                    Island island = islandManager.getIsland(player);

                    if (island == null) {
                        plugin.getMessageManager().sendMessage(player,
                                configLoad.getString("Command.Island.Members.Owner.Message"));
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        return;
                    }

                    if ((is.getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Members.Item.Barrier.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == XMaterial.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Members.Item.Exit.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
                    } else if ((is.getType() == Material.HOPPER) && (is.hasItemMeta())) {
                        if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Menu.Members.Item.Type.Displayname")))) {
                            Type type1 = (Type) playerData.getType();

                            if (type1.ordinal() + 1 == Type.values().length) {
                                playerData.setType(Type.DEFAULT);
                            } else {
                                playerData.setType(Type.values()[type1.ordinal() + 1]);
                            }
                        } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes(
                                '&', configLoad.getString("Menu.Members.Item.Sort.Displayname")))) {
                            Sort sort1 = (Sort) playerData.getSort();

                            if (sort1.ordinal() + 1 == Sort.values().length) {
                                playerData.setSort(Sort.DEFAULT);
                            } else {
                                playerData.setSort(Sort.values()[sort1.ordinal() + 1]);
                            }
                        }

                        soundManager.playSound(player, XSound.BLOCK_WOODEN_BUTTON_CLICK_ON);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, (Type) playerData.getType(),
                                (Sort) playerData.getSort()), 1L);
                    } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Members.Item.Statistics.Displayname"))))) {
                        soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == Material.BARRIER) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Members.Item.Nothing.Displayname"))))) {
                        soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) && (is.hasItemMeta())) {
                        if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Menu.Members.Item.Previous.Displayname")))) {
                            playerData.setPage(MenuType.MEMBERS, playerData.getPage(MenuType.MEMBERS) - 1);
                            soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, (Type) playerData.getType(),
                                    (Sort) playerData.getSort()), 1L);
                        } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes(
                                '&', configLoad.getString("Menu.Members.Item.Next.Displayname")))) {
                            playerData.setPage(MenuType.MEMBERS, playerData.getPage(MenuType.MEMBERS) + 1);
                            soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player, (Type) playerData.getType(), (Sort) playerData.getSort()), 1L);
                        } else {
                            String playerName = ChatColor.stripColor(is.getItemMeta().getDisplayName());
                            UUID playerUUID;

                            Player targetPlayer = Bukkit.getServer().getPlayer(playerName);

                            if (targetPlayer == null) {
                                playerUUID = new OfflinePlayer(playerName).getUUID();
                            } else {
                                playerUUID = targetPlayer.getUniqueId();
                            }

                            if (!(playerUUID.equals(player.getUniqueId())
                                    || island.hasRole(IslandRole.OWNER, playerUUID))) {
                                if (island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                                    if (event.getClick() == ClickType.LEFT) {
                                        if (island.hasRole(IslandRole.MEMBER, playerUUID)) {
                                            Bukkit.getServer().dispatchCommand(player,
                                                    "island promote " + playerName);
                                        } else {
                                            Bukkit.getServer().dispatchCommand(player,
                                                    "island demote " + playerName);
                                        }

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player, (Type) playerData.getType(),
                                                        (Sort) playerData.getSort()), 3L);

                                        return;
                                    } else if (event.getClick() == ClickType.RIGHT) {
                                        Bukkit.getServer().dispatchCommand(player, "island kick " + playerName);

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player, (Type) playerData.getType(),
                                                        (Sort) playerData.getSort()), 3L);

                                        return;
                                    }
                                } else if (island.hasRole(IslandRole.OPERATOR, player.getUniqueId())
                                        && permissionManager.hasPermission(island, "Kick", IslandRole.OPERATOR)) {
                                    Bukkit.getServer().dispatchCommand(player, "island kick " + playerName);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player, (Type) playerData.getType(),
                                                    (Sort) playerData.getSort()), 3L);

                                    return;
                                }
                            }

                            soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                            event.setWillClose(false);
                            event.setWillDestroy(false);
                        }
                    }
                }
            });

            PlayerData playerData = playerDataManager.getPlayerData(player);
            Island island = islandManager.getIsland(player);

            List<UUID> displayedMembers = new ArrayList<>();
            Set<UUID> islandMembers = island.getRole(IslandRole.MEMBER);
            Set<UUID> islandOperators = island.getRole(IslandRole.OPERATOR);

            if (type == Members.Type.DEFAULT) {
                displayedMembers.add(island.getOwnerUUID());
                displayedMembers.addAll(islandOperators);
                displayedMembers.addAll(islandMembers);
            } else if (type == Members.Type.MEMBERS) {
                displayedMembers.addAll(islandMembers);
            } else if (type == Members.Type.OPERATORS) {
                displayedMembers.addAll(islandOperators);
            } else if (type == Members.Type.OWNER) {
                displayedMembers.add(island.getOwnerUUID());
            }

            if (sort == Members.Sort.PLAYTIME) {
                Map<Integer, UUID> sortedPlaytimes = new TreeMap<>();

                for (UUID displayedMemberList : displayedMembers) {
                    Player targetPlayer = Bukkit.getServer().getPlayer(displayedMemberList);

                    if (targetPlayer == null) {
                        sortedPlaytimes.put(YamlConfiguration
                                .loadConfiguration(
                                        new File(new File(plugin.getDataFolder().toString() + "/player-data"),
                                                displayedMemberList.toString() + ".yml"))
                                .getInt("Statistics.Island.Playtime"), displayedMemberList);
                    } else {
                        sortedPlaytimes.put(plugin.getPlayerDataManager().getPlayerData(targetPlayer).getPlaytime(),
                                displayedMemberList);
                    }
                }

                displayedMembers.clear();

                for (Integer sortedPlaytimeList : sortedPlaytimes.keySet()) {
                    displayedMembers.add(sortedPlaytimes.get(sortedPlaytimeList));
                }
            } else if (sort == Members.Sort.MEMBER_SINCE) {
                Map<Date, UUID> sortedDates = new TreeMap<>();

                for (UUID displayedMemberList : displayedMembers) {
                    Player targetPlayer = Bukkit.getServer().getPlayer(displayedMemberList);

                    try {
                        if (targetPlayer == null) {
                            sortedDates
                                    .put(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                                    .parse(YamlConfiguration
                                                            .loadConfiguration(new File(
                                                                    new File(plugin.getDataFolder().toString()
                                                                            + "/player-data"),
                                                                    displayedMemberList.toString() + ".yml"))
                                                            .getString("Statistics.Island.Join")),
                                            displayedMemberList);
                        } else {
                            sortedDates.put(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(
                                            plugin.getPlayerDataManager().getPlayerData(targetPlayer).getMemberSince()),
                                    displayedMemberList);
                        }
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }

                displayedMembers.clear();

                for (Date sortedDateList : sortedDates.keySet()) {
                    displayedMembers.add(sortedDates.get(sortedDateList));
                }
            } else if (sort == Members.Sort.LAST_ONLINE) {
                List<UUID> onlineMembers = new ArrayList<>(displayedMembers);
                Map<Date, UUID> sortedDates = new TreeMap<>();

                for (UUID displayedMemberList : displayedMembers) {
                    Player targetPlayer = Bukkit.getServer().getPlayer(displayedMemberList);

                    if (targetPlayer == null) {
                        onlineMembers.remove(displayedMemberList);

                        try {
                            sortedDates
                                    .put(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                                    .parse(YamlConfiguration
                                                            .loadConfiguration(new File(
                                                                    new File(plugin.getDataFolder().toString()
                                                                            + "/player-data"),
                                                                    displayedMemberList.toString() + ".yml"))
                                                            .getString("Statistics.Island.LastOnline")),
                                            displayedMemberList);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                displayedMembers.clear();
                displayedMembers.addAll(onlineMembers);

                for (Date sortedDateList : sortedDates.keySet()) {
                    displayedMembers.add(sortedDates.get(sortedDateList));
                }
            }

            boolean[] operatorActions = new boolean[]{false, false};

            if (island.hasRole(IslandRole.OWNER, player.getUniqueId())) {
                operatorActions = new boolean[]{true, true};
            } else if (island.hasRole(IslandRole.OPERATOR, player.getUniqueId())) {
                if (permissionManager.hasPermission(island, "Kick", IslandRole.OPERATOR)) {
                    operatorActions = new boolean[]{false, true};
                }
            }

            int playerMenuPage = playerData.getPage(MenuType.MEMBERS), nextEndIndex = displayedMembers.size() - playerMenuPage * 36;

            nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(),
                    configLoad.getString("Menu.Members.Item.Exit.Displayname"), null, null, null, null), 0, 8);
            nInv.addItem(nInv.createItem(new ItemStack(Material.HOPPER),
                    configLoad.getString("Menu.Members.Item.Type.Displayname"),
                    configLoad.getStringList("Menu.Members.Item.Type.Lore"),
                    new Placeholder[]{new Placeholder("%type", type.name())}, null, null), 3);
            nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                    configLoad.getString("Menu.Members.Item.Statistics.Displayname"),
                    configLoad.getStringList("Menu.Members.Item.Statistics.Lore"),
                    new Placeholder[]{
                            new Placeholder("%island_members",
                                    "" + (islandMembers.size() + islandOperators.size() + 1)),
                            new Placeholder("%island_capacity",
                                    "" + island.getMaxMembers(player)),
                            new Placeholder("%members", "" + islandMembers.size()),
                            new Placeholder("%operators", "" + islandOperators.size())},
                    null, null), 4);
            nInv.addItem(nInv.createItem(new ItemStack(Material.HOPPER),
                    configLoad.getString("Menu.Members.Item.Sort.Displayname"),
                    configLoad.getStringList("Menu.Members.Item.Sort.Lore"),
                    new Placeholder[]{new Placeholder("%sort", StringUtil.capitalizeUppercaseLetters(sort.name()))},
                    null, null), 5);
            nInv.addItem(
                    nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(),
                            configLoad.getString("Menu.Members.Item.Barrier.Displayname"), null, null, null, null),
                    9, 10, 11, 12, 13, 14, 15, 16, 17);

            if (playerMenuPage != 1) {
                nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(
                                "ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=",
                                "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="),
                        configLoad.getString("Menu.Members.Item.Previous.Displayname"), null, null, null, null), 1);
            }

            if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(
                                "wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=",
                                "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="),
                        configLoad.getString("Menu.Members.Item.Next.Displayname"), null, null, null, null), 7);
            }

            if (displayedMembers.isEmpty()) {
                nInv.addItem(
                        nInv.createItem(new ItemStack(Material.BARRIER),
                                configLoad.getString("Menu.Members.Item.Nothing.Displayname"), null, null, null, null),
                        31);
            } else {
                int index = playerMenuPage * 36 - 36,
                        endIndex = index >= displayedMembers.size() ? displayedMembers.size() - 1 : index + 36,
                        inventorySlot = 17;

                for (; index < endIndex; index++) {
                    if (displayedMembers.size() > index) {
                        inventorySlot++;

                        UUID playerUUID = displayedMembers.get(index);

                        String[] playerTexture;
                        String playerName, islandRole, islandPlaytimeFormatted, memberSinceFormatted,
                                lastOnlineFormatted = "";

                        long[] playTimeDurationTime, memberSinceDurationTime = null, lastOnlineDurationTime = null;

                        int islandPlaytime;

                        Player targetPlayer = Bukkit.getServer().getPlayer(playerUUID);

                        if (targetPlayer == null) {
                            OfflinePlayer offlinePlayer = new OfflinePlayer(playerUUID);
                            playerName = offlinePlayer.getName();
                            playerTexture = offlinePlayer.getTexture();
                            islandPlaytime = offlinePlayer.getPlaytime();
                            playTimeDurationTime = NumberUtil.getDuration(Integer.valueOf(islandPlaytime));

                            try {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                memberSinceDurationTime = NumberUtil.getDuration(
                                        simpleDateFormat.parse(offlinePlayer.getMemberSince()), new Date());
                                lastOnlineDurationTime = NumberUtil
                                        .getDuration(simpleDateFormat.parse(offlinePlayer.getLastOnline()), new Date());
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            playerName = targetPlayer.getName();

                            playerData = plugin.getPlayerDataManager().getPlayerData(targetPlayer);
                            playerTexture = playerData.getTexture();
                            islandPlaytime = playerData.getPlaytime();
                            playTimeDurationTime = NumberUtil.getDuration(islandPlaytime);

                            try {
                                memberSinceDurationTime = NumberUtil.getDuration(
                                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(playerData.getMemberSince()),
                                        new Date());
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                        }

                        if (islandMembers.contains(playerUUID)) {
                            islandRole = configLoad.getString("Menu.Members.Item.Member.Role.Word.Member");
                        } else if (islandOperators.contains(playerUUID)) {
                            islandRole = configLoad.getString("Menu.Members.Item.Member.Role.Word.Operator");
                        } else {
                            islandRole = configLoad.getString("Menu.Members.Item.Member.Role.Word.Owner");
                        }

                        if (islandPlaytime >= 86400) {
                            islandPlaytimeFormatted = playTimeDurationTime[0] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Days") + ", "
                                    + playTimeDurationTime[1] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Hours") + ", "
                                    + playTimeDurationTime[2] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Minutes") + ", "
                                    + playTimeDurationTime[3] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Seconds");
                        } else if (islandPlaytime >= 3600) {
                            islandPlaytimeFormatted = playTimeDurationTime[1] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Hours") + ", "
                                    + playTimeDurationTime[2] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Minutes") + ", "
                                    + playTimeDurationTime[3] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Seconds");
                        } else if (islandPlaytime >= 60) {
                            islandPlaytimeFormatted = playTimeDurationTime[2] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Minutes") + ", "
                                    + playTimeDurationTime[3] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Seconds");
                        } else {
                            islandPlaytimeFormatted = playTimeDurationTime[3] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Seconds");
                        }

                        if (memberSinceDurationTime[0] != 0L) {
                            memberSinceFormatted = memberSinceDurationTime[0] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Days") + ", "
                                    + memberSinceDurationTime[1] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Hours") + ", "
                                    + memberSinceDurationTime[2] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Minutes") + ", "
                                    + memberSinceDurationTime[3] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Seconds");
                        } else if (memberSinceDurationTime[1] != 0L) {
                            memberSinceFormatted = memberSinceDurationTime[1] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Hours") + ", "
                                    + memberSinceDurationTime[2] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Minutes") + ", "
                                    + memberSinceDurationTime[3] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Seconds");
                        } else if (memberSinceDurationTime[2] != 0L) {
                            memberSinceFormatted = memberSinceDurationTime[2] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Minutes") + ", "
                                    + memberSinceDurationTime[3] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Seconds");
                        } else {
                            memberSinceFormatted = memberSinceDurationTime[3] + " "
                                    + configLoad.getString("Menu.Members.Item.Member.Word.Seconds");
                        }

                        if (lastOnlineDurationTime != null) {
                            if (lastOnlineDurationTime[0] != 0L) {
                                lastOnlineFormatted = lastOnlineDurationTime[0] + " "
                                        + configLoad.getString("Menu.Members.Item.Member.Word.Days") + ", "
                                        + lastOnlineDurationTime[1] + " "
                                        + configLoad.getString("Menu.Members.Item.Member.Word.Hours") + ", "
                                        + lastOnlineDurationTime[2] + " "
                                        + configLoad.getString("Menu.Members.Item.Member.Word.Minutes") + ", "
                                        + lastOnlineDurationTime[3] + " "
                                        + configLoad.getString("Menu.Members.Item.Member.Word.Seconds");
                            } else if (lastOnlineDurationTime[1] != 0L) {
                                lastOnlineFormatted = lastOnlineDurationTime[1] + " "
                                        + configLoad.getString("Menu.Members.Item.Member.Word.Hours") + ", "
                                        + lastOnlineDurationTime[2] + " "
                                        + configLoad.getString("Menu.Members.Item.Member.Word.Minutes") + ", "
                                        + lastOnlineDurationTime[3] + " "
                                        + configLoad.getString("Menu.Members.Item.Member.Word.Seconds");
                            } else if (lastOnlineDurationTime[2] != 0L) {
                                lastOnlineFormatted = lastOnlineDurationTime[2] + " "
                                        + configLoad.getString("Menu.Members.Item.Member.Word.Minutes") + ", "
                                        + lastOnlineDurationTime[3] + " "
                                        + configLoad.getString("Menu.Members.Item.Member.Word.Seconds");
                            } else {
                                lastOnlineFormatted = lastOnlineDurationTime[3] + " "
                                        + configLoad.getString("Menu.Members.Item.Member.Word.Seconds");
                            }
                        }

                        List<String> itemLore = new ArrayList<>();
                        itemLore.addAll(configLoad.getStringList("Menu.Members.Item.Member.Role.Lore"));
                        itemLore.addAll(configLoad.getStringList("Menu.Members.Item.Member.Playtime.Lore"));
                        itemLore.addAll(configLoad.getStringList("Menu.Members.Item.Member.Since.Lore"));

                        if (lastOnlineDurationTime != null) {
                            itemLore.addAll(configLoad.getStringList("Menu.Members.Item.Member.LastOnline.Lore"));
                        }

                        if (!(playerUUID.equals(player.getUniqueId())
                                || island.hasRole(IslandRole.OWNER, playerUUID))) {
                            if (operatorActions[0] && operatorActions[1]) {
                                if (!island.hasRole(IslandRole.OWNER, playerUUID)) {
                                    itemLore.add("");

                                    if (island.hasRole(IslandRole.MEMBER, playerUUID)) {
                                        itemLore.add(configLoad.getString("Menu.Members.Item.Member.Action.Lore")
                                                .replace("%click",
                                                        configLoad
                                                                .getString("Menu.Members.Item.Member.Word.Left-Click"))
                                                .replace("%action", configLoad
                                                        .getString("Menu.Members.Item.Member.Action.Word.Promote")));
                                    } else {
                                        itemLore.add(configLoad.getString("Menu.Members.Item.Member.Action.Lore")
                                                .replace("%click",
                                                        configLoad
                                                                .getString("Menu.Members.Item.Member.Word.Left-Click"))
                                                .replace("%action", configLoad
                                                        .getString("Menu.Members.Item.Member.Action.Word.Demote")));
                                    }

                                    itemLore.add(configLoad.getString("Menu.Members.Item.Member.Action.Lore")
                                            .replace("%click",
                                                    configLoad.getString("Menu.Members.Item.Member.Word.Right-Click"))
                                            .replace("%action",
                                                    configLoad.getString("Menu.Members.Item.Member.Action.Word.Kick")));
                                }
                            } else if (!operatorActions[0] && operatorActions[1]) {
                                if (!(playerUUID.equals(player.getUniqueId())
                                        && island.getRole(IslandRole.OPERATOR).contains(playerUUID)
                                        && island.hasRole(IslandRole.OWNER, playerUUID))) {
                                    itemLore.add("");
                                    itemLore.add(configLoad.getString("Menu.Members.Item.Member.Action.Lore")
                                            .replace("%click",
                                                    configLoad.getString("Menu.Members.Item.Member.Word.Click"))
                                            .replace("%action",
                                                    configLoad.getString("Menu.Members.Item.Member.Action.Word.Kick")));
                                }
                            }
                        }

                        nInv.addItem(
                                nInv.createItem(ItemUtils.getCustomHead(playerTexture[0], playerTexture[1]),
                                        configLoad.getString("Menu.Members.Item.Member.Displayname").replace("%player",
                                                playerName),
                                        itemLore,
                                        new Placeholder[]{new Placeholder("%role", islandRole),
                                                new Placeholder("%playtime", islandPlaytimeFormatted),
                                                new Placeholder("%since", memberSinceFormatted),
                                                new Placeholder("%last_online", lastOnlineFormatted)},
                                        null, null),
                                inventorySlot);
                    }
                }
            }

            nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Members.Title")));
            nInv.setRows(6);

            Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
        }
    }

    public enum Type {
        DEFAULT, MEMBERS, OPERATORS, OWNER
    }

    public enum Sort {
        DEFAULT, PLAYTIME, MEMBER_SINCE, LAST_ONLINE
    }
}
