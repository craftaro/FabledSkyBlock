package com.songoda.skyblock.permission;

import com.craftaro.core.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.core.utils.TextUtils;
import com.songoda.skyblock.SkyBlock;
import com.songoda.skyblock.island.Island;
import com.songoda.skyblock.island.IslandRole;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicPermission {
    private final String name;
    private final XMaterial icon;
    private final PermissionType type;

    protected BasicPermission(@Nonnull String name, @Nonnull XMaterial icon, @Nonnull PermissionType type) {
        this.name = name;
        this.icon = icon;
        this.type = type;
    }

    public ItemStack getItem(Island island, IslandRole role) {
        return getItem(island.hasPermission(role, this), role);
    }

    public ItemStack getItem(boolean permissionEnabled, IslandRole role) {
        ItemStack is = this.icon.parseItem();
        FileConfiguration configLoad = SkyBlock.getInstance().getLanguage();

        List<String> itemLore = new ArrayList<>();

        ItemMeta im = is.getItemMeta();

        String roleName = role.name();

        if (role == IslandRole.VISITOR || role == IslandRole.MEMBER || role == IslandRole.COOP) {
            roleName = "Default";
        }

        String nameFinal = TextUtils.formatText(configLoad.getString("Menu.Settings." + roleName + ".Item.Setting." + this.name + ".Displayname", this.name));

        if (im != null) {
            im.setDisplayName(nameFinal);
            for (String itemLoreList : configLoad
                    .getStringList("Menu.Settings." + roleName + ".Item.Setting.Status."
                            + (permissionEnabled ? "Enabled" : "Disabled") + ".Lore")) {
                itemLore.add(TextUtils.formatText(itemLoreList));
            }

            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            im.setLore(itemLore);
            is.setItemMeta(im);
        }

        return is;
    }

    public String getName() {
        return this.name;
    }

    public XMaterial getIcon() {
        return this.icon;
    }

    public PermissionType getType() {
        return this.type;
    }
}
