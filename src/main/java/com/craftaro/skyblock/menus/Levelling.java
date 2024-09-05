package com.craftaro.skyblock.menus;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.MajorServerVersion;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.utils.SkullItemCreator;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.third_party.com.cryptomorin.xseries.XSound;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.utils.NumberUtils;
import com.craftaro.skyblock.SkyBlock;
import com.craftaro.skyblock.cooldown.Cooldown;
import com.craftaro.skyblock.cooldown.CooldownManager;
import com.craftaro.skyblock.cooldown.CooldownPlayer;
import com.craftaro.skyblock.cooldown.CooldownType;
import com.craftaro.skyblock.island.Island;
import com.craftaro.skyblock.island.IslandLevel;
import com.craftaro.skyblock.island.IslandManager;
import com.craftaro.skyblock.levelling.IslandLevelManager;
import com.craftaro.skyblock.message.MessageManager;
import com.craftaro.skyblock.placeholder.Placeholder;
import com.craftaro.skyblock.playerdata.PlayerData;
import com.craftaro.skyblock.playerdata.PlayerDataManager;
import com.craftaro.skyblock.sound.SoundManager;
import com.craftaro.skyblock.utils.NumberUtil;
import com.craftaro.skyblock.utils.item.nInventoryUtil;
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
import java.util.Optional;
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

            if ((XMaterial.BLACK_STAINED_GLASS_PANE.isSimilar(is)) && (is.hasItemMeta())
                    && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Levelling.Item.Barrier.Displayname"))))) {
                soundManager.playSound(player, XSound.BLOCK_GLASS_BREAK);

                event.setWillClose(false);
                event.setWillDestroy(false);
            } else if ((XMaterial.OAK_FENCE_GATE.isSimilar(is)) && (is.hasItemMeta())
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
            } else if ((XMaterial.FIREWORK_STAR.isSimilar(is)) && (is.hasItemMeta())
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
            } else if ((XMaterial.PLAYER_HEAD.isSimilar(is)) && (is.hasItemMeta())) {
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
            Optional<XMaterial> materials = CompatibleMaterial.getMaterial(materialName);


            if (!materials.isPresent()) {
                continue;
            }

            ItemStack is = materials.get().parseItem();

            if (is == null || is.getItemMeta() == null) {
                continue;
            }

            is.setAmount(Math.min(Math.toIntExact(value), 64));
            is.setType(CompatibleMaterial.getMaterial(is.getType()).get().parseMaterial());

            testInventory.clear();
            testInventory.setItem(0, is);

            if (testInventory.getItem(0) != null) {
                islandMaterials.put(materialName, value);
            }
        }

        int playerMenuPage = playerData.getPage(MenuType.LEVELLING), nextEndIndex = islandMaterials.size() - playerMenuPage * 36;

        nInv.addItem(nInv.createItem(XMaterial.OAK_FENCE_GATE.parseItem(), configLoad.getString("Menu.Levelling.Item.Exit.Displayname"), null, null, null, null), 0, 8);
        if (player.hasPermission("fabledskyblock.island.level.rescan")) {
            nInv.addItem(nInv.createItem(XMaterial.FIREWORK_STAR.parseItem(), configLoad.getString("Menu.Levelling.Item.Rescan.Displayname"), configLoad.getStringList("Menu.Levelling.Item.Rescan.Lore"), null, null,
                    new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 3, 5);
        }
        nInv.addItem(
                nInv.createItem(new ItemStack(Material.PAINTING), configLoad.getString("Menu.Levelling.Item.Statistics.Displayname"), configLoad.getStringList("Menu.Levelling.Item.Statistics.Lore"),
                        new Placeholder[]{new Placeholder("%level_points", NumberUtils.formatNumber(level.getPoints())), new Placeholder("%level", NumberUtils.formatNumber(level.getLevel()))}, null, null),
                4);
        nInv.addItem(nInv.createItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem(), configLoad.getString("Menu.Levelling.Item.Barrier.Displayname"), null, null, null, null), 9, 10, 11, 12, 13, 14, 15, 16, 17);

        if (playerMenuPage != 1) {
            ItemStack Lhead = SkullItemCreator.byTextureHash("3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23");
            nInv.addItem(nInv.createItem(Lhead,
                    configLoad.getString("Menu.Levelling.Item.Previous.Displayname"), null, null, null, null), 1);
        }

        if (!(nextEndIndex == 0 || nextEndIndex < 0)) {
            ItemStack Rhead = SkullItemCreator.byTextureHash("1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b");
            nInv.addItem(nInv.createItem(Rhead,
                    configLoad.getString("Menu.Levelling.Item.Next.Displayname"), null, null, null, null), 7);
        }

        if (islandMaterials.isEmpty()) {
            nInv.addItem(nInv.createItem(XMaterial.BARRIER.parseItem(), configLoad.getString("Menu.Levelling.Item.Nothing.Displayname"), null, null, null, null), 31);
        } else {
            int index = playerMenuPage * 36 - 36, endIndex = index >= islandMaterials.size() ? islandMaterials.size() - 1 : index + 36, inventorySlot = 17;

            for (; index < endIndex; index++) {
                if (islandMaterials.size() <= index) {
                    break;
                }

                String material = (String) islandMaterials.keySet().toArray()[index];
                Optional<XMaterial> materials = CompatibleMaterial.getMaterial(material);

                if (!materials.isPresent()) {
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


                String name = plugin.getLocalizationManager().getLocalizationFor(XMaterial.class).getLocale(materials.get());

                if (materials.get() == XMaterial.FARMLAND && MajorServerVersion.isServerVersionBelow(MajorServerVersion.V1_9)) {
                    materials = Optional.of(XMaterial.DIRT);
                }

                ItemStack is = materials.get().parseItem();
                is.setAmount(Math.min(Math.toIntExact(materialAmount), 64));
                is.setType(CompatibleMaterial.getMaterial(is.getType()).get().parseMaterial());

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
