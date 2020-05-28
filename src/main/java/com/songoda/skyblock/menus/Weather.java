package com.songoda.skyblock.menus;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.island.IslandRole;
import com.songoda.skyblock.island.IslandWorld;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.permission.PermissionManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.utils.item.nInventoryUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class Weather {

    private static Weather instance;

    public static Weather getInstance() {
        if (instance == null) {
            instance = new Weather();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock skyblock = SkyBlock.getInstance();

        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        MessageManager messageManager = skyblock.getMessageManager();
        IslandManager islandManager = skyblock.getIslandManager();
        PermissionManager permissionManager = skyblock.getPermissionManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        if (playerDataManager.hasPlayerData(player)) {
            FileConfiguration configLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                    .getFileConfiguration();

            nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                if (playerDataManager.hasPlayerData(player)) {
                    Island island = islandManager.getIsland(player);

                    if (island == null) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Weather.Owner.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);
                        player.closeInventory();

                        return;
                    } else if (!((island.hasRole(IslandRole.Operator, player.getUniqueId())
                            && permissionManager.hasPermission(island, "Biome", IslandRole.Operator))
                            || island.hasRole(IslandRole.Owner, player.getUniqueId()))) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Command.Island.Weather.Permission.Message"));
                        soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                        player.closeInventory();

                        return;
                    }

                    ItemStack is = event.getItem();

                    if ((is.getType() == Material.NAME_TAG) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Weather.Item.Info.Displayname"))))) {
                        soundManager.playSound(player, CompatibleSound.ENTITY_CHICKEN_EGG.getSound(), 1.0F, 1.0F);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if ((is.getType() == CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getMaterial())
                            && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Weather.Item.Barrier.Displayname"))))) {
                        soundManager.playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                        event.setWillClose(false);
                        event.setWillDestroy(false);
                    } else if (is.getType() == CompatibleMaterial.BARRIER.getMaterial()) {
                        event.setWillClose(false);
                        event.setWillDestroy(false);
                        soundManager.playSound(player,  CompatibleSound.ENTITY_VILLAGER_NO.getSound(), 1.0F, 1.0F);
                    } else if ((is.getType() == CompatibleMaterial.SUNFLOWER.getMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Weather.Item.Time.Displayname"))))) {
                        int islandTime = island.getTime();

                        if (islandTime == 0) {
                            island.setTime(1000);
                        } else if (islandTime == 1000) {
                            island.setTime(6000);
                        } else if (islandTime == 6000) {
                            island.setTime(12000);
                        } else if (islandTime == 12000) {
                            island.setTime(13000);
                        } else if (islandTime == 13000) {
                            island.setTime(18000);
                        } else if (islandTime == 18000) {
                            island.setTime(0);
                        }

                        if (!island.isWeatherSynchronized()) {
                            for (Player all : islandManager.getPlayersAtIsland(island, IslandWorld.Normal)) {
                                all.resetPlayerTime();
                                all.resetPlayerWeather();
                                all.setPlayerTime(island.getTime(),
                                        fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                                                .getFileConfiguration().getBoolean("Island.Weather.Time.Cycle"));
                                all.setPlayerWeather(island.getWeather());
                            }
                        }

                        soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                        Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                    } else if ((is.getType() == Material.GHAST_TEAR) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Weather.Item.Weather.Displayname"))))) {
                        if (island.getWeather() == WeatherType.DOWNFALL) {
                            island.setWeather(WeatherType.CLEAR);
                        } else {
                            island.setWeather(WeatherType.DOWNFALL);
                        }

                        if (!island.isWeatherSynchronized()) {
                            for (Player all : islandManager.getPlayersAtIsland(island, IslandWorld.Normal)) {
                                all.resetPlayerTime();
                                all.resetPlayerWeather();
                                all.setPlayerTime(island.getTime(),
                                        fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                                                .getFileConfiguration().getBoolean("Island.Weather.Time.Cycle"));
                                all.setPlayerWeather(island.getWeather());
                            }
                        }

                        soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                        Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                    } else if ((is.getType() == Material.TRIPWIRE_HOOK) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Weather.Item.Synchronised.Displayname"))))) {
                        if (island.isWeatherSynchronized()) {
                            island.setWeatherSynchronized(false);

                            int islandTime = island.getTime();
                            WeatherType islandWeather = island.getWeather();

                            for (Player all : islandManager.getPlayersAtIsland(island, IslandWorld.Normal)) {
                                all.setPlayerTime(islandTime,
                                        fileManager.getConfig(new File(skyblock.getDataFolder(), "config.yml"))
                                                .getFileConfiguration().getBoolean("Island.Weather.Time.Cycle"));
                                all.setPlayerWeather(islandWeather);
                            }
                        } else {
                            island.setWeatherSynchronized(true);

                            for (Player all : islandManager.getPlayersAtIsland(island, IslandWorld.Normal)) {
                                all.resetPlayerTime();
                                all.resetPlayerWeather();
                            }
                        }

                        soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                        Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                    }
                }
            });

            Island island = islandManager.getIsland(player);

            String timeName = "", timeChoice = "", weatherSynchronised, weatherChoice, synchronisedChoice;
            int islandTime = island.getTime();

            if (island.isWeatherSynchronized()) {
                weatherSynchronised = configLoad.getString("Menu.Weather.Item.Info.Synchronised.Enabled");
            } else {
                weatherSynchronised = configLoad.getString("Menu.Weather.Item.Info.Synchronised.Disabled");
            }

            if (islandTime == 0) {
                timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Dawn");
                timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Dawn");
            } else if (islandTime == 1000) {
                timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Day");
                timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Day");
            } else if (islandTime == 6000) {
                timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Noon");
                timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Noon");
            } else if (islandTime == 12000) {
                timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Dusk");
                timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Dusk");
            } else if (islandTime == 13000) {
                timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Night");
                timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Night");
            } else if (islandTime == 18000) {
                timeName = configLoad.getString("Menu.Weather.Item.Info.Time.Midnight");
                timeChoice = configLoad.getString("Menu.Weather.Item.Time.Choice.Midnight");
            }

            if (island.getWeather() != WeatherType.CLEAR) {
                weatherChoice = configLoad.getString("Menu.Weather.Item.Weather.Choice.Downfall");
            } else {
                weatherChoice = configLoad.getString("Menu.Weather.Item.Weather.Choice.Clear");
            }

            if (island.isWeatherSynchronized()) {
                synchronisedChoice = configLoad.getString("Menu.Weather.Item.Synchronised.Choice.Disable");
            } else {
                synchronisedChoice = configLoad.getString("Menu.Weather.Item.Synchronised.Choice.Enable");
            }

            nInv.addItem(nInv.createItem(new ItemStack(Material.NAME_TAG),
                    configLoad.getString("Menu.Weather.Item.Info.Displayname"),
                    configLoad.getStringList("Menu.Weather.Item.Info.Lore." + (island.isWeatherSynchronized() ? "Synchronised" : "Unsynchronised")),
                    new Placeholder[]{new Placeholder("%synchronised", weatherSynchronised),
                            new Placeholder("%time_name", timeName), new Placeholder("%time", "" + island.getTime()),
                            new Placeholder("%weather", island.getWeatherName())},
                    null, null), 0);
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.Weather.Item.Barrier.Displayname"), null, null, null, null), 1);

            if (!island.isWeatherSynchronized()) {
                nInv.addItem(nInv.createItem(CompatibleMaterial.SUNFLOWER.getItem(),
                        configLoad.getString("Menu.Weather.Item.Time.Displayname"),
                        configLoad.getStringList("Menu.Weather.Item.Time.Lore"),
                        new Placeholder[]{new Placeholder("%choice", timeChoice)}, null, null), 2);
                nInv.addItem(nInv.createItem(new ItemStack(Material.GHAST_TEAR),
                        configLoad.getString("Menu.Weather.Item.Weather.Displayname"),
                        configLoad.getStringList("Menu.Weather.Item.Weather.Lore"),
                        new Placeholder[]{new Placeholder("%choice", weatherChoice)}, null, null), 3);
            } else {
                nInv.addItem(nInv.createItem(CompatibleMaterial.BARRIER.getItem(),
                        configLoad.getString("Menu.Weather.Item.Disabled.Time.Displayname"),
                        configLoad.getStringList("Menu.Weather.Item.Disabled.Time.Lore"),
                        new Placeholder[]{new Placeholder("%choice", timeChoice)}, null, null), 2);
                nInv.addItem(nInv.createItem(new ItemStack(Material.BARRIER),
                        configLoad.getString("Menu.Weather.Item.Disabled.Weather.Displayname"),
                        configLoad.getStringList("Menu.Weather.Item.Disabled.Weather.Lore"),
                        new Placeholder[]{new Placeholder("%choice", weatherChoice)}, null, null), 3);
            }

            nInv.addItem(nInv.createItem(new ItemStack(Material.TRIPWIRE_HOOK),
                    configLoad.getString("Menu.Weather.Item.Synchronised.Displayname"),
                    configLoad.getStringList("Menu.Weather.Item.Synchronised.Lore"),
                    new Placeholder[]{new Placeholder("%choice", synchronisedChoice)}, null, null), 4);

            nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Weather.Title")));
            nInv.setType(InventoryType.HOPPER);

            Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
        }
    }
}
