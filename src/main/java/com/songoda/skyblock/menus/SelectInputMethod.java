package com.songoda.skyblock.menus;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.utils.item.MenuClickRegistry;
import com.songoda.skyblock.utils.item.nInventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

/**
 * Used to select an input method using a gui.
 */
public class SelectInputMethod {

    private static SelectInputMethod instance;

    public static SelectInputMethod getInstance() {return instance == null ? instance = new SelectInputMethod() : instance;}

    private HashMap<UUID,InputMethodSelectlistener> listeners;

    public SelectInputMethod() {
        listeners = new HashMap<>();
        MenuClickRegistry.getInstance().register((executors -> {
            executors.put(MenuClickRegistry.RegistryKey.fromLanguageFile("Menu.Input.Item.All.Displayname", CompatibleMaterial.IRON_BLOCK), (ints,player,e) -> {
                listeners.get(player.getUniqueId()).choose(InputMethodSelectlistener.InputMethod.ALL);
                Bukkit.getScheduler().runTask(SkyBlock.getInstance(), () -> {
                    listeners.remove(player);
                });
                e.setWillClose(true);
                e.setWillDestroy(true);
            });
        }));

        MenuClickRegistry.getInstance().register((executors -> {
            executors.put(MenuClickRegistry.RegistryKey.fromLanguageFile("Menu.Input.Item.Custom.Displayname", CompatibleMaterial.OAK_SIGN), (ints,player,e) -> {
                listeners.get(player.getUniqueId()).choose(InputMethodSelectlistener.InputMethod.CUSTOM);
                Bukkit.getScheduler().runTask(SkyBlock.getInstance(), () -> {
                    listeners.remove(player);
                });
                e.setWillClose(true);
                e.setWillDestroy(true);
            });
        }));

        MenuClickRegistry.getInstance().register((executors -> {
            executors.put(MenuClickRegistry.RegistryKey.fromLanguageFile("Menu.Input.Item.Exit.Displayname", CompatibleMaterial.OAK_FENCE_GATE), (ints,player,e) -> {
                listeners.get(player.getUniqueId()).choose(InputMethodSelectlistener.InputMethod.CANCELED);
                Bukkit.getScheduler().runTask(SkyBlock.getInstance(), () -> {
                    listeners.remove(player);
                });
                e.setWillClose(true);
                e.setWillDestroy(true);
            });
        }));

        MenuClickRegistry.getInstance().register((executors -> {
            executors.put(MenuClickRegistry.RegistryKey.fromLanguageFile("Menu.Input.Item.Barrier.Displayname", CompatibleMaterial.BLACK_STAINED_GLASS_PANE), (ints,player,e) -> {
                e.setWillClose(false);
                e.setWillDestroy(false);
            });
        }));
    }

    public void open(Player player, String action, InputMethodSelectlistener listener) {
        SkyBlock skyblock = SkyBlock.getInstance();
        FileManager.Config config = skyblock.getFileManager().getConfig(new File(skyblock.getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();
        nInventoryUtil nInv = new nInventoryUtil(player, event -> MenuClickRegistry.getInstance().dispatch(player, event));

        listeners.put(player.getUniqueId(), listener);

        nInv.addItem(nInv.createItem(CompatibleMaterial.BLACK_STAINED_GLASS_PANE.getItem(), configLoad.getString("Menu.Input.Item.Barrier.Displayname"),
                configLoad.getStringList("Menu.Input.Item.Barrier.Lore"), null, null, null), 0,1,3,5,7,8);

        nInv.addItem(nInv.createItem(CompatibleMaterial.IRON_BLOCK.getItem(), configLoad.getString("Menu.Input.Item.All.Displayname"),
                configLoad.getStringList("Menu.Input.Item.All.Lore"), null, null, null), 2);

        nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_FENCE_GATE.getItem(), configLoad.getString("Menu.Input.Item.Exit.Displayname"),
                configLoad.getStringList("Menu.Input.Item.Exit.Lore"), null, null, null), 4);

        nInv.addItem(nInv.createItem(CompatibleMaterial.OAK_SIGN.getItem(), configLoad.getString("Menu.Input.Item.Custom.Displayname"),
                configLoad.getStringList("Menu.Input.Item.Custom.Lore"), null, null, null), 6);

        nInv.setRows(1);

        nInv.setTitle(configLoad.getString("Menu.Input.Title"));

        Bukkit.getScheduler().runTask(skyblock,nInv::open);
    }
}
