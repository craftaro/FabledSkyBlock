package com.songoda.skyblock.menus;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.StringUtil;
import com.songoda.skyblock.utils.item.SkullUtil;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import com.songoda.skyblock.utils.player.OfflinePlayer;
import com.songoda.skyblock.utils.version.Materials;
import com.songoda.skyblock.utils.version.Sounds;
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
import java.util.*;

public class Members {

    private static Members instance;

    public static Members getInstance() {
        if (instance == null) {
            instance = new Members();
        }

        return instance;
    }

    public void open(Player player, Members.Type type, Members.Sort sort) {
        SkyBlock skyblock = SkyBlock.getInstance();

        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        if (playerDataManager.hasPlayerData(player)) {
            FileConfiguration configLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                    .getFileConfiguration();

            nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                if (playerDataManager.hasPlayerData(player)) {
                    PlayerData playerData = playerDataManager.getPlayerData(player);

                    if (playerData.getType() == null || playerData.getSort() == null) {
                        playerData.setType(Type.Default);
                        playerData.setSort(Sort.Default);
                    }

                    ItemStack is = event.getItem();
                    Island island = islandManager.getIsland(player);

                    if (island == null) {
                        skyblock.getMessageManager().sendMessage(player,
                                configLoad.getString("Command.Island.Members.Owner.Message"));
                        soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

                        return;
                    }

                    if ((is.getType() == Materials.BLACK_STAINED_GLASS_PANE.parseMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Members.Item.Barrier.Displayname"))))) {
                        soundManager.playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == Materials.OAK_FENCE_GATE.parseMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Members.Item.Exit.Displayname"))))) {
                        soundManager.playSound(player, Sounds.CHEST_CLOSE.bukkitSound(), 1.0F, 1.0F);
                    } else if ((is.getType() == Material.HOPPER) && (is.hasItemMeta())) {
                        if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Menu.Members.Item.Type.Displayname")))) {
                            Type type1 = (Type) playerData.getType();

                            if (type1.ordinal() + 1 == Type.values().length) {
                                playerData.setType(Type.Default);
                            } else {
                                playerData.setType(Type.values()[type1.ordinal() + 1]);
                            }
                        } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes(
                                '&', configLoad.getString("Menu.Members.Item.Sort.Displayname")))) {
                            Sort sort1 = (Sort) playerData.getSort();

                            if (sort1.ordinal() + 1 == Sort.values().length) {
                                playerData.setSort(Sort.Default);
                            } else {
                                playerData.setSort(Sort.values()[sort1.ordinal() + 1]);
                            }
                        }

                        soundManager.playSound(player, Sounds.WOOD_CLICK.bukkitSound(), 1.0F, 1.0F);

                        Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, (Type) playerData.getType(),
                                (Sort) playerData.getSort()), 1L);
                    } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Members.Item.Statistics.Displayname"))))) {
                        soundManager.playSound(player, Sounds.VILLAGER_YES.bukkitSound(), 1.0F, 1.0F);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == Material.BARRIER) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Members.Item.Nothing.Displayname"))))) {
                        soundManager.playSound(player, Sounds.ANVIL_LAND.bukkitSound(), 1.0F, 1.0F);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == SkullUtil.createItemStack().getType()) && (is.hasItemMeta())) {
                        if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Menu.Members.Item.Previous.Displayname")))) {
                            playerData.setPage(playerData.getPage() - 1);
                            soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, (Type) playerData.getType(),
                                    (Sort) playerData.getSort()), 1L);
                        } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes(
                                '&', configLoad.getString("Menu.Members.Item.Next.Displayname")))) {
                            playerData.setPage(playerData.getPage() + 1);
                            soundManager.playSound(player, Sounds.ARROW_HIT.bukkitSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player, (Type) playerData.getType(),
                                    (Sort) playerData.getSort()), 1L);
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
                                    || island.hasRole(IslandRole.Owner, playerUUID))) {
                                if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                                    if (event.getClick() == ClickType.LEFT) {
                                        if (island.hasRole(IslandRole.Member, playerUUID)) {
                                            Bukkit.getServer().dispatchCommand(player,
                                                    "island promote " + playerName);
                                        } else {
                                            Bukkit.getServer().dispatchCommand(player,
                                                    "island demote " + playerName);
                                        }

                                        Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                                () -> open(player, (Type) playerData.getType(),
                                                        (Sort) playerData.getSort()), 3L);

                                        return;
                                    } else if (event.getClick() == ClickType.RIGHT) {
                                        Bukkit.getServer().dispatchCommand(player, "island kick " + playerName);

                                        Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                                () -> open(player, (Type) playerData.getType(),
                                                        (Sort) playerData.getSort()), 3L);

                                        return;
                                    }
                                } else if (island.hasRole(IslandRole.Operator, player.getUniqueId())
                                        && island.getSetting(IslandRole.Operator, "Kick").getStatus()) {
                                    Bukkit.getServer().dispatchCommand(player, "island kick " + playerName);

                                    Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                            () -> open(player, (Type) playerData.getType(),
                                                    (Sort) playerData.getSort()), 3L);

                                    return;
                                }
                            }

                            soundManager.playSound(player, Sounds.CHICKEN_EGG_POP.bukkitSound(), 1.0F, 1.0F);

                            event.setWillClose(false);
                            event.setWillDestroy(false);
                        }
                    }
                }
            });

            PlayerData playerData = playerDataManager.getPlayerData(player);
            Island island = islandManager.getIsland(player);

            List<UUID> displayedMembers = new ArrayList<>();
            Set<UUID> islandMembers = island.getRole(IslandRole.Member);
            Set<UUID> islandOperators = island.getRole(IslandRole.Operator);

            if (type == Members.Type.Default) {
                displayedMembers.add(island.getOwnerUUID());
                displayedMembers.addAll(islandOperators);
                displayedMembers.addAll(islandMembers);
            } else if (type == Members.Type.Members) {
                displayedMembers.addAll(islandMembers);
            } else if (type == Members.Type.Operators) {
                displayedMembers.addAll(islandOperators);
            } else if (type == Members.Type.Owner) {
                displayedMembers.add(island.getOwnerUUID());
            }

            if (sort == Members.Sort.Playtime) {
                Map<Integer, UUID> sortedPlaytimes = new TreeMap<>();

                for (UUID displayedMemberList : displayedMembers) {
                    Player targetPlayer = Bukkit.getServer().getPlayer(displayedMemberList);

                    if (targetPlayer == null) {
                        sortedPlaytimes.put(YamlConfiguration
                                .loadConfiguration(
                                        new File(new File(skyblock.getDataFolder().toString() + "/player-data"),
                                                displayedMemberList.toString() + ".yml"))
                                .getInt("Statistics.Island.Playtime"), displayedMemberList);
                    } else {
                        sortedPlaytimes.put(skyblock.getPlayerDataManager().getPlayerData(targetPlayer).getPlaytime(),
                                displayedMemberList);
                    }
                }

                displayedMembers.clear();

                for (Integer sortedPlaytimeList : sortedPlaytimes.keySet()) {
                    displayedMembers.add(sortedPlaytimes.get(sortedPlaytimeList));
                }
            } else if (sort == Members.Sort.MemberSince) {
                Map<Date, UUID> sortedDates = new TreeMap<>();

                for (UUID displayedMemberList : displayedMembers) {
                    Player targetPlayer = Bukkit.getServer().getPlayer(displayedMemberList);

                    try {
                        if (targetPlayer == null) {
                            sortedDates
                                    .put(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                                    .parse(YamlConfiguration
                                                            .loadConfiguration(new File(
                                                                    new File(skyblock.getDataFolder().toString()
                                                                            + "/player-data"),
                                                                    displayedMemberList.toString() + ".yml"))
                                                            .getString("Statistics.Island.Join")),
                                            displayedMemberList);
                        } else {
                            sortedDates.put(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(
                                    skyblock.getPlayerDataManager().getPlayerData(targetPlayer).getMemberSince()),
                                    displayedMemberList);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                displayedMembers.clear();

                for (Date sortedDateList : sortedDates.keySet()) {
                    displayedMembers.add(sortedDates.get(sortedDateList));
                }
            } else if (sort == Members.Sort.LastOnline) {
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
                                                                    new File(skyblock.getDataFolder().toString()
                                                                            + "/player-data"),
                                                                    displayedMemberList.toString() + ".yml"))
                                                            .getString("Statistics.Island.LastOnline")),
                                            displayedMemberList);
                        } catch (ParseException e) {
                            e.printStackTrace();
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

            if (island.hasRole(IslandRole.Owner, player.getUniqueId())) {
                operatorActions = new boolean[]{true, true};
            } else if (island.hasRole(IslandRole.Operator, player.getUniqueId())) {
                if (island.getSetting(IslandRole.Operator, "Kick").getStatus()) {
                    operatorActions = new boolean[]{false, true};
                }
            }

            int playerMenuPage = playerData.getPage(), nextEndIndex = displayedMembers.size() - playerMenuPage * 36;

            nInv.addItem(nInv.createItem(Materials.OAK_FENCE_GATE.parseItem(),
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
                                    "" + fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                                            .getFileConfiguration().getInt("Island.Member.Capacity")),
                            new Placeholder("%members", "" + islandMembers.size()),
                            new Placeholder("%operators", "" + islandOperators.size())},
                    null, null), 4);
            nInv.addItem(nInv.createItem(new ItemStack(Material.HOPPER),
                    configLoad.getString("Menu.Members.Item.Sort.Displayname"),
                    configLoad.getStringList("Menu.Members.Item.Sort.Lore"),
                    new Placeholder[]{new Placeholder("%sort", StringUtil.capatilizeUppercaseLetters(sort.name()))},
                    null, null), 5);
            nInv.addItem(
                    nInv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(),
                            configLoad.getString("Menu.Members.Item.Barrier.Displayname"), null, null, null, null),
                    9, 10, 11, 12, 13, 14, 15, 16, 17);

            if (playerMenuPage != 1) {
                nInv.addItem(nInv.createItem(SkullUtil.create(
                        "ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=",
                        "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="),
                        configLoad.getString("Menu.Members.Item.Previous.Displayname"), null, null, null, null), 1);
            }

            if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
                nInv.addItem(nInv.createItem(SkullUtil.create(
                        "wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=",
                        "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="),
                        configLoad.getString("Menu.Members.Item.Next.Displayname"), null, null, null, null), 7);
            }

            if (displayedMembers.size() == 0) {
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
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            playerName = targetPlayer.getName();

                            playerData = skyblock.getPlayerDataManager().getPlayerData(targetPlayer);
                            playerTexture = playerData.getTexture();
                            islandPlaytime = playerData.getPlaytime();
                            playTimeDurationTime = NumberUtil.getDuration(Integer.valueOf(islandPlaytime));

                            try {
                                memberSinceDurationTime = NumberUtil.getDuration(
                                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(playerData.getMemberSince()),
                                        new Date());
                            } catch (ParseException e) {
                                e.printStackTrace();
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
                                || island.hasRole(IslandRole.Owner, playerUUID))) {
                            if (operatorActions[0] && operatorActions[1]) {
                                if (!island.hasRole(IslandRole.Owner, playerUUID)) {
                                    itemLore.add("");

                                    if (island.hasRole(IslandRole.Member, playerUUID)) {
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
                                        && island.getRole(IslandRole.Operator).contains(playerUUID)
                                        && island.hasRole(IslandRole.Owner, playerUUID))) {
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
                                nInv.createItem(SkullUtil.create(playerTexture[0], playerTexture[1]),
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

            Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
        }
    }

    public enum Type {

        Default, Members, Operators, Owner

    }

    public enum Sort {

        Default, Playtime, MemberSince, LastOnline

    }
}
