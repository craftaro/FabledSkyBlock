package com.songoda.skyblock.menus;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.songoda.core.compatibility.CompatibleSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
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
import com.songoda.skyblock.utils.item.SkullUtil;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import com.songoda.skyblock.utils.version.NMSUtil;

public class Levelling {

    private static Levelling instance;

    public static Levelling getInstance() {
        if (instance == null) {
            instance = new Levelling();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock skyblock = SkyBlock.getInstance();

        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        IslandLevelManager levellingManager = skyblock.getLevellingManager();
        CooldownManager cooldownManager = skyblock.getCooldownManager();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        if (!playerDataManager.hasPlayerData(player))
            return;

        PlayerData playerData = skyblock.getPlayerDataManager().getPlayerData(player);
        FileConfiguration configLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration();

        nInventoryUtil nInv = new nInventoryUtil(player, event -> {
            if (islandManager.getIsland(player) == null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Owner.Message"));
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                player.closeInventory();
                return;
            }

            if (!playerDataManager.hasPlayerData(player))
                return;

            ItemStack is = event.getItem();

            if ((is.getType() == CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getMaterial()) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Barrier.Displayname"))))) {
                soundManager.playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                event.setWillClose(false);
                event.setWillDestroy(false);
            } else if ((is.getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial()) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Exit.Displayname"))))) {
                soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1.0F, 1.0F);
            } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Statistics.Displayname"))))) {
                soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_YES.getSound(), 1.0F, 1.0F);

                event.setWillClose(false);
                event.setWillDestroy(false);
            } else if ((is.getType() == Material.BARRIER) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Nothing.Displayname"))))) {
                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                event.setWillClose(false);
                event.setWillDestroy(false);
            } else if ((is.getType() == CompatibleMaterial.FIREWORK_STAR.getMaterial()) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Rescan.Displayname"))))) {
                Island island = islandManager.getIsland(player);
                OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID());

                if (cooldownManager.hasPlayer(CooldownType.Levelling, offlinePlayer) && !player.hasPermission("fabledskyblock.bypass.cooldown")) {
                    CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.Levelling, offlinePlayer);
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

                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                    event.setWillClose(false);
                    event.setWillDestroy(false);

                    return;
                }

