package com.songoda.skyblock.menus;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.api.event.island.IslandUpgradeEvent;
import com.songoda.skyblock.api.utils.APIUtil;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandManager;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.upgrade.UpgradeManager;
import com.songoda.skyblock.utils.NumberUtil;
import com.songoda.skyblock.utils.item.nInventoryUtil;

import com.songoda.skyblock.utils.version.NMSUtil;
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

import java.io.File;
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
        SkyBlock skyblock = SkyBlock.getInstance();

        PlayerDataManager playerDataManager = skyblock.getPlayerDataManager();
        MessageManager messageManager = skyblock.getMessageManager();
        UpgradeManager upgradeManager = skyblock.getUpgradeManager();
        IslandManager islandManager = skyblock.getIslandManager();
        SoundManager soundManager = skyblock.getSoundManager();
        FileManager fileManager = skyblock.getFileManager();

        FileConfiguration configLoad = fileManager.getConfig(new File(skyblock.getDataFolder(), "language.yml"))
                .getFileConfiguration();

        if (!EconomyManager.isEnabled()) {
            messageManager.sendMessage(player, configLoad.getString("Island.Upgrade.Disabled.Message"));
            soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

            return;
        }

        if (playerDataManager.hasPlayerData(player) && playerDataManager.getPlayerData(player).getOwner() != null) {
            Island island = islandManager.getIsland(player);

            nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                if (!EconomyManager.isEnabled()) {
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
                        if (is.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                                configLoad.getString("Menu.Upgrade.Item.Speed.Displayname")))) {
                            if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Speed)) {
                                if (island.isUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Speed)) {
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Speed,
                                            false);

                                    for (Player all : islandManager.getPlayersAtIsland(island)) {
                                        all.removePotionEffect(PotionEffectType.SPEED);
                                    }
                                } else {
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Speed,
                                            true);
                                }

                                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                        () -> open(player), 1L);
                            } else {
                                List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                        .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Speed);

                                if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
                                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                                    if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Upgrade.Bought.Message")
                                                        .replace("%upgrade", is.getItemMeta().getDisplayName()));
                                        soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                                        EconomyManager.withdrawBalance(player, upgrade.getCost());
                                        island.setUpgrade(player,
                                                com.songoda.skyblock.upgrade.Upgrade.Type.Speed, true);

                                        Bukkit.getServer().getScheduler().runTaskLater(skyblock,
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
                            if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Jump)) {
                                if (island.isUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Jump)) {
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Jump,
                                            false);

                                    for (Player all : islandManager.getPlayersAtIsland(island)) {
                                        all.removePotionEffect(PotionEffectType.JUMP);
                                    }
                                } else {
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Jump,
                                            true);
                                }

                                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(skyblock,
                                        () -> open(player), 1L);
                            } else {
                                List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                        .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Jump);

                                if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
                                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                                    if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                                        messageManager.sendMessage(player,
                                                configLoad.getString("Island.Upgrade.Bought.Message")
                                                        .replace("%upgrade", is.getItemMeta().getDisplayName()));
                                        soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                                        EconomyManager.withdrawBalance(player, upgrade.getCost());
                                        island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Jump,
                                                true);

                                        Bukkit.getServer().getScheduler().runTaskLater(skyblock,
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
                        if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Crop)) {
                            if (island.isUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Crop)) {
                                island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Crop, false);
                            } else {
                                island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Crop, true);
                            }

                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                        } else {
                            List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                    .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Crop);

                            if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
                                com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                                if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Bought.Message")
                                                    .replace("%upgrade", is.getItemMeta().getDisplayName()));
                                    soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                                    EconomyManager.withdrawBalance(player, upgrade.getCost());
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Crop,
                                            true);

                                    Bukkit.getServer().getScheduler().runTaskLater(skyblock,
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
                        if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Fly)) {
                            if (island.isUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Fly)) {
                                island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Fly, false);
                                islandManager.updateFlightAtIsland(island);
                            } else {
                                island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Fly, true);
                                islandManager.updateFlightAtIsland(island);
                            }

                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                        } else {
                            List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                    .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Fly);

                            if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
                                com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                                if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Bought.Message")
                                                    .replace("%upgrade", is.getItemMeta().getDisplayName()));
                                    soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                                    EconomyManager.withdrawBalance(player, upgrade.getCost());
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Fly,
                                            true);

                                    islandManager.updateFlightAtIsland(island);

                                    Bukkit.getServer().getScheduler().runTaskLater(skyblock,
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
                        if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Drops)) {
                            if (island.isUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Drops)) {
                                island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Drops,
                                        false);
                            } else {
                                island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Drops, true);
                            }

                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                        } else {
                            List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                    .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Drops);

                            if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
                                com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                                if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Bought.Message")
                                                    .replace("%upgrade", is.getItemMeta().getDisplayName()));
                                    soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                                    EconomyManager.withdrawBalance(player, upgrade.getCost());
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Drops,
                                            true);

                                    Bukkit.getServer().getScheduler().runTaskLater(skyblock,
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
                                .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Members);

                        if (upgrades != null && upgrades.size() > 0) {
                            for (int i = 0; i < upgrades.size(); i++) {
                                com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(i);
                                int tier = i + 1;

                                if (is.getItemMeta().getDisplayName()
                                        .equals(ChatColor.translateAlternateColorCodes('&',
                                                configLoad.getString("Menu.Upgrade.Item.Members.Displayname")
                                                        .replace("%tier", "" + tier)))) {
                                    if (upgrade.getValue() > island.getMaxMembers()
                                            && upgrade.getValue() != island.getMaxMembers()) {
                                        if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                                            messageManager.sendMessage(player,
                                                    configLoad.getString("Island.Upgrade.Bought.Message").replace(
                                                            "%upgrade", is.getItemMeta().getDisplayName()));
                                            soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F,
                                                    1.0F);

                                            EconomyManager.withdrawBalance(player, upgrade.getCost());
                                            island.setMaxMembers(upgrade.getValue());

                                            Bukkit.getServer().getPluginManager().callEvent(new IslandUpgradeEvent(
                                                    island.getAPIWrapper(), player, APIUtil.fromImplementation(
                                                    com.songoda.skyblock.upgrade.Upgrade.Type.Members)));

                                            Bukkit.getServer().getScheduler().runTaskLater(skyblock,
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
                                .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Size);

                        if (upgrades != null && upgrades.size() > 0) {
                            for (int i = 0; i < upgrades.size(); i++) {
                                com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(i);
                                int tier = i + 1;

                                if (is.getItemMeta().getDisplayName()
                                        .equals(ChatColor.translateAlternateColorCodes('&',
                                                configLoad.getString("Menu.Upgrade.Item.Size.Displayname")
                                                        .replace("%tier", "" + tier)))) {
                                    if (upgrade.getValue() > island.getSize()
                                            && upgrade.getValue() != island.getSize()) {
                                        if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                                            messageManager.sendMessage(player,
                                                    configLoad.getString("Island.Upgrade.Bought.Message").replace(
                                                            "%upgrade", is.getItemMeta().getDisplayName()));
                                            soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F,
                                                    1.0F);

                                            EconomyManager.withdrawBalance(player, upgrade.getCost());
                                            island.setSize(upgrade.getValue());
                                            islandManager.updateBorder(island);

                                            Bukkit.getServer().getPluginManager().callEvent(new IslandUpgradeEvent(
                                                    island.getAPIWrapper(), player, APIUtil.fromImplementation(
                                                    com.songoda.skyblock.upgrade.Upgrade.Type.Size)));

                                            Bukkit.getServer().getScheduler().runTaskLater(skyblock,
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
                        if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Spawner)) {
                            if (island.isUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Spawner)) {
                                island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Spawner,
                                        false);
                            } else {
                                island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Spawner,
                                        true);
                            }

                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(skyblock, () -> open(player), 1L);
                        } else {
                            List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                    .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Spawner);

                            if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
                                com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);

                                if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                                    messageManager.sendMessage(player,
                                            configLoad.getString("Island.Upgrade.Bought.Message")
                                                    .replace("%upgrade", is.getItemMeta().getDisplayName()));
                                    soundManager.playSound(player, CompatibleSound.ENTITY_PLAYER_LEVELUP.getSound(), 1.0F, 1.0F);

                                    EconomyManager.withdrawBalance(player, upgrade.getCost());
                                    island.setUpgrade(player, com.songoda.skyblock.upgrade.Upgrade.Type.Spawner,
                                            true);

                                    Bukkit.getServer().getScheduler().runTaskLater(skyblock,
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
            int NMSVersion = NMSUtil.getVersionNumber();

            if(player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.Speed.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Speed);
    
                if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);
        
                    if (NMSVersion > 8) {
                        PotionMeta pm = (PotionMeta) potion.getItemMeta();
            
                        if (NMSVersion > 9) {
                            pm.setBasePotionData(new PotionData(PotionType.SPEED));
                        } else {
                            pm.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1, 0), true);
                        }
            
                        potion.setItemMeta(pm);
                    } else {
                        potion = new ItemStack(Material.POTION, 1, (short) 8194);
                    }
        
                    if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Speed)) {
                        nInv.addItem(nInv.createItem(potion,
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Upgrade.Item.Speed.Displayname")),
                                configLoad.getStringList("Menu.Upgrade.Item.Speed.Claimed.Lore"),
                                new Placeholder[]{
                                        new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
                                        new Placeholder("%status",
                                                getStatus(island, com.songoda.skyblock.upgrade.Upgrade.Type.Speed))},
                                null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 0);
                    } else {
                        if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                            nInv.addItem(nInv.createItem(potion,
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Speed.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Speed.Claimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost()))},
                                    null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 0);
                        } else {
                            nInv.addItem(nInv.createItem(potion,
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Speed.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Speed.Unclaimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost()))},
                                    null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 0);
                        }
                    }
                }
            }
    
            if(player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.Jump.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Jump);
    
                if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);
        
                    if (NMSVersion > 8) {
                        potion = new ItemStack(Material.POTION);
                        PotionMeta pm = (PotionMeta) potion.getItemMeta();
            
                        if (NMSVersion > 9) {
                            pm.setBasePotionData(new PotionData(PotionType.JUMP));
                        } else {
                            pm.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 1, 0), true);
                        }
            
                        potion.setItemMeta(pm);
                    } else {
                        potion = new ItemStack(Material.POTION, 1, (short) 8203);
                    }
        
                    if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Jump)) {
                        nInv.addItem(nInv.createItem(potion,
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Upgrade.Item.Jump.Displayname")),
                                configLoad.getStringList("Menu.Upgrade.Item.Jump.Claimed.Lore"),
                                new Placeholder[]{
                                        new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
                                        new Placeholder("%status",
                                                getStatus(island, com.songoda.skyblock.upgrade.Upgrade.Type.Jump))},
                                null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 1);
                    } else {
                        if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                            nInv.addItem(nInv.createItem(potion,
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Jump.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Jump.Claimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost()))},
                                    null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 1);
                        } else {
                            nInv.addItem(nInv.createItem(potion,
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Jump.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Jump.Unclaimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost()))},
                                    null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 1);
                        }
                    }
                }
            }
    
            if(player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.Crop.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Crop);
    
                if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);
        
                    if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Crop)) {
                        nInv.addItem(nInv.createItem(CompatibleMaterial.WHEAT_SEEDS.getItem(),
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Upgrade.Item.Crop.Displayname")),
                                configLoad.getStringList("Menu.Upgrade.Item.Crop.Claimed.Lore"),
                                new Placeholder[]{
                                        new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
                                        new Placeholder("%status",
                                                getStatus(island, com.songoda.skyblock.upgrade.Upgrade.Type.Crop))},
                                null, null), 3);
                    } else {
                        if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                            nInv.addItem(nInv.createItem(CompatibleMaterial.WHEAT_SEEDS.getItem(),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Crop.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Crop.Claimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost()))},
                                    null, null), 3);
                        } else {
                            nInv.addItem(nInv.createItem(CompatibleMaterial.WHEAT_SEEDS.getItem(),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Crop.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Crop.Unclaimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost()))},
                                    null, null), 3);
                        }
                    }
                }
            }
    
            if(player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.Fly.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Fly);
    
                if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);
        
                    if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Fly)) {
                        nInv.addItem(nInv.createItem(new ItemStack(Material.FEATHER),
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Upgrade.Item.Fly.Displayname")),
                                configLoad.getStringList("Menu.Upgrade.Item.Fly.Claimed.Lore"),
                                new Placeholder[]{
                                        new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
                                        new Placeholder("%status",
                                                getStatus(island, com.songoda.skyblock.upgrade.Upgrade.Type.Fly))},
                                null, null), 4);
                    } else {
                        if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                            nInv.addItem(nInv.createItem(new ItemStack(Material.FEATHER),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Fly.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Fly.Claimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost()))},
                                    null, null), 4);
                        } else {
                            nInv.addItem(nInv.createItem(new ItemStack(Material.FEATHER),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Fly.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Fly.Unclaimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost()))},
                                    null, null), 4);
                        }
                    }
                }
            }
    
            if(player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.Drops.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Drops);
    
                if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);
        
                    if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Drops)) {
                        nInv.addItem(nInv.createItem(new ItemStack(Material.SPIDER_EYE),
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Upgrade.Item.Drops.Displayname")),
                                configLoad.getStringList("Menu.Upgrade.Item.Drops.Claimed.Lore"),
                                new Placeholder[]{
                                        new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
                                        new Placeholder("%status",
                                                getStatus(island, com.songoda.skyblock.upgrade.Upgrade.Type.Drops))},
                                null, null), 5);
                    } else {
                        if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                            nInv.addItem(nInv.createItem(new ItemStack(Material.SPIDER_EYE),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Drops.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Drops.Claimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost()))},
                                    null, null), 5);
                        } else {
                            nInv.addItem(nInv.createItem(new ItemStack(Material.SPIDER_EYE),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Drops.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Drops.Unclaimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost()))},
                                    null, null), 5);
                        }
                    }
                }
            }
    
            if(player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.Members.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Members);
    
                if (upgrades != null && upgrades.size() > 0) {
                    for (int i = 0; i < upgrades.size(); i++) {
                        com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(i);
                        int tier = i + 1;
            
                        if (tier != upgrades.size()) {
                            if (upgrade.getValue() <= island.getMaxMembers()) {
                                continue;
                            }
                        }
            
                        if (island.getMaxMembers() >= upgrade.getValue()) {
                            nInv.addItem(nInv.createItem(new ItemStack(Material.BOOKSHELF),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Members.Displayname").replace("%tier",
                                                    "" + tier)),
                                    configLoad.getStringList("Menu.Upgrade.Item.Members.Claimed.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
                                            new Placeholder("%tier", "" + tier),
                                            new Placeholder("%maxMembers", "" + upgrade.getValue())},
                                    null, null), 6);
                        } else {
                            if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                                nInv.addItem(
                                        nInv.createItem(new ItemStack(Material.BOOKSHELF),
                                                ChatColor.translateAlternateColorCodes('&',
                                                        configLoad.getString("Menu.Upgrade.Item.Members.Displayname")
                                                                .replace("%tier", "" + tier)),
                                                configLoad.getStringList("Menu.Upgrade.Item.Members.Claimable.Lore"),
                                                new Placeholder[]{
                                                        new Placeholder("%cost",
                                                                NumberUtil.formatNumberByDecimal(upgrade.getCost())),
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
                                                                NumberUtil.formatNumberByDecimal(upgrade.getCost())),
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
    
            if(player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.Size.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Size);
    
                if (upgrades != null && upgrades.size() > 0) {
                    for (int i = 0; i < upgrades.size(); i++) {
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
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
                                            new Placeholder("%tier", "" + tier),
                                            new Placeholder("%size", "" + upgrade.getValue())},
                                    null, null), 7);
                        } else {
                            if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                                nInv.addItem(
                                        nInv.createItem(new ItemStack(Material.BEACON),
                                                ChatColor.translateAlternateColorCodes('&',
                                                        configLoad.getString("Menu.Upgrade.Item.Size.Displayname")
                                                                .replace("%tier", "" + tier)),
                                                configLoad.getStringList("Menu.Upgrade.Item.Size.Claimable.Lore"),
                                                new Placeholder[]{
                                                        new Placeholder("%cost",
                                                                NumberUtil.formatNumberByDecimal(upgrade.getCost())),
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
                                                                NumberUtil.formatNumberByDecimal(upgrade.getCost())),
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
    
            if(player.hasPermission("fabledskyblock.upgrade." + com.songoda.skyblock.upgrade.Upgrade.Type.Spawner.name().toLowerCase())) {
                upgrades = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Spawner);
    
                if (upgrades != null && upgrades.size() > 0 && upgrades.get(0).isEnabled()) {
                    com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(0);
        
                    if (island.hasUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Spawner)) {
                        nInv.addItem(nInv.createItem(CompatibleMaterial.SPAWNER.getItem(),
                                ChatColor.translateAlternateColorCodes('&',
                                        configLoad.getString("Menu.Upgrade.Item.Spawner.Displayname")),
                                configLoad.getStringList("Menu.Upgrade.Item.Spawner.Claimed.Lore"),
                                new Placeholder[]{
                                        new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost())),
                                        new Placeholder("%status",
                                                getStatus(island, com.songoda.skyblock.upgrade.Upgrade.Type.Spawner))},
                                null, null), 8);
                    } else {
                        if (EconomyManager.hasBalance(player, upgrade.getCost())) {
                            nInv.addItem(nInv.createItem(CompatibleMaterial.SPAWNER.getItem(),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Spawner.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Spawner.Claimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost()))},
                                    null, null), 8);
                        } else {
                            nInv.addItem(nInv.createItem(CompatibleMaterial.SPAWNER.getItem(),
                                    ChatColor.translateAlternateColorCodes('&',
                                            configLoad.getString("Menu.Upgrade.Item.Spawner.Displayname")),
                                    configLoad.getStringList("Menu.Upgrade.Item.Spawner.Unclaimable.Lore"),
                                    new Placeholder[]{
                                            new Placeholder("%cost", NumberUtil.formatNumberByDecimal(upgrade.getCost()))},
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
        SkyBlock skyblock = SkyBlock.getInstance();
        FileConfiguration configLoad = skyblock.getFileManager()
                .getConfig(new File(skyblock.getDataFolder(), "language.yml")).getFileConfiguration();
        String upgradeStatus;

        if (island.isUpgrade(type)) {
            upgradeStatus = configLoad.getString("Menu.Upgrade.Item.Word.Disable");
        } else {
            upgradeStatus = configLoad.getString("Menu.Upgrade.Item.Word.Enable");
        }

        return upgradeStatus;
    }
}
