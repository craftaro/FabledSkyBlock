package com.songoda.skyblock.menus;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.CompatibleSound;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.utils.item.MenuClickRegistry;
import com.songoda.skyblock.utils.item.MenuClickRegistry.RegistryKey;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public final class ControlPanel {

    private static ControlPanel instance;

    public static ControlPanel getInstance() {
        return instance == null ? instance = new ControlPanel() : instance;
    }

    private ControlPanel() {

        MenuClickRegistry.getInstance().register((executors) -> {

            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Teleport.Displayname", CompatibleMaterial.OAK_DOOR), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island teleport"), 1L);
            });

            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Lock.Displayname", CompatibleMaterial.IRON_DOOR), (inst, player, e) -> {

                final Island island = SkyBlock.getInstance().getIslandManager().getIsland((Player) player);

                switch (island.getStatus()) {
                    case OPEN:
                        Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island whitelist on"), 1L);
                        break;
                    case CLOSED:
                        Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island open"), 1L);
                        break;
                    case WHITELISTED:
                        Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island close"), 1L);
                        break;
                }
            });

            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Barrier.Displayname", CompatibleMaterial.BLACK_STAINED_GLASS_PANE), (inst, player, e) -> {
                inst.getSoundManager().playSound(player, CompatibleSound.BLOCK_GLASS_BREAK.getSound(), 1.0F, 1.0F);

                e.setWillClose(false);
                e.setWillDestroy(false);
            });

            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Level.Displayname", CompatibleMaterial.EXPERIENCE_BOTTLE), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island level"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Settings.Displayname", CompatibleMaterial.NAME_TAG), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island settings"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Members.Displayname", CompatibleMaterial.ITEM_FRAME), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island members"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Biome.Displayname", CompatibleMaterial.OAK_SAPLING), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island biome"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Weather.Displayname", CompatibleMaterial.CLOCK), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island weather"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Bans.Displayname", CompatibleMaterial.IRON_AXE), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island bans"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Visitors.Displayname", CompatibleMaterial.OAK_SIGN), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island visitors"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Upgrades.Displayname", CompatibleMaterial.ANVIL), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island upgrades"), 1L);
            });

        });

    }

    public void open(Player player) {
        SkyBlock plugin = SkyBlock.getInstance();

        Config config = plugin.getFileManager().getConfig(new File(plugin.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        nInventoryUtil nInv = new nInventoryUtil(player, event -> {
            MenuClickRegistry.getInstance().dispatch(player, event);
        });

        // Teleport to island and open/close island
        if(player.hasPermission("fabledskyblock.island.teleport")){
            nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_DOOR.getItem(), configLoad.getString("Menu.ControlPanel.Item.Teleport.Displayname"),
                    configLoad.getStringList("Menu.ControlPanel.Item.Teleport.Lore"), null, null, null), 1);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 1);
        }

        if(player.hasPermission("fabledskyblock.island.close")){
            nInv.addItem(nInv.createItem(CompatibleMaterial.IRON_DOOR.getItem(), configLoad.getString("Menu.ControlPanel.Item.Lock.Displayname"),
                    configLoad.getStringList("Menu.ControlPanel.Item.Lock.Lore"), null, null, null), 10);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 10);
        }

        // Glass panes barriers
        nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(), configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 0, 2, 5, 8,
                9, 11, 14, 17);

        // 4 Items at the left
        if(player.hasPermission("fabledskyblock.island.level")){
            nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.EXPERIENCE_BOTTLE.getMaterial()), configLoad.getString("Menu.ControlPanel.Item.Level.Displayname"),
                    configLoad.getStringList("Menu.ControlPanel.Item.Level.Lore"), null, null, null), 3);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 3);
        }
        if(player.hasPermission("fabledskyblock.island.settings")){
            nInv.addItem(nInv.createItem(new ItemStack(Material.NAME_TAG), configLoad.getString("Menu.ControlPanel.Item.Settings.Displayname"),
                    configLoad.getStringList("Menu.ControlPanel.Item.Settings.Lore"), null, null, null), 4);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 4);
        }
        if(player.hasPermission("fabledskyblock.island.weather")){
            nInv.addItem(nInv.createItem(CompatibleMaterial.CLOCK.getItem(), configLoad.getString("Menu.ControlPanel.Item.Weather.Displayname"),
                    configLoad.getStringList("Menu.ControlPanel.Item.Weather.Lore"), null, null, null), 12);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 12);
        }
        if(player.hasPermission("fabledskyblock.island.biome")){
            nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_SAPLING.getItem(), configLoad.getString("Menu.ControlPanel.Item.Biome.Displayname"),
                    configLoad.getStringList("Menu.ControlPanel.Item.Biome.Lore"), null, null, null), 13);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 13);
        }

        // 4 Items at the right
        if(player.hasPermission("fabledskyblock.island.members")){
            nInv.addItem(nInv.createItem(new ItemStack(Material.ITEM_FRAME), configLoad.getString("Menu.ControlPanel.Item.Members.Displayname"),
                    configLoad.getStringList("Menu.ControlPanel.Item.Members.Lore"), null, null, null), 16);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 16);
        }
        if(player.hasPermission("fabledskyblock.island.bans")){
            nInv.addItem(nInv.createItem(new ItemStack(Material.IRON_AXE), configLoad.getString("Menu.ControlPanel.Item.Bans.Displayname"),
                    configLoad.getStringList("Menu.ControlPanel.Item.Bans.Lore"), null, null, new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES }), 6);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 6);
        }
        if(player.hasPermission("fabledskyblock.island.visitors")){
            nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.OAK_SIGN.getMaterial()), configLoad.getString("Menu.ControlPanel.Item.Visitors.Displayname"),
                    configLoad.getStringList("Menu.ControlPanel.Item.Visitors.Lore"), null, null, null), 7);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 7);
        }
        if(player.hasPermission("fabledskyblock.island.upgrade")){
            nInv.addItem(nInv.createItem(new ItemStack(CompatibleMaterial.ANVIL.getMaterial()), configLoad.getString("Menu.ControlPanel.Item.Upgrades.Displayname"),
                    configLoad.getStringList("Menu.ControlPanel.Item.Upgrades.Lore"), null, null, null), 15);
        } else {
            nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(),
                    configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 15);
        }

        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Title")));
        nInv.setRows(2);

        Bukkit.getServer().getScheduler().runTask(plugin, () -> nInv.open());
    }
}
