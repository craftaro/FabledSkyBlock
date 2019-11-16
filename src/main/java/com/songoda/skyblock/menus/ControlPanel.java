package com.songoda.skyblock.menus;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager.Config;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.utils.item.MenuClickRegistry;
import com.songoda.skyblock.utils.item.MenuClickRegistry.RegistryKey;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import com.songoda.skyblock.utils.version.Materials;
import com.songoda.skyblock.utils.version.Sounds;

public final class ControlPanel {

    private static ControlPanel instance;

    public static ControlPanel getInstance() {
        return instance == null ? instance = new ControlPanel() : instance;
    }

    private ControlPanel() {

        MenuClickRegistry.getInstance().register((executors) -> {

            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Teleport.Displayname", Materials.OAK_DOOR), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island teleport"), 1L);
            });

            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Lock.Displayname", Materials.IRON_DOOR), (inst, player, e) -> {

                final Island island = SkyBlock.getInstance().getIslandManager().getIsland((Player) player);

                if (island.isOpen()) {
                    Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island close"), 1L);
                } else {
                    Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island open"), 1L);
                }
            });

            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Barrier.Displayname", Materials.BLACK_STAINED_GLASS_PANE), (inst, player, e) -> {
                inst.getSoundManager().playSound(player, Sounds.GLASS.bukkitSound(), 1.0F, 1.0F);

                e.setWillClose(false);
                e.setWillDestroy(false);
            });

            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Level.Displayname", Materials.EXPERIENCE_BOTTLE), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island level"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Settings.Displayname", Materials.NAME_TAG), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island settings"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Members.Displayname", Materials.ITEM_FRAME), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island members"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Biome.Displayname", Materials.OAK_SAPLING), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island biome"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Weather.Displayname", Materials.CLOCK), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island weather"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Bans.Displayname", Materials.IRON_AXE), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island bans"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Visitors.Displayname", Materials.OAK_SIGN), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island visitors"), 1L);
            });
            executors.put(RegistryKey.fromLanguageFile("Menu.ControlPanel.Item.Upgrades.Displayname", Materials.ANVIL), (inst, player, e) -> {
                Bukkit.getServer().getScheduler().runTaskLater(inst, () -> Bukkit.getServer().dispatchCommand(player, "island upgrades"), 1L);
            });

        });

    }

    public void open(Player player) {
        SkyBlock skyblock = SkyBlock.getInstance();

        Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        nInventoryUtil nInv = new nInventoryUtil(player, event -> {
            MenuClickRegistry.getInstance().dispatch(player, event);
        });

        // Teleport to island and open/close island
        nInv.addItem(nInv.createItem(Materials.OAK_DOOR.parseItem(), configLoad.getString("Menu.ControlPanel.Item.Teleport.Displayname"),
                configLoad.getStringList("Menu.ControlPanel.Item.Teleport.Lore"), null, null, null), 1);
        nInv.addItem(nInv.createItem(Materials.IRON_DOOR.parseItem(), configLoad.getString("Menu.ControlPanel.Item.Lock.Displayname"),
                configLoad.getStringList("Menu.ControlPanel.Item.Lock.Lore"), null, null, null), 10);

        // Glass panes barriers
        nInv.addItem(nInv.createItem(Materials.BLACK_STAINED_GLASS_PANE.parseItem(), configLoad.getString("Menu.ControlPanel.Item.Barrier.Displayname"), null, null, null, null), 0, 2, 5, 8,
                9, 11, 14, 17);

        // 4 Items at the left
        nInv.addItem(nInv.createItem(new ItemStack(Materials.EXPERIENCE_BOTTLE.parseMaterial()), configLoad.getString("Menu.ControlPanel.Item.Level.Displayname"),
                configLoad.getStringList("Menu.ControlPanel.Item.Level.Lore"), null, null, null), 3);
        nInv.addItem(nInv.createItem(new ItemStack(Material.NAME_TAG), configLoad.getString("Menu.ControlPanel.Item.Settings.Displayname"),
                configLoad.getStringList("Menu.ControlPanel.Item.Settings.Lore"), null, null, null), 4);
        nInv.addItem(nInv.createItem(Materials.CLOCK.parseItem(), configLoad.getString("Menu.ControlPanel.Item.Weather.Displayname"),
                configLoad.getStringList("Menu.ControlPanel.Item.Weather.Lore"), null, null, null), 12);
        nInv.addItem(nInv.createItem(Materials.OAK_SAPLING.parseItem(), configLoad.getString("Menu.ControlPanel.Item.Biome.Displayname"),
                configLoad.getStringList("Menu.ControlPanel.Item.Biome.Lore"), null, null, null), 13);

        // 4 Items at the right
        nInv.addItem(nInv.createItem(new ItemStack(Material.ITEM_FRAME), configLoad.getString("Menu.ControlPanel.Item.Members.Displayname"),
                configLoad.getStringList("Menu.ControlPanel.Item.Members.Lore"), null, null, null), 16);
        nInv.addItem(nInv.createItem(new ItemStack(Material.IRON_AXE), configLoad.getString("Menu.ControlPanel.Item.Bans.Displayname"),
                configLoad.getStringList("Menu.ControlPanel.Item.Bans.Lore"), null, null, new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES }), 6);
        nInv.addItem(nInv.createItem(new ItemStack(Materials.OAK_SIGN.parseMaterial()), configLoad.getString("Menu.ControlPanel.Item.Visitors.Displayname"),
                configLoad.getStringList("Menu.ControlPanel.Item.Visitors.Lore"), null, null, null), 7);
        nInv.addItem(nInv.createItem(new ItemStack(Materials.ANVIL.parseMaterial()), configLoad.getString("Menu.ControlPanel.Item.Upgrades.Displayname"),
                configLoad.getStringList("Menu.ControlPanel.Item.Upgrades.Lore"), null, null, null), 15);

        nInv.setTitle(ChatColor.translateAlternateColorCodes('&', configLoad.getString("Menu.ControlPanel.Title")));
        nInv.setRows(2);

        Bukkit.getServer().getScheduler().runTask(skyblock, () -> nInv.open());
    }
}
