package com.songoda.skyblock.menus;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.cooldown.Cooldown;
import com.songoda.skyblock.cooldown.CooldownManager;
import com.songoda.skyblock.cooldown.CooldownPlayer;
import com.songoda.skyblock.cooldown.CooldownType;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.structure.Structure;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Creator {
    private static Creator instance;

    public static Creator getInstance() {
        if (instance == null) {
            instance = new Creator();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        CooldownManager cooldownManager = plugin.getCooldownManager();
        MessageManager messageManager = plugin.getMessageManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        FileConfiguration configLoad = plugin.getLanguage();

        List<Structure> availableStructures = new ArrayList<>();

        for (Structure structureList : plugin.getStructureManager().getStructures()) {
            if (structureList.getDisplayname() == null || structureList.getDisplayname().isEmpty()
                    || structureList.getOverworldFile() == null || structureList.getOverworldFile().isEmpty()
                    || structureList.getNetherFile() == null || structureList.getNetherFile().isEmpty()) {
                continue;
            }

            if (structureList.isPermission()) {
                if (!player.hasPermission(structureList.getPermission()) && !player.hasPermission("fabledskyblock.island.*")
                        && !player.hasPermission("fabledskyblock.*")) {
                    continue;
                }
            }

            availableStructures.add(structureList);
        }

        int inventoryRows = 0;

        if (availableStructures.isEmpty()) {
            plugin.getMessageManager().sendMessage(player,
                    configLoad.getString("Island.Creator.Selector.None.Message"));
            plugin.getSoundManager().playSound(player, XSound.BLOCK_ANVIL_LAND);

            return;
        } else if (availableStructures.size() <= 9) {
            inventoryRows = 1;
        } else if (availableStructures.size() <= 18) {
            inventoryRows = 2;
        } else if (availableStructures.size() <= 27) {
            inventoryRows = 3;
        } else if (availableStructures.size() <= 36) {
            inventoryRows = 4;
        } else if (availableStructures.size() <= 45) {
            inventoryRows = 5;
        } else if (availableStructures.size() <= 54) {
            inventoryRows = 6;
        }

        nInventoryUtil nInv = new nInventoryUtil(player, event -> {
            if (islandManager.getIsland(player) != null) {
                messageManager.sendMessage(player, configLoad.getString("Command.Island.Create.Owner.Message"));
                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                return;
            }

            Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                ItemStack is = event.getItem();

                for (Structure structureList : plugin.getStructureManager().getStructures()) {
                    if ((structureList.getMaterial().isSimilar(is)) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName()
                            .equals(ChatColor.translateAlternateColorCodes('&', configLoad
                                    .getString("Menu.Creator.Selector.Item.Island.Displayname")
                                    .replace("%displayname", structureList.getDisplayname()))))) {
                        if (structureList.isPermission() && structureList.getPermission() != null
                                && !structureList.getPermission().isEmpty()) {
                            if (!player.hasPermission(structureList.getPermission())
                                    && !player.hasPermission("fabledskyblock.island.*")
                                    && !player.hasPermission("fabledskyblock.*")) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Creator.Selector.Permission.Message"));
                                soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player), 1L);

                                return;
                            }
                        }

                        if (!fileManager.isFileExist(
                                new File(new File(plugin.getDataFolder().toString() + "/" +
                                        (structureList.getOverworldFile().endsWith(".structure") ? "structures" : "schematics")),
                                        structureList.getOverworldFile()))) {
                            messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.File.Overworld.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            event.setWillClose(false);
                            event.setWillDestroy(false);

                            return;
                        } else if (!fileManager.isFileExist(
                                new File(new File(plugin.getDataFolder().toString() + "/" +
                                        (structureList.getNetherFile().endsWith(".structure") ? "structures" : "schematics")),
                                        structureList.getNetherFile()))) {
                            messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.File.Nether.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            event.setWillClose(false);
                            event.setWillDestroy(false);

                            return;
                        } else if (!fileManager.isFileExist(
                                new File(new File(plugin.getDataFolder().toString() + "/" +
                                        (structureList.getEndFile().endsWith(".structure") ? "structures" : "schematics")),
                                        structureList.getEndFile()))) {
                            messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.File.End.Message"));
                            soundManager.playSound(player, XSound.BLOCK_ANVIL_LAND);

                            event.setWillClose(false);
                            event.setWillDestroy(false);

                            return;
                        }

                        if (event.getClick().isLeftClick()) {
                            if (plugin.getConfiguration().getBoolean("Island.Creation.Cooldown.Creation.Enable")
                                    && cooldownManager.hasPlayer(CooldownType.CREATION, player)) {
                                CooldownPlayer cooldownPlayer = cooldownManager
                                        .getCooldownPlayer(CooldownType.CREATION, player);
                                Cooldown cooldown = cooldownPlayer.getCooldown();

                                if (cooldown.getTime() < 60) {
                                    messageManager.sendMessage(player, configLoad
                                            .getString("Island.Creator.Selector.Cooldown.Message")
                                            .replace("%time", cooldown.getTime() + " "
                                                    + configLoad.getString(
                                                    "Island.Creator.Selector.Cooldown.Word.Second")));
                                } else {
                                    long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
                                    messageManager.sendMessage(player, configLoad
                                            .getString("Island.Creator.Selector.Cooldown.Message")
                                            .replace("%time", durationTime[2] + " "
                                                    + configLoad.getString(
                                                    "Island.Creator.Selector.Cooldown.Word.Minute")
                                                    + " " + durationTime[3] + " "
                                                    + configLoad.getString(
                                                    "Island.Creator.Selector.Cooldown.Word.Second")));
                                }

                                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                                return;
                            }

                            if (islandManager.createIsland(player, structureList)) {
                                messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.Created.Message"));
                                soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);
                            }
                        } else if (event.getClick().isRightClick()) {
                            if (fileManager.getConfig(new File(plugin.getDataFolder(), "config.yml"))
                                    .getFileConfiguration().getBoolean("Island.Preview.Cooldown.Enable")
                                    && cooldownManager.hasPlayer(CooldownType.PREVIEW, player)) {
                                CooldownPlayer cooldownPlayer = cooldownManager.getCooldownPlayer(CooldownType.PREVIEW, player);
                                Cooldown cooldown = cooldownPlayer.getCooldown();

                                if (cooldown.getTime() < 60) {
                                    messageManager.sendMessage(player, configLoad
                                            .getString("Island.Preview.Cooldown.Message")
                                            .replace("%time", cooldown.getTime() + " "
                                                    + configLoad.getString(
                                                    "Island.Preview.Cooldown.Word.Second")));
                                } else {
                                    long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
                                    messageManager.sendMessage(player, configLoad
                                            .getString("Island.Preview.Cooldown.Message")
                                            .replace("%time", durationTime[2] + " "
                                                    + configLoad.getString(
                                                    "Island.Preview.Cooldown.Word.Minute")
                                                    + " " + durationTime[3] + " "
                                                    + configLoad.getString(
                                                    "Island.Preview.Cooldown.Word.Second")));
                                }

                                soundManager.playSound(player, XSound.ENTITY_VILLAGER_NO);
                                event.setWillClose(false);
                                event.setWillDestroy(false);

                                return;
                            }

                            if (islandManager.previewIsland(player, structureList)) {
                                messageManager.sendMessage(player, configLoad.getString("Island.Creator.Selector.Preview.Message"));
                                soundManager.playSound(player, XSound.BLOCK_NOTE_BLOCK_PLING);
                            }
                        }

                        return;
                    }
                }
            });
        });

        for (int i = 0; i < availableStructures.size(); i++) {
            Structure structure = availableStructures.get(i);
            List<String> itemLore = new ArrayList<>();

            for (String itemLoreList : configLoad.getStringList("Menu.Creator.Selector.Item.Island.Lore")) {
                if (itemLoreList.contains("%description")) {
                    if (structure.getDescription() == null || structure.getDescription().isEmpty()) {
                        itemLore.add(configLoad.getString("Menu.Creator.Selector.Item.Island.Word.Empty"));
                    } else {
                        for (String descriptionList : structure.getDescription()) {
                            itemLore.add(ChatColor.translateAlternateColorCodes('&', descriptionList));
                        }
                    }
                } else {
                    itemLore.add(ChatColor.translateAlternateColorCodes('&', itemLoreList));
                }
            }

            nInv.addItem(nInv.createItem(structure.getMaterial().parseItem(),
                    ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Creator.Selector.Item.Island.Displayname")
                                    .replace("%displayname", structure.getDisplayname())),
                    itemLore, null, null, null), i);
        }

        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Creator.Selector.Title")));
        nInv.setRows(inventoryRows);

        Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
    }
}
