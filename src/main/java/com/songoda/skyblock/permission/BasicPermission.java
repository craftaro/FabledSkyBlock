package com.songoda.skyblock.permission;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.config.FileManager;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandRole;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicPermission {

    private final String name;
    private final CompatibleMaterial icon;
    private final PermissionType type;

    protected BasicPermission(String name, CompatibleMaterial icon, PermissionType type) {
        this.name = name;
        this.icon = icon;
        this.type = type;
    }

    public ItemStack getItem(Island island, IslandRole role) {
        ItemStack is = icon.getItem();
        FileManager.Config config = SkyBlock.getInstance().getFileManager()
                .getConfig(new File(SkyBlock.getInstance().getDataFolder(), "language.yml"));
        FileConfiguration configLoad = config.getFileConfiguration();

        List<String> itemLore = new ArrayList<>();

        ItemMeta im = is.getItemMeta();

        String roleName = role.name();

        if (role == IslandRole.Visitor
                || role == IslandRole.Member
                || role == IslandRole.Coop)
            roleName = "Default";

        String nameFinal = configLoad.getString("Menu.Settings." + roleName + ".Item.Setting." + name + ".Displayname");

        im.setDisplayName(TextUtils.formatText(nameFinal == null ? name : nameFinal));

        for (String itemLoreList : configLoad
                .getStringList("Menu.Settings." + roleName + ".Item.Setting.Status."
                        + (island.hasPermission(role, this) ? "Enabled" : "Disabled") + ".Lore"))
            itemLore.add(TextUtils.formatText(itemLoreList));

        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        im.setLore(itemLore);
        is.setItemMeta(im);

        return is;
    }

    /**
     * Use this to check additional perms.
     *
     * @return
     */
    public boolean extraCheck() {
        return true;
    }

    public String getName() {
        return name;
    }

    public CompatibleMaterial getIcon() {
        return icon;
    }

    public PermissionType getType() {
        return type;
    }
}
