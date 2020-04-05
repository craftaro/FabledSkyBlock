package com.songoda.skyblock.menus;

import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
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
        SkyBlock skyblock = SkyBlock.getInstance();

        CooldownManager cooldownManager = skyblock.getCooldownManager();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        Config config = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        List<Structure> availableStructures = new ArrayList<>();

        for (Structure structureList : skyblock.getStructureManager().getStructures()) {
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

        if (availableStructures.size() == 0) {
            skyblock.getMessageManager().sendMessage(player,
                    configLoad.getString("Island.Creator.Selector.None.Message"));
            skyblock.getSoundManager().playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

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
                soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                return;
            }

            Bukkit.getServer().getScheduler().runTaskAsynchronously(skyblock, () -> {
                ItemStack is = event.getItem();

                for (Structure structureList : skyblock.getStructureManager().getStructures()) {
                    if ((is.getType() == structureList.getMaterial().getMaterial()) && (is.hasItemMeta())
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
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                        () -> open(player), 1L);

                                return;
                            }
                        }

                        if (!fileManager.isFileExist(
                                new File(new File(skyblock.getDataFolder().toString() + "/" +
                                        (structureList.getOverworldFile().endsWith(".structure") ? "structures" : "schematics")),
                                        structureList.getOverworldFile()))) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Creator.Selector.File.Overworld.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            event.setWillClose(false);
                            event.setWillDestroy(false);

                            return;
                        } else if (!fileManager.isFileExist(
                                new File(new File(skyblock.getDataFolder().toString() + "/" +
                                        (structureList.getNetherFile().endsWith(".structure") ? "structures" : "schematics")),
                                        structureList.getNetherFile()))) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Creator.Selector.File.Nether.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            event.setWillClose(false);
                            event.setWillDestroy(false);

                            return;
                        } else if (!fileManager.isFileExist(
                                new File(new File(skyblock.getDataFolder().toString() + "/" +
                                        (structureList.getEndFile().endsWith(".structure") ? "structures" : "schematics")),
                                        structureList.getEndFile()))) {
                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Creator.Selector.File.End.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            event.setWillClose(false);
                            event.setWillDestroy(false);

                            return;
                        } else if (fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                                .getFileConfiguration().getBoolean("Island.Creation.Cooldown.Creation.Enable")
                                && cooldownManager.hasPlayer(CooldownType.Creation, player)) {
                            CooldownPlayer cooldownPlayer = cooldownManager
                                    .getCooldownPlayer(CooldownType.Creation, player);
                            Cooldown cooldown = cooldownPlayer.getCooldown();

                            if (cooldown.getTime() < 60) {
                                messageManager.sendMessage(player, config.getFileConfiguration()
                                        .getString("Island.Creator.Selector.Cooldown.Message")
                                        .replace("%time", cooldown.getTime() + " "
                                                + config.getFileConfiguration().getString(
                                                "Island.Creator.Selector.Cooldown.Word.Second")));
                            } else {
                                long[] durationTime = NumberUtil.getDuration(cooldown.getTime());
                                messageManager.sendMessage(player, config.getFileConfiguration()
                                        .getString("Island.Creator.Selector.Cooldown.Message")
                                        .replace("%time", durationTime[2] + " "
                                                + config.getFileConfiguration().getString(
                                                "Island.Creator.Selector.Cooldown.Word.Minute")
                                                + " " + durationTime[3] + " "
                                                + config.getFileConfiguration().getString(
                                                "Island.Creator.Selector.Cooldown.Word.Second")));
                            }

                            soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);

                            event.setWillClose(false);
                            event.setWillDestroy(false);

                            return;
                        }

                        if(event.getClick().isLeftClick()) {
                            if (islandManager.createIsland(player, structureList)) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Creator.Selector.Created.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
                            }
                        } else if(event.getClick().isRightClick()) {
                            if (islandManager.previewIsland(player, structureList)) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Creator.Selector.Preview.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(), 1.0F, 1.0F);
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

            nInv.addItem(nInv.createItem(structure.getMaterial().getItem(),
                    ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Creator.Selector.Item.Island.Displayname")
                                    .replace("%displayname", structure.getDisplayname())),
                    itemLore, null, null, null), i);
        }

        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Creator.Selector.Title")));
        nInv.setRows(inventoryRows);

        Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
    }
}
