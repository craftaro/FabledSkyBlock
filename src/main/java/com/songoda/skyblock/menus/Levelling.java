package com.songoda.skyblock.menus;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.utils.NumberUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.cooldown.Cooldown;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownPlayer;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandLevel;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.levelling.IslandLevelManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Levelling {
    private static Levelling instance;

    public static Levelling getInstance() {
        if (instance == null) {
            instance = new Levelling();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        IslandLevelManager levellingManager = plugin.getLevellingManager();
        CooldownManager cooldownManager = plugin.getCooldownManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();

        if (!playerDataManager.hasPlayerData(player)) {
            return;
        }

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        FileConfiguration configLoad = plugin.getLanguage();

        nInventoryUtil nInv = new nInventoryUtil(player, event -> {
            if (islandManager.getIsland(player) == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Owner.Message"));
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);
                player.closeInventory();
                return;
            }

            if (!playerDataManager.hasPlayerData(player)) {
                return;
            }

            ItemStack is = event.getItem();

            if ((is.getType() == CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getMaterial()) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Barrier.Displayname"))))) {
                soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                event.setWillClose(false);
                event.setWillDestroy(false);
            } else if ((is.getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial()) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Exit.Displayname"))))) {
                soundManager.playSound(player, XSound.BLOCK_CHEST_CLOSE);
            } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Statistics.Displayname"))))) {
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                event.setWillClose(false);
                event.setWillDestroy(false);
            } else if ((is.getType() == Material.BARRIER) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Nothing.Displayname"))))) {
                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                event.setWillClose(false);
                event.setWillDestroy(false);
            } else if ((is.getType() == CompatibleMaterial.FIREWORK_STAR.getMaterial()) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Rescan.Displayname"))))) {
                Island island = islandManager.getIsland(player);
                OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());

                if (cooldownManager.hasPlayer(CooldownType.LEVELLING, offlinePlayer) && !player.hasPermission("fabledskyblock.bypass.cooldown")) {
                    CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.LEVELLING, offlinePlayer);
                    Cooldown cooldown = cooldownPlayer.getCooldown();

                    long[] durationTime = NumberUtil.getDuration(cooldown.getTime());

                    if (cooldown.getTime() >= 3600) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Level.Cooldown.Message").replace("%time",
                                        durationTime[1] + " " + configLoad.getString("Command.Island.Level.Cooldown.Word.Minute") + " " + durationTime[2] + " "
                                                + configLoad.getString("Command.Island.Level.Cooldown.Word.Minute") + " " + durationTime[3] + " "
                                                + configLoad.getString("Command.Island.Level.Cooldown.Word.Second")));
                    } else if (cooldown.getTime() >= 60) {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Cooldown.Message").replace("%time", durationTime[2] + " "
                                + configLoad.getString("Command.Island.Level.Cooldown.Word.Minute") + " " + durationTime[3] + " " + configLoad.getString("Command.Island.Level.Cooldown.Word.Second")));
                    } else {
                        messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Cooldown.Message").replace("%time",
                                cooldown.getTime() + " " + configLoad.getString("Command.Island.Level.Cooldown.Word.Second")));
                    }

                    soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                    event.setWillClose(false);
                    event.setWillDestroy(false);

                    return;
                }

                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Processing.Message"));
                    soundManager.playSound(player, XSound.ENTITY_VILLAGER_YES);

                    cooldownManager.createPlayer(CooldownType.LEVELLING, Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()));
                    levellingManager.startScan(player, island);
                });
            } else if ((is.getType() == CompatibleMaterial.PLAYER_HEAD.getMaterial()) && (is.hasItemMeta())) {
                PlayerData playerData1 = plugin.getPlayerDataManager().getPlayerData(player);

                if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Previous.Displayname")))) {
                    playerData1.setPage(MenuType.LEVELLING, playerData1.getPage(MenuType.LEVELLING) - 1);
                    soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Next.Displayname")))) {
                    playerData1.setPage(MenuType.LEVELLING, playerData1.getPage(MenuType.LEVELLING) + 1);
                    soundManager.playSound(player, XSound.ENTITY_ARROW_HIT);

                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                } else {
                    soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                }
            } else {
                soundManager.playSound(player, XSound.ENTITY_CHICKEN_EGG);

                event.setWillClose(false);
                event.setWillDestroy(false);
            }
        });

        Island island = islandManager.getIsland(player);
        IslandLevel level = island.getLevel();

        Map<String, Long> testIslandMaterials = level.getMaterials();
        List<String> testIslandMaterialKeysOrdered = testIslandMaterials.keySet().stream().sorted().collect(Collectors.toList());
        LinkedHashMap<String, Long> islandMaterials = new LinkedHashMap<>();

        // Filter out ItemStacks that can't be displayed in the inventory
        Inventory testInventory = Bukkit.createInventory(null, 9);

        for (String materialName : testIslandMaterialKeysOrdered) {
            if (plugin.getLevelling().getString("Materials." + materialName + ".Points") == null ||
                    !plugin.getConfiguration().getBoolean("Island.Levelling.IncludeEmptyPointsInList") &&
                            plugin.getLevelling().getInt("Materials." + materialName + ".Points") <= 0) {
                continue;
            }

            long value = testIslandMaterials.get(materialName);
            CompatibleMaterial materials = CompatibleMaterial.getMaterial(materialName);

            if (materials == null) {
                continue;
            }

            ItemStack is = materials.getItem();

            if (is == null || is.getItemMeta() == null) {
                continue;
            }

            is.setAmount(Math.min(Math.toIntExact(value), 64));
            is.setType(CompatibleMaterial.getMaterial(is).getMaterial());

            testInventory.clear();
            testInventory.setItem(0, is);

            if (testInventory.getItem(0) != null) {
                islandMaterials.put(materialName, value);
            }
        }

        int playerMenuPage = playerData.getPage(MenuType.LEVELLING), nextEndIndex = islandMaterials.size() - playerMenuPage * 36;

        nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(), configLoad.getString("Menu.Levelling.Item.Exit.Displayname"), null, null, null, null), 0, 8);
        if (player.hasPermission("fabledskyblock.island.level.rescan")) {
            nInv.addItem(nInv.createItem(CompatibleMaterial.FIREWORK_STAR.getItem(), configLoad.getString("Menu.Levelling.Item.Rescan.Displayname"), configLoad.getStringList("Menu.Levelling.Item.Rescan.Lore"), null, null,
                    new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 3, 5);
        }
        nInv.addItem(
                nInv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Levelling.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Levelling.Item.Statistics.Lore"),
                        new Placeholder[]{new Placeholder("%level_points", NumberUtils.formatNumber(level.getPoints())), new Placeholder("%level", NumberUtils.formatNumber(level.getLevel()))}, null, null),
                4);
        nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(), configLoad.getString("Menu.Levelling.Item.Barrier.Displayname"), null, null, null, null), 9, 10, 11, 12, 13, 14, 15, 16, 17);

        if (playerMenuPage != 1) {
            nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(
                            "ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=",
                            "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="),
                    configLoad.getString("Menu.Levelling.Item.Previous.Displayname"), null, null, null, null), 1);
        }

        if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
            nInv.addItem(nInv.createItem(ItemUtils.getCustomHead(
                            "wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=",
                            "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="),
                    configLoad.getString("Menu.Levelling.Item.Next.Displayname"), null, null, null, null), 7);
        }

        if (islandMaterials.isEmpty()) {
            nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.BARRIER.getMaterial()), configLoad.getString("Menu.Levelling.Item.Nothing.Displayname"), null, null, null, null), 31);
        } else {
            int index = playerMenuPage * 36 - 36, endIndex = index >= islandMaterials.size() ? islandMaterials.size() - 1 : index + 36, inventorySlot = 17;

            for (; index < endIndex; index++) {
                if (islandMaterials.size() <= index) {
                    break;
                }

                String material = (String) islandMaterials.keySet().toArray()[index];
                CompatibleMaterial materials = CompatibleMaterial.getMaterial(material);

                if (materials == null) {
                    break;
                }

                long materialAmount = islandMaterials.get(material);

                if (plugin.getLevelling().getString("Materials." + material + ".Points") == null) {
                    break;
                }

                double pointsMultiplier = plugin.getLevelling().getDouble("Materials." + material + ".Points");

                if (!plugin.getConfiguration().getBoolean("Island.Levelling.IncludeEmptyPointsInList") && pointsMultiplier == 0) {
                    return;
                }

                inventorySlot++;

                long materialLimit = plugin.getLevelling().getLong("Materials." + material + ".Limit", -1);
                long materialAmountCounted = Math.min(materialLimit, materialAmount);

                if (materialLimit == -1) {
                    materialAmountCounted = materialAmount;
                }

                double pointsEarned = materialAmountCounted * pointsMultiplier;


                String name = plugin.getLocalizationManager().getLocalizationFor(CompatibleMaterial.class).getLocale(materials);

                if (materials == CompatibleMaterial.FARMLAND && ServerVersion.isServerVersionBelow(ServerVersion.V1_9)) {
                    materials = CompatibleMaterial.DIRT;
                }

                ItemStack is = materials.getItem();
                is.setAmount(Math.min(Math.toIntExact(materialAmount), 64));
                is.setType(CompatibleMaterial.getMaterial(is).getMaterial());

                long finalMaterialAmountCounted = materialAmountCounted;
                List<String> lore = configLoad.getStringList("Menu.Levelling.Item.Material.Lore");
                lore.replaceAll(x -> x.replace("%points", NumberUtils.formatNumber(pointsEarned)).replace("%blocks", NumberUtils.formatNumber(materialAmount))
                        .replace("%material", name).replace("%counted", NumberUtils.formatNumber(finalMaterialAmountCounted)));

                nInv.addItem(nInv.createItem(is, configLoad.getString("Menu.Levelling.Item.Material.Displayname").replace("%points", NumberUtils.formatNumber(pointsEarned))
                                .replace("%blocks", NumberUtils.formatNumber(materialAmount)).replace("%material", name).replace("%counted", NumberUtils.formatNumber(finalMaterialAmountCounted))
                        , lore, null, null, null), inventorySlot);

            }
        }

        nInv.setTitle(plugin.formatText(configLoad.getString("Menu.Levelling.Title")));
        nInv.setRows(6);

        Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
    }
}
