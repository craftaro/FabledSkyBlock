package com.songoda.skyblock.menus;

import com.craftaro.core.compatibility.CompatibleMaterial;
import com.craftaro.core.compatibility.CompatibleSound;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.hooks.economies.Economy;
import com.craftaro.core.utils.NumberUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.island.IslandUpgradeEvent;
import com.songoda.skyblock.api.utils.APIUtil;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.upgrade.UpgradeManager;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

public class Upgrade {
    private static Upgrade instance;

    public static Upgrade getInstance() {
        if (instance == null) {
            instance = new Upgrade();
        }

        return instance;
    }

    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getPlugin(SkyBlock.class);

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        MessageManager messageManager = plugin.getMessageManager();
        UpgradeManager upgradeManager = plugin.getUpgradeManager();
        IslandManager islandManager = plugin.getIslandManager();
        SoundManager soundManager = plugin.getSoundManager();
        Economy economy = plugin.getEconomyManager().getEconomy();

        FileConfiguration configLoad = plugin.getLanguage();

        if (!economy.isEnabled()) {
            messageManager.sendMessage(player, configLoad.getString("Island.Upgrade.Disabled.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

            return;
        }

        if (playerDataManager.hasPlayerData(player) && playerDataManager.getPlayerData(player).getOwner() != null) {
            Island island = islandManager.getIsland(player);

            nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                if (!economy.isEnabled()) {
                    messageManager.sendMessage(player, configLoad.getString("Island.Upgrade.Disabled.Message"));
                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                    return;
                }

                if (playerDataManager.hasPlayerData(player)) {
                    PlayerData playerData = playerDataManager.getPlayerData(player);

                    if (playerData.getOwner() == null) {
                        messageManager.sendMessage(player, configLoad.getString("Island.Upgrade.Owner.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        return;
                    }

                    ItemStack is = event.getItem();

                    if ((is.getType() == Material.POTION) && (is.hasItemMeta())) {
                        if (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                                configLoad.getString("Menu.Upgrade.Item.Speed.Displayname")))) {
                            if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.SPEED)) {
                                if (island.isUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.SPEED)) {
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.SPEED,
                                            false);

                                    for (Player all : islandManager.getPlayersAtIsland(island)) {
                                        all.removePotionEffect(PotionEffectType.SPEED);
                                    }
                                } else {
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.SPEED,
                                            true);
                                }

                                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player), 1L);
                            } else {
                                List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                        .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.SPEED);

                                if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()) {
                                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                                    if (economy.hasBalance(player, upgrade.getCost())) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Upgrade.Bought.Message")
                                                        .replace("%upgrade", is.getItemMeta().getDisplayName()));
                                        soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                                        economy.withdrawBalance(player, upgrade.getCost());
                                        island.setUpgrade(player,
                                                com.songoda.skyblock.upgrade.Upgrade.Type.SPEED, true);

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    } else {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Upgrade.Money.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        event.setWillClose(false);
                                        event.setWillDestroy(false);
                                    }
                                } else {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Exist.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                }
                            }
                        } else if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes(
                                '&', configLoad.getString("Menu.Upgrade.Item.Jump.Displayname")))) {
                            if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.JUMP)) {
                                if (island.isUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.JUMP)) {
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.JUMP,
                                            false);

                                    for (Player all : islandManager.getPlayersAtIsland(island)) {
                                        all.removePotionEffect(PotionEffectType.JUMP);
                                    }
                                } else {
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.JUMP,
                                            true);
                                }

                                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> open(player), 1L);
                            } else {
                                List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                        .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.JUMP);

                                if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()) {
                                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                                    if (economy.hasBalance(player, upgrade.getCost())) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Upgrade.Bought.Message")
                                                        .replace("%upgrade", is.getItemMeta().getDisplayName()));
                                        soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                                        economy.withdrawBalance(player, upgrade.getCost());
                                        island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.JUMP,
                                                true);

                                        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                () -> open(player), 1L);
                                    } else {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Upgrade.Money.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                        event.setWillClose(false);
                                        event.setWillDestroy(false);
                                    }
                                } else {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Exist.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                }
                            }
                        }
                    } else if ((is.getType() == CompatibleMaterial.WHEAT_SEEDS.getMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Upgrade.Item.Crop.Displayname"))))) {
                        if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.CROP)) {
                            island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.CROP, !island.isUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.CROP));

                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else {
                            List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                    .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.CROP);

                            if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()) {
                                com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                                if (economy.hasBalance(player, upgrade.getCost())) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Bought.Message")
                                                    .replace("%upgrade", is.getItemMeta().getDisplayName()));
                                    soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                                    economy.withdrawBalance(player, upgrade.getCost());
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.CROP,
                                            true);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Money.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                }
                            } else {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Upgrade.Exist.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            }
                        }
                    } else if ((is.getType() == Material.FEATHER) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Upgrade.Item.Fly.Displayname"))))) {
                        if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.FLY)) {
                            if (island.isUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.FLY)) {
                                island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.FLY, false);
                                islandManager.updateFlightAtIsland(island);
                            } else {
                                island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.FLY, true);
                                islandManager.updateFlightAtIsland(island);
                            }

                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else {
                            List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                    .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.FLY);

                            if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()) {
                                com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                                if (economy.hasBalance(player, upgrade.getCost())) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Bought.Message")
                                                    .replace("%upgrade", is.getItemMeta().getDisplayName()));
                                    soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                                    economy.withdrawBalance(player, upgrade.getCost());
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.FLY,
                                            true);

                                    islandManager.updateFlightAtIsland(island);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Money.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                }
                            } else {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Upgrade.Exist.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            }
                        }
                    } else if ((is.getType() == Material.SPIDER_EYE) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Upgrade.Item.Drops.Displayname"))))) {
                        if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.DROPS)) {
                            island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.DROPS,
                                    !island.isUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.DROPS));

                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else {
                            List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                    .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.DROPS);

                            if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()) {
                                com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                                if (economy.hasBalance(player, upgrade.getCost())) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Bought.Message")
                                                    .replace("%upgrade", is.getItemMeta().getDisplayName()));
                                    soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                                    economy.withdrawBalance(player, upgrade.getCost());
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.DROPS,
                                            true);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Money.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                }
                            } else {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Upgrade.Exist.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            }
                        }
                    } else if ((is.getType() == Material.BOOKSHELF) && (is.hasItemMeta())) {
                        List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.MEMBERS);

                        if (upgrades != null && !upgrades.isEmpty()) {
                            for (int i = 0; i < upgrades.size(); ++i) {
                                com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(i);
                                int tier = i + 1;

                                if (is.getItemMeta().getDisplayName()
                                        .equals(ChatColor.translateAlternateColorCodes('&',
                                                configLoad.getString("Menu.Upgrade.Item.Members.Displayname")
                                                        .replace("%tier", "" + tier)))) {
                                    if (upgrade.getValue() > island.getMaxMembers(player)
                                            && upgrade.getValue() != island.getMaxMembers(player)) {
                                        if (economy.hasBalance(player, upgrade.getCost())) {
                                            messageManager.sendMessage(player,
                                                    configLoad.getString("Island.Upgrade.Bought.Message").replace(
                                                            "%upgrade", is.getItemMeta().getDisplayName()));
                                            soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F,
                                                    1.0F);

                                            economy.withdrawBalance(player, upgrade.getCost());
                                            island.setMaxMembers(upgrade.getValue());

                                            Bukkit.getServer().getPluginManager().callEvent(new IslandUpgradeEvent(
                                                    island.getAPIWrapper(), player, APIUtil.fromImplementation(
                                                    com.songoda.skyblock.upgrade.Upgrade.Type.MEMBERS)));

                                            Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                    () -> open(player), 1L);
                                        } else {
                                            messageManager.sendMessage(player,
                                                    configLoad.getString("Island.Upgrade.Money.Message"));
                                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                    1.0F);

                                            event.setWillClose(false);
                                            event.setWillDestroy(false);
                                        }

                                        return;
                                    }
                                }
                            }

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Upgrade.Claimed.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            event.setWillClose(false);
                            event.setWillDestroy(false);
                        }
                    } else if ((is.getType() == Material.BEACON) && (is.hasItemMeta())) {
                        List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.SIZE);

                        if (upgrades != null && !upgrades.isEmpty()) {
                            for (int i = 0; i < upgrades.size(); ++i) {
                                com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(i);
                                int tier = i + 1;

                                if (is.getItemMeta().getDisplayName()
                                        .equals(ChatColor.translateAlternateColorCodes('&',
                                                configLoad.getString("Menu.Upgrade.Item.Size.Displayname")
                                                        .replace("%tier", "" + tier)))) {
                                    if (upgrade.getValue() > island.getSize()
                                            && upgrade.getValue() != island.getSize()) {
                                        if (economy.hasBalance(player, upgrade.getCost())) {
                                            messageManager.sendMessage(player,
                                                    configLoad.getString("Island.Upgrade.Bought.Message").replace(
                                                            "%upgrade", is.getItemMeta().getDisplayName()));
                                            soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F,
                                                    1.0F);

                                            economy.withdrawBalance(player, upgrade.getCost());
                                            island.setSize(upgrade.getValue());
                                            islandManager.updateBorder(island);

                                            Bukkit.getServer().getPluginManager().callEvent(new IslandUpgradeEvent(
                                                    island.getAPIWrapper(), player, APIUtil.fromImplementation(
                                                    com.songoda.skyblock.upgrade.Upgrade.Type.SIZE)));

                                            Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                                    () -> open(player), 1L);
                                        } else {
                                            messageManager.sendMessage(player,
                                                    configLoad.getString("Island.Upgrade.Money.Message"));
                                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                    1.0F);

                                            event.setWillClose(false);
                                            event.setWillDestroy(false);
                                        }

                                        return;
                                    }
                                }
                            }

                            messageManager.sendMessage(player,
                                    configLoad.getString("Island.Upgrade.Claimed.Message"));
                            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                            event.setWillClose(false);
                            event.setWillDestroy(false);
                        }
                    } else if ((is.getType() == CompatibleMaterial.SPAWNER.getMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            configLoad.getString("Menu.Upgrade.Item.Spawner.Displayname"))))) {
                        if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.SPAWNER)) {
                            island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.SPAWNER,
                                    !island.isUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.SPAWNER));

                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else {
                            List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                    .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.SPAWNER);

                            if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()) {
                                com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                                if (economy.hasBalance(player, upgrade.getCost())) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Bought.Message")
                                                    .replace("%upgrade", is.getItemMeta().getDisplayName()));
                                    soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                                    economy.withdrawBalance(player, upgrade.getCost());
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.SPAWNER,
                                            true);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> open(player), 1L);
                                } else {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Money.Message"));
                                    soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                    event.setWillClose(false);
                                    event.setWillDestroy(false);
                                }
                            } else {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Upgrade.Exist.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            }
                        }
                    }
                }
            });

            List<com.songoda.skyblock.upgrade.Upgrade> upgrades;

            ItemStack potion = new ItemStack(Material.POTION);

            if (player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.SPEED.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.SPEED);

                if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()) {
                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
                        PotionMeta pm = (PotionMeta) potion.getItemMeta();

                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_10)) {
                            pm.setBasePotionData(new PotionData(PotionType.SPEED));
                        } else {
                            pm.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1, 0), true);
                        }

                        potion.setItemMeta(pm);
                    } else {
                        potion = new ItemStack(Material.POTION, 1, (short) 8194);
                    }

                    if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.SPEED)) {
                        nInv.addItem(nInv.createItem(potion,
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Upgrade.Item.Speed.Displayname")),
                                configLoad.getStringList("Menu.Upgrade.Item.Speed.Claimed.Lore"),
                                new Placeholder[]{
                                        new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                        new Placeholder("%status",
                                                getStatus(island, com.songoda.skyblock.upgrade.Upgrade.Type.SPEED))},
                                null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 0);
                    } else {
                        if (economy.hasBalance(player, upgrade.getCost())) {
                            nInv.addItem(nInv.createItem(potion,
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Speed.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Speed.Claimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost()))},
                                    null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 0);
                        } else {
                            nInv.addItem(nInv.createItem(potion,
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Speed.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Speed.Unclaimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost()))},
                                    null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 0);
                        }
                    }
                }
            }

            if (player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.JUMP.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.JUMP);

                if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()) {
                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                    if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_8)) {
                        potion = new ItemStack(Material.POTION);
                        PotionMeta pm = (PotionMeta) potion.getItemMeta();

                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) {
                            pm.setBasePotionData(new PotionData(PotionType.JUMP));
                        } else {
                            pm.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 1, 0), true);
                        }

                        potion.setItemMeta(pm);
                    } else {
                        potion = new ItemStack(Material.POTION, 1, (short) 8203);
                    }

                    if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.JUMP)) {
                        nInv.addItem(nInv.createItem(potion,
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Upgrade.Item.Jump.Displayname")),
                                configLoad.getStringList("Menu.Upgrade.Item.Jump.Claimed.Lore"),
                                new Placeholder[]{
                                        new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                        new Placeholder("%status",
                                                getStatus(island, com.songoda.skyblock.upgrade.Upgrade.Type.JUMP))},
                                null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 1);
                    } else {
                        if (economy.hasBalance(player, upgrade.getCost())) {
                            nInv.addItem(nInv.createItem(potion,
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Jump.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Jump.Claimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost()))},
                                    null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 1);
                        } else {
                            nInv.addItem(nInv.createItem(potion,
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Jump.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Jump.Unclaimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost()))},
                                    null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 1);
                        }
                    }
                }
            }

            if (player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.CROP.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.CROP);

                if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()) {
                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                    if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.CROP)) {
                        nInv.addItem(nInv.createItem(CompatibleMaterial.WHEAT_SEEDS.getItem(),
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Upgrade.Item.Crop.Displayname")),
                                configLoad.getStringList("Menu.Upgrade.Item.Crop.Claimed.Lore"),
                                new Placeholder[]{
                                        new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                        new Placeholder("%status",
                                                getStatus(island, com.songoda.skyblock.upgrade.Upgrade.Type.CROP))},
                                null, null), 3);
                    } else {
                        if (economy.hasBalance(player, upgrade.getCost())) {
                            nInv.addItem(nInv.createItem(CompatibleMaterial.WHEAT_SEEDS.getItem(),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Crop.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Crop.Claimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost()))},
                                    null, null), 3);
                        } else {
                            nInv.addItem(nInv.createItem(CompatibleMaterial.WHEAT_SEEDS.getItem(),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Crop.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Crop.Unclaimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost()))},
                                    null, null), 3);
                        }
                    }
                }
            }

            if (player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.FLY.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.FLY);

                if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()) {
                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                    if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.FLY)) {
                        nInv.addItem(nInv.createItem(new ItemStack(Material.FEATHER),
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Upgrade.Item.Fly.Displayname")),
                                configLoad.getStringList("Menu.Upgrade.Item.Fly.Claimed.Lore"),
                                new Placeholder[]{
                                        new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                        new Placeholder("%status",
                                                getStatus(island, com.songoda.skyblock.upgrade.Upgrade.Type.FLY))},
                                null, null), 4);
                    } else {
                        if (economy.hasBalance(player, upgrade.getCost())) {
                            nInv.addItem(nInv.createItem(new ItemStack(Material.FEATHER),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Fly.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Fly.Claimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost()))},
                                    null, null), 4);
                        } else {
                            nInv.addItem(nInv.createItem(new ItemStack(Material.FEATHER),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Fly.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Fly.Unclaimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost()))},
                                    null, null), 4);
                        }
                    }
                }
            }

            if (player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.DROPS.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.DROPS);

                if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()) {
                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                    if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.DROPS)) {
                        nInv.addItem(nInv.createItem(new ItemStack(Material.SPIDER_EYE),
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Upgrade.Item.Drops.Displayname")),
                                configLoad.getStringList("Menu.Upgrade.Item.Drops.Claimed.Lore"),
                                new Placeholder[]{
                                        new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                        new Placeholder("%status",
                                                getStatus(island, com.songoda.skyblock.upgrade.Upgrade.Type.DROPS))},
                                null, null), 5);
                    } else {
                        if (economy.hasBalance(player, upgrade.getCost())) {
                            nInv.addItem(nInv.createItem(new ItemStack(Material.SPIDER_EYE),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Drops.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Drops.Claimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost()))},
                                    null, null), 5);
                        } else {
                            nInv.addItem(nInv.createItem(new ItemStack(Material.SPIDER_EYE),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Drops.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Drops.Unclaimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost()))},
                                    null, null), 5);
                        }
                    }
                }
            }

            if (player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.MEMBERS.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.MEMBERS);

                if (upgrades != null && !upgrades.isEmpty()) {
                    for (int i = 0; i < upgrades.size(); ++i) {
                        com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(i);
                        int tier = i + 1;

                        if (tier != upgrades.size()) {
                            if (upgrade.getValue() <= island.getMaxMembers(player)) {
                                continue;
                            }
                        }

                        if (island.getMaxMembers(player) >= upgrade.getValue()) {
                            nInv.addItem(nInv.createItem(new ItemStack(Material.BOOKSHELF),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Members.Displayname").replace("%tier",
                                                    "" + tier)),
                                    configLoad.getStringList("Menu.Upgrade.Item.Members.Claimed.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                            new Placeholder("%tier", "" + tier),
                                            new Placeholder("%maxMembers", "" + upgrade.getValue())},
                                    null, null), 6);
                        } else {
                            if (economy.hasBalance(player, upgrade.getCost())) {
                                nInv.addItem(
                                        nInv.createItem(new ItemStack(Material.BOOKSHELF),
                                                ChatColor.translateAlternateColorCodes('&',
                                                        configLoad.getString("Menu.Upgrade.Item.Members.Displayname")
                                                                .replace("%tier", "" + tier)),
                                                configLoad.getStringList("Menu.Upgrade.Item.Members.Claimable.Lore"),
                                                new Placeholder[]{
                                                        new Placeholder("%cost",
                                                                NumberUtils.formatNumber(upgrade.getCost())),
                                                        new Placeholder("%tier", "" + tier),
                                                        new Placeholder("%maxMembers", "" + upgrade.getValue())},
                                                null, null),
                                        6);
                            } else {
                                nInv.addItem(
                                        nInv.createItem(new ItemStack(Material.BOOKSHELF),
                                                ChatColor.translateAlternateColorCodes('&',
                                                        configLoad.getString("Menu.Upgrade.Item.Members.Displayname")
                                                                .replace("%tier", "" + tier)),
                                                configLoad.getStringList("Menu.Upgrade.Item.Members.Unclaimable.Lore"),
                                                new Placeholder[]{
                                                        new Placeholder("%cost",
                                                                NumberUtils.formatNumber(upgrade.getCost())),
                                                        new Placeholder("%tier", "" + tier),
                                                        new Placeholder("%maxMembers", "" + upgrade.getValue())},
                                                null, null),
                                        6);
                            }
                        }

                        break;
                    }
                }
            }

            if (player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.SIZE.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.SIZE);

                if (upgrades != null && !upgrades.isEmpty()) {
                    for (int i = 0; i < upgrades.size(); ++i) {
                        com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(i);
                        int tier = i + 1;

                        if (tier != upgrades.size()) {
                            if (upgrade.getValue() <= island.getSize()) {
                                continue;
                            }
                        }

                        if (island.getSize() >= upgrade.getValue()) {
                            nInv.addItem(nInv.createItem(new ItemStack(Material.BEACON),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Size.Displayname").replace("%tier",
                                                    "" + tier)),
                                    configLoad.getStringList("Menu.Upgrade.Item.Size.Claimed.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                            new Placeholder("%tier", "" + tier),
                                            new Placeholder("%size", "" + upgrade.getValue())},
                                    null, null), 7);
                        } else {
                            if (economy.hasBalance(player, upgrade.getCost())) {
                                nInv.addItem(
                                        nInv.createItem(new ItemStack(Material.BEACON),
                                                ChatColor.translateAlternateColorCodes('&',
                                                        configLoad.getString("Menu.Upgrade.Item.Size.Displayname")
                                                                .replace("%tier", "" + tier)),
                                                configLoad.getStringList("Menu.Upgrade.Item.Size.Claimable.Lore"),
                                                new Placeholder[]{
                                                        new Placeholder("%cost",
                                                                NumberUtils.formatNumber(upgrade.getCost())),
                                                        new Placeholder("%tier", "" + tier),
                                                        new Placeholder("%size", "" + upgrade.getValue())},
                                                null, null),
                                        7);
                            } else {
                                nInv.addItem(
                                        nInv.createItem(new ItemStack(Material.BEACON),
                                                ChatColor.translateAlternateColorCodes('&',
                                                        configLoad.getString("Menu.Upgrade.Item.Size.Displayname")
                                                                .replace("%tier", "" + tier)),
                                                configLoad.getStringList("Menu.Upgrade.Item.Size.Unclaimable.Lore"),
                                                new Placeholder[]{
                                                        new Placeholder("%cost",
                                                                NumberUtils.formatNumber(upgrade.getCost())),
                                                        new Placeholder("%tier", "" + tier),
                                                        new Placeholder("%size", "" + upgrade.getValue())},
                                                null, null),
                                        7);
                            }
                        }

                        break;
                    }
                }
            }

            if (player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.SPAWNER.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.SPAWNER);

                if (upgrades != null && !upgrades.isEmpty() && upgrades.get(0).isEnabled()) {
                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                    if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.SPAWNER)) {
                        nInv.addItem(nInv.createItem(CompatibleMaterial.SPAWNER.getItem(),
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Upgrade.Item.Spawner.Displayname")),
                                configLoad.getStringList("Menu.Upgrade.Item.Spawner.Claimed.Lore"),
                                new Placeholder[]{
                                        new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                        new Placeholder("%status",
                                                getStatus(island, com.songoda.skyblock.upgrade.Upgrade.Type.SPAWNER))},
                                null, null), 8);
                    } else {
                        if (economy.hasBalance(player, upgrade.getCost())) {
                            nInv.addItem(nInv.createItem(CompatibleMaterial.SPAWNER.getItem(),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Spawner.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Spawner.Claimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost()))},
                                    null, null), 8);
                        } else {
                            nInv.addItem(nInv.createItem(CompatibleMaterial.SPAWNER.getItem(),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Spawner.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Spawner.Unclaimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost()))},
                                    null, null), 8);
                        }
                    }
                }
            }

            nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.Upgrade.Title")));
            nInv.setRows(1);
            nInv.open();
        }
    }

    private String getStatus(Island island, com.songoda.skyblock.upgrade.Upgrade.Type type) {
        FileConfiguration configLoad = SkyBlock.getPlugin(SkyBlock.class).getLanguage();
        if (island.isUpgrade(type)) {
            return configLoad.getString("Menu.Upgrade.Item.Word.Disable");
        } else {
            return configLoad.getString("Menu.Upgrade.Item.Word.Enable");
        }
    }
}
