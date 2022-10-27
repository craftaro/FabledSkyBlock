package com.songoda.skyblock.menus.admin;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.utils.NumberUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.message.MessageManager;
import com.songoda.skyblock.placeholder.Placeholder;
import com.songoda.skyblock.playerdata.PlayerData;
import com.songoda.skyblock.playerdata.PlayerDataManager;
import com.songoda.skyblock.sound.SoundManager;
import com.songoda.skyblock.upgrade.UpgradeManager;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Upgrade {

    private static Upgrade instance;

    public static Upgrade getInstance() {
        if (instance == null) {
            instance = new Upgrade();
        }

        return instance;
    }

    @SuppressWarnings("deprecation")
    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getInstance();

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        MessageManager messageManager = plugin.getMessageManager();
        UpgradeManager upgradeManager = plugin.getUpgradeManager();
        SoundManager soundManager = plugin.getSoundManager();
        FileManager fileManager = plugin.getFileManager();

        if (playerDataManager.isPlayerDataLoaded(player) && playerDataManager.getPlayerData(player).getViewer() != null) {
            FileConfiguration configLoad = plugin.getLanguage();
            Viewer viewer = (Upgrade.Viewer) playerDataManager.getPlayerData(player).getViewer();

            if (viewer == null || viewer.getType() == Upgrade.Viewer.Type.Upgrades) {
                nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                    if (!(player.hasPermission("fabledskyblock.admin.upgrade") || player.hasPermission("fabledskyblock.admin.*")
                            || player.hasPermission("fabledskyblock.*"))) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Upgrade.Permission.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        return;
                    }

                    ItemStack is = event.getItem();
                    com.songoda.skyblock.upgrade.Upgrade upgrade = null;

                    if ((is.getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                            configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Exit.Displayname"))))) {
                        soundManager.playSound(player, CompatibleSound.BLOCK_CHEST_CLOSE.getSound(), 1.0F, 1.0F);

                        return;
                    } else if ((is.getType() == Material.POTION) && (is.hasItemMeta())) {
                        if (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Speed.Displayname")))) {
                            upgrade = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Speed)
                                    .get(0);
                            viewer.setUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Speed);
                        } else if (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Jump.Displayname")))) {
                            upgrade = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Jump)
                                    .get(0);
                            viewer.setUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Jump);
                        }
                    } else if ((is.getType() == CompatibleMaterial.WHEAT_SEEDS.getMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                            configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Crop.Displayname"))))) {
                        upgrade = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Crop)
                                .get(0);
                        viewer.setUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Crop);
                    } else if ((is.getType() == Material.FEATHER) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                            configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Fly.Displayname"))))) {
                        upgrade = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Fly)
                                .get(0);
                        viewer.setUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Fly);
                    } else if ((is.getType() == Material.SPIDER_EYE) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                            configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Drops.Displayname"))))) {
                        upgrade = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Drops)
                                .get(0);
                        viewer.setUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Drops);
                    } else if ((is.getType() == Material.BEACON) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                            configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Size.Displayname"))))) {
                        viewer.setType(Viewer.Type.Size);
                        viewer.setUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Size);

                        soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else if ((is.getType() == Material.BOOKSHELF) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName().equals(plugin.formatText(
                            configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Members.Displayname"))))) {
                        viewer.setType(Viewer.Type.Members);
                        viewer.setUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Members);

                        soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                    } else if ((is.getType() == CompatibleMaterial.SPAWNER.getMaterial()) && (is.hasItemMeta())
                            && (is.getItemMeta().getDisplayName()
                            .equals(plugin.formatText(configLoad
                                    .getString("Menu.Admin.Upgrade.Upgrades.Item.Spawner.Displayname"))))) {
                        upgrade = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Spawner)
                                .get(0);
                        viewer.setUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Spawner);
                    }

                    if (upgrade != null) {
                        if (event.getClick() == ClickType.LEFT) {
                            upgrade.setEnabled(!upgrade.isEnabled());

                            if (playerDataManager.isPlayerDataLoaded(player)) {
                                com.songoda.skyblock.upgrade.Upgrade.Type upgradeType = ((Viewer) playerDataManager
                                        .getPlayerData(player).getViewer()).getUpgrade();

                                boolean enabled = upgrade.isEnabled();
                                Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin,
                                        () -> {
                                            Config config = fileManager.getConfig(new File(
                                                    plugin.getDataFolder(), "upgrades.yml"));
                                            FileConfiguration configLoad1 = config
                                                    .getFileConfiguration();

                                            configLoad1.set(
                                                    "Upgrades." + upgradeType.name() + ".Enable",
                                                    enabled);

                                            try {
                                                configLoad1.save(config.getFile());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        });
                            }

                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else if (event.getClick() == ClickType.RIGHT) {
                            soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                                AnvilGui gui = new AnvilGui(player);
                                gui.setAction(event1 -> {

                                    if (!(player.hasPermission("fabledskyblock.admin.upgrade")
                                            || player.hasPermission("fabledskyblock.admin.*")
                                            || player.hasPermission("fabledskyblock.*"))) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Island.Admin.Upgrade.Permission.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(),
                                                1.0F, 1.0F);

                                        return;
                                    } else if (!(gui.getInputText().matches("[0-9]+")
                                            || gui.getInputText().matches("([0-9]*)\\.([0-9]{1,2}$)"))) {
                                        messageManager.sendMessage(player, configLoad
                                                .getString("Island.Admin.Upgrade.Numerical.Message"));
                                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(),
                                                1.0F, 1.0F);

                                        player.closeInventory();

                                        return;
                                    }

                                    if (playerDataManager.isPlayerDataLoaded(player)) {
                                        double upgradeCost = Double.valueOf(gui.getInputText());
                                        com.songoda.skyblock.upgrade.Upgrade.Type upgradeType = ((Viewer) playerDataManager
                                                .getPlayerData(player).getViewer()).getUpgrade();

                                        com.songoda.skyblock.upgrade.Upgrade upgrade1 = upgradeManager
                                                .getUpgrades(upgradeType).get(0);
                                        upgrade1.setCost(upgradeCost);
                                        soundManager.playSound(player, CompatibleSound.BLOCK_NOTE_BLOCK_PLING.getSound(),
                                                1.0F, 1.0F);

                                        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin,
                                                () -> {
                                                    Config config = fileManager.getConfig(new File(
                                                            plugin.getDataFolder(), "upgrades.yml"));
                                                    FileConfiguration configLoad1 = config
                                                            .getFileConfiguration();

                                                    configLoad1.set(
                                                            "Upgrades." + upgradeType.name() + ".Cost",
                                                            upgradeCost);

                                                    try {
                                                        configLoad1.save(config.getFile());
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                });

                                        Bukkit.getServer().getScheduler()
                                                .runTaskLater(plugin, () -> open(player), 1L);
                                    }
                                    player.closeInventory();
                                });

                                ItemStack is1 = new ItemStack(Material.NAME_TAG);
                                ItemMeta im = is1.getItemMeta();
                                im.setDisplayName(
                                        configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Word.Enter"));
                                is1.setItemMeta(im);

                                gui.setInput(is1);
                                plugin.getGuiManager().showGUI(player, gui);

                            }, 1L);
                        } else {
                            event.setWillClose(false);
                            event.setWillDestroy(false);
                        }
                    }
                });

                ItemStack speedPotion = new ItemStack(Material.POTION);
                ItemStack jumpPotion = new ItemStack(Material.POTION);
                com.songoda.skyblock.upgrade.Upgrade upgrade;

                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                    PotionMeta pm = (PotionMeta) speedPotion.getItemMeta();
                    pm.setBasePotionData(new PotionData(PotionType.SPEED));
                    speedPotion.setItemMeta(pm);
                } else {
                    speedPotion = new ItemStack(Material.POTION, 1, (short) 8194);
                }

                upgrade = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Speed).get(0);
                nInv.addItem(nInv.createItem(speedPotion,
                        plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Speed.Displayname")),
                        configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Speed.Lore"),
                        new Placeholder[]{
                                new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                new Placeholder("%status", getStatus(upgrade))},
                        null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 0);

                if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)) {
                    PotionMeta pm = (PotionMeta) jumpPotion.getItemMeta();
                    pm.setBasePotionData(new PotionData(PotionType.JUMP));
                    jumpPotion.setItemMeta(pm);
                } else {
                    jumpPotion = new ItemStack(Material.POTION, 1, (short) 8203);
                }

                upgrade = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Jump).get(0);
                nInv.addItem(nInv.createItem(jumpPotion,
                        plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Jump.Displayname")),
                        configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Jump.Lore"),
                        new Placeholder[]{
                                new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                new Placeholder("%status", getStatus(upgrade))},
                        null, new ItemFlag[]{ItemFlag.HIDE_POTION_EFFECTS}), 1);

                upgrade = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Crop).get(0);
                nInv.addItem(nInv.createItem(CompatibleMaterial.WHEAT_SEEDS.getItem(),
                        plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Crop.Displayname")),
                        configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Crop.Lore"),
                        new Placeholder[]{
                                new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                new Placeholder("%status", getStatus(upgrade))},
                        null, null), 2);

                upgrade = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Fly).get(0);
                nInv.addItem(nInv.createItem(new ItemStack(Material.FEATHER),
                        plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Fly.Displayname")),
                        configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Fly.Lore"),
                        new Placeholder[]{
                                new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                new Placeholder("%status", getStatus(upgrade))},
                        null, null), 3);

                upgrade = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Drops).get(0);
                nInv.addItem(nInv.createItem(new ItemStack(Material.SPIDER_EYE),
                        plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Drops.Displayname")),
                        configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Drops.Lore"),
                        new Placeholder[]{
                                new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                new Placeholder("%status", getStatus(upgrade))},
                        null, null), 4);

                // Size
                List<com.songoda.skyblock.upgrade.Upgrade> upgradesSize = upgradeManager
                        .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Size);
                int upgradeTiersSize = 0;

                if (upgradesSize != null) {
                    upgradeTiersSize = upgradesSize.size();
                }

                nInv.addItem(nInv.createItem(new ItemStack(Material.BEACON),
                        plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Size.Displayname")),
                        configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Size.Lore"),
                        new Placeholder[]{new Placeholder("%tiers", "" + upgradeTiersSize)}, null, null), 5);

                // Members
                List<com.songoda.skyblock.upgrade.Upgrade> upgradesMembers = upgradeManager
                        .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Members);
                int upgradeTiersMembers = 0;

                if (upgradesMembers != null) {
                    upgradeTiersMembers = upgradesMembers.size();
                }

                nInv.addItem(nInv.createItem(CompatibleMaterial.BOOKSHELF.getItem(), plugin.formatText(
                        configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Members.Displayname")),
                        configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Members.Lore"),
                        new Placeholder[]{new Placeholder("%tiers", "" + upgradeTiersMembers)}, null, null), 4);

                upgrade = upgradeManager.getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Spawner).get(0);
                nInv.addItem(nInv.createItem(CompatibleMaterial.SPAWNER.getItem(),
                        plugin.formatText(configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Spawner.Displayname")),
                        configLoad.getStringList("Menu.Admin.Upgrade.Upgrades.Item.Spawner.Lore"),
                        new Placeholder[]{
                                new Placeholder("%cost", NumberUtils.formatNumber(upgrade.getCost())),
                                new Placeholder("%status", getStatus(upgrade))},
                        null, null), 6);

                nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                        plugin.formatText(configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Exit.Displayname")),
                        null, null, null, null), 8);

                nInv.setTitle(plugin.formatText(configLoad.getString("Menu.Admin.Upgrade.Upgrades.Title")));
                nInv.setRows(1);

                Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
            } else if (viewer.getType() == Upgrade.Viewer.Type.Size) {
                nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                    if (!(player.hasPermission("fabledskyblock.admin.upgrade") || player.hasPermission("fabledskyblock.admin.*")
                            || player.hasPermission("fabledskyblock.*"))) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Upgrade.Permission.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        return;
                    }

                    if (playerDataManager.isPlayerDataLoaded(player)) {
                        PlayerData playerData = playerDataManager.getPlayerData(player);
                        ItemStack is = event.getItem();

                        if ((is.getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial()) && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName()
                                .equals(plugin.formatText(configLoad
                                        .getString("Menu.Admin.Upgrade.Size.Item.Return.Displayname"))))) {
                            playerData.setViewer(new Viewer(Viewer.Type.Upgrades, null));
                            soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta()
                                .getDisplayName().equals(plugin.formatText(configLoad
                                        .getString("Menu.Admin.Upgrade.Size.Item.Information.Displayname"))))) {
                            List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                    .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Size);

                            if (upgrades != null && upgrades.size() >= 5) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Upgrade.Tier.Limit.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else {
                                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> {
                                            AnvilGui gui = new AnvilGui(player);
                                            gui.setAction(event1 -> {

                                                if (playerDataManager.isPlayerDataLoaded(player)
                                                        && playerDataManager
                                                        .getPlayerData(player) != null) {
                                                    if (!gui.getInputText().matches("[0-9]+")) {
                                                        messageManager.sendMessage(player,
                                                                configLoad.getString(
                                                                        "Island.Admin.Upgrade.Numerical.Message"));
                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                1.0F);

                                                        player.closeInventory();

                                                        return;
                                                    } else {
                                                        List<com.songoda.skyblock.upgrade.Upgrade> upgrades1 = upgradeManager
                                                                .getUpgrades(
                                                                        com.songoda.skyblock.upgrade.Upgrade.Type.Size);

                                                        if (upgrades1 != null && upgrades1.size() >= 5) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Tier.Limit.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            Bukkit.getServer().getScheduler()
                                                                    .runTaskLater(plugin,
                                                                            () -> open(player), 1L);

                                                            return;
                                                        }
                                                    }

                                                    int size = Integer.valueOf(gui.getInputText());

                                                    if (size > 1000) {
                                                        messageManager.sendMessage(player,
                                                                configLoad.getString(
                                                                        "Island.Admin.Upgrade.Tier.Size.Message"));
                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                1.0F);

                                                        event.setWillClose(false);
                                                        event.setWillDestroy(false);

                                                        return;
                                                    } else if (upgradeManager.hasUpgrade(
                                                            com.songoda.skyblock.upgrade.Upgrade.Type.Size,
                                                            size)) {
                                                        messageManager.sendMessage(player,
                                                                configLoad.getString(
                                                                        "Island.Admin.Upgrade.Tier.Exist.Message"));
                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                1.0F);

                                                        player.closeInventory();

                                                        return;
                                                    }

                                                    soundManager.playSound(player,
                                                            CompatibleSound.BLOCK_ANVIL_USE.getSound(), 1.0F, 1.0F);
                                                    upgradeManager.addUpgrade(
                                                            com.songoda.skyblock.upgrade.Upgrade.Type.Size,
                                                            size);

                                                    Bukkit.getServer().getScheduler()
                                                            .runTaskLater(plugin,
                                                                    () -> open(player), 1L);
                                                }
                                                player.closeInventory();
                                            });

                                            ItemStack is12 = new ItemStack(Material.NAME_TAG);
                                            ItemMeta im = is12.getItemMeta();
                                            im.setDisplayName(configLoad
                                                    .getString("Menu.Admin.Upgrade.Size.Item.Word.Size.Enter"));
                                            is12.setItemMeta(im);

                                            gui.setInput(is12);
                                            plugin.getGuiManager().showGUI(player, gui);

                                        }, 1L);
                            }
                        } else if ((is.getType() == CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getMaterial())
                                && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName()
                                .equals(plugin.formatText(configLoad
                                        .getString("Menu.Admin.Upgrade.Size.Item.Barrier.Displayname"))))) {
                            soundManager.playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                            event.setWillClose(false);
                            event.setWillDestroy(false);
                        } else if ((is.getType() == Material.PAPER) && (is.hasItemMeta())) {
                            int slot = event.getSlot();
                            int tier = slot - 3;

                            com.songoda.skyblock.upgrade.Upgrade upgrade = upgradeManager
                                    .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Size).get(tier);

                            if (upgrade != null) {
                                if (event.getClick() == ClickType.LEFT) {
                                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> {
                                                AnvilGui gui = new AnvilGui(player);
                                                gui.setAction(event1 -> {

                                                    if (!(player.hasPermission("fabledskyblock.admin.upgrade")
                                                            || player.hasPermission("fabledskyblock.admin.*")
                                                            || player.hasPermission("fabledskyblock.*"))) {
                                                        messageManager.sendMessage(player,
                                                                configLoad.getString(
                                                                        "Island.Admin.Upgrade.Permission.Message"));
                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                1.0F);

                                                        return;
                                                    }

                                                    if (playerDataManager.isPlayerDataLoaded(player)
                                                            && playerDataManager
                                                            .getPlayerData(player) != null) {
                                                        if (!gui.getInputText().matches("[0-9]+")) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Numerical.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            player.closeInventory();

                                                            return;
                                                        } else if (upgradeManager.getUpgrades(
                                                                com.songoda.skyblock.upgrade.Upgrade.Type.Size)
                                                                .get(tier) == null) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Tier.Selected.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            Bukkit.getServer().getScheduler()
                                                                    .runTaskLater(plugin,
                                                                            () -> open(player), 1L);

                                                            return;
                                                        }

                                                        int size = Integer.valueOf(gui.getInputText());

                                                        if (size > 1000) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Tier.Size.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            event.setWillClose(false);
                                                            event.setWillDestroy(false);

                                                            return;
                                                        } else if (upgradeManager.hasUpgrade(
                                                                com.songoda.skyblock.upgrade.Upgrade.Type.Size,
                                                                size)) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Tier.Exist.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            event.setWillClose(false);
                                                            event.setWillDestroy(false);

                                                            return;
                                                        }

                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_USE.getSound(), 1.0F, 1.0F);
                                                        upgradeManager.getUpgrades(
                                                                com.songoda.skyblock.upgrade.Upgrade.Type.Size)
                                                                .get(tier).setValue(size);
                                                        fileManager
                                                                .getConfig(
                                                                        new File(plugin.getDataFolder(),
                                                                                "upgrades.yml"))
                                                                .getFileConfiguration()
                                                                .set("Upgrades.Size." + tier + ".Value",
                                                                        size);

                                                        Bukkit.getServer().getScheduler()
                                                                .runTaskLater(plugin,
                                                                        () -> open(player), 1L);
                                                    }

                                                    player.closeInventory();
                                                });

                                                ItemStack is13 = new ItemStack(Material.NAME_TAG);
                                                ItemMeta im = is13.getItemMeta();
                                                im.setDisplayName(configLoad.getString(
                                                        "Menu.Admin.Upgrade.Size.Item.Word.Size.Enter"));
                                                is13.setItemMeta(im);

                                                gui.setInput(is13);
                                                plugin.getGuiManager().showGUI(player, gui);

                                            }, 1L);

                                    return;
                                } else if (event.getClick() == ClickType.MIDDLE) {
                                    soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
                                    upgradeManager.removeUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Size,
                                            upgrade.getCost(), upgrade.getValue());
                                } else if (event.getClick() == ClickType.RIGHT) {
                                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> {
                                                AnvilGui gui = new AnvilGui(player);
                                                gui.setAction(event1 -> {

                                                    if (!(player.hasPermission("fabledskyblock.admin.upgrade")
                                                            || player.hasPermission("fabledskyblock.admin.*")
                                                            || player.hasPermission("fabledskyblock.*"))) {
                                                        messageManager.sendMessage(player,
                                                                configLoad.getString(
                                                                        "Island.Admin.Upgrade.Permission.Message"));
                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                1.0F);

                                                        return;
                                                    }

                                                    if (playerDataManager.isPlayerDataLoaded(player)
                                                            && playerDataManager
                                                            .getPlayerData(player) != null) {
                                                        if (!(gui.getInputText().matches("[0-9]+")
                                                                || gui.getInputText().matches(
                                                                "([0-9]*)\\.([0-9]{2}$)"))) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Numerical.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            player.closeInventory();

                                                            return;
                                                        } else if (upgradeManager.getUpgrades(
                                                                com.songoda.skyblock.upgrade.Upgrade.Type.Size)
                                                                .get(tier) == null) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Tier.Selected.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            Bukkit.getServer().getScheduler()
                                                                    .runTaskLater(plugin,
                                                                            () -> open(player), 1L);

                                                            return;
                                                        }

                                                        double cost = Double.valueOf(gui.getInputText());

                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_USE.getSound(), 1.0F, 1.0F);
                                                        upgradeManager.getUpgrades(
                                                                com.songoda.skyblock.upgrade.Upgrade.Type.Size)
                                                                .get(tier).setCost(cost);
                                                        fileManager
                                                                .getConfig(
                                                                        new File(plugin.getDataFolder(),
                                                                                "upgrades.yml"))
                                                                .getFileConfiguration()
                                                                .set("Upgrades.Size." + tier + ".Cost",
                                                                        cost);

                                                        Bukkit.getServer().getScheduler()
                                                                .runTaskLater(plugin,
                                                                        () -> open(player), 1L);
                                                    }

                                                    player.closeInventory();
                                                });

                                                ItemStack is14 = new ItemStack(Material.NAME_TAG);
                                                ItemMeta im = is14.getItemMeta();
                                                im.setDisplayName(configLoad.getString(
                                                        "Menu.Admin.Upgrade.Size.Item.Word.Cost.Enter"));
                                                is14.setItemMeta(im);

                                                gui.setInput(is14);
                                                plugin.getGuiManager().showGUI(player, gui);

                                            }, 1L);

                                    return;
                                } else {
                                    event.setWillClose(false);
                                    event.setWillDestroy(false);

                                    return;
                                }
                            }

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }
                    }
                });

                nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                        plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Size.Item.Return.Displayname")),
                        null, null, null, null), 0);
                nInv.addItem(nInv.createItem(new

                                ItemStack(Material.PAINTING),
                        plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Size.Item.Information.Displayname")),
                        configLoad.getStringList("Menu.Admin.Upgrade.Size.Item.Information.Lore"), null, null, null),
                        1);
                nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                        plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Size.Item.Barrier.Displayname")),
                        null, null, null, null), 2);

                List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                        .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Size);

                if (upgrades != null) {
                    for (int i = 0; i < 5; i++) {
                        if (upgrades.size() >= i + 1) {
                            com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(i);
                            int tier = i + 1;

                            if (upgrade != null) {
                                nInv.addItem(nInv.createItem(new ItemStack(Material.PAPER, tier),
                                        plugin.formatText(
                                                configLoad.getString("Menu.Admin.Upgrade.Size.Item.Tier.Displayname")
                                                        .replace("%tier", "" + tier)),
                                        configLoad.getStringList("Menu.Admin.Upgrade.Size.Item.Tier.Lore"),
                                        new Placeholder[]{new Placeholder("%size", "" + upgrade.getValue()),
                                                new Placeholder("%cost",
                                                        NumberUtils.formatNumber(upgrade.getCost()))},
                                        null, null), i + 3);
                            }
                        }
                    }
                }

                nInv.setTitle(plugin.formatText(
                        configLoad.getString("Menu.Admin.Upgrade.Size.Title")));
                nInv.setRows(1);

                Bukkit.getServer().

                        getScheduler().

                        runTask(plugin, nInv::open);
            } else if (viewer.getType() == Viewer.Type.Members) {
                nInventoryUtil nInv = new nInventoryUtil(player, event -> {
                    if (!(player.hasPermission("fabledskyblock.admin.upgrade") || player.hasPermission("fabledskyblock.admin.*")
                            || player.hasPermission("fabledskyblock.*"))) {
                        messageManager.sendMessage(player,
                                configLoad.getString("Island.Admin.Upgrade.Permission.Message"));
                        soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                        return;
                    }

                    if (playerDataManager.isPlayerDataLoaded(player)) {
                        PlayerData playerData = playerDataManager.getPlayerData(player);
                        ItemStack is = event.getItem();

                        if ((is.getType() == CompatibleMaterial.OAK_FENCE_GATE.getMaterial()) && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName()
                                .equals(plugin.formatText(configLoad
                                        .getString("Menu.Admin.Upgrade.Members.Item.Return.Displayname"))))) {
                            playerData.setViewer(new Viewer(Viewer.Type.Upgrades, null));
                            soundManager.playSound(player, CompatibleSound.ENTITY_ARROW_HIT.getSound(), 1.0F, 1.0F);

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        } else if ((is.getType() == Material.PAINTING) && (is.hasItemMeta()) && (is.getItemMeta()
                                .getDisplayName().equals(plugin.formatText(configLoad
                                        .getString("Menu.Admin.Upgrade.Members.Item.Information.Displayname"))))) {
                            List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                                    .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Members);

                            if (upgrades != null && upgrades.size() >= 5) {
                                messageManager.sendMessage(player,
                                        configLoad.getString("Island.Admin.Upgrade.Tier.Limit.Message"));
                                soundManager.playSound(player, CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F, 1.0F);

                                event.setWillClose(false);
                                event.setWillDestroy(false);
                            } else {
                                soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                        () -> {
                                            AnvilGui gui = new AnvilGui(player);
                                            gui.setAction(event1 -> {

                                                if (playerDataManager.isPlayerDataLoaded(player)
                                                        && playerDataManager
                                                        .getPlayerData(player) != null) {
                                                    if (!gui.getInputText().matches("[0-9]+")) {
                                                        messageManager.sendMessage(player,
                                                                configLoad.getString(
                                                                        "Island.Admin.Upgrade.Numerical.Message"));
                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                1.0F);

                                                        player.closeInventory();

                                                        return;
                                                    } else {
                                                        List<com.songoda.skyblock.upgrade.Upgrade> upgrades1 = upgradeManager
                                                                .getUpgrades(
                                                                        com.songoda.skyblock.upgrade.Upgrade.Type.Members);

                                                        if (upgrades1 != null && upgrades1.size() >= 5) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Tier.Limit.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            Bukkit.getServer().getScheduler()
                                                                    .runTaskLater(plugin,
                                                                            () -> open(player), 1L);

                                                            return;
                                                        }
                                                    }

                                                    int size = Integer.valueOf(gui.getInputText());

                                                    if (size > 1000) {
                                                        messageManager.sendMessage(player,
                                                                configLoad.getString(
                                                                        "Island.Admin.Upgrade.Tier.Members.Message"));
                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                1.0F);

                                                        event.setWillClose(false);
                                                        event.setWillDestroy(false);

                                                        return;
                                                    } else if (upgradeManager.hasUpgrade(
                                                            com.songoda.skyblock.upgrade.Upgrade.Type.Members,
                                                            size)) {
                                                        messageManager.sendMessage(player,
                                                                configLoad.getString(
                                                                        "Island.Admin.Upgrade.Tier.Exist.Message"));
                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                1.0F);

                                                        player.closeInventory();

                                                        return;
                                                    }

                                                    soundManager.playSound(player,
                                                            CompatibleSound.BLOCK_ANVIL_USE.getSound(), 1.0F, 1.0F);
                                                    upgradeManager.addUpgrade(
                                                            com.songoda.skyblock.upgrade.Upgrade.Type.Members,
                                                            size);

                                                    Bukkit.getServer().getScheduler()
                                                            .runTaskLater(plugin,
                                                                    () -> open(player), 1L);
                                                }

                                                player.closeInventory();

                                            });

                                            ItemStack is12 = new ItemStack(Material.NAME_TAG);
                                            ItemMeta im = is12.getItemMeta();
                                            im.setDisplayName(configLoad
                                                    .getString("Menu.Admin.Upgrade.Members.Item.Word.Members.Enter"));
                                            is12.setItemMeta(im);

                                            gui.setInput(is12);
                                            plugin.getGuiManager().showGUI(player, gui);

                                        }, 1L);
                            }
                        } else if ((is.getType() == CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getMaterial())
                                && (is.hasItemMeta())
                                && (is.getItemMeta().getDisplayName()
                                .equals(plugin.formatText(configLoad
                                        .getString("Menu.Admin.Upgrade.Members.Item.Barrier.Displayname"))))) {
                            soundManager.playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                            event.setWillClose(false);
                            event.setWillDestroy(false);
                        } else if ((is.getType() == Material.PAPER) && (is.hasItemMeta())) {
                            int slot = event.getSlot();
                            int tier = slot - 3;

                            com.songoda.skyblock.upgrade.Upgrade upgrade = upgradeManager
                                    .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Members).get(tier);

                            if (upgrade != null) {
                                if (event.getClick() == ClickType.LEFT) {
                                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> {
                                                AnvilGui gui = new AnvilGui(player);
                                                gui.setAction(event1 -> {

                                                    if (!(player.hasPermission("fabledskyblock.admin.upgrade")
                                                            || player.hasPermission("fabledskyblock.admin.*")
                                                            || player.hasPermission("fabledskyblock.*"))) {
                                                        messageManager.sendMessage(player,
                                                                configLoad.getString(
                                                                        "Island.Admin.Upgrade.Permission.Message"));
                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                1.0F);

                                                        return;
                                                    }

                                                    if (playerDataManager.isPlayerDataLoaded(player)
                                                            && playerDataManager
                                                            .getPlayerData(player) != null) {
                                                        if (!gui.getInputText().matches("[0-9]+")) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Numerical.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            player.closeInventory();

                                                            return;
                                                        } else if (upgradeManager.getUpgrades(
                                                                com.songoda.skyblock.upgrade.Upgrade.Type.Members)
                                                                .get(tier) == null) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Tier.Selected.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            Bukkit.getServer().getScheduler()
                                                                    .runTaskLater(plugin,
                                                                            () -> open(player), 1L);

                                                            return;
                                                        }

                                                        int size = Integer.valueOf(gui.getInputText());

                                                        if (size > 1000) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Tier.Members.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            event.setWillClose(false);
                                                            event.setWillDestroy(false);

                                                            return;
                                                        } else if (upgradeManager.hasUpgrade(
                                                                com.songoda.skyblock.upgrade.Upgrade.Type.Members,
                                                                size)) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Tier.Exist.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            event.setWillClose(false);
                                                            event.setWillDestroy(false);

                                                            return;
                                                        }

                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_USE.getSound(), 1.0F, 1.0F);
                                                        upgradeManager.getUpgrades(
                                                                com.songoda.skyblock.upgrade.Upgrade.Type.Members)
                                                                .get(tier).setValue(size);
                                                        fileManager
                                                                .getConfig(
                                                                        new File(plugin.getDataFolder(),
                                                                                "upgrades.yml"))
                                                                .getFileConfiguration()
                                                                .set("Upgrades.Members." + tier + ".Value",
                                                                        size);

                                                        Bukkit.getServer().getScheduler()
                                                                .runTaskLater(plugin,
                                                                        () -> open(player), 1L);
                                                    }

                                                    player.closeInventory();
                                                });

                                                ItemStack is13 = new ItemStack(Material.NAME_TAG);
                                                ItemMeta im = is13.getItemMeta();
                                                im.setDisplayName(configLoad.getString(
                                                        "Menu.Admin.Upgrade.Members.Item.Word.Members.Enter"));
                                                is13.setItemMeta(im);

                                                gui.setInput(is13);
                                                plugin.getGuiManager().showGUI(player, gui);

                                            }, 1L);

                                    return;
                                } else if (event.getClick() == ClickType.MIDDLE) {
                                    soundManager.playSound(player, CompatibleSound.ENTITY_IRON_GOLEM_ATTACK.getSound(), 1.0F, 1.0F);
                                    upgradeManager.removeUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type.Members,
                                            upgrade.getCost(), upgrade.getValue());
                                } else if (event.getClick() == ClickType.RIGHT) {
                                    soundManager.playSound(player, CompatibleSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getSound(), 1.0F, 1.0F);

                                    Bukkit.getServer().getScheduler().runTaskLater(plugin,
                                            () -> {
                                                AnvilGui gui = new AnvilGui(player);
                                                gui.setAction(event1 -> {

                                                    if (!(player.hasPermission("fabledskyblock.admin.upgrade")
                                                            || player.hasPermission("fabledskyblock.admin.*")
                                                            || player.hasPermission("fabledskyblock.*"))) {
                                                        messageManager.sendMessage(player,
                                                                configLoad.getString(
                                                                        "Island.Admin.Upgrade.Permission.Message"));
                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                1.0F);

                                                        return;
                                                    }

                                                    if (playerDataManager.isPlayerDataLoaded(player)
                                                            && playerDataManager
                                                            .getPlayerData(player) != null) {
                                                        if (!(gui.getInputText().matches("[0-9]+")
                                                                || gui.getInputText().matches(
                                                                "([0-9]*)\\.([0-9]{2}$)"))) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Numerical.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            player.closeInventory();

                                                            return;
                                                        } else if (upgradeManager.getUpgrades(
                                                                com.songoda.skyblock.upgrade.Upgrade.Type.Members)
                                                                .get(tier) == null) {
                                                            messageManager.sendMessage(player,
                                                                    configLoad.getString(
                                                                            "Island.Admin.Upgrade.Tier.Selected.Message"));
                                                            soundManager.playSound(player,
                                                                    CompatibleSound.BLOCK_ANVIL_LAND.getSound(), 1.0F,
                                                                    1.0F);

                                                            Bukkit.getServer().getScheduler()
                                                                    .runTaskLater(plugin,
                                                                            () -> open(player), 1L);

                                                            return;
                                                        }

                                                        double cost = Double.valueOf(gui.getInputText());

                                                        soundManager.playSound(player,
                                                                CompatibleSound.BLOCK_ANVIL_USE.getSound(), 1.0F, 1.0F);
                                                        upgradeManager.getUpgrades(
                                                                com.songoda.skyblock.upgrade.Upgrade.Type.Members)
                                                                .get(tier).setCost(cost);
                                                        fileManager
                                                                .getConfig(
                                                                        new File(plugin.getDataFolder(),
                                                                                "upgrades.yml"))
                                                                .getFileConfiguration()
                                                                .set("Upgrades.Members." + tier + ".Cost",
                                                                        cost);

                                                        Bukkit.getServer().getScheduler()
                                                                .runTaskLater(plugin,
                                                                        () -> open(player), 1L);
                                                    }

                                                    player.closeInventory();
                                                });

                                                ItemStack is14 = new ItemStack(Material.NAME_TAG);
                                                ItemMeta im = is14.getItemMeta();
                                                im.setDisplayName(configLoad.getString(
                                                        "Menu.Admin.Upgrade.Members.Item.Word.Cost.Enter"));
                                                is14.setItemMeta(im);

                                                gui.setInput(is14);
                                                plugin.getGuiManager().showGUI(player, gui);

                                            }, 1L);

                                    return;
                                } else {
                                    event.setWillClose(false);
                                    event.setWillDestroy(false);

                                    return;
                                }
                            }

                            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> open(player), 1L);
                        }
                    }
                });

                nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(),
                        plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Members.Item.Return.Displayname")),
                        null, null, null, null), 0);
                nInv.addItem(nInv.createItem(new ItemStack(Material.PAINTING),
                        plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Members.Item.Information.Displayname")),
                        configLoad.getStringList("Menu.Admin.Upgrade.Members.Item.Information.Lore"), null, null, null),
                        1);
                nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                        plugin.formatText(
                                configLoad.getString("Menu.Admin.Upgrade.Members.Item.Barrier.Displayname")),
                        null, null, null, null), 2);

                List<com.songoda.skyblock.upgrade.Upgrade> upgrades = upgradeManager
                        .getUpgrades(com.songoda.skyblock.upgrade.Upgrade.Type.Members);

                if (upgrades != null) {
                    for (int i = 0; i < 5; i++) {
                        if (upgrades.size() >= i + 1) {
                            com.songoda.skyblock.upgrade.Upgrade upgrade = upgrades.get(i);
                            int tier = i + 1;

                            if (upgrade != null) {
                                nInv.addItem(nInv.createItem(new ItemStack(Material.PAPER, tier),
                                        plugin.formatText(
                                                configLoad.getString("Menu.Admin.Upgrade.Members.Item.Tier.Displayname")
                                                        .replace("%tier", "" + tier)),
                                        configLoad.getStringList("Menu.Admin.Upgrade.Members.Item.Tier.Lore"),
                                        new Placeholder[]{new Placeholder("%maxMembers", "" + upgrade.getValue()),
                                                new Placeholder("%cost",
                                                        NumberUtils.formatNumber(upgrade.getCost()))},
                                        null, null), i + 3);
                            }
                        }
                    }
                }

                nInv.setTitle(plugin.formatText(configLoad.getString("Menu.Admin.Upgrade.Members.Title")));
                nInv.setRows(1);

                Bukkit.getServer().getScheduler().runTask(plugin, nInv::open);
            }
        }
    }

    private String getStatus(com.songoda.skyblock.upgrade.Upgrade upgrade) {
        FileConfiguration configLoad = SkyBlock.getInstance().getLanguage();

        if (upgrade.isEnabled()) {
            return configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Word.Disable");
        } else {
            return configLoad.getString("Menu.Admin.Upgrade.Upgrades.Item.Word.Enable");
        }
    }

    public static class Viewer {

        private Type type;
        private com.songoda.skyblock.upgrade.Upgrade.Type upgrade;

        public Viewer(Type type, com.songoda.skyblock.upgrade.Upgrade.Type upgrade) {
            this.type = type;
            this.upgrade = upgrade;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public com.songoda.skyblock.upgrade.Upgrade.Type getUpgrade() {
            return upgrade;
        }

        public void setUpgrade(com.songoda.skyblock.upgrade.Upgrade.Type upgrade) {
            this.upgrade = upgrade;
        }

        public enum Type {

            Upgrades, Size, Members

        }
    }
}