                Bukkit.getServer().getScheduler().runTaskAsynchronously(skyblock, () -> {
                    messageManager.sendMessage(player, configLoad.getString("Command.Island.Level.Processing.Message"));
                    soundManager.playSound(player, CompatibleSound.ENTITY_VILLAGER_YES.getSound(), 1.0F, 1.0F);

                    cooldownManager.createPlayer(CooldownType.Levelling, Bukkit.getServer().getOfflinePlayer(island.getOwnerUUID()));
                    levellingManager.startScan(player, island);
                });
            } else if ((is.getType() == SkullUtil.createItemStack().getType()) && (is.hasItemMeta())) {
                PlayerData playerData1 = skyblock.getPlayerDataManager().getPlayerData(player);

                if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Previous.Displayname")))) {
                    playerData1.setPage(MenuType.LEVELLING, playerData1.getPage(MenuType.LEVELLING) - 1);
                    soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Next.Displayname")))) {
                    playerData1.setPage(MenuType.LEVELLING, playerData1.getPage(MenuType.LEVELLING) + 1);
                    soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);

                    Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                } else {
                    soundManager.playSound(player, CompatibleSound.ENTITY_CHICKEN_EGG.getSound(), 1.0F, 1.0F);

                    event.setWillClose(false);
                    event.setWillDestroy(false);
                }
            } else {
                soundManager.playSound(player, CompatibleSound.ENTITY_CHICKEN_EGG.getSound(), 1.0F, 1.0F);

                event.setWillClose(false);
                event.setWillDestroy(false);
            }
        });

        Island island = islandManager.getIsland(player);
        IslandLevel level = island.getLevel();

        Map<String, Long> testIslandMaterials = level.getMaterials();
        List<String> testIslandMaterialKeysOrdered = testIslandMaterials.keySet().stream().sorted().collect(Collectors.toList());
        LinkedHashMap<String, Long> islandMaterials = new LinkedHashMap<>();

        Config mainConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "levelling.yml"));
        Config settingsConfig = fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"));

        // Filter out ItemStacks that can't be displayed in the inventory
        Inventory testInventory = Bukkit.createInventory(null, 9);

        for (String materialName : testIslandMaterialKeysOrdered) {
            if (mainConfig.getFileConfiguration().getString("Materials." + materialName + ".Points") == null ||
                    !settingsConfig.getFileConfiguration().getBoolean("Island.Levelling.IncludeEmptyPointsInList") &&
                            mainConfig.getFileConfiguration().getInt("Materials." + materialName + ".Points") <= 0)
                continue;

            long value = testIslandMaterials.get(materialName);
            CompatibleMaterial materials = CompatibleMaterial.getMaterial(materialName);

            if (materials == null) continue;

            ItemStack is = materials.getItem();

            if (is == null || is.getItemMeta() == null) continue;

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
        nInv.addItem(nInv.createItem(CompatibleMaterial.FIREWORK_STAR.getItem(), configLoad.getString("Menu.Levelling.Item.Rescan.Displayname"), configLoad.getStringList("Menu.Levelling.Item.Rescan.Lore"), null, null,
                new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 3, 5);
        nInv.addItem(
                nInv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Levelling.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Levelling.Item.Statistics.Lore"),
                        new Placeholder[]{new Placeholder("%level_points", NumberUtil.formatNumberByDecimal(level.getPoints())), new Placeholder("%level", NumberUtil.formatNumberByDecimal(level.getLevel()))}, null, null),
                4);
        nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(), configLoad.getString("Menu.Levelling.Item.Barrier.Displayname"), null, null, null, null), 9, 10, 11, 12, 13, 14, 15, 16, 17);

        if (playerMenuPage != 1) {
            nInv.addItem(nInv.createItem(SkullUtil.create(
                    "ToR1w9ZV7zpzCiLBhoaJH3uixs5mAlMhNz42oaRRvrG4HRua5hC6oyyOPfn2HKdSseYA9b1be14fjNRQbSJRvXF3mlvt5/zct4sm+cPVmX8K5kbM2vfwHJgCnfjtPkzT8sqqg6YFdT35mAZGqb9/xY/wDSNSu/S3k2WgmHrJKirszaBZrZfnVnqITUOgM9TmixhcJn2obeqICv6tl7/Wyk/1W62wXlXGm9+WjS+8rRNB+vYxqKR3XmH2lhAiyVGbADsjjGtBVUTWjq+aPw670SjXkoii0YE8sqzUlMMGEkXdXl9fvGtnWKk3APSseuTsjedr7yq+AkXFVDqqkqcUuXwmZl2EjC2WRRbhmYdbtY5nEfqh5+MiBrGdR/JqdEUL4yRutyRTw8mSUAI6X2oSVge7EdM/8f4HwLf33EO4pTocTqAkNbpt6Z54asLe5Y12jSXbvd2dFsgeJbrslK7e4uy/TK8CXf0BP3KLU20QELYrjz9I70gtj9lJ9xwjdx4/xJtxDtrxfC4Afmpu+GNYA/mifpyP3GDeBB5CqN7btIvEWyVvRNH7ppAqZIPqYJ7dSDd2RFuhAId5Yq98GUTBn+eRzeigBvSi1bFkkEgldfghOoK5WhsQtQbXuBBXITMME3NaWCN6zG7DxspS6ew/rZ8E809Xe0ArllquIZ0sP+k=",
                    "eyJ0aW1lc3RhbXAiOjE0OTU3NTE5MTYwNjksInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZWJmOTA3NDk0YTkzNWU5NTViZmNhZGFiODFiZWFmYjkwZmI5YmU0OWM3MDI2YmE5N2Q3OThkNWYxYTIzIn19fQ=="),
                    configLoad.getString("Menu.Levelling.Item.Previous.Displayname"), null, null, null, null), 1);
        }

        if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
            nInv.addItem(nInv.createItem(SkullUtil.create(
                    "wZPrsmxckJn4/ybw/iXoMWgAe+1titw3hjhmf7bfg9vtOl0f/J6YLNMOI0OTvqeRKzSQVCxqNOij6k2iM32ZRInCQyblDIFmFadQxryEJDJJPVs7rXR6LRXlN8ON2VDGtboRTL7LwMGpzsrdPNt0oYDJLpR0huEeZKc1+g4W13Y4YM5FUgEs8HvMcg4aaGokSbvrYRRcEh3LR1lVmgxtbiUIr2gZkR3jnwdmZaIw/Ujw28+Et2pDMVCf96E5vC0aNY0KHTdMYheT6hwgw0VAZS2VnJg+Gz4JCl4eQmN2fs4dUBELIW2Rdnp4U1Eb+ZL8DvTV7ofBeZupknqPOyoKIjpInDml9BB2/EkD3zxFtW6AWocRphn03Z203navBkR6ztCMz0BgbmQU/m8VL/s8o4cxOn+2ppjrlj0p8AQxEsBdHozrBi8kNOGf1j97SDHxnvVAF3X8XDso+MthRx5pbEqpxmLyKKgFh25pJE7UaMSnzH2lc7aAZiax67MFw55pDtgfpl+Nlum4r7CK2w5Xob2QTCovVhu78/6SV7qM2Lhlwx/Sjqcl8rn5UIoyM49QE5Iyf1tk+xHXkIvY0m7q358oXsfca4eKmxMe6DFRjUDo1VuWxdg9iVjn22flqz1LD1FhGlPoqv0k4jX5Q733LwtPPI6VOTK+QzqrmiuR6e8=",
                    "eyJ0aW1lc3RhbXAiOjE0OTM4NjgxMDA2NzMsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ=="),
                    configLoad.getString("Menu.Levelling.Item.Next.Displayname"), null, null, null, null), 7);
        }

        if (islandMaterials.size() == 0) {
            nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.BARRIER.getMaterial()), configLoad.getString("Menu.Levelling.Item.Nothing.Displayname"), null, null, null, null), 31);
        } else {
            int index = playerMenuPage * 36 - 36, endIndex = index >= islandMaterials.size() ? islandMaterials.size() - 1 : index + 36, inventorySlot = 17;

            for (; index < endIndex; index++) {
                if (islandMaterials.size() <= index)
                    break;

                String material = (String) islandMaterials.keySet().toArray()[index];
                CompatibleMaterial materials = CompatibleMaterial.getMaterial(material);

                if (materials == null)
                    break;

                long materialAmount = islandMaterials.get(material);

                if (mainConfig.getFileConfiguration().getString("Materials." + material + ".Points") == null)
                    break;

                int pointsMultiplier = mainConfig.getFileConfiguration().getInt("Materials." + material + ".Points");

                if (!settingsConfig.getFileConfiguration().getBoolean("Island.Levelling.IncludeEmptyPointsInList") && pointsMultiplier == 0)
                    return;

                inventorySlot++;

                long materialLimit = mainConfig.getFileConfiguration().getLong("Materials." + material + ".Limit", -1);
                long materialAmountCounted = Math.min(materialLimit, materialAmount);

                if (materialLimit == -1)
                    materialAmountCounted = materialAmount;

                long pointsEarned = materialAmountCounted * pointsMultiplier;


                String name = skyblock.getLocalizationManager().getLocalizationFor(CompatibleMaterial.class).getLocale(materials);

                if (materials == CompatibleMaterial.FARMLAND && NMSUtil.getVersionNumber() < 9)
                    materials = CompatibleMaterial.DIRT;

                ItemStack is = materials.getItem();
                is.setAmount(Math.min(Math.toIntExact(materialAmount), 64));
                is.setType(CompatibleMaterial.getMaterial(is).getMaterial());

                List<String> lore = configLoad.getStringList("Menu.Levelling.Item.Material.Lore");
                lore.replaceAll(x -> x.replace("%points", NumberUtil.formatNumberByDecimal(pointsEarned)).replace("%blocks", NumberUtil.formatNumberByDecimal(materialAmount)).replace("%material", name));

                nInv.addItem(nInv.createItem(is, configLoad.getString("Menu.Levelling.Item.Material.Displayname").replace("%points", NumberUtil.formatNumberByDecimal(pointsEarned))
                        .replace("%blocks", NumberUtil.formatNumberByDecimal(materialAmount)).replace("%material", name), lore, null, null, null), inventorySlot);
            }
        }

        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Title")));
        nInv.setRows(6);

        Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
    }
}